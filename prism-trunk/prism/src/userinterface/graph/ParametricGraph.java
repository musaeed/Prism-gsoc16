//==============================================================================
//	
//	Copyright (c) 2016
//	Authors:
//	* Muhammad Omer Saeed <muhammad.omar555@gmail.com> (University of Bonn)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

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

/**
 * The class extends the Graph class and provides the capability to plot a parametric function
 * @author Muhammad Omer Saeed
 */
public class ParametricGraph extends Graph{

	private static final long serialVersionUID = 1L;

	private HashMap<SeriesKey, Function> functionCache;
	private int resolution;
	private double lowerBound;
	private double upperBound;
	
	/**Extra settings for the parametric graphs*/
	
	private IntegerSetting resolutionSetting;

	/**
	 * Creates a new parametric graph with the specified title
	 * @param title
	 */
	public ParametricGraph(String title){
		super(title);

		functionCache = new HashMap<SeriesKey, Function>();
		resolution = 100;
		lowerBound = 0.0;
		upperBound = 1.0;
		
		initSettings();
		updateGraph();
	}
	
	/**
	 * Initialize all the settings that will be shown in the graph options panel
	 */
	public void initSettings(){
		
		resolutionSetting = new IntegerSetting("Sampling rate (Parametric)", resolution, 
				"Change the sampling resolution of the generated parameteric function", this, false);
		
	}

	/**
	 * Returns the functions as a hash map with the series keys they are associated to
	 * @return {@code HashMap<SeriesKey, Function>} function cache
	 */
	public HashMap<SeriesKey, Function> getFunctionCache() {
		return functionCache;
	}

	/**
	 * Returns the current resolution or the sampling rate of the functions 
	 * @return
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * Sets the resolution or the sampling rate of the current functions in this graph
	 * @param resolution
	 */
	public void setResolution(int resolution) {
		this.resolution = resolution;
		
		try {
			resolutionSetting.setValue(resolution);
		} catch (SettingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the lower bound of the current graph
	 * @return
	 */
	public double getLowerBound() {
		return lowerBound;
	}
	
	/**
	 * Sets the lower bound of the current graph
	 * @param lowerBound
	 */
	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * Returns the upper bound of the current graph
	 * @return
	 */
	public double getUpperBound() {
		return upperBound;
	}

	/**
	 * Sets the upper bound of the current graph
	 * @param upperBound
	 */
	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * Add a new series to the current graph
	 * 
	 * @param seriesName the name of the series
	 * @param func the function that has to be sampled to plot the graph
	 * @return The series key for this series
	 */
	public SeriesKey addSeries(String seriesName, Function func){

		SeriesKey key = super.addSeries(seriesName);
		functionCache.put(key, func);

		return key;
	}
	
	/**
	 * Add a new function to the function cache {@code HashMap<SeriesKey, Function>}
	 * @param key the series key for the function
	 * @param func the function
	 */
	public void addFunction(SeriesKey key , Function func){
		functionCache.put(key, func);
	}

	/**
	 * Hides all the points and just shows the lines for all the series which has been plotted using a function 
	 * and not with the normal experiments in prism
	 */
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
	
	/**
	 * Hide the points and just show the plain line for the series specified by the series key
	 * @param key The key of the series whose shapes should be hidden
	 */
	public void hideShape(SeriesKey key){
		
		synchronized (getSeriesLock()) {
			
			Vector<SeriesKey> keys = getAllSeriesKeys();	
			
			for(int i = 0 ; i < keys.size() ; i++){
				
				if(key == keys.get(i)){

					getErrorRenderer().setSeriesShapesVisible(i, false);
					
				}
				
			}
			
		}
		
	}

	/**
	 * Plots a series specified by the key by sampling the corresponding function from the function cache
	 * @param key the key of the function that has to be sampled
	 */
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
	
	/**
	 * Re-plots all the series that has a key-function pair available in the current function cache 
	 */
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
	
	/**
	 * Updates the graph if the resolution setting is changed. All the parametric series are re-plotted with the new resolution
	 */
	private void updateGraph(){
		
		if(resolutionSetting.getIntegerValue() != resolution){
			setResolution(resolutionSetting.getIntegerValue());
			rePlot();
		}
	}

	/**
	 * Exports the current parametric plot to a gnu format file. The exported file can be opened via GNU plot
	 * @param file the file to which the data has to be written
	 * @throws IOException
	 */
	public void exportToGnuplotParametric(File file) throws IOException {
		
		PrintWriter out = new PrintWriter(new FileWriter(file));
		
		// give some info to the user
		out.println("#=========================================");
		out.println("# Generated by PRISM Chart Package");
		out.println("#=========================================");
		out.println("# usage: gnuplot <filename>");
		out.println("# Written by Muhammad Omer Saeed <muhammad.omar555@gmail.com>");
		
		out.println();
		
		// set some properties
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
			
			// add the data to the file
			
			for(int i = 0 ; i < getAllSeriesKeys().size() ; i++){
				
				XYSeries series = keyToSeries.get(getAllSeriesKeys().get(i));
				
				out.println("#X   #Y");
				
				for(int j = 0 ; j < series.getItemCount() ; j++){
					
					out.println(series.getX(j) + "	" + series.getY(j));
					
				}
				
				out.println("end series");
				
			}
			
		}
		
		// finishing up
		out.println("pause -1");
		out.flush();
		out.close();
		
	}
	
	

}
