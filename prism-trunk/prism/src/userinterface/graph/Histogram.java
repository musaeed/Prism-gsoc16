package userinterface.graph;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalDataItem;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;

public class Histogram extends ChartPanel{

	
	private static final long serialVersionUID = 1L;
	
	/** Actual JFreeChart representation of datasets. */
	private JFreeChart chart;

	/** XYPlot of this JFreeChart */
	private XYPlot plot;
	
	/**
	 * Maps SeriesKeys to a XYSeries. (Make sure to synchronize on
	 * seriesCollection)
	 */
	private HashMap<SeriesKey, XYIntervalSeries> keyToSeries;
	
	/**
	 * Allows us to batch graph points (JFreeChart is not realtime). (Make sure
	 * to synchronize on seriesCollection)
	 */
	private HashMap<SeriesKey, LinkedList<PrismXYDataItem>> graphCache;
	
	private ArrayList<Double> dataCache;
	
	/**
	 * Collection of all the histograms a single chart will hold
	 */
	private XYIntervalSeriesCollection seriesCollection;
	
	private double maxProb;
	private double minProb;
	private int numOfBuckets;
	
	/**
	 * 
	 */
	public Histogram(){
		this("");
	}
	
	/**
	 * 
	 * @param title 
	 */
	public Histogram(String title){
		
		super(ChartFactory.createXYBarChart(title, "Probability", false, "No. of states", 
				new XYIntervalSeriesCollection(), PlotOrientation.VERTICAL, true, true, false));
		
		init();
	}
	
	/**
	 * Initialize all the fields of the Histogram
	 */
	public void init(){
		
		keyToSeries = new HashMap<SeriesKey, XYIntervalSeries>();
		graphCache = new HashMap<SeriesKey, LinkedList<PrismXYDataItem>>();
		chart = this.getChart();
		plot = chart.getXYPlot();
		seriesCollection = (XYIntervalSeriesCollection)plot.getDataset();
		maxProb = 0.0;
		minProb = 1.0;
		numOfBuckets = 10; // default value, can be altered
		addToolTip();
		
		/*final IntervalMarker target = new IntervalMarker(400.0, 700.0);
        target.setLabel("Target Range");
        target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
        target.setLabelAnchor(RectangleAnchor.LEFT);
        target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        target.setPaint(new Color(222, 222, 255, 128));
        plot.addRangeMarker(target, Layer.BACKGROUND);*/
		
	}
	
	
	
	public int getNumOfBuckets() {
		return numOfBuckets;
	}

	public void setNumOfBuckets(int numOfBuckets) {
		this.numOfBuckets = numOfBuckets;
	}

	/**
	 * Add a series to the buffered graph data.
	 * 
	 * @param seriesName
	 *            Name of series to add to graph.
	 */
	public SeriesKey addSeries(String seriesName) 
	{
		SeriesKey key;

		synchronized (seriesCollection) 
		{
			seriesName = getUniqueSeriesName(seriesName);

			// create a new XYSeries without sorting, disallowing duplicates
			//PrismXYSeries newSeries = new PrismXYSeries(seriesName);
			XYIntervalSeries newSeries = new XYIntervalSeries(seriesName);
			this.seriesCollection.addSeries(newSeries);
			// allocate a new cache for this series

			key = new SeriesKey();

			this.keyToSeries.put(key, newSeries);
			this.graphCache.put(key, new LinkedList<PrismXYDataItem>());

//			SeriesSettings graphSeries = new SeriesSettings(this, key);
//			this.keyToGraphSeries.put(key, graphSeries);
//			graphSeries.addObserver(this);

//			this.seriesList.updateSeriesList();			
		}		

		return key;		
	}
	
	private String getUniqueSeriesName(String seriesName)
	{
		synchronized (seriesCollection) 
		{
			int counter = 0;
			String name = seriesName;

			/* Name sure seriesName is unique */
			while (true)
			{
				boolean nameExists = false;

				for (Map.Entry<SeriesKey, XYIntervalSeries> entry : keyToSeries.entrySet())
				{
					if (name.equals(entry.getValue().getKey()))
					{
						nameExists = true;
						break;
					}
				}

				if (nameExists)
				{
					counter++;
					name = seriesName + " (" + counter + ")";
				}
				else
				{
					break;
				}				 
			}

			return name;
		}	
	}
	
	/**
	 * Wholly remove a series from the current graph, by key.
	 * @param seriesKey SeriesKey of series to remove.
	 */
	public void removeSeries(SeriesKey seriesKey) 
	{
		synchronized (seriesCollection) {
			// Delete from keyToSeries and seriesCollection.
			if (keyToSeries.containsKey(seriesKey)) {
				XYIntervalSeries series = keyToSeries.get(seriesKey);
				seriesCollection.removeSeries(series);
				keyToSeries.remove(seriesKey);
			}

			// Remove any cache.
			if (graphCache.containsKey(seriesKey)) {
				graphCache.remove(seriesKey);
			}

			/*if (keyToGraphSeries.containsKey(seriesKey))
			{
				keyToGraphSeries.get(seriesKey).deleteObservers();				
				keyToGraphSeries.remove(seriesKey);
			}*/

		//	this.seriesList.updateSeriesList();	
		}

	//	seriesList.updateSeriesList();
	}

	/**
	 * Add a point to the specified graph series.
	 * @param seriesKey Key of series to update.
	 * @param dataItem XYDataItem object to insert into this series.
	 */
	public void addPointToSeries(SeriesKey seriesKey, XYIntervalDataItem dataItem) {

		synchronized (seriesCollection) {
			if (graphCache.containsKey(seriesKey)) {
					//LinkedList<PrismXYDataItem> seriesCache = graphCache
						//	.get(seriesKey);
					//seriesCache.add(dataItem);
					
				//	PrismXYSeries series = (PrismXYSeries)keyToSeries.get(seriesKey);
				XYIntervalSeries series = keyToSeries.get(seriesKey);
					series.add(dataItem.getX(), dataItem.getXLowValue(), dataItem.getXHighValue(), dataItem.getYValue(), dataItem.getYLowValue(), dataItem.getYHighValue());
			}
		}
	}
	
	public void plotSeries(SeriesKey seriesKey){
		
		for(double item : dataCache){
			
			if(item > maxProb)
				maxProb = item;
			
			if(item < minProb)
				minProb = item;
		}
		

		
		double range = (maxProb - minProb) / (double)numOfBuckets;
		
		for(int i = 0 ; i < numOfBuckets ; i++){
			
			double minRange = minProb + (range*i);
			double maxRange = minRange + range;
			
			double height;
			
			if(i == (numOfBuckets-1))
				height = countProbabilities(minRange, maxRange, true);
			else
				height = countProbabilities(minRange, maxRange, false);
				
			double x = (minRange + maxRange) / 2.0;
			addPointToSeries(seriesKey, new XYIntervalDataItem(x, minRange, maxRange, height, height, height));
		}
		
		dataCache.clear();
	}
	
	public int countProbabilities(double minRange, double maxRange, boolean includeLast){
		
		int count = 0;
		
		if(includeLast){
			maxRange += 1;
		}
		
		for(int i = 0 ; i < dataCache.size() ; i++){
			
			double prob = dataCache.get(i);
			
			if(prob >= minRange && prob < maxRange){
				count++;
			}
		}
		
		return count;
	}
	
	public void addDataToCache(ArrayList<Double> probs){
		
		dataCache = probs;
		
	}
	
	public void addToolTip(){
		((XYBarRenderer)plot.getRenderer()).setBaseToolTipGenerator(new XYToolTipGenerator() {
			
			@Override
			public String generateToolTip(XYDataset dataset, int seriesIndex, int item) {
				
				XYIntervalSeriesCollection collection = (XYIntervalSeriesCollection)dataset;
				XYIntervalSeries series = collection.getSeries(seriesIndex);
				
				double minX = series.getXLowValue(item);
				double meanX = series.getX(item).doubleValue();
				double maxX = series.getXHighValue(item);
				double height = series.getYValue(item);
				
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(String.format("<html><p style='color:#0000ff;'>Prop: '%s'</p>", dataset.getSeriesKey(seriesIndex)));
				stringBuilder.append("<table style=\"width:100%\">");
				stringBuilder.append("<tr><td> Min probability: </td><td>" + (Math.round( minX * 10000.0 ) / 10000.0)  + "</td></tr>");
				stringBuilder.append("<tr><td> Mean probability: </td><td>" + (Math.round( meanX * 10000.0 ) / 10000.0)  + "</td></tr>");
				stringBuilder.append("<tr><td> Max  probability: </td><td>" + (Math.round( maxX * 10000.0 ) / 10000.0) +  "</td></tr>");
				stringBuilder.append("<tr><td> Number of states: </td><td>" + height +  "</td></tr></table>");
				stringBuilder.append("</html>");
				
				return stringBuilder.toString();
			}
		});
	}
	
	
	public class SeriesKey 
	{
		public SeriesKey next = null;
	}

}
