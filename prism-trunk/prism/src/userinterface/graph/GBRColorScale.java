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

import com.orsoncharts.Range;
import com.orsoncharts.renderer.AbstractColorScale;
import com.orsoncharts.renderer.ColorScale;

/**
 * A Green->Blue->Red color scale that can be applied to the 3D surface charts
 * @author Muhammad Omer Saeed
 */
public class GBRColorScale extends AbstractColorScale implements ColorScale{

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new GBR color scale with the specified range
	 * @param range
	 */
	protected GBRColorScale(Range range) {
		super(range);
	}


	/**
	 * Calculate the color to be applied to the value
	 * @param value the value for which the color has to be calculated
	 */
	@Override
	public Color valueToColor(double value) {
		
		if(value < getRange().getMin()){
			return valueToColor(getRange().getMin());
		}
		
		if(value > getRange().getMax()){
			return valueToColor(getRange().getMax());
		}
		
		
		// if it is inside the range we want
		float fraction = (float)getRange().percent(value);
		
		// we should do a linear interpolation between green and blue
		
		if(fraction > 0 && fraction < 0.5f){
			
			int r = (int) (255.0 - (1.f-fraction)*255.0);
			int g = (int) (255.0 - fraction*255.0);
			int b = (int) (255.0 - (0.5f-fraction)*255.0);
			
			return new Color(r, g, b);
		}
		
		if(fraction >= 0.5 && fraction < 1.0){
			
			int r = (int) (255.0 - (1.f-fraction)*255.0);
			int g = (int) (255.0 - fraction*255.0);
			int b = (int) (255.0 - (fraction-0.5f)*255.0);
			
			
			return new Color(r,g,b);
			
		}
		
		if(fraction == 0.f){
			return Color.GREEN;
		}
		else 
			return Color.RED;
		
	}

}
