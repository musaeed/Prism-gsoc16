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
import java.util.Observable;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;

import settings.BooleanSetting;
import settings.ColorSetting;
import settings.FontColorPair;
import settings.FontColorSetting;
import settings.Setting;
import settings.SettingDisplay;
import settings.SettingException;
import settings.SettingOwner;
import settings.SingleLineStringSetting;

public class AxisSettingsHistogram extends Observable implements SettingOwner{
	
	
	private String name;
	private SettingDisplay display;
	
	/** Our graph object. */
	private Histogram histogram;
	
	/** JFreeChart representation of graphs. */
	private JFreeChart chart;
	
	/** XYPlot of this JFreeChart */
	private XYPlot plot;
	
	/** JFreeChart representation of this axis. */
	private ValueAxis axis;
	
	/** True when this reprents the domain axis. */
	private boolean isDomain;
	
	/**Our settings that can be changed for a histogram*/
	private SingleLineStringSetting heading;
	private FontColorSetting headingFont;
	private FontColorSetting numberFont;
	
	private BooleanSetting showGrid;
	private ColorSetting gridColour;
	
	/**
	 * Creates a new instance of the axis settings for a histogram plot
	 * @param name
	 * @param isDomain
	 * @param histogram
	 */
	public AxisSettingsHistogram(String name, boolean isDomain, Histogram histogram) {
		
		this.name = name;
		this.isDomain = isDomain;
		this.histogram = histogram;
		this.chart = histogram.getChart();
		this.plot = chart.getXYPlot();
		this.axis = (isDomain) ? this.plot.getDomainAxis() : this.plot.getRangeAxis();
		init();
	}
	
	
	/**
	 * Initializes all the settings
	 */
	public void init(){
		
		
		heading = new SingleLineStringSetting("heading", name, "The heading for this axis", this, true);
		headingFont = new FontColorSetting("heading font", new FontColorPair(new Font("SansSerif", Font.PLAIN, 12), Color.black), "The font for this axis' heading.", this, true);
		numberFont = new FontColorSetting("numbering font", new FontColorPair(new Font("SansSerif", Font.PLAIN, 12), Color.black), "The font used to number the axis.", this, true);
		
		showGrid = new BooleanSetting("show gridlines", new Boolean(true), "Should the gridlines be visible", this, true);
		gridColour = new ColorSetting("gridline colour", new Color(204,204,204), "The colour of the gridlines", this, true);
		updateAxis();
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
		
		return heading.getStringValue();
	}

	@Override
	public String getSettingOwnerClassName() {
		return "AxisSettingHistogram";
	}

	@Override
	public int getNumSettings() {
		return 5;
	}

	@Override
	public Setting getSetting(int index) {
		
		switch(index)
		{
			case 0 : return heading;
			case 1 : return headingFont;
			case 2 : return numberFont;
			case 3 : return showGrid;
			case 4 : return gridColour;
			default: return null;
		}
		
	}

	@Override
	public void notifySettingChanged(Setting setting) {
		updateAxis();
		notifyObservers(this);
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
	 * Getter for property heading.
	 * @return Value of property heading.
	 */
	public String getHeading()
	{
		return heading.getStringValue();
	}
	
	/**
	 * Setter for property heading.
	 * @param value Value of property heading.
	 */
	public void setHeading(String value)
	{
		try
		{
			heading.setValue(value);
			updateAxis();
			setChanged();
			notifyObservers(this);
		}
		catch (SettingException e)
		{
			// Shouldn't happen.
		}
	}	
	
	/**
	 * Getter for property headingFont.
	 * @return Value of property headingFont.
	 */
	public FontColorPair getHeadingFont()
	{
		return headingFont.getFontColorValue();
	}
	
	/**
	 * Setter for property headingfont.
	 * @param value Value of property headingfont.
	 */
	public void setHeadingFont(FontColorPair value)
	{
		try
		{
			headingFont.setValue(value);
			updateAxis();
			setChanged();
			notifyObservers(this);
		}
		catch (SettingException e)
		{
			// Shouldn't happen.
		}
	}
		
	/**
	 * Getter for property numberFont.
	 * @return Value of property numberFont.
	 */
	public FontColorPair getNumberFont()
	{
		return numberFont.getFontColorValue();
	}
	
	/**
	 * Setter for property numberfont.
	 * @param value Value of property numberfont.
	 */
	public void setNumberFont(FontColorPair value)
	{
		try
		{
			numberFont.setValue(value);
			updateAxis();
			setChanged();
			notifyObservers(this);
		}
		catch (SettingException e)
		{
			// Shouldn't happen.
		}
	}
	
	/**
	 * Getter for property showGrid.
	 * @return Value of property showGrid.
	 */
	public boolean showGrid()
	{
		return showGrid.getBooleanValue();
	}
	
	
	
	/**
	 * Setter for property showGrid.
	 * @param value Value of property showGrid.
	 */
	public void showGrid(boolean value)
	{
		try
		{
			showGrid.setValue(new Boolean(value));
			updateAxis();
			setChanged();
			notifyObservers(this);
		}
		catch (SettingException e)
		{
			// Shouldn't happen.
		}
	}
	
	/**
	 * Getter for property gridColour.
	 * @return Value of property gridColour.
	 */
	public Color getGridColour() 
	{
		return gridColour.getColorValue();
	}
	
	/**
	 * Setter for property gridColour.
	 * @param value Value of property gridColour.
	 */
	public void setGridColour(Color value)
	{
		try
		{
			gridColour.setValue(value);
			updateAxis();
			setChanged();
			notifyObservers(this);
		}
		catch (SettingException e)
		{
			// Shouldn't happen.
		}
	}
	
	/**
	 * Updates all the settings of the histogram axis after a change event is recieved
	 */
	private void updateAxis(){
		
		/** -- Check done, now look for smaller changes. */		
		
		/* If the heading of the axis does not match the heading set in the settings... */
		if (!(axis.getLabel().equals(getHeading())))
		{
			axis.setLabel(getHeading());
		}
		
		/* Update axis heading font if appropriate */
		if (!(axis.getLabelFont().equals(getHeadingFont().f)))
		{
			axis.setLabelFont(getHeadingFont().f);
		}	
		
		/* Update axis heading colour if appropriate */
		if (!(axis.getLabelPaint().equals(getHeadingFont().c)))
		{
			axis.setLabelPaint(getHeadingFont().c);
		}
		
		/* Update axis numbering font if appropriate */
		if (!(axis.getTickLabelFont().equals(getNumberFont().f)))
		{
			axis.setTickLabelFont(getNumberFont().f);
		}	
		
		/* Update axis numbering colour if appropriate */
		if (!(axis.getTickLabelPaint().equals(getNumberFont().c)))
		{
			axis.setTickLabelPaint(getNumberFont().c);
		}
		
		/* Update gridlines if appropriate. */
		if (isDomain && (plot.isDomainGridlinesVisible() != showGrid.getBooleanValue()))
		{
			plot.setDomainGridlinesVisible(showGrid.getBooleanValue());
		}		
		
		if (!isDomain && (plot.isRangeGridlinesVisible() != showGrid.getBooleanValue()))
		{
			plot.setRangeGridlinesVisible(showGrid.getBooleanValue());
		}
		
		/* Update gridline colour if appropriate. */
		if (isDomain && (!plot.getDomainGridlinePaint().equals(gridColour.getColorValue())))
		{
			plot.setDomainGridlinePaint(gridColour.getColorValue());
		}
	}
	

}
