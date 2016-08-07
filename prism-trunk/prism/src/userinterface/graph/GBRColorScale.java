package userinterface.graph;

import java.awt.Color;

import com.orsoncharts.Range;
import com.orsoncharts.renderer.AbstractColorScale;
import com.orsoncharts.renderer.ColorScale;

public class GBRColorScale extends AbstractColorScale implements ColorScale{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected GBRColorScale(Range range) {
		super(range);
	}


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
