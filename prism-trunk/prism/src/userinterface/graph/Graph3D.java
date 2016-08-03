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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.Chart3DPanel;
import com.orsoncharts.Range;
import com.orsoncharts.data.function.Function3D;
import com.orsoncharts.data.xyz.XYZDataItem;
import com.orsoncharts.data.xyz.XYZSeries;
import com.orsoncharts.data.xyz.XYZSeriesCollection;
import com.orsoncharts.graphics3d.swing.DisplayPanel3D;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.GradientColorScale;
import com.orsoncharts.renderer.RainbowScale;
import com.orsoncharts.renderer.xyz.ScatterXYZRenderer;
import com.orsoncharts.renderer.xyz.SurfaceRenderer;
import com.orsoncharts.util.Orientation;

import prism.DefinedConstant;
import settings.ChoiceSetting;
import settings.ColorSetting;
import settings.DoubleSetting;
import settings.FontColorPair;
import settings.FontColorSetting;
import settings.MultipleLineStringSetting;
import settings.Setting;
import settings.SettingDisplay;
import settings.SettingException;
import settings.SettingOwner;
import userinterface.GUIPrism;
import userinterface.properties.GUIGraphHandler;

public class Graph3D extends JPanel  implements SettingOwner, EntityResolver, Observer, Printable{

	private static final long serialVersionUID = 1L;
	
	public static final int SCATTER = 1;
	public static final int SURFACE = 2;
	
	protected Chart3DPanel panel;
	protected DisplayPanel3D dPanel;
	protected Chart3D chart;
	protected XYZPlot plot;
	protected GUIGraphHandler graphHandler;
	protected String xLabel, yLabel, zLabel;
	
	
	/** Display for settings. Required to implement SettingsOwner */
	private SettingDisplay display;
	
	private AxisSettings3D xAxisSetting, yAxisSetting, zAxisSetting;
	
	private DisplaySettings3D displaySettings;
	
	/** Settings of this graph. */
	protected MultipleLineStringSetting graphTitle;
	private FontColorSetting titleFont;
	private ChoiceSetting legendOrientation;
	private DoubleSetting rotateIncrement;
	private DoubleSetting rollIncrement;
	private ChoiceSetting chartType;
	
	/**Settings specifically for scatter plot*/
	private DoubleSetting pointSize;
	private ColorSetting dataColor;
	
	/**Settings specifically for surface plot*/
	private ChoiceSetting scaleMethod;
	private ColorSetting lowColor;
	private ColorSetting highColor;
	
	/** For scatter plot */
	private ScatterXYZRenderer rendererScatter;
	private XYZSeries seriesScatter;
	private XYZSeriesCollection seriesCollectionScatter;
	
	/**For surface plot*/
	protected SurfaceRenderer rendererSurface;
	private DataInterpolateFunction function;
	private DefinedConstant rangingConstantX, rangingConstantY;
	
	
	private ArrayList<XYZDataItem> dataCache;
	
	protected int plotType;
	
	/**
	 * 
	 */
	public Graph3D(){
		initSettings();
	}
	
	/**
	 * 
	 */
	public void initSettings(){
		
		xAxisSetting = new AxisSettings3D("x axis settings", AxisSettings3D.XAXIS, this);
		yAxisSetting = new AxisSettings3D("y axis settings", AxisSettings3D.YAXIS, this);
		zAxisSetting = new AxisSettings3D("z axis settings", AxisSettings3D.ZAXIS, this);
		
		displaySettings = new DisplaySettings3D(this);
		displaySettings.addObserver(this);
		
		graphTitle = new MultipleLineStringSetting("title", "", 
				"the main title heading for the chart", this, false);
		
		titleFont = new FontColorSetting("title font", new FontColorPair(new Font(Font.SANS_SERIF, Font.PLAIN, 11), 
				Color.BLACK), "the font of the chart's title", this, false);
		
		legendOrientation = new ChoiceSetting("legend orientation", new String[]{"Horizontal","Vertical"}, "Horizontal" 
				, "change the orientation of the legend", this, false);
		
		rotateIncrement = new DoubleSetting("Rotate increment", 1.0, "rotate increment value for the plot", this, false);
		rollIncrement = new DoubleSetting("roll increment", 1.0, "roll increment for the plot", this, false);
		
		chartType = new ChoiceSetting("Plot type", new String[]{"Scatter plot","Surface plot"}, "Scatter plot" , "select the plot type to visualize the data", this, false);
		
		scaleMethod = new ChoiceSetting("scale method", new String[]{"Rainbow scale", "Gradient scale"}, "Rainbow scale", 
				"change the scale method for the chart", this, false);
		
		lowColor = new ColorSetting("low color", Color.WHITE, "low color for the gradient scale", this, false);
		lowColor.setEnabled(false);
		
		highColor = new ColorSetting("high color", Color.BLACK, "high color of the gradient scale", this, false);
		highColor.setEnabled(false);
		
		pointSize = new DoubleSetting("Size of data points", 0.2, "change the size of data points in 3d space", this, false);
		dataColor = new ColorSetting("data color", Color.RED, "the color of the data points in 3d space", this, false);
		
		dataCache = new ArrayList<XYZDataItem>();
	}
	
	/**
	 * 
	 */
	public void initScatterPlot(){
		
		seriesCollectionScatter = new XYZSeriesCollection();
		setLayout(new BorderLayout());
		plotType = SCATTER;
	}
	
	/**
	 * 
	 * @param item
	 */
	public void addPointToDataCache(XYZDataItem item){
		dataCache.add(item);
	}
	
	/**
	 * 
	 * @param seriesName
	 */
	public void plotScatter(String seriesName){
		
		seriesScatter = new XYZSeries(seriesName);
		
		for(XYZDataItem item : dataCache){
			seriesScatter.add(item);
		}
		
		seriesCollectionScatter.add(seriesScatter);
		chart = Chart3DFactory.createScatterChart("", "", seriesCollectionScatter, xLabel, zLabel, yLabel);
		plot = (XYZPlot)chart.getPlot();
		rendererScatter = (ScatterXYZRenderer)plot.getRenderer();
		panel = new Chart3DPanel(chart);
		panel.setComponentPopupMenu(graphHandler.getGraphMenu());
		dPanel = new DisplayPanel3D(panel, true, false);
		panel.addMouseListener(graphHandler);
		
		// make sure there is nothing inside this panel
		{
			removeAll();
			revalidate();
			repaint();
		}
		
		add(dPanel,  BorderLayout.CENTER);
		
		/*update all the settings*/
		updateGraph();
		displaySettings.updateDisplay();
	}
	
	/**
	 * 
	 */
	public void initSurfacePlot(){
		
		function = new DataInterpolateFunction();
		setLayout(new BorderLayout());
		plotType = SURFACE;
	}
	
	/**
	 * 
	 * @param rX
	 * @param rY
	 */
	public void setRangingConstants(DefinedConstant rX, DefinedConstant rY){
		
		this.rangingConstantX = rX;
		this.rangingConstantY = rY;
	}
	
	/**
	 * 
	 */
	public void plotSurface(){
		
		chart = Chart3DFactory.createSurfaceChart("", "", function, xLabel, zLabel, yLabel);
		plot = (XYZPlot)chart.getPlot();
		plot.getXAxis().setRange(new Double(rangingConstantX.getLow().toString()), new Double(rangingConstantX.getHigh().toString()));
		plot.getZAxis().setRange(new Double(rangingConstantY.getLow().toString()), new Double(rangingConstantY.getHigh().toString()));
		rendererSurface = (SurfaceRenderer)plot.getRenderer();
		panel = new Chart3DPanel(chart);
		panel.setComponentPopupMenu(graphHandler.getGraphMenu());
		dPanel = new DisplayPanel3D(panel, true, false);
		panel.addMouseListener(graphHandler);
		
		// make sure there is nothing inside this panel
		{
			removeAll();
			revalidate();
			repaint();
		}
		
		add(dPanel,  BorderLayout.CENTER);
		
		/*update all the settings*/
		updateGraph();
		displaySettings.updateDisplay();
	}
	
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title){
		
		try {
			graphTitle.setValue(title);
		} catch (SettingException e) {
			e.printStackTrace();
		}
		
		updateGraph();
	}
	
	/**
	 * 
	 * @param xLabel
	 * @param yLabel
	 * @param zLabel
	 */
	public void setAxisLabels(String xLabel, String yLabel, String zLabel){
		
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.zLabel = zLabel;
	}

	/**
	 * 
	 * @return
	 */
	public Chart3D getChart() {
		return chart;
	}
	
	public DisplayPanel3D getDisplayPanel(){
		return this.dPanel;
	}

	/**
	 * 
	 * @return
	 */
	public XYZPlot getPlot() {
		return plot;
	}

	/**
	 * 
	 * @return
	 */
	public SurfaceRenderer getSurfaceRenderer() {
		return rendererSurface;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		InputSource inputSource = null;

		// override the resolve method for the dtd
		if (systemId.endsWith("dtd")) {
			// get appropriate dtd from classpath
			InputStream inputStream = Graph.class.getClassLoader().getResourceAsStream("dtds/chartformat.dtd");
			if (inputStream != null)
				inputSource = new InputSource(inputStream);
		}
		return inputSource;
	}


	@Override
	public int getSettingOwnerID() {
		return prism.PropertyConstants.MODEL;
	}


	@Override
	public String getSettingOwnerName() {
		return "";
	}


	@Override
	public String getSettingOwnerClassName() {
		return "Model";
	}


	@Override
	public int getNumSettings() {
		
		if(plotType == SCATTER){
			return 8;
		}
		else if(plotType == SURFACE){
			return 9;
		}
		else{
			return 0;
			
		}
	}


	@Override
	public Setting getSetting(int index) {
		
		switch(index){
		
		case 0:
			return this.graphTitle;
		case 1:
			return this.titleFont;
		case 2:
			return this.legendOrientation;
		case 3:
			return this.rotateIncrement;
		case 4:
			return this.rollIncrement;
		case 5:
			return this.chartType;
		case 6:
			return plotType == SCATTER ? this.pointSize: this.scaleMethod;
		case 7:
			return plotType == SCATTER ? this.dataColor : this.lowColor;
		case 8:
			return plotType == SCATTER ? null : this.highColor;
		default:
				return null;
		
		}
	}


	@Override
	public void notifySettingChanged(Setting setting) {
		updateGraph();
	}

	@Override
	public void setDisplay(SettingDisplay display) 
	{
		this.display = display;
	}
	
	@Override
	public SettingDisplay getDisplay() 
	{
		return display;
	}
	
	/**
	 * 
	 */
	public void updateGraph(){
		
		/*graph title*/
		if(graphTitle.getStringValue().equals(this.chart.getTitle().toString())){
			chart.setTitle(graphTitle.getStringValue());
		}
		
		/*graph title font*/
		{
			chart.setTitle(graphTitle.getStringValue(), titleFont.getFontColorValue().f, titleFont.getFontColorValue().c);
		}
		
		/*legend orientation*/
		if(legendOrientation.getStringValue().equals("Horizontal") && (chart.getLegendOrientation() != Orientation.HORIZONTAL)){
			chart.setLegendOrientation(Orientation.HORIZONTAL);
		}
		else if(legendOrientation.getStringValue().equals("Vertical") && (chart.getLegendOrientation() != Orientation.VERTICAL)){
			chart.setLegendOrientation(Orientation.VERTICAL);
		}
		
		/*rotate increment*/
		if(rotateIncrement.getDoubleValue() != panel.getRotateIncrement()){
			panel.setRotateIncrement(rotateIncrement.getDoubleValue());
		}
		
		/*roll increment*/
		if(rollIncrement.getDoubleValue() != panel.getRollIncrement()){
			panel.setRollIncrement(rollIncrement.getDoubleValue());
		}
		
		/*chart type*/
		
		if(chartType.getStringValue().equals("Scatter plot") && plotType == SURFACE){
			
			initScatterPlot();
			plotScatter("Series");
			plotType = SCATTER;
			
		}
		
		if(chartType.getStringValue().equals("Surface plot") && plotType == SCATTER){
			
			initSurfacePlot();
			function.setData(dataCache);
			plotSurface();
			plotType = SURFACE;
		}
		
		/*scale method*/
		if(scaleMethod.getStringValue().equals("Rainbow scale") && plotType == SURFACE && rendererSurface != null){
			
			lowColor.setEnabled(false);
			highColor.setEnabled(false);
			rendererSurface.setColorScale(new RainbowScale(new Range(0.0, 1.0)));
		}
		else if(scaleMethod.getStringValue().equals("Gradient scale") && plotType == SURFACE && rendererSurface != null){
			
			try {
				legendOrientation.setValue("Horizontal");
			} catch (SettingException e) {
				e.printStackTrace();
			}
			
			chart.setLegendOrientation(Orientation.HORIZONTAL);
			lowColor.setEnabled(true);
			highColor.setEnabled(true);
			rendererSurface.setColorScale(new GradientColorScale(new Range(0.0, 1.0), lowColor.getColorValue(), highColor.getColorValue()));
		}
		
		/*low color*/
		
		if(lowColor.isEnabled() && plotType == SURFACE){
			
			if(!lowColor.getColorValue().equals(((GradientColorScale)rendererSurface.getColorScale()).getLowColor()) && rendererSurface != null)
				rendererSurface.setColorScale(new GradientColorScale(new Range(0.0, 1.0), lowColor.getColorValue(), highColor.getColorValue()));
		}
		
		/*high color*/
		
		if(highColor.isEnabled() && plotType == SURFACE && rendererSurface != null){
			
			if(!highColor.getColorValue().equals(((GradientColorScale)rendererSurface.getColorScale()).getHighColor()))
				rendererSurface.setColorScale(new GradientColorScale(new Range(0.0, 1.0), lowColor.getColorValue(), highColor.getColorValue()));
		}
		
		/*data point size*/
		
		if(plotType == SCATTER && rendererScatter != null){
			
			if(rendererScatter.getSize() != pointSize.getDoubleValue()){
				rendererScatter.setSize(pointSize.getDoubleValue());
			}
			
		}
		
		/*data point color*/
		if(plotType == SCATTER && rendererScatter != null){
			
			if(!rendererScatter.getColorSource().getColor(0, 0).equals(dataColor.getColorValue())){
				
				rendererScatter.setColors(dataColor.getColorValue());
			}
		}
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		return 0;
	}

	/**
	 * 
	 * @return
	 */
	public AxisSettings3D getxAxisSetting() {
		return xAxisSetting;
	}

	/**
	 * 
	 * @return
	 */
	public AxisSettings3D getyAxisSetting() {
		return yAxisSetting;
	}

	/**
	 * 
	 * @return
	 */
	public AxisSettings3D getzAxisSetting() {
		return zAxisSetting;
	}
	
	/**
	 * 
	 * @return
	 */
	public DisplaySettings3D getDisplaySettings(){
		return displaySettings;
	}
	
	/**
	 * 
	 * @param gh
	 */
	public void addMouseListener(GUIGraphHandler gh){
		this.graphHandler = gh;
	}
	
	/**
	 * 
	 * @return
	 */
	public Chart3DPanel getChart3DPanel(){
		return this.panel;
	}
	
	/**
	 * 
	 * @param file
	 * @param panel
	 */
	public static void exportToPDF(File file, Chart3DPanel panel){
		
		PdfWriter out = null;
		Document document = new com.itextpdf.text.Document(PageSize.A4.rotate());
		
		int width = 800, height = 500;
		
		try{
			
			out = PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();
			PdfContentByte contentByte = out.getDirectContent();
			PdfTemplate template = contentByte.createTemplate(width, height);
			@SuppressWarnings("deprecation")
			Graphics2D graphics2d = template.createGraphics(width, height,new DefaultFontMapper());
			Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width,height);

			panel.getChart().draw(graphics2d, rectangle2d);

			graphics2d.dispose();
			contentByte.addTemplate(template, 0, 0);
			
			
		} catch(Exception e){
			
			JOptionPane.showMessageDialog(GUIPrism.getGUI(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}
		
		document.close();
	}
	
	public void exportToGnuplot(File file) throws IOException{
		//TODO
	}
	
	public void exportToMatlab(File file) throws IOException{
		//TODO
	}
	
	/**
	 * 
	 */
	public void createPrintJob(){

	}
	
	/**
	 * 
	 * @author Muhammad Omer Saeed
	 *
	 */
	private class DataInterpolateFunction implements Function3D{
		
		private static final long serialVersionUID = -4229820807542439735L;
		private double [][] data;
		private double xLow, xHigh, xStep,  yLow, yHigh, yStep;
		int xNumSteps,yNumSteps;
		
		/**
		 * 
		 */
		public DataInterpolateFunction(){
			
			this.xLow = (double)rangingConstantX.getLow();
			this.xHigh = (double)rangingConstantX.getHigh();
			this.xStep = (double)rangingConstantX.getStep();
			this.xNumSteps = rangingConstantX.getNumSteps();
			
			this.yLow = (double)rangingConstantY.getLow();
			this.yHigh = (double)rangingConstantY.getHigh();
			this.yStep = (double)rangingConstantY.getStep();
			this.yNumSteps = rangingConstantY.getNumSteps();
		}
		
		/**
		 * 
		 * @param cache
		 */
		public void setData(ArrayList<XYZDataItem> cache){
			
			data = new double[rangingConstantX.getNumSteps()][rangingConstantY.getNumSteps()];
			
			int index = 0;
			
			for(int i = 0 ; i < rangingConstantX.getNumSteps() ; i++){
				for(int j = 0 ; j < rangingConstantY.getNumSteps() ; j++){
					
					data[i][j] = cache.get(index++).getY();
				}
			}
			
		}
		
		/**
		 * 
		 */
		public void printData(){
			
			int index = 0;
			
			for(int i = 0 ; i < rangingConstantX.getNumSteps() ; i++){
				for(int j = 0 ; j < rangingConstantY.getNumSteps() ; j++){
					
					XYZDataItem item = dataCache.get(index++);
					double x = item.getX();
					double y = item.getZ();
					
					System.out.println("(" + x + "," + y + ")" + " = " + data[i][j] + " ");
				}
			}
		}

		/**
		 * 
		 */
		@Override
		public double getValue(double x, double y) {
			
			if(x < xLow || x > xHigh || y < yLow || y > yHigh){
				return 0.0;
			}
			
			int xIndexLow = (int)((x-xLow) / xStep) > xNumSteps-1 ? xNumSteps-1 : (int)((x-xLow) / xStep);
			int xIndexHigh = xIndexLow+1 > xNumSteps-1 ? xIndexLow : xIndexLow + 1;
			int yIndexLow = (int)((y - yLow) / yStep) > yNumSteps-1 ? yNumSteps-1 : (int)((y - yLow) / yStep);
			int yIndexHigh = yIndexLow+1 > yNumSteps-1 ? yIndexLow : yIndexLow + 1;
			
			// if true then we just need to do linear interpolation in y
			if(xIndexLow == xIndexHigh){
				return data[xIndexLow][yIndexLow] + ((data[xIndexLow][yIndexHigh] - data[xIndexLow][yIndexLow]) / (yHigh - yLow))*(y - yLow);
				
			}
			
			//if true we just need to do linear interpolation in x
			if(yIndexLow == yIndexHigh){
				
				return data[xIndexLow][yIndexLow] + ((data[xIndexHigh][yIndexLow] - data[xIndexLow][yIndexLow]) / (xHigh - xLow))*(x - xLow);
			}
			
			// bilinear interpolation see (https://en.wikipedia.org/wiki/Bilinear_interpolation)
			double biLinearlyInterpolated = ((1.0)/((xHigh - xLow)*(yHigh - yLow))) * 
											((data[xIndexLow][yIndexLow] * (xHigh - x)*(yHigh - y)) + 
											(data[xIndexHigh][yIndexLow] * (x - xLow)*(yHigh - y)) +
											(data[xIndexLow][yIndexHigh] * (xHigh - x) * (y - yLow)) +
											(data[xIndexHigh][yIndexHigh] * (x - xLow) * (y - yLow)));

			return biLinearlyInterpolated;
			
		}
		
	}
}