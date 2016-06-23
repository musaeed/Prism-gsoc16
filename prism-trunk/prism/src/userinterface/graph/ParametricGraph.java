package userinterface.graph;

import java.util.HashMap;

import javax.swing.JOptionPane;

import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;

import param.BigRational;
import param.Function;
import settings.DoubleSetting;
import settings.IntegerSetting;
import settings.Setting;

public class ParametricGraph extends Graph{

	private static final long serialVersionUID = 1L;

	private HashMap<SeriesKey, Function> functionCache;
	private int resolution;
	private double lowerBound;
	private double upperBound;
	
	/**Extra settings for the parametric graphs*/
	
	private IntegerSetting resolutionSetting;
	private DoubleSetting lowerBoundSetting;
	private DoubleSetting upperBoundSetting;

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
		
		resolutionSetting = new IntegerSetting("Resolution", resolution, 
				"Change the sampling resolution of the generated parameteric function", this, false);
		
		lowerBoundSetting = new DoubleSetting("Lower range bound", lowerBound, "Change the lower range bound of the paramteric graph",
				this, false);
		
		upperBoundSetting = new DoubleSetting("Upper range bound", upperBound, "Change the upper range bound of the parametric graph",
				this, false);
	}

	public HashMap<SeriesKey, Function> getFunctionCache() {
		return functionCache;
	}

	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
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

	public void hideShapes(){

		int seriesCount = super.getChart().getXYPlot().getDataset().getSeriesCount();

		for(int i = 0 ; i < seriesCount ; i++){
			super.getErrorRenderer().setSeriesShapesVisible(i, false);
		}
		
	}

	public void plotSeries(SeriesKey key){

		Function func = functionCache.get(key);

		for (int i = 0; i < resolution; i++) {

			double val = (double) i / (double) resolution;

			if(val < lowerBound || val > upperBound){
				continue;
			}

			BigRational br = func.evaluate(new param.Point(new BigRational[] {new BigRational(i, resolution)}));
			PrismXYDataItem di = new PrismXYDataItem(((double)i)/resolution, br.doubleValue());
			super.addPointToSeries(key, di);
		}

		hideShapes();

	}
	
	public void rePlot(){
		
		for(SeriesKey key : getAllSeriesKeys()){
			XYSeries series = super.keyToSeries.get(key);
			series.clear();
			plotSeries(key);
		}
		
	}
	
	public void domainChanged(){
		
		super.getChart().getXYPlot().getDomainAxis().setRange(new Range(lowerBound, upperBound));
		super.getChart().fireChartChanged();
	}

	@Override
	public int getNumSettings() {
		return 8;
	}
	
	@Override
	public Setting getSetting(int index) {
		
		if(index < 5){
			
			return super.getSetting(index);
			
		}
		else{
			
			switch (index) {
			
			case 5:
				return resolutionSetting;
			case 6:
				return lowerBoundSetting;
			case 7:
				return upperBoundSetting;
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
		
		if(lowerBoundSetting.getDoubleValue() != lowerBound){
			
			if(lowerBoundSetting.getDoubleValue() < 0.0 || lowerBoundSetting.getDoubleValue() > upperBoundSetting.getDoubleValue()
					|| lowerBoundSetting.getDoubleValue() > 1.0){
				
				JOptionPane.showMessageDialog(this, "The lower bound should be postive and less than upper bound!", "Error", JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			setLowerBound(lowerBoundSetting.getDoubleValue());
			domainChanged();
		}
		
		if(upperBoundSetting.getDoubleValue() != upperBound){
			
			if(upperBoundSetting.getDoubleValue() > 1.0 || upperBoundSetting.getDoubleValue() < lowerBoundSetting.getDoubleValue()
					|| upperBoundSetting.getDoubleValue() < 0.0){
				
				JOptionPane.showMessageDialog(this, "The upper bound should be less than unity and greater than the lower bound!", "Error", JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			setUpperBound(upperBoundSetting.getDoubleValue());
			domainChanged();
		}
	}

}
