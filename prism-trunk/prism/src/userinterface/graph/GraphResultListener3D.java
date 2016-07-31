package userinterface.graph;

import com.orsoncharts.data.xyz.XYZDataItem;

import parser.Values;
import prism.PrismLangException;
import prism.ResultListener;
import prism.ResultsCollection;

public class GraphResultListener3D implements ResultListener{
	
	private Graph3D graphModel;
	private String rangeConstantX, rangeConstantY;
	String seriesName;
	
	public GraphResultListener3D(Graph3D model, String rangeConstantX, String rangeConstantY, String seriesName) {
		this.graphModel = model;
		this.rangeConstantX = rangeConstantX;
		this.rangeConstantY = rangeConstantY;
		this.seriesName = seriesName;
		graphModel.initScatterPlot();
	}

	@Override
	public void notifyResult(ResultsCollection resultsCollection, Values values, Object result) {
		
/*		for(int i = 0 ; i < values.getNumValues() ; i++){
			
			try {
				System.out.println(values.getName(i) + ": " + values.getDoubleValue(i));
			} catch (PrismLangException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Result: " + result);		*/
		
		try {
			graphModel.addPointToDataCache(new XYZDataItem(values.getDoubleValue(0), values.getDoubleValue(1), new Double(result.toString())));
		} catch (NumberFormatException | PrismLangException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void done(){
		
		graphModel.plotScatter(seriesName);
	}

}
