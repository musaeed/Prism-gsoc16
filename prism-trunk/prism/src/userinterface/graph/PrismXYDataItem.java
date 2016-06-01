package userinterface.graph;

import org.jfree.data.xy.XYDataItem;

public class PrismXYDataItem extends XYDataItem{


	private static final long serialVersionUID = 1L;

	private double error;

	public PrismXYDataItem(Number x, Number y){
		super(x,y);
		error = 0.0;
	}


	public PrismXYDataItem(double x, double y) {
		super(x,y);
		error = 0.0;
	}

	public PrismXYDataItem(double x, double y, double error){
		super(x,y);
		this.error = error;
	}

	public double getError(){
		return this.error;
	}

	public void setError(double error){
		this.error = error;
	}
	
	@Override
	public String toString(){
		
		String res = "\nPrismXYDataItem: \n";
		res += "X: " + this.getXValue() + "\n";
		res += "Y: " + this.getYValue() + "\n";
		res += "Error: " + this.getError() + "\n";
		
		return res;
	}
}