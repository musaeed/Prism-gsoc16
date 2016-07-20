package userinterface.graph;

import java.awt.BorderLayout;

import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.Chart3DPanel;
import com.orsoncharts.Range;
import com.orsoncharts.data.function.Function3D;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.RainbowScale;
import com.orsoncharts.renderer.xyz.SurfaceRenderer;

import param.BigRational;
import param.Function;
import settings.IntegerSetting;
import settings.Setting;

public class ParametricGraph3D extends Graph3D {
	
	private static final long serialVersionUID = 1L;

	private ParamFunction function;
	private double lowerBoundX, lowerBoundY;
	private double upperBoundX, upperBoundY;
	
	private IntegerSetting xResolution;
	private IntegerSetting yResolution;
	
	public ParametricGraph3D(Function func){
		this();
		function = new ParamFunction(func);
	}
	
	public ParametricGraph3D(){
		initSettings();
		lowerBoundX = lowerBoundY = 0.0;
		upperBoundX = upperBoundY = 1.0;
		
		xResolution = new IntegerSetting("Sampling rate X", 25, "change the sampling rate of the x axis", this, false);
		yResolution = new IntegerSetting("Sampling rate Y", 25, "change the sampling rate of the y axis", this, false);
	}
	
	public ParametricGraph3D(Function func, double lowerBoundX, double upperBoundX, 
			double lowerBoundY, double upperBoundY){
		
		function = new ParamFunction(func);
		this.lowerBoundX = lowerBoundX;
		this.upperBoundX = upperBoundX;
		this.lowerBoundY = lowerBoundY;
		this.upperBoundY = upperBoundX;
	}
	
	public void setBounds(double lowerX, double upperX, double lowerY, double upperY){
		
		this.lowerBoundX = lowerX;
		this.upperBoundX = upperX;
		this.lowerBoundY = lowerY;
		this.upperBoundY = upperY;
	}
	
	public void setFunction(Function func){
		function = new ParamFunction(func);
	}

	public void plot(String title, String xLabel, String yLabel, String zLabel){
		
		chart = Chart3DFactory.createSurfaceChart("", "", function, xLabel, yLabel, zLabel);
		
		panel = new Chart3DPanel(chart);
		panel.addMouseListener(graphHandler);
		panel.setComponentPopupMenu(graphHandler.getGraphMenu());

		
		plot = (XYZPlot)chart.getPlot();
		plot.getXAxis().setRange(lowerBoundX, upperBoundX);
		plot.getZAxis().setRange(lowerBoundY, upperBoundY);
		
		renderer = (SurfaceRenderer) plot.getRenderer();
		renderer.setColorScale(new RainbowScale(new Range(0.0, 1.0)));
		
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		
		initSettings();
		
		/*Update all the settings*/
		
		this.getDisplaySettings().updateDisplay();
		this.updateGraph();
		this.getxAxisSetting().updateAxis();
		this.getyAxisSetting().updateAxis();
		this.getzAxisSetting().updateAxis();
	}

	@Override
	public int getNumSettings() {
		return 8;
	}

	@Override
	public Setting getSetting(int index) {

		if(index < 6){
			return super.getSetting(index);
		}

		switch(index){
		
		case 6:
			return xResolution;
		case 7:
			return yResolution;
		default:
			return null;

		}
	}
	
	@Override
	public void updateGraph(){
		
		super.updateGraph();
	
		if(xResolution.getIntegerValue() != renderer.getXSamples()){
			renderer.setXSamples(xResolution.getIntegerValue());
		}
		
		if(yResolution.getIntegerValue() != renderer.getXSamples()){
			renderer.setZSamples(yResolution.getIntegerValue());			
		}
	}

	private class ParamFunction implements Function3D{
		
		private static final long serialVersionUID = 1L;
		private Function func;
		
		public ParamFunction(Function func) {
			this.func = func;
		}

		@Override
		public double getValue(double x, double y) {
			
			BigRational xVal = new BigRational((int) (x*100000.0), 100000);
			BigRational yVal = new BigRational((int) (y*100000.0), 100000);
			
			BigRational br = func.evaluate(new param.Point(new BigRational[] {xVal, yVal}));
			return br.doubleValue();
		}
		
	}
}
