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
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.xyz.SurfaceRenderer;
import com.orsoncharts.table.RectanglePainter;

import settings.BooleanSetting;
import settings.ColorSetting;
import settings.Setting;
import settings.SettingDisplay;
import settings.SettingOwner;

/**
 * Creates a new instance of the Display settings for the 3D plots
 * @author Muhammad Omer Saeed
 */
public class DisplaySettings3D extends Observable implements SettingOwner{

	
	/* Display for settings. */
	private SettingDisplay display;
	
	/* Our graph object. */
	private Graph3D graph;
	
	/*our settings that can be altered for the 3d chart display*/
	private BooleanSetting antiAlias;
	private BooleanSetting faceoutLines;
	private ColorSetting chartBoxColor;
	
	/**
	 * Initialize all the settings and set our graph object
	 * @param graph
	 */
	public DisplaySettings3D(Graph3D graph) {
		
		this.graph = graph;
		
		antiAlias = new BooleanSetting("Anti alias", true, "set anti alias", this, false);
		faceoutLines = new BooleanSetting("Face out lines", false, "Draw face out lines?", this, false);
		chartBoxColor = new ColorSetting("Chart box color", Color.WHITE, "set the color of the chart box", this, false);
		
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
		return 3;
	}

	@Override
	public Setting getSetting(int index) {

		switch(index){
		case 0:
			return this.antiAlias;
		case 1:
			return this.faceoutLines;
		case 2:
			return this.chartBoxColor;
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
	
	
	/**
	 * Updates the display of the chart with all the new settings when any setting is changed
	 */
	public void updateDisplay(){
		
		/*Draw anti aliased?*/
		if(antiAlias.getBooleanValue() != graph.getChart().getAntiAlias()){
			graph.getChart().setAntiAlias(antiAlias.getBooleanValue());
		}
		
		/*draw face out lines?*/
		if(((XYZPlot)graph.getChart().getPlot()).getRenderer() instanceof SurfaceRenderer){
			
			if(faceoutLines.getBooleanValue() != ((SurfaceRenderer)((XYZPlot)graph.getChart().getPlot()).getRenderer()).getDrawFaceOutlines()){
				((SurfaceRenderer)((XYZPlot)graph.getChart().getPlot()).getRenderer()).setDrawFaceOutlines(faceoutLines.getBooleanValue());
			}
			
		}
		
		/*chart box color*/
		if(!chartBoxColor.getColorValue().equals(graph.getChart().getChartBoxColor())){
			graph.getChart().setChartBoxColor(chartBoxColor.getColorValue());
		}
	}

}