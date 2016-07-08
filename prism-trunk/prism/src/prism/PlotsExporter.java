//==============================================================================
//	
//	Copyright (c) 2016
//	Authors:
//	Muhammad Omer Saeed <muhammad.omar555@gmail.com> University of Bonn
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

package prism;

import java.io.File;
import java.io.IOException;

import param.BigRational;
import param.RegionValues;
import parser.Values;
import userinterface.graph.Graph;
import userinterface.graph.GraphException;
import userinterface.graph.ParametricGraph;
import userinterface.graph.PrismXYDataItem;
import userinterface.graph.PrismXYSeries;
import userinterface.graph.SeriesKey;

public class PlotsExporter {

	// even the simple graphs are parametric type so thats why here is parametricgraph used instead of just graph
	private ParametricGraph graph;
	//the format the graph has to be exported in
	private PlotsExportFormat format;
	//the file where data has to be written
	private File file;
	//is the graph parametric or simple?
	private boolean isParametric;
	
	/**
	 * The supported formats for exporting the plots from the command line
	 * @author Muhammad Omer Saeed
	 */
	
	public enum PlotsExportFormat{
		JPG,PNG,MATLAB,GNUPLOT,EPS,GRA;

		public String fullname(){

			switch(this){

			case JPG:
				return "JPEG";
			case PNG:
				return "PNG";
			case MATLAB:
				return "MATLAB";
			case GNUPLOT:
				return "GNUPLOT";
			case EPS:
				return "EPS";
			case GRA:
				return "Prism Graph file";
			default:
				return this.toString();
			}
		}

		/**
		 * For parsing the format given as a string to PlotExportFormat enumeration
		 * @param formatName the format in string
		 * @return the enum corresponding to the format
		 */
		public static PlotsExportFormat parse(String formatName){

			switch(formatName){

			case "jpg":
				return PlotsExportFormat.JPG;
			case "png":
				return PlotsExportFormat.PNG;
			case "m":
				return PlotsExportFormat.MATLAB;
			case "gnuplot":
			case "gplot":
			case "gpi":
			case "plt":
			case "gp":	
				return PlotsExportFormat.GNUPLOT;
			case "gra":
				return PlotsExportFormat.GRA;
			default:
				return PlotsExportFormat.JPG;

			}
		}
	}
	
	/**
	 * Constructor for the plot exporter
	 * @param format format of the plot to be exported. Can be null in which case it will be deduced
	 * @param filename
	 */
	public PlotsExporter(String format, String filename)
	{
		setFormatByName(format);
		file = new File(filename);
		graph = new ParametricGraph("");
		isParametric = false;
	}
	
	/**
	 * Set the format by giving a string format
	 * @param formatName
	 */
	public void setFormatByName(String formatName)
	{
		if(formatName == null){
			return;
		}
		setFormat(PlotsExportFormat.parse(formatName));
	}
	
	public void setFormat(PlotsExportFormat format){
		this.format = format;
	}
	
	/**
	 * add a new series to the current graph
	 * @return the serieskey
	 */
	public SeriesKey addSeries(){
		
		return graph.addSeries("New series");
	}

	/**
	 * nothing to do in the start as of now
	 */
	public void start(){
		//TODO
	}
	
	/**
	 * add the data to the graphs and set the corresponding properties depending if the graph is parametric or not
	 * @param values x data
	 * @param result y data and possibly error data too
	 * @param key the key to which series the data has to be added
	 */
	public void exportResult(final Values values, final Object result, SeriesKey key){
		
		// parametric case
		if(result instanceof RegionValues){
			
			isParametric = true;
			RegionValues vals = (RegionValues) result;
			param.Function f = vals.getResult(0).getInitStateValueAsFunction();
			PrismXYSeries series = (PrismXYSeries)graph.getXYSeries(key);
			
			graph.addFunction(key, f);
			
			for (int i = 0; i < 100; i++) {

				double val = (double) i / (double) 100;

				if(val < 0.0 || val > 1.0){
					continue;
				}

				BigRational br = f.evaluate(new param.Point(new BigRational[] {new BigRational(i, 100)}));
				PrismXYDataItem di = new PrismXYDataItem(((double)i)/100, br.doubleValue());
				series.addOrUpdate(di);
			}
			
			graph.hideShapes();
			
			return;
		}
		
		//normal case
		double xVal, yVal=0.0, error = 0.0;
		
		try {
			
			xVal = values.getDoubleValue(0);
			
		} catch (PrismLangException e) {
			e.printStackTrace();
			return;
		}
		
		if(result instanceof Double){
			
			yVal = (double) result;
		}
		else if(result instanceof Pair<?, ?>){
			
			Pair<Double, Double> pair = (Pair<Double, Double>) result;
			yVal = pair.first;
			error = pair.second;
		}
		else{
			System.out.println(result.getClass());
		}
		
		
		
		PrismXYSeries series = (PrismXYSeries)graph.getXYSeries(key);
		PrismXYDataItem item = new PrismXYDataItem(xVal, yVal, error);
		series.addOrUpdate(item);

	}
	
	/**
	 * Deduce the format of the export plot if not given by user. Default: jpg
	 */
	public void deduceFormat(){
		
		String filename = file.getName();
		String ext = filename.substring(filename.indexOf('.')+1);
		
		setFormat(PlotsExportFormat.parse(ext));
		
		
	}
	
	/**
	 * After all the data has been added to the plots, write the data to the specified file
	 */
	public void end(){
		
		// if not format provided, deduce!
		if(format == null){
			deduceFormat();
		}
		
		switch(format){
		
		case JPG:
			try {
				
				Graph.exportToJPEG(file, graph.getChart(), 800, 500);
			
			} catch (GraphException | IOException e) {
				e.printStackTrace();
			}
			break;
			
		case PNG:
			
			try {
				
				Graph.exportToPNG(file, graph.getChart(), 800, 500, false);
			
			} catch (GraphException | IOException e) {
				e.printStackTrace();
			}
			break;
			
		case GRA:
			
			try {
				graph.save(file);
			} catch (PrismException e) {
				e.printStackTrace();
			}
			break;
			
		case EPS:
			
			try {
				Graph.exportToEPS(file, 800, 500, graph.getChart());
			} catch (GraphException | IOException e) {
				e.printStackTrace();
			}
			break;
			
		case GNUPLOT:
			
			try {
				if(isParametric)
					graph.exportToGnuplotParametric(file);
				else{
					
					graph.exportToGnuplot(file);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			break;
		
		case MATLAB:
			try {
				graph.exportToMatlab(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
			
		}
		
		
	}

}
