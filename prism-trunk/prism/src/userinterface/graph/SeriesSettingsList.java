//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Mark Kattenbelt <mark.kattenbelt@comlab.ox.ac.uk> (University of Oxford)
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

import java.util.*;

import javax.swing.*;

import org.jfree.chart.ChartPanel;

/**
 * Representation of an axis of a Graph.
 * The settings are propagated to the JFreeChart library.
 */
@SuppressWarnings("serial")
public class SeriesSettingsList extends AbstractListModel implements Observer
{
	private ChartPanel graph;
	
	private HashMap<Integer, SeriesKey> seriesKeys;
	
	public SeriesSettingsList(Graph graph)
	{
		this.graph = graph;
		this.seriesKeys = new HashMap<Integer, SeriesKey>();
	}	

	public Object getElementAt(int index) 
	{
		
		if(graph instanceof Graph){
			
			Graph temp = (Graph) graph;
			
			synchronized (temp.getSeriesLock())
			{
				return temp.getGraphSeries(seriesKeys.get(index));
			}
			
		}
		else{
			
			return null;
		}
		
	}
	
	public SeriesKey getKeyAt(int index)
	{
		if(graph instanceof Graph){
			
			Graph temp = (Graph) graph;
			
			synchronized (temp.getSeriesLock())
			{
				return seriesKeys.get(index);
			}
			
		}
		else {
			
			Histogram temp = (Histogram)graph;
			synchronized (temp.getSeriesLock())
			{
				return seriesKeys.get(index);
			}
		}

	}

	public int getSize() 
	{
		return seriesKeys.size();
	}
	
	public void updateSeriesList()
	{
		
		if(graph instanceof Graph){
			
			Graph temp = (Graph) graph;
			
			synchronized (temp.getSeriesLock())
			{
				for (Map.Entry<Integer, SeriesKey> entry : seriesKeys.entrySet())
				{			
					SeriesSettings series = temp.getGraphSeries(entry.getValue());
					if (series != null)
						series.deleteObserver(this);
				}
				
				seriesKeys.clear();
				
				for (SeriesKey key: temp.getAllSeriesKeys())
				{
					seriesKeys.put(temp.getJFreeChartIndex(key), key);
					temp.getGraphSeries(key).updateSeries();
					temp.getGraphSeries(key).addObserver(this);				
				}
			}
			
		}

		fireContentsChanged(this, 0, this.getSize());		
	}
	
	public void update(Observable o, Object arg) 
	{		
		fireContentsChanged(this, 0, this.getSize());
	}
}
