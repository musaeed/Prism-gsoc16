//==============================================================================
//	
//	Copyright (c) 2016
//	Authors:
//	* Muhammad Omer Saeed <muhammad.omar555@gmail.com> / <saeedm@informatik.uni-bonn.de> (University of Bonn)
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


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.JOptionPane;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

import userinterface.GUIPrism;

/**
 * Error renderer for our 2D charts, This class supports Error bars and Deviations as of yet.
 * @author Muhammad Omer Saeed
 *
 */
public class PrismErrorRenderer extends XYLineAndShapeRenderer{


	private static final long serialVersionUID = 1L;

	/**Type of error representations supported by this class*/
	public static final int ERRORBARS = 0;
	public static final int ERRORDEVIATION = 1;
	/**....end....*/

	/**Should the error be rendered?*/
	private boolean drawError;

	/** The cap length of the error bar*/
	private double capLength;

	/** The alpha transparency for the interval shading. */
	private double alpha;

	/** Current error rendering method*/
	private int currentMethod;

	/**The current paint (color information) of the error bars*/
	private transient Stroke errorStroke;

	/**
	 * The constructor for the renderer. Initialize the fields.
	 * @author Muhammad Omer Saeed
	 */
	public PrismErrorRenderer(){
		super(false, true);
		this.capLength = 5.0;
		this.alpha = 0.5f;
		this.currentMethod = PrismErrorRenderer.ERRORBARS;
	}


	/**
	 * Returns the alpha transparency for the background shading.
	 *
	 * @return The alpha transparency.
	 *
	 * @see #setAlpha(float)
	 */
	public double getAlpha() {
		return this.alpha;
	}


	/**
	 * Sets the alpha transparency for the background shading, and sends a
	 * RendererChangeEvent to all registered listeners.
	 *
	 * @param alpha   the alpha (in the range 0.0f to 1.0f).
	 *
	 * @see #getAlpha()
	 */
	public void setAlpha(double alpha) {
		
		if (alpha < 0.0 || alpha > 1.0) {	
			JOptionPane.showMessageDialog(GUIPrism.getGUI(), "Requires 'alpha' in the range 0.0 to 1.0.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		this.alpha = alpha;
		fireChangeEvent();
	}

	/**
	 * This method is overridden so that this flag cannot be changed---it is
	 * set to <code>true for this renderer.
	 *
	 * @param flag  ignored.
	 */
	public void setDrawSeriesLineAsPath(boolean flag) {
		// ignore
	}
	
	/**
	 * Set the current method of rendering errors in the plot
	 * @param method the method to be used to render errors in the plot
	 */
	
	public void setCurrentMethod(int method){
		
		this.currentMethod = method;
		
		if(currentMethod == PrismErrorRenderer.ERRORDEVIATION){
			
			super.setDrawSeriesLineAsPath(true);	
		}
		else{
			super.setDrawSeriesLineAsPath(false);
		}
	}
	
	/**
	 * Get the current error method used to render errors on the plot
	 * @return currentMethod
	 */
	public int getCurrentMethod(){
		return currentMethod;
	}

	/**
	 * Getter method for drawError
	 * 
	 * @return drawError tells whether the error should be rendered or not
	 */

	public boolean getDrawError() {
		return drawError;
	}
	
	/**
	 * Setter method for drawError. Tells whethet the error should be rendered or not
	 * @param drawError The value to be set
	 */

	public void setDrawError(boolean drawError) {
		this.drawError = drawError;
	}

	/**
	 * Get the length of the cap of the error bars
	 * @return capLength the current length of the error bars' caps
	 */
	public double getCapLength() {
		return capLength;
	}

	/**
	 * Set the cap length of the error bars
	 * @param capLength the value to be set
	 */
	public void setCapLength(double capLength) {
		this.capLength = capLength;
	}

	/**
	 * This method is needed for displaying the graph in the correct range (not too zoomed out or in)
	 * @param dataset the dataset which needs to be plotted
	 * @author Muhammad Omer Saeed
	 */

	public Range findRangeBounds(XYDataset dataset) {

		if (dataset != null) {

			XYSeriesCollection collection = (XYSeriesCollection) dataset;
			List<XYSeries> series = collection.getSeries();
			double max = Double.MIN_VALUE, min = Double.MAX_VALUE;

			for(XYSeries s : series){
				for(int i = 0 ; i < s.getItemCount() ; i++){
					PrismXYDataItem item = (PrismXYDataItem)s.getDataItem(i);

					if((item.getYValue() - item.getError()) < min){
						min = (item.getYValue() - item.getError());
					}

					if((item.getYValue() + item.getError()) > max){
						max = (item.getYValue() + item.getError());
					}
				}
			}

			if(max == Double.MIN_VALUE && min == Double.MAX_VALUE){
				return null;
			}
			else
				return new Range(min, max);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns the number of passes (through the dataset) used by this
	 * renderer.
	 *
	 * @return <code>3.
	 */
	public int getPassCount() {
		return 3;
	}


	/**
	 * Initialises and returns a state object that can be passed to each
	 * invocation of the {@link #drawItem} method.
	 *
	 * @param g2  the graphics target.
	 * @param dataArea  the data area.
	 * @param plot  the plot.
	 * @param dataset  the dataset.
	 * @param info  the plot rendering info.
	 *
	 * @return A newly initialised state object.
	 */
	public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea,
			XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {
		State state = new State(info);
		state.seriesPath = new GeneralPath();
		state.setProcessVisibleItemsOnly(false);
		return state;
	}

	/**
	 * Returns <code>true if this is the pass where the shapes are
	 * drawn.
	 *
	 * @param pass  the pass index.
	 *
	 * @return A boolean.
	 *
	 * @see #isLinePass(int)
	 */
	protected boolean isItemPass(int pass) {
		return (pass == 2);
	}

	/**
	 * Returns <code>true if this is the pass where the lines are
	 * drawn.
	 *
	 * @param pass  the pass index.
	 *
	 * @return A boolean.
	 *
	 * @see #isItemPass(int)
	 */
	protected boolean isLinePass(int pass) {
		return (pass == 1);
	}


	/**
	 * Draws the visual representation for one data item.
	 *
	 * @param g2  the graphics output target.
	 * @param state  the renderer state.
	 * @param dataArea  the data area.
	 * @param info  the plot rendering info.
	 * @param plot  the plot.
	 * @param domainAxis  the domain axis.
	 * @param rangeAxis  the range axis.
	 * @param dataset  the dataset.
	 * @param series  the series index.
	 * @param item  the item index.
	 * @param crosshairState  the crosshair state.
	 * @param pass  the pass index
	 * @author Muhammad Omer Saeed.
	 */

	@Override
	public void drawItem(Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
			int series, int item, CrosshairState crosshairState, int pass) {


		if(!drawError){
			super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis,
					dataset, series, item, crosshairState, pass);
			return;
		}

		switch(currentMethod){

		case PrismErrorRenderer.ERRORBARS:

			if (pass == 0 && dataset instanceof XYSeriesCollection && getItemVisible(series, item)) {

				synchronized (dataset) {
				
					XYSeriesCollection collection = (XYSeriesCollection) dataset;

					PlotOrientation orientation = plot.getOrientation();
					// draw the error bar for the y-interval
					XYSeries s = collection.getSeries(series);
					PrismXYDataItem it = (PrismXYDataItem)s.getDataItem(item);


					double y0 = it.getYValue() + it.getError();
					double y1 = it.getYValue() - it.getError();
					double x = collection.getXValue(series, item);


					RectangleEdge edge = plot.getRangeAxisEdge();
					double yy0 = rangeAxis.valueToJava2D(y0, dataArea, edge);
					double yy1 = rangeAxis.valueToJava2D(y1, dataArea, edge);
					double xx = domainAxis.valueToJava2D(x, dataArea,
							plot.getDomainAxisEdge());
					Line2D line;
					Line2D cap1;
					Line2D cap2;
					double adj = this.capLength / 2.0;
					if (orientation == PlotOrientation.VERTICAL) {
						line = new Line2D.Double(xx, yy0, xx, yy1);
						cap1 = new Line2D.Double(xx - adj, yy0, xx + adj, yy0);
						cap2 = new Line2D.Double(xx - adj, yy1, xx + adj, yy1);
					}
					else {  // PlotOrientation.HORIZONTAL
						line = new Line2D.Double(yy0, xx, yy1, xx);
						cap1 = new Line2D.Double(yy0, xx - adj, yy0, xx + adj);
						cap2 = new Line2D.Double(yy1, xx - adj, yy1, xx + adj);
					}

						g2.setPaint(getItemPaint(series, item));
					
					if (this.errorStroke != null) {
						g2.setStroke(this.errorStroke);
					}
					else {
						g2.setStroke(getItemStroke(series, item));
					}
					g2.draw(line);
					g2.draw(cap1);
					g2.draw(cap2);
				}

			}
			
			super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis,
					dataset, series, item, crosshairState, pass);

			break;

		case PrismErrorRenderer.ERRORDEVIATION:
			
			synchronized (dataset) {
				// do nothing if item is not visible
				if (!getItemVisible(series, item)) {
					return;
				}

				// first pass draws the shading
				if (pass == 0) {

					XYSeriesCollection collection = (XYSeriesCollection) dataset;
					XYSeries s = collection.getSeries(series);
					PrismXYDataItem it = (PrismXYDataItem)s.getDataItem(item);
					
					State drState = (State) state;

					double x = collection.getXValue(series, item);
					double yLow = it.getYValue() - it.getError();
					double yHigh  = it.getYValue() + it.getError();

					RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
					RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

					double xx = domainAxis.valueToJava2D(x, dataArea, xAxisLocation);
					double yyLow = rangeAxis.valueToJava2D(yLow, dataArea,
							yAxisLocation);
					double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea,
							yAxisLocation);

					PlotOrientation orientation = plot.getOrientation();
					if (orientation == PlotOrientation.HORIZONTAL) {
						drState.lowerCoordinates.add(new double[] {yyLow, xx});
						drState.upperCoordinates.add(new double[] {yyHigh, xx});
					}
					else if (orientation == PlotOrientation.VERTICAL) {
						drState.lowerCoordinates.add(new double[] {xx, yyLow});
						drState.upperCoordinates.add(new double[] {xx, yyHigh});
					}

					if (item == (dataset.getItemCount(series) - 1)) {
						// last item in series, draw the lot...
						// set up the alpha-transparency...
						Composite originalComposite = g2.getComposite();
						g2.setComposite(AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, (float)this.alpha));
						g2.setPaint(getItemPaint(series, item));
						GeneralPath area = new GeneralPath();
						double[] coords = (double[]) drState.lowerCoordinates.get(0);
						area.moveTo((float) coords[0], (float) coords[1]);
						for (int i = 1; i < drState.lowerCoordinates.size(); i++) {
							coords = (double[]) drState.lowerCoordinates.get(i);
							area.lineTo((float) coords[0], (float) coords[1]);
						}
						int count = drState.upperCoordinates.size();
						coords = (double[]) drState.upperCoordinates.get(count - 1);
						area.lineTo((float) coords[0], (float) coords[1]);
						for (int i = count - 2; i >= 0; i--) {
							coords = (double[]) drState.upperCoordinates.get(i);
							area.lineTo((float) coords[0], (float) coords[1]);
						}
						area.closePath();
						g2.fill(area);
						g2.setComposite(originalComposite);

						drState.lowerCoordinates.clear();
						drState.upperCoordinates.clear();
					}
				}
				if (isLinePass(pass)) {

					// the following code handles the line for the y-values...it's
					// all done by code in the super class
					if (item == 0) {
						State s = (State) state;
						s.seriesPath.reset();
						s.setLastPointGood(false);
					}

					if (getItemLineVisible(series, item)) {
						drawPrimaryLineAsPath(state, g2, plot, dataset, pass,
								series, item, domainAxis, rangeAxis, dataArea);
					}
				}

				// second pass adds shapes where the items are ..
				else if (isItemPass(pass)) {

					// setup for collecting optional entity info...
					EntityCollection entities = null;
					if (info != null) {
						entities = info.getOwner().getEntityCollection();
					}

					drawSecondaryPass(g2, plot, dataset, pass, series, item,
							domainAxis, dataArea, rangeAxis, crosshairState, entities);
				}
				
			}

			
			break;
		default:
			return;
		}
	}



	/**
	 * Tests this instance for equality with an arbitrary object.
	 *
	 * @param obj  the object (<code>null</code> permitted).
	 *
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof PrismErrorRenderer)) {
			return false;
		}
		PrismErrorRenderer that = (PrismErrorRenderer) obj;
		if (this.drawError != that.drawError) {
			return false;
		}
		if (this.drawError != that.drawError) {
			return false;
		}
		if (this.capLength != that.capLength) {
			return false;
		}
		if (!ObjectUtilities.equal(this.errorStroke, that.errorStroke)) {
			return false;
		}
		return super.equals(obj);
	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream  the input stream.
	 *
	 * @throws IOException  if there is an I/O error.
	 * @throws ClassNotFoundException  if there is a classpath problem.
	 */
	private void readObject(ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		this.errorStroke = SerialUtilities.readStroke(stream);
	}

	/**
	 * Provides serialization support.
	 *
	 * @param stream  the output stream.
	 *
	 * @throws IOException  if there is an I/O error.
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		SerialUtilities.writeStroke(this.errorStroke, stream);
	}

	/**
	 * A state object that is passed to each call to <code>drawItem.
	 */
	public static class State extends XYLineAndShapeRenderer.State {

		/**
		 * A list of coordinates for the upper y-values in the current series
		 * (after translation into Java2D space).
		 */
		public List upperCoordinates;

		/**
		 * A list of coordinates for the lower y-values in the current series
		 * (after translation into Java2D space).
		 */
		public List lowerCoordinates;

		/**
		 * Creates a new state instance.
		 *
		 * @param info  the plot rendering info.
		 */
		public State(PlotRenderingInfo info) {
			super(info);
			this.lowerCoordinates = new java.util.ArrayList();
			this.upperCoordinates = new java.util.ArrayList();
		}

	}


}
