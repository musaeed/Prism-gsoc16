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

import org.jfree.data.xy.XYDataItem;

/**
 * Custom data item for Prism. This data item supports error info in addition to the usual x and y values
 * @author Muhammad Omer Saeed
 */
public class PrismXYDataItem extends XYDataItem{


	private static final long serialVersionUID = 1L;

	/**The error of this point*/
	private double error;

	/**
	 * Creates a new data item using {@code Number}
	 * @param x the x value
	 * @param y the y value
	 */
	public PrismXYDataItem(Number x, Number y){
		super(x,y);
		error = 0.0;
	}

	/**
	 * Creates a new data item using {@code Double}
	 * @param x the x value
	 * @param y the y value
	 */
	public PrismXYDataItem(double x, double y) {
		super(x,y);
		error = 0.0;
	}

	/**
	 * Creates a new data item using {@code Double} and the error at this point
	 * @param x the x value
	 * @param y the y value
	 * @param error the error value
	 */
	public PrismXYDataItem(double x, double y, double error){
		super(x,y);
		this.error = error;
	}

	/**
	 * Get the error of the current point
	 * @return
	 */
	public double getError(){
		return this.error;
	}

	/**
	 * Set the error of the current point
	 * @param error
	 */
	public void setError(double error){
		this.error = error;
	}
	
	/**
	 * Used for debugging mostly
	 */
	@Override
	public String toString(){
		
		String res = "\nPrismXYDataItem: \n";
		res += "X: " + this.getXValue() + "\n";
		res += "Y: " + this.getYValue() + "\n";
		res += "Error: " + this.getError() + "\n";
		
		return res;
	}
}