package userinterface.graph;

import com.orsoncharts.data.xyz.XYZDataItem;

import parser.Values;
import prism.DefinedConstant;
import prism.PrismLangException;
import prism.ResultListener;
import prism.ResultsCollection;

public class GraphResultListener3D implements ResultListener{
	
	private Graph3D graphModel;
	private DefinedConstant rangeConstantX, rangeConstantY;
	String seriesName;
	
	public GraphResultListener3D(Graph3D model, DefinedConstant rangeConstantX, DefinedConstant rangeConstantY, String seriesName) {
		this.graphModel = model;
		this.rangeConstantX = rangeConstantX;
		this.rangeConstantY = rangeConstantY;
		this.seriesName = seriesName;
		graphModel.initScatterPlot();
		graphModel.setRangingConstants(rangeConstantX, rangeConstantY);
	}

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
	
	public void done(){
		
		graphModel.plotScatter(seriesName);
	}

}
