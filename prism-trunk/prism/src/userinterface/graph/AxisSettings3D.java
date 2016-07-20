package userinterface.graph;

import java.awt.Color;
import java.awt.Font;
import java.util.Observable;

import com.orsoncharts.Chart3D;
import com.orsoncharts.axis.ValueAxis3D;
import com.orsoncharts.plot.XYZPlot;

import settings.BooleanSetting;
import settings.FontColorPair;
import settings.FontColorSetting;
import settings.Setting;
import settings.SettingDisplay;
import settings.SettingOwner;

public class AxisSettings3D extends Observable implements SettingOwner{

	public static int XAXIS = 0;
	public static int YAXIS = 1;
	public static int ZAXIS = 2;
	
	private String name;
	private SettingDisplay display;
	
	/** Our graph object. */
	private Graph3D graph;
	
	/** Orson chart representation of graphs. */
	private Chart3D chart;
	
	/** XYZPlot of this Orson chart */
	private XYZPlot plot;
	
	/** Orson chart representation of this axis. */
	private ValueAxis3D axis;
	
	/** tells us about the type of this axis */
	private int axisType;
	
	private FontColorSetting labelFont;
	private BooleanSetting visible;
	private BooleanSetting inverted;
	
	
	public AxisSettings3D(String name, int axisType, Graph3D graph) {
		
		this.name = name;
		this.axisType = axisType;
		this.graph = graph;
		this.chart = graph.getChart();
		this.plot = graph.getPlot();
		
		labelFont = new FontColorSetting("label font", new FontColorPair(new Font(Font.SANS_SERIF, Font.PLAIN, 16), Color.black),
				"change the font of the label", this, false);
		visible = new BooleanSetting("Axis visible?", true, "change the visibility of the axis", this, false);
		inverted = new BooleanSetting("Inverted", false, "invert the axis", this, false);
		
		display = null;	
		
		updateAxis();
		setChanged();
		notifyObservers();
		
	}
	
	
	@Override
	public int compareTo(Object o) {
		
		if(o instanceof SettingOwner)
		{
			SettingOwner po = (SettingOwner) o;
			if(getSettingOwnerID() < po.getSettingOwnerID() )return -1;
			else if(getSettingOwnerID() > po.getSettingOwnerID()) return 1;
			else return 0;
		}
		else return 0;
	}

	@Override
	public int getSettingOwnerID() {
		return prism.PropertyConstants.AXIS;
	}

	@Override
	public String getSettingOwnerName() {
		return "axisname";
	}

	@Override
	public String getSettingOwnerClassName() {
		return "Axis";
	}

	@Override
	public int getNumSettings() {
		return 3;
	}

	@Override
	public Setting getSetting(int index) {

		switch(index){

		case 0:
			return this.labelFont;
		case 1:
			return this.visible;
		case 2:
			return this.inverted;
		default:
			return null;
		}
		
	}

	@Override
	public void notifySettingChanged(Setting setting) {
		updateAxis();
		notifyObservers(this);
	}

	@Override
	public SettingDisplay getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDisplay(SettingDisplay display) {
		// TODO Auto-generated method stub
		
	}
	
	public void updateAxis(){
		
	}

}
