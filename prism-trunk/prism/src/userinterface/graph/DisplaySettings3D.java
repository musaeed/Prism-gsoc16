package userinterface.graph;

import java.util.Observable;

import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.xyz.SurfaceRenderer;

import settings.BooleanSetting;
import settings.Setting;
import settings.SettingDisplay;
import settings.SettingOwner;

public class DisplaySettings3D extends Observable implements SettingOwner{

	
	/* Display for settings. */
	private SettingDisplay display;
	
	/* Our graph object. */
	private Graph3D graph;
	
	private BooleanSetting antiAlias;
	private BooleanSetting faceoutLines;
	
	public DisplaySettings3D(Graph3D graph) {
		
		this.graph = graph;
		
		antiAlias = new BooleanSetting("Anti alias", true, "set anti alias", this, false);
		faceoutLines = new BooleanSetting("Face out lines", false, "Draw face out lines?", this, false);
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
		return prism.PropertyConstants.GRAPH_DISPLAY;
	}

	@Override
	public String getSettingOwnerName() {
		
		if (graph != null && graph.getName() != null)
			return graph.getName();
			
		return "";
	}

	@Override
	public String getSettingOwnerClassName() {
		return "Display";
	}

	@Override
	public int getNumSettings() {
		return 2;
	}

	@Override
	public Setting getSetting(int index) {

		switch(index){
		case 0:
			return this.antiAlias;
		case 1:
			return this.faceoutLines;
		default:
			return null;
		}
	}

	@Override
	public void notifySettingChanged(Setting setting) {
		notifyObservers(this);
		updateDisplay();
	}

	@Override
	public SettingDisplay getDisplay() {
		return display;
	}

	@Override
	public void setDisplay(SettingDisplay display) {
		this.display = display;
	}
	
	public void updateDisplay(){
		
		/*Draw anti aliased?*/
		if(antiAlias.getBooleanValue() != graph.getChart().getAntiAlias()){
			graph.getChart().setAntiAlias(antiAlias.getBooleanValue());
		}
		
		/*draw face out lines?*/
		if(faceoutLines.getBooleanValue() != ((SurfaceRenderer)((XYZPlot)graph.getChart().getPlot()).getRenderer()).getDrawFaceOutlines()){
			((SurfaceRenderer)((XYZPlot)graph.getChart().getPlot()).getRenderer()).setDrawFaceOutlines(faceoutLines.getBooleanValue());
		}
	}

}