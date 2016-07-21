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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DPanel;
import com.orsoncharts.Range;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.GradientColorScale;
import com.orsoncharts.renderer.RainbowScale;
import com.orsoncharts.renderer.xyz.SurfaceRenderer;
import com.orsoncharts.util.Orientation;

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
import userinterface.properties.GUIGraphHandler;

public class Graph3D extends JPanel  implements SettingOwner, EntityResolver, Observer, Printable{

	private static final long serialVersionUID = 1L;
	
	protected Chart3DPanel panel;
	protected Chart3D chart;
	protected XYZPlot plot;
	protected SurfaceRenderer renderer;
	protected GUIGraphHandler graphHandler;
	
	
	/** Display for settings. Required to implement SettingsOwner */
	private SettingDisplay display;
	
	private AxisSettings3D xAxisSetting, yAxisSetting, zAxisSetting;
	
	private DisplaySettings3D displaySettings;
	
	/** Settings of this graph. */
	protected MultipleLineStringSetting graphTitle;
	private FontColorSetting titleFont;
	private ChoiceSetting legendOrientation;
	private ChoiceSetting scaleMethod;
	private ColorSetting lowColor;
	private ColorSetting highColor;
	private DoubleSetting rotateIncrement;
	private DoubleSetting rollIncrement;
	
	/**
	 * 
	 */
	public Graph3D(){
	
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
		
		scaleMethod = new ChoiceSetting("scale method", new String[]{"Rainbow scale", "Gradient scale"}, "Rainbow scale", 
				"change the scale method for the chart", this, false);
		
		lowColor = new ColorSetting("low color", Color.WHITE, "low color for the gradient scale", this, false);
		lowColor.setEnabled(false);
		
		highColor = new ColorSetting("high color", Color.BLACK, "high color of the gradient scale", this, false);
		highColor.setEnabled(false);
		
		rotateIncrement = new DoubleSetting("Rotate increment", 10.0, "rotate increment value for the plot", this, false);
		rollIncrement = new DoubleSetting("roll increment", 10.0, "roll increment for the plot", this, false);
	}
	
	


	/**
	 * 
	 * @return
	 */
	public Chart3D getChart() {
		return chart;
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
	public SurfaceRenderer getRenderer() {
		return renderer;
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
		return 8;
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
			return this.scaleMethod;
		case 4:
			return this.lowColor;
		case 5:
			return this.highColor;
		case 6:
			return this.rotateIncrement;
		case 7:
			return this.rollIncrement;
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
		
		/*scale method*/
		if(scaleMethod.getStringValue().equals("Rainbow scale")){
			
			lowColor.setEnabled(false);
			highColor.setEnabled(false);
			renderer.setColorScale(new RainbowScale(new Range(0.0, 1.0)));
		}
		else if(scaleMethod.getStringValue().equals("Gradient scale")){
			
			try {
				legendOrientation.setValue("Horizontal");
			} catch (SettingException e) {
				e.printStackTrace();
			}
			
			chart.setLegendOrientation(Orientation.HORIZONTAL);
			lowColor.setEnabled(true);
			highColor.setEnabled(true);
			renderer.setColorScale(new GradientColorScale(new Range(0.0, 1.0), lowColor.getColorValue(), highColor.getColorValue()));
		}
		
		/*low color*/
		
		if(lowColor.isEnabled()){
			
			if(!lowColor.getColorValue().equals(((GradientColorScale)renderer.getColorScale()).getLowColor()))
				renderer.setColorScale(new GradientColorScale(new Range(0.0, 1.0), lowColor.getColorValue(), highColor.getColorValue()));
		}
		
		/*high color*/
		
		if(highColor.isEnabled()){
			
			if(!highColor.getColorValue().equals(((GradientColorScale)renderer.getColorScale()).getHighColor()))
				renderer.setColorScale(new GradientColorScale(new Range(0.0, 1.0), lowColor.getColorValue(), highColor.getColorValue()));
		}
		
		/*rotate increment*/
		if(rotateIncrement.getDoubleValue() != panel.getRotateIncrement()){
			panel.setRotateIncrement(rotateIncrement.getDoubleValue());
		}
		
		/*roll increment*/
		if(rollIncrement.getDoubleValue() != panel.getRollIncrement()){
			panel.setRollIncrement(rollIncrement.getDoubleValue());
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
}