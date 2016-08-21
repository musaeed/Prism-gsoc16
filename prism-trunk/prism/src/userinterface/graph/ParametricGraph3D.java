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

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.Chart3DPanel;
import com.orsoncharts.Range;
import com.orsoncharts.data.function.Function3D;
import com.orsoncharts.graphics3d.swing.DisplayPanel3D;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.RainbowScale;
import com.orsoncharts.renderer.xyz.SurfaceRenderer;

import param.BigRational;
import param.Function;
import parser.Values;
import parser.ast.Expression;
import parser.ast.ExpressionConstant;
import parser.ast.ExpressionIdent;
import parser.type.TypeDouble;
import parser.visitor.ASTTraverseModify;
import prism.PrismLangException;
import settings.IntegerSetting;
import settings.Setting;
import settings.SettingException;
import userinterface.GUIPrism;

/**
 * This class is responsible for plotting 3d continuous function graphs. We always need to provide a function to the class
 * which can be evaluated at any point within the valid range
 * @author Muhammad Omer Saeed
 */
public class ParametricGraph3D extends Graph3D {
	
	private static final long serialVersionUID = 1L;

	private ParamFunction function;
	private double lowerBoundX, lowerBoundY;
	private double upperBoundX, upperBoundY;
	
	private IntegerSetting xResolution;
	private IntegerSetting yResolution;
	
	//needed when we plot using an expression rather a function
	private Values singleGlobalValues;
	private String xAxisConstant, yAxisConstant;
	
	/**
	 * Creates a new Parametric Graph using the Function func
	 * @param func the function that will be plotted
	 */
	public ParametricGraph3D(Function func){
		this();
		setFunction(func);
	}
	
	/**
	 * Creates a new Parametric Graph using the Expression that can be evaluated at any point in the valid range
	 * @param expr the Expression to use
	 */
	public ParametricGraph3D(Expression expr){
		this();
		setExpression(expr);
	}
	
	/**
	 * Creates a new Parametric graph
	 */
	public ParametricGraph3D(){
		initSettings();
		lowerBoundX = lowerBoundY = 0.0;
		upperBoundX = upperBoundY = 1.0;
		
		xResolution = new IntegerSetting("Sampling rate X", 25, "change the sampling rate of the x axis", this, false);
		yResolution = new IntegerSetting("Sampling rate Y", 25, "change the sampling rate of the y axis", this, false);
		plotType = SURFACE;
		
		try {
			this.chartType.setValue("Surface plot");
			
		} catch (SettingException e) {
			e.printStackTrace();
		}
		
		this.chartType.setEnabled(false);
		this.legendOrientation.setEnabled(true);
	}
	
	/**
	 * 
	 * Creates a new Parametric Graph with the following properties set
	 * 
	 * @param func	The function to be plotted
	 * @param lowerBoundX	The lower bound of x axis
	 * @param upperBoundX	The upper bound of the x axis
	 * @param lowerBoundY	The lower bound of the y axis
	 * @param upperBoundY	The upper bound of the y axis
	 */
	public ParametricGraph3D(Function func, double lowerBoundX, double upperBoundX, 
			double lowerBoundY, double upperBoundY){
		
		function = new ParamFunction(func);
		this.lowerBoundX = lowerBoundX;
		this.upperBoundX = upperBoundX;
		this.lowerBoundY = lowerBoundY;
		this.upperBoundY = upperBoundX;
		plotType = SURFACE;
		
		try {
			this.chartType.setValue("Surface plot");
			
		} catch (SettingException e) {
			e.printStackTrace();
		}
		
		this.chartType.setEnabled(false);
	}
	
	/**
	 * Sets the bounds or range of the x and y axis
	 * 
	 * @param lowerX The lower bound of x axis
	 * @param upperX The upper bound of x axis
	 * @param lowerY The lower bound of y axis
	 * @param upperY The upper bound of y axis
	 */
	public void setBounds(double lowerX, double upperX, double lowerY, double upperY){
		
		this.lowerBoundX = lowerX;
		this.upperBoundX = upperX;
		this.lowerBoundY = lowerY;
		this.upperBoundY = upperY;
	}
	
