package userinterface.graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import org.jfree.data.xy.XYSeries;

import param.BigRational;
import param.Function;
import settings.IntegerSetting;
import settings.Setting;
import settings.SettingException;

public class ParametricGraph extends Graph{

	private static final long serialVersionUID = 1L;

	private HashMap<SeriesKey, Function> functionCache;
	private int resolution;
	private double lowerBound;
	private double upperBound;
	
	/**Extra settings for the parametric graphs*/
	
	private IntegerSetting resolutionSetting;


	public ParametricGraph(String title){
		super(title);

		functionCache = new HashMap<SeriesKey, Function>();
		resolution = 100;
		lowerBound = 0.0;
		upperBound = 1.0;
		
		initSettings();
		updateGraph();
	}
	
	public void initSettings(){
		
		resolutionSetting = new IntegerSetting("Sampling rate (Parametric)", resolution, 
				"Change the sampling resolution of the generated parameteric function", this, false);
		
	}

	public HashMap<SeriesKey, Function> getFunctionCache() {
		return functionCache;
	}

	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
		
		try {
			resolutionSetting.setValue(resolution);
		} catch (SettingException e) {
			e.printStackTrace();
		}
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	public SeriesKey addSeries(String seriesName, Function func){

		SeriesKey key = super.addSeries(seriesName);
		functionCache.put(key, func);

		return key;
	}
	
	public void addFunction(SeriesKey key , Function func){
		functionCache.put(key, func);
	}

	public void hideShapes(){
		

		synchronized (getSeriesLock()) {	
			Vector<SeriesKey> keys = getAllSeriesKeys();	

			for(int i = 0 ; i < keys.size() ; i++){

				if(functionCache.containsKey(keys.get(i)))
				{
					getErrorRenderer().setSeriesShapesVisible(i, false);
				}
				
			}
			
		}
	}

	public void plotSeries(SeriesKey key){

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Function func = functionCache.get(key);

				for (int i = 0; i < resolution; i++) {

					double val = (double) i / (double) resolution;

					if(val < lowerBound || val > upperBound){
						continue;
					}

					BigRational br = func.evaluate(new param.Point(new BigRational[] {new BigRational(i, resolution)}));
					PrismXYDataItem di = new PrismXYDataItem(((double)i)/resolution, br.doubleValue());
					addPointToSeries(key, di);
				}

				hideShapes();
			}
		}).start();
	}
	
	public void rePlot(){
		
		for(SeriesKey key : getAllSeriesKeys()){
			
			if(functionCache.get(key) == null){
				
				continue;
			}
			
			XYSeries series = super.keyToSeries.get(key);
			series.clear();
			plotSeries(key);
		}
		
	}
	
	
	

	@Override
	public void addPointToSeries(SeriesKey seriesKey, PrismXYDataItem dataItem) {
		super.addPointToSeries(seriesKey, dataItem);
		
		hideShapes();
	}

	@Override
	public int getNumSettings() {
		return 10;
	}
	
	@Override
	public Setting getSetting(int index) {

		if(index < 9){
			
			return super.getSetting(index);
			
		}
		else{
			
			switch (index) {
			
			case 9:
				return resolutionSetting;
			default:
				return null;
			}
			
		}
	}

	@Override
	public void notifySettingChanged(Setting setting) {
		super.notifySettingChanged(setting);
		updateGraph();
	}
	
	private void updateGraph(){
		
		if(resolutionSetting.getIntegerValue() != resolution){
			setResolution(resolutionSetting.getIntegerValue());
			rePlot();
		}
	}

	public void exportToGnuplotParametric(File file) throws IOException {
		
		PrintWriter out = new PrintWriter(new FileWriter(file));
		
		out.println("#=========================================");
		out.println("# Generated by PRISM Chart Package");
		out.println("#=========================================");
		out.println("# usage: gnuplot <filename>");
		out.println("# Written by Muhammad Omer Saeed <muhammad.omar555@gmail.com>");
		
		out.println();
		
		synchronized (getSeriesLock()) 
		{
			out.println("set xrange [" + getChart().getXYPlot().getDomainAxis().getRange().getLowerBound() + 
					":" + getChart().getXYPlot().getDomainAxis().getRange().getUpperBound() + "]");
			
			out.println("set yrange [" + getChart().getXYPlot().getRangeAxis().getRange().getLowerBound() + 
					":" + getChart().getXYPlot().getRangeAxis().getRange().getUpperBound()*1.5 + "]");
			
			out.println("set title " + "\"" + getChart().getTitle().getText() + "\"");
			
			out.println("set xlabel " + "\"" + getChart().getXYPlot().getDomainAxis().getLabel() + "\"");
			
			out.println("set ylabel " + "\"" + getChart().getXYPlot().getRangeAxis().getLabel() + "\"");
			
			out.println();
			
			for(int i = 0 ; i < getAllSeriesKeys().size() ; i++){
				
				if(i==0){
					
					XYSeries series = keyToSeries.get(getAllSeriesKeys().get(i));
					out.print("plot '-' using 1:2 with lines title " + "\"" + series.getKey()  + "\"");
				}
				else{
					
					XYSeries series = keyToSeries.get(getAllSeriesKeys().get(i));
					out.print(",\"\" using 1:2 with lines title " + "\"" + series.getKey() + "\"");
				}
				
			}
			
			out.println("\n");
			
			for(int i = 0 ; i < getAllSeriesKeys().size() ; i++){
				
				XYSeries series = keyToSeries.get(getAllSeriesKeys().get(i));
				
				out.println("#X   #Y");
				
				for(int j = 0 ; j < series.getItemCount() ; j++){
					
					out.println(series.getX(j) + "	" + series.getY(j));
					
				}
				
				out.println("end series");
				
			}
			
		}
		
		out.println("pause -1");
		out.flush();
		out.close();
		
	}
	
	

}
