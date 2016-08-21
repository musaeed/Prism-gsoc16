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

import com.orsoncharts.data.xyz.XYZDataItem;

import parser.Values;
import prism.DefinedConstant;
import prism.PrismLangException;
import prism.ResultListener;
import prism.ResultsCollection;

/**
 * A result listener for 3D plots, used to collect data for the Scatter plots
 * 
 * @author Muhammad Omer Saeed
 */
public class GraphResultListener3D implements ResultListener{
	
	/**Our graph model to which data has to be sent*/
	private Graph3D graphModel;
	/**Our Range constants for x and y axis*/
	private DefinedConstant rangeConstantX, rangeConstantY;
	/**The name of the series we are sending data for*/
	String seriesName;
	
	/**
	 * Creates a new result listener for 3D plot
	 * 
	 * @param model
	 * @param rangeConstantX
	 * @param rangeConstantY
	 * @param seriesName
	 */
	public GraphResultListener3D(Graph3D model, DefinedConstant rangeConstantX, DefinedConstant rangeConstantY, String seriesName) {
		this.graphModel = model;
		this.rangeConstantX = rangeConstantX;
		this.rangeConstantY = rangeConstantY;
		this.seriesName = seriesName;
		graphModel.initScatterPlot();
		graphModel.setRangingConstants(rangeConstantX, rangeConstantY);
	}

	/**
	 * Notufy the result to our graph model
	 */
	@Override
	public void notifyResult(ResultsCollection resultsCollection, Values values, Object result) {
		
		double z = 0.0;
		
		if(result instanceof Double){
			z =  new Double(result.toString());
		}
		else if(result instanceof prism.Pair<?, ?>){
			z = ((prism.Pair<Double, Double>)result).first;
		}
		else if(result instanceof Exception)
			return;
		
		try {
			graphModel.addPointToDataCache(new XYZDataItem(new Double(values.getValueOf(rangeConstantX.getName()).toString()),z,new Double(values.getValueOf(rangeConstantY.getName()).toString()) ));
		} catch (NumberFormatException | PrismLangException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Called when we are done collecting the data. This function will do the actual plotting of the 3D chart
	 */
	public void done(){
		graphModel.plotScatter(seriesName);
	}

}