	/**
	 * Parses the arguments from string to doubles and sets the bounds or range of x and y axis
	 * 
	 * @param lowerX The lower bound of x axis
	 * @param upperX The upper bound of x axis
	 * @param lowerY The lower bound of y axis
	 * @param upperY The upper bound of y axis
	 */
	public void setBounds(String lowerX, String upperX, String lowerY, String upperY){
		
		this.lowerBoundX = Double.parseDouble(lowerX);
		this.upperBoundX = Double.parseDouble(upperX);
		this.lowerBoundY = Double.parseDouble(lowerY);
		this.upperBoundY = Double.parseDouble(upperY);
	}
	
	/**
	 * Sets the function that has to be plotted
	 * @param func 
	 */
	public void setFunction(Function func){
		function = new ParamFunction(func);
	}
	
	/**
	 * Sets the global values needed sometimes to evaluate an expression
	 * @param vals the global values
	 */
	public void setGlobalValues(Values vals){
		this.singleGlobalValues = vals;
	}
	
	/**
	 * Sets the axis constants to be used in plotting the 3d graph
	 * @param x
	 * @param y
	 */
	public void setAxisConstants(String x, String y){
		
		this.xAxisConstant = x;
		this.yAxisConstant = y;
	}
	
	/**
	 * Sets the expression that has to be plotted
	 * @param expr
	 */
	public void setExpression(Expression expr){
		
		function = new ParamFunction(expr);
	}
	
	/**
	 * Sets the sampling rate of the x and the y axes
	 * 
	 * @param xSamples The sampling rate of x axis
	 * @param ySamples The sampling rate of y axis
	 */
	public void setSamplingRates(int xSamples, int ySamples){
		
		rendererSurface.setXSamples(xSamples);
		rendererSurface.setZSamples(ySamples);
	}

	/**
	 * Plots the graph. Please note that the function should have already been initialized using either {@link param.Function}
	 * or an {@link Expression}
	 * 
	 * @param title The title of the plot
	 * @param xLabel The x label of the plot
	 * @param yLabel The y label of the plot
	 * @param zLabel The z label of the plot
	 */
	public void plot(String title, String xLabel, String yLabel, String zLabel){
		
		this.xLabel = xLabel;
		this.yLabel = zLabel;
		
		//check that the function is already initialized
		if(function == null){
			JOptionPane.showMessageDialog(GUIPrism.getGUI(), "Please provide a function before plotting!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		chart = Chart3DFactory.createSurfaceChart("", title, function, xLabel, yLabel, zLabel);
		
		panel = new Chart3DPanel(chart);
		panel.setComponentPopupMenu(graphHandler.getGraphMenu());
		
		graphHandler.getGraphMenu().addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				
				if(((JPopupMenu)e.getSource()).getInvoker() instanceof Chart3DPanel){
					
					graphHandler.doGraph3DEnables();
					
				}
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});

		dPanel = new DisplayPanel3D(panel, true, false);
		panel.addMouseListener(graphHandler);
		
		plot = (XYZPlot)chart.getPlot();
		plot.getXAxis().setRange(lowerBoundX, upperBoundX);
		plot.getZAxis().setRange(lowerBoundY, upperBoundY);
		
		rendererSurface = (SurfaceRenderer) plot.getRenderer();
		rendererSurface.setColorScale(new RainbowScale(new Range(0.0, 1.0)));
		setLayout(new BorderLayout());
		add(dPanel,  BorderLayout.CENTER);
		
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

	/**
	 * The number of settings we have for this graph type
	 */
	@Override
	public int getNumSettings() {
		return 11;
	}

	@Override
	public Setting getSetting(int index) {

		if(index < 9){
			return super.getSetting(index);
		}

		switch(index){
		
		case 9:
			return xResolution;
		case 10:
			return yResolution;
		default:
			return null;

		}
	}
	
	/**
	 * Updates all the settings. Should be called after the user makes any change in the {@link GraphOptions}
	 */
	@Override
	public void updateGraph(){
		
		super.updateGraph();
	
		if(xResolution.getIntegerValue() != rendererSurface.getXSamples()){
			rendererSurface.setXSamples(xResolution.getIntegerValue());
		}
		
		if(yResolution.getIntegerValue() != rendererSurface.getXSamples()){
			rendererSurface.setZSamples(yResolution.getIntegerValue());			
		}
	}
	
	/**
	 * Exports the plot as a GNU plot readable file
	 */
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
		
		double rateX = (upperBoundX - lowerBoundX) / rendererSurface.getXSamples();
		double rateY = (upperBoundY - lowerBoundY) / rendererSurface.getZSamples();
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
	 * Exports the plot as a MATLAB readable file
	 */
	@Override
	public void exportToMatlab(File file) throws IOException{

		PrintWriter out = new PrintWriter(new FileWriter(file));

		out.println("%=========================================");
		out.println("% Generated by PRISM Chart Package");
		out.println("%=========================================");
		out.println("% usage: run <filename>");
		out.println("% Written by Muhammad Omer Saeed <muhammad.omar555@gmail.com>");
		
		

		double rateX = (upperBoundX - lowerBoundX) / 100.0;
		double rateY = (upperBoundY - lowerBoundY) / 100.0;
		Function func = function.getFunction();

		/*writing data*/

		out.println("data = [");
		out.println("%X 	%Y		%Z");

		for(double x = lowerBoundX ; x <= upperBoundX ; x+= rateX){
			for(double y = lowerBoundY ; y <= upperBoundY ; y += rateY){

				BigRational xVal = new BigRational((int) (x*1000000.0), 1000000);
				BigRational yVal = new BigRational((int) (y*1000000.0), 1000000);

				BigRational z = func.evaluate(new param.Point(new BigRational[] {xVal, yVal}));
				
				out.println(x + "    " + y + "    " + z.doubleValue() + ";");

			}
		}
		
		out.println("];");
		
		out.println("X = reshape(data(:,1),  " + 100.0 + ", []);");
		out.println("Y = reshape(data(:,2),  " + 100.0 + ", []);");
		out.println("Z = reshape(data(:,3),  " + 100.0 + ", []);");
		
		/*setting properties*/
		
		out.println("figure;");
		out.println("hold on;");
		out.println("colormap(hsv);");
		out.println("surf(X, Y, Z);");
		out.println("title('" + graphTitle.getStringValue() + "');");
		out.println("xlabel(' "+ this.xLabel +" ');");
		out.println("ylabel(' "+ this.yLabel + "');");
		out.println("zlabel('Probability');");
		out.println("colorbar;");
		out.println("hold off;");
		
		/*finishing up*/
		out.flush();
		out.close();
	}

	
	/**
	 * Custom function that acts as an interface between the {@link Function} and {@link Function3D}.
	 * This function can be evaluated at any point within the given valid range
	 * @author Muhammad Omer Saeed
	 *
	 */
	private class ParamFunction implements Function3D{
		
		private static final long serialVersionUID = 1L;
		private Function func;
		private Expression expr;
		
		/**
		 * Creates a new {@link ParamFunction} using a {@link Function}
		 * @param func
		 */
		public ParamFunction(Function func) {
			this.func = func;
		}
		
		/**
		 * Creates a new {@linkp ParamFunction} using an {@link Expression}
		 * @param expr
		 */
		public ParamFunction(Expression expr){
			this.expr = expr;
		}
		
		/**
		 * Get the function if not {@code null}
		 * @return
		 */
		public Function getFunction(){
			return this.func;
		}
		
		/**
		 * Get the Expression if not {@code null}
		 * @return
		 */
		public Expression getExpression(){
			return this.expr;
		}

		/**
		 * Evaluates the function value at different x and y values depending upon our sampling rate of the two axes
		 * @param x the x value
		 * @param y the y value
		 */
		@Override
		public double getValue(double x, double y) {
			
			if(func != null){
				BigRational xVal = new BigRational((int) (x*1000000.0), 1000000);
				BigRational yVal = new BigRational((int) (y*1000000.0), 1000000);

				BigRational br = func.evaluate(new param.Point(new BigRational[] {xVal, yVal}));
				return br.doubleValue();
			}
			// use the expression instead to evaluate
			else{
				
				Values vals = new Values();
				vals.addValue(xAxisConstant, x);
				vals.addValue(yAxisConstant, y);
				
				for(int i = 0 ; i < singleGlobalValues.getNumValues() ; i++){
					try {
					
						vals.addValue(singleGlobalValues.getName(i), singleGlobalValues.getDoubleValue(i));
					
					} catch (PrismLangException e) {
						e.printStackTrace();
					}
				}
				
				double ans = -1.0;
				
				try {
					ans = (double)expr.evaluate(vals);
				} catch (PrismLangException e) {
					e.printStackTrace();
				}
				
				return ans;
			}
		}
		
	}
}