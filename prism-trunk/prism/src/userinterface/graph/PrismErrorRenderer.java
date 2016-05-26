package userinterface.graph;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.jfree.chart.axis.ValueAxis;
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

/**
 * This class is a renderer which plots error bars on the graph. 
 * 
 * @author Muhammad Omer Saeed
 *
 */
public class PrismErrorRenderer extends XYLineAndShapeRenderer{


	private static final long serialVersionUID = 1L;

	private boolean drawError;
	private double capLength;
	private transient Paint errorPaint;
	private transient Stroke errorStroke;


	public PrismErrorRenderer(){
		super(false, true);

		this.capLength = 5.0;
	}


	public boolean getDrawError() {
		return drawError;
	}


	public void setDrawError(boolean drawError) {
		this.drawError = drawError;
	}


	public double getCapLength() {
		return capLength;
	}


	public void setCapLength(double capLength) {
		this.capLength = capLength;
	}


	public Paint getErrorPaint() {
		return errorPaint;
	}


	public void setErrorPaint(Paint errorPaint) {
		this.errorPaint = errorPaint;
	}
	
	public Color getErrorColor(){
		return (Color)errorPaint;
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



		if (pass == 0 && dataset instanceof XYSeriesCollection && getItemVisible(series, item)) {

			XYSeriesCollection collection = (XYSeriesCollection) dataset;

			PlotOrientation orientation = plot.getOrientation();
			if (this.drawError) {
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
				if (this.errorPaint != null) {
					g2.setPaint(this.errorPaint);
				}
				else {
					g2.setPaint(getItemPaint(series, item));
				}
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
		if (!PaintUtilities.equal(this.errorPaint, that.errorPaint)) {
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
		this.errorPaint = SerialUtilities.readPaint(stream);
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
		SerialUtilities.writePaint(this.errorPaint, stream);
		SerialUtilities.writeStroke(this.errorStroke, stream);
	}


}
