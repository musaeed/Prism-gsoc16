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

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.Chart3DPanel;
import com.orsoncharts.Range;
import com.orsoncharts.data.function.Function3D;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.RainbowScale;
import com.orsoncharts.renderer.xyz.SurfaceRenderer;

import param.BigRational;
import param.Function;
import settings.IntegerSetting;
import settings.Setting;
import settings.SettingException;

public class ParametricGraph3D extends Graph3D {
	
	private static final long serialVersionUID = 1L;

	private ParamFunction function;
	private double lowerBoundX, lowerBoundY;
	private double upperBoundX, upperBoundY;
	
	private IntegerSetting xResolution;
	private IntegerSetting yResolution;
	
	/**
	 * 
	 * @param func
	 */
	public ParametricGraph3D(Function func){
		this();
		function = new ParamFunction(func);
	}
	
	/**
	 * 
	 */
	public ParametricGraph3D(){
		initSettings();
		lowerBoundX = lowerBoundY = 0.0;
		upperBoundX = upperBoundY = 1.0;
		
		xResolution = new IntegerSetting("Sampling rate X", 25, "change the sampling rate of the x axis", this, false);
		yResolution = new IntegerSetting("Sampling rate Y", 25, "change the sampling rate of the y axis", this, false);
	}
	
	/**
	 * 
	 * @param func
	 * @param lowerBoundX
	 * @param upperBoundX
	 * @param lowerBoundY
	 * @param upperBoundY
	 */
	public ParametricGraph3D(Function func, double lowerBoundX, double upperBoundX, 
			double lowerBoundY, double upperBoundY){
		
		function = new ParamFunction(func);
		this.lowerBoundX = lowerBoundX;
		this.upperBoundX = upperBoundX;
		this.lowerBoundY = lowerBoundY;
		this.upperBoundY = upperBoundX;
	}
	
	/**
	 * 
	 * @param lowerX
	 * @param upperX
	 * @param lowerY
	 * @param upperY
	 */
	public void setBounds(double lowerX, double upperX, double lowerY, double upperY){
		
		this.lowerBoundX = lowerX;
		this.upperBoundX = upperX;
		this.lowerBoundY = lowerY;
		this.upperBoundY = upperY;
	}
	
	/**
	 * 
	 * @param func
	 */
	public void setFunction(Function func){
		function = new ParamFunction(func);
	}

	/**
	 * 
	 * @param title
	 * @param xLabel
	 * @param yLabel
	 * @param zLabel
	 */
	public void plot(String title, String xLabel, String yLabel, String zLabel){
		
		this.xLabel = xLabel;
		this.yLabel = zLabel;
		
		chart = Chart3DFactory.createSurfaceChart("", title, function, xLabel, yLabel, zLabel);
		
		panel = new Chart3DPanel(chart);
		panel.addMouseListener(graphHandler);
		panel.setComponentPopupMenu(graphHandler.getGraphMenu());
		
		plot = (XYZPlot)chart.getPlot();
		plot.getXAxis().setRange(lowerBoundX, upperBoundX);
		plot.getZAxis().setRange(lowerBoundY, upperBoundY);
		
		renderer = (SurfaceRenderer) plot.getRenderer();
		renderer.setColorScale(new RainbowScale(new Range(0.0, 1.0)));
		
		setLayout(new BorderLayout());
		add(panel,  BorderLayout.CENTER);
		
		initSettings();
		
		/*Update all the settings*/
		
		try {
			this.graphTitle.setValue(title);
		} catch (SettingException e) {
			e.printStackTrace();
		}
		this.getDisplaySettings().updateDisplay();
		this.updateGraph();
		this.getxAxisSetting().updateAxis();
		this.getyAxisSetting().updateAxis();
		this.getzAxisSetting().updateAxis();
	}

	@Override
	public int getNumSettings() {
		return 10;
	}

	@Override
	public Setting getSetting(int index) {

		if(index < 8){
			return super.getSetting(index);
		}

		switch(index){
		
		case 8:
			return xResolution;
		case 9:
			return yResolution;
		default:
			return null;

		}
	}
	
	@Override
	public void updateGraph(){
		
		super.updateGraph();
	
		if(xResolution.getIntegerValue() != renderer.getXSamples()){
			renderer.setXSamples(xResolution.getIntegerValue());
		}
		
		if(yResolution.getIntegerValue() != renderer.getXSamples()){
			renderer.setZSamples(yResolution.getIntegerValue());			
		}
	}
	
	@Override
	public void exportToGnuplot(File file) throws IOException{
		
		PrintWriter out = new PrintWriter(new FileWriter(file));
		
		out.println("#=========================================");
		out.println("# Generated by PRISM Chart Package");
		out.println("#=========================================");
		out.println("# usage: gnuplot <filename>");
		out.println("# Written by Muhammad Omer Saeed <muhammad.omar555@gmail.com>");
		
		out.println();
		
		/*setting graph properties*/
		out.println("set xrange[" + lowerBoundX + ":" + upperBoundX + "]");
		out.println("set yrange[" + lowerBoundY + ":" + upperBoundY + "]");
		out.println("set title '" + this.graphTitle.getStringValue() + "'");
		out.println("set xlabel " + "\"" + this.xLabel + "\"");
		out.println("set ylabel " + "\"" + this.yLabel + "\"");
		out.println("set zlabel " + "\"probability\"" );
		out.println("set hidden3d");
		out.println("set dgrid3d 50,50 qnorm 2");
		out.println("set pm3d");
		out.println("set palette model HSV defined ( 0 0 1 1, 1 1 1 1 )");
		
		out.println("splot '-' with lines");
		out.println();
		
		out.println("#X		#Y		#Z");
		
		double rateX = (upperBoundX - lowerBoundX) / renderer.getXSamples();
		double rateY = (upperBoundY - lowerBoundY) / renderer.getZSamples();
		Function func = function.getFunction();
		
		for(double x = lowerBoundX ; x <= upperBoundX ; x+= rateX){
			for(double y = lowerBoundY ; y <= upperBoundY ; y += rateY){
				
				BigRational xVal = new BigRational((int) (x*1000000.0), 1000000);
				BigRational yVal = new BigRational((int) (y*1000000.0), 1000000);
				
				BigRational z = func.evaluate(new param.Point(new BigRational[] {xVal, yVal}));
				
				out.println(x + " 		" + y  + "	 	" + z.doubleValue());
			}
		}
		
		/*finishing up*/
		out.println("end plot 3d");
		out.println();
		
		out.println("pause -1");
		
		out.flush();
		out.close();
	}

	/**
	 * 
	 * @author Muhammad Omer Saeed
	 *
	 */
	private class ParamFunction implements Function3D{
		
		private static final long serialVersionUID = 1L;
		private Function func;
		
		public ParamFunction(Function func) {
			this.func = func;
		}
		
		public Function getFunction(){
			return this.func;
		}

		@Override
		public double getValue(double x, double y) {
			
			BigRational xVal = new BigRational((int) (x*1000000.0), 1000000);
			BigRational yVal = new BigRational((int) (y*1000000.0), 1000000);
			
			BigRational br = func.evaluate(new param.Point(new BigRational[] {xVal, yVal}));
			return br.doubleValue();
		}
		
	}
}