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
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.xyz.SurfaceRenderer;
import com.orsoncharts.util.Orientation;

import settings.ChoiceSetting;
import settings.FontColorPair;
import settings.FontColorSetting;
import settings.MultipleLineStringSetting;
import settings.Setting;
import settings.SettingDisplay;
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
	private MultipleLineStringSetting graphTitle;
	private FontColorSetting titleFont;
	private ChoiceSetting legendOrientation;
	private ChoiceSetting scaleMethod;
	
	
	public Graph3D(){
	
	}
	
	public void initSettings(){
		
		xAxisSetting = new AxisSettings3D("x axis settings", AxisSettings3D.XAXIS, this);
		yAxisSetting = new AxisSettings3D("y axis settings", AxisSettings3D.YAXIS, this);
		zAxisSetting = new AxisSettings3D("z axis settings", AxisSettings3D.ZAXIS, this);
		
		displaySettings = new DisplaySettings3D(this);
		displaySettings.addObserver(this);
		
		graphTitle = new MultipleLineStringSetting("title", "", 
				"the main title heading for the chart", this, false);
		
		titleFont = new FontColorSetting("title font", new FontColorPair(new Font(Font.SANS_SERIF, Font.PLAIN, 15), 
				Color.BLACK), "the font of the chart's title", this, false);
		
		legendOrientation = new ChoiceSetting("legend orientation", new String[]{"Horizontal","Vertical"}, "Horizontal" 
				, "change the orientation of the legend", this, false);
		
		scaleMethod = new ChoiceSetting("scale method", new String[]{"Rainbow scale"}, "Rainbow scale", 
				"change the scale method for the chart", this, false);
		
	}
	
	


	public Chart3D getChart() {
		return chart;
	}

	public XYZPlot getPlot() {
		return plot;
	}

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
		return 4;
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
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		return 0;
	}

	public AxisSettings3D getxAxisSetting() {
		return xAxisSetting;
	}

	public AxisSettings3D getyAxisSetting() {
		return yAxisSetting;
	}

	public AxisSettings3D getzAxisSetting() {
		return zAxisSetting;
	}
	
	public DisplaySettings3D getDisplaySettings(){
		return displaySettings;
	}
	
	public void addMouseListener(GUIGraphHandler gh){
		this.graphHandler = gh;
	}
	
	public Chart3DPanel getChart3DPanel(){
		return this.panel;
	}
}
