//==============================================================================
//	
//	Copyright (c) 2016-
//	Authors:
//
//  Muhammad Omer Saeed <muhammad.omar555@gmail.com / saeedm@informatik.uni-bonn.> (University of Bonn)
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalDataItem;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.sun.glass.events.KeyEvent;

import prism.Pair;
import settings.BooleanSetting;
import settings.ChoiceSetting;
import settings.FontColorPair;
import settings.FontColorSetting;
import settings.MultipleLineStringSetting;
import settings.Setting;
import settings.SettingDisplay;
import settings.SettingException;
import settings.SettingOwner;
import userinterface.GUIPrism;
import userinterface.properties.GUIGraphHandler;

/**
 * 
 * This class provides the functionality for plotting Histograms in Prism
 * 
 * @author Muhammad Omer Saeed
 */

public class Histogram extends ChartPanel implements SettingOwner, Observer{

	
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
	 * This holds the data that we receive from the other components of Prism 
	 * which require to plot a Histogram. It is emptied once the data is plotted.
	 */
	private ArrayList<Double> dataCache;
	
	/**
	 * Maps SeriesKeys to a Graph Series. (Make sure to synchronize on
	 * seriesCollection)
	 */
	private HashMap<SeriesKey, SeriesSettings> keyToGraphSeries;
	
	/**
	 * Collection of all the histograms a single chart will hold
	 */
	private XYIntervalSeriesCollection seriesCollection;
	
	/**
	 * The maximum probability in the {@link dataCache}
	 */
	private double maxProb;
	
	/**
	 * The minimum probability in the {@link dataCache}
	 */
	private double minProb;
	
	/**
	 * The number of buckets the probability range will be divided in
	 */
	private int numOfBuckets;
	
	/**
	 * The title of the Histogram
	 */
	private String title;
	
	
	/** Display for settings. Required to implement SettingsOwner */
	private SettingDisplay display;
	
	/** Settings of the X axis. 
	 */
	private AxisSettingsHistogram xAxisSettings;

	/**
	 * Settings of the Y axis
	 */
	private AxisSettingsHistogram yAxisSettings;
	
	/** Display settings */
	private DisplaySettings displaySettings;
	
	/** GraphSeriesList */
	private SeriesSettingsList seriesList;
	
	/**Settings for histogram*/
	private MultipleLineStringSetting graphTitle;
	private FontColorSetting titleFont;
	private BooleanSetting legendVisible;
	private ChoiceSetting legendPosition;
	private FontColorSetting legendFont;
	
	/**
	 * Tells whether the Histogram is newly created or it already has some series plotted on it
	 */
	private boolean isNew;
	
	/**
	 * Holds the tick mark locations of the x axis
	 */
	private ArrayList<Double> ticks;
	
	/**
	 * Pointer needed in the Histogram property dialog
	 */
	private static Histogram hist;
	
	/**
	 * Pointer needed in the Histogram property dialog
	 */
	private static SeriesKey key;

	
	/**
	 * Creates an instance of the Histogram with no title
	 */
	public Histogram(){
		this("");
	}
	
	/**
	 * Creates an instance of the Histogram
	 * @param title the title of the Histogram
	 */
	public Histogram(String title){
		
		super(ChartFactory.createXYBarChart(title, "", false, "No. of states", 
				new XYIntervalSeriesCollection(), PlotOrientation.VERTICAL, true, true, false));
		
		this.title = title;
		init();
		initSettings();
	}
	
	/**
	 * Initialize all the fields of the Histogram and set some properties
	 */
	public void init(){
		
		keyToSeries = new HashMap<SeriesKey, XYIntervalSeries>();
		keyToGraphSeries = new HashMap<SeriesKey, SeriesSettings>();
		chart = this.getChart();
		plot = chart.getXYPlot();
		plot.setBackgroundPaint((Paint)Color.white);
		
		plot.setDrawingSupplier(new DefaultDrawingSupplier(
				SeriesSettings.DEFAULT_PAINTS,
				DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
				SeriesSettings.DEFAULT_SHAPES
				));	
		
		seriesCollection = (XYIntervalSeriesCollection)plot.getDataset();
		maxProb = 1.0;
		minProb = 0.0;
		numOfBuckets = 10; // default value, can be altered
		plot.setRenderer(new ClusteredXYBarRenderer());
		addToolTip();
		ticks = new ArrayList<Double>();
		setCustomDomainAxis();
	}
	
	
	/**
	 * Make a custom X axis so that we can control the tick marker locations
	 */
	public void setCustomDomainAxis(){
		
		plot.setDomainAxis(new NumberAxis("Probability"){

			private static final long serialVersionUID = 1L;

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
				
				List list = new ArrayList<>();
				
				for(Double d : ticks){
					

				    double rounded = Math.round(d * 1000);

					rounded = rounded/1000;
					
					list.add(new NumberTick(rounded, rounded+"", TextAnchor.TOP_CENTER, TextAnchor.TOP_CENTER, 0.0));
					
				}
				
				return list;
			}
			
		});
	}
	
	/**
	 * Initialize all the settings of the graph
	 */
	public void initSettings(){
		
		xAxisSettings = new AxisSettingsHistogram("Probability", true, this);
		yAxisSettings = new AxisSettingsHistogram("No. of states", false, this);
		xAxisSettings.addObserver(this);
		yAxisSettings.addObserver(this);
		displaySettings = new DisplaySettings(this);
		
		graphTitle = new MultipleLineStringSetting("title", title,
				"The main title heading for the chart.", this, false);
		titleFont = new FontColorSetting("title font", new FontColorPair(
				new Font("SansSerif", Font.PLAIN, 14), Color.black),
				"The font for the chart's title", this, false);
		legendVisible = new BooleanSetting(
				"legend visible?",
				new Boolean(true),
				"Should the legend, which displays all of the series headings, be displayed?",
				this, false);

		String[] choices = { "Left", "Right", "Bottom", "Top" };
		legendPosition = new ChoiceSetting("legend position", choices,
				choices[Graph.BOTTOM], "The position of the legend", this, false);
		legendFont = new FontColorSetting("legend font", new FontColorPair(
				new Font("SansSerif", Font.PLAIN, 11), Color.black),
				"The font for the legend", this, false);
		
		seriesList = new SeriesSettingsList(this);
		
		updateGraph(); // apply the settings
	}
	
	
	/**
	 * Get the number of buckets
	 * @return number of buckets
	 */
	public int getNumOfBuckets() {
		return numOfBuckets;
	}

	/**
	 * Set the number of buckets for the Histogram
	 * @param numOfBuckets the new value of number of buckets
	 */
	public void setNumOfBuckets(int numOfBuckets) {
		this.numOfBuckets = numOfBuckets;
	}
	
	
	/**
	 * Get the maximum probability
	 * @return maxProb
	 */
	public double getMaxProb() {
		return maxProb;
	}

	/**
	 * Set the maximum probability
	 * @param maxProb the new value of max probability
	 */
	public void setMaxProb(double maxProb) {
		this.maxProb = maxProb;
	}

	/**
	 * Get the minimum probability
	 * @return minProb
	 */
	public double getMinProb() {
		return minProb;
	}

	/**
	 * Set the minimum probability
	 * @param minProb the new value of the minimum probability
	 */
	public void setMinProb(double minProb) {
		this.minProb = minProb;
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
			XYIntervalSeries newSeries = new XYIntervalSeries(seriesName);
			this.seriesCollection.addSeries(newSeries);
			// allocate a new cache for this series

			key = new SeriesKey();

			this.keyToSeries.put(key, newSeries);
			SeriesSettings graphSeries = new SeriesSettings(this, key);
			this.keyToGraphSeries.put(key, graphSeries);
			graphSeries.addObserver(this);

			this.seriesList.updateSeriesList();			
		}		

		return key;		
	}
	
	/**
	 * Creates a unique name of the series to avoid conflict with any existing series name
	 * @param seriesName the series name that has to be made unique
	 * @return the unique series name
	 */
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
			
			if (keyToGraphSeries.containsKey(seriesKey))
			{
				keyToGraphSeries.get(seriesKey).deleteObservers();				
				keyToGraphSeries.remove(seriesKey);
			}

			this.seriesList.updateSeriesList();	
		}

		seriesList.updateSeriesList();
	}
	

	/**
	 * Add a point to the specified graph series.
	 * @param seriesKey Key of series to update.
	 * @param dataItem XYDataItem object to insert into this series.
	 */
	public void addPointToSeries(SeriesKey seriesKey, XYIntervalDataItem dataItem) {

		synchronized (seriesCollection) {
			
			XYIntervalSeries series = keyToSeries.get(seriesKey);
			series.add(dataItem.getX(), dataItem.getXLowValue(), dataItem.getXHighValue(),
					dataItem.getYValue(), dataItem.getYLowValue(), dataItem.getYHighValue());

		}
	}
	
	/**
	 * Plots the series which is referenced by seriesKey.
	 * @param seriesKey the key to the series that has to be plotted
	 */
	public void plotSeries(SeriesKey seriesKey){
				
		double range = (maxProb - minProb) / (double)numOfBuckets; // Our range for which the histogram will be shown
		
		for(int i = 0 ; i < numOfBuckets ; i++){
			
			double minRange = minProb + (range*i);
			double maxRange = minRange + range;
			
			double height;
			
			if(i == (numOfBuckets-1)) // include the first value  
				height = countProbabilities(minRange, maxRange, true);
			else
				height = countProbabilities(minRange, maxRange, false);
				
			double x = (minRange + maxRange) / 2.0;
			
			addPointToSeries(seriesKey, new XYIntervalDataItem(x, minRange, maxRange, height, height, height));
			
			
			/**Only update the x axis tick marks if this is the first series which is being plotted*/
			if(isNew){
			
				if(i == 0)
					ticks.add(minRange);

				ticks.add(maxRange);
			}
			
		}
		
		/**Set the visible range of the plot*/
		plot.getDomainAxis().setRange(minProb-0.02, maxProb+0.02);
		
		/** clear the cache, we don't need it anymore*/
		dataCache.clear();
	}
	
	/**
	 * Counts the number of states that fall in the range (minRange,maxRange]
	 * 
	 * @param minRange the minimum range of the probability
	 * @param maxRange the maximum range of the probability
	 * @param includeLast should the last value be included? i.e, count the states in the range (minRange, maxRange)
	 * @return the number of states that fall in the specified range
	 */
	
	public int countProbabilities(double minRange, double maxRange, boolean includeLast){
		
		int count = 0;
		
		if(includeLast){
			maxRange += 1; // just increase the max range so that the the last value is included in the check
		}
		
		for(int i = 0 ; i < dataCache.size() ; i++){
			
			double prob = dataCache.get(i);
			
			if(prob >= minRange && prob < maxRange){
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Set the data Cache
	 * @param probs the cache
	 */
	public void addDataToCache(ArrayList<Double> probs){
		
		dataCache = probs;
		
	}
	
	/**
	 * Add custom tool tip for the Histogram to show more info
	 */
	public void addToolTip(){
		
		((ClusteredXYBarRenderer)plot.getRenderer()).setBaseToolTipGenerator(new XYToolTipGenerator() {
			
			@Override
			public String generateToolTip(XYDataset dataset, int seriesIndex, int item) {
				
				XYIntervalSeriesCollection collection = (XYIntervalSeriesCollection)dataset;
				XYIntervalSeries series = collection.getSeries(seriesIndex);
				
				double minX = series.getXLowValue(item);
				double maxX = series.getXHighValue(item);
				double height = series.getYValue(item);
				
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(String.format("<html><p style='color:#0000ff;'>Prop: '%s'</p>", dataset.getSeriesKey(seriesIndex)));
				stringBuilder.append("<table style=\"width:100%\">");
				stringBuilder.append("<tr><td> Min range: </td><td>" + (Math.round( minX * 10000.0 ) / 10000.0)  + "</td></tr>");
				stringBuilder.append("<tr><td> Max range: </td><td>" + (Math.round( maxX * 10000.0 ) / 10000.0) +  "</td></tr>");
				stringBuilder.append("<tr><td> Number of states: </td><td>" + height +  "</td></tr></table>");
				stringBuilder.append("</html>");
				
				return stringBuilder.toString();
			}
		});
		
	}
	
	/**
	 * Get whether this Histogram is new or some series have already been plotted on it
	 * @return
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * Set whether this Histogram is new or some series have already been plotted on it
	 * @param isNew the new value of isNew
	 */
	public void setIsNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * Generates the property dialog for a Histogram. Allows the user to select either a new or an exisitng Histogram
	 * to plot data on
	 * 
	 * @param defaultSeriesName
	 * @param handler instance of {@link GUIGraphHandler}
	 * @param minVal the min value in data cache
	 * @param maxVal the max value in data cache
	 * @return Either a new instance of a Histogram or an old one depending on what the user selects
	 */
	
	public static Pair<Histogram, SeriesKey> showPropertiesDialog(String defaultSeriesName, GUIGraphHandler handler, double minVal, double maxVal){
		
		// make sure that the probabilities are valid
		if(maxVal > 1.0)
			maxVal = 1.0; 
		if(minVal < 0.0)
			minVal = 0.0;
		
		// set properties for the dialog
		JDialog dialog = new JDialog(GUIPrism.getGUI(), "Histogram properties", true);
		dialog.setLayout(new BorderLayout());
		
		JPanel p1 = new JPanel(new FlowLayout());
		p1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Number of buckets"));
		
		JPanel p2 = new JPanel(new FlowLayout());
		p2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Series name"));
		
		
		JSpinner buckets = new JSpinner(new SpinnerNumberModel(10, 5, Integer.MAX_VALUE, 1));
		buckets.setToolTipText("Select the number of buckets for this Histogram");
		
		// provides the ability to select a new or an old histogram to plot the series on
		JTextField seriesName = new JTextField(defaultSeriesName);
		JRadioButton newSeries = new JRadioButton("New Histogram");
		JRadioButton existing = new JRadioButton("Existing Histogram");
		newSeries.setSelected(true);
		JPanel seriesSelectPanel = new JPanel();
		seriesSelectPanel.setLayout(new BoxLayout(seriesSelectPanel, BoxLayout.Y_AXIS));
		JPanel seriesTypeSelect = new JPanel(new FlowLayout());
		JPanel seriesOptionsPanel = new JPanel(new FlowLayout());
		seriesTypeSelect.add(newSeries);
		seriesTypeSelect.add(existing);
		JComboBox<String> seriesOptions = new JComboBox<>();
		seriesOptionsPanel.add(seriesOptions);
		seriesSelectPanel.add(seriesTypeSelect);
		seriesSelectPanel.add(seriesOptionsPanel);
		seriesSelectPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Add series to"));
		
		// provides ability to select the min/max range of the plot
		JLabel minValsLabel = new JLabel("Min range:");
		JSpinner minVals = new JSpinner(new SpinnerNumberModel(0.0, 0.0, minVal, 0.01));
		minVals.setToolTipText("Does not allow value more than the min value in the probabilities");
		JLabel maxValsLabel = new JLabel("Max range:");
		JSpinner maxVals = new JSpinner(new SpinnerNumberModel(1.0, maxVal, 1.0, 0.01));
		maxVals.setToolTipText("Does not allow value less than the max value in the probabilities");
		JPanel minMaxPanel = new JPanel();
		minMaxPanel.setLayout(new BoxLayout(minMaxPanel, BoxLayout.X_AXIS));
		JPanel leftValsPanel = new JPanel(new BorderLayout());
		JPanel rightValsPanel = new JPanel(new BorderLayout());
		minMaxPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Range"));
		leftValsPanel.add(minValsLabel, BorderLayout.WEST);
		leftValsPanel.add(minVals, BorderLayout.CENTER);
		rightValsPanel.add(maxValsLabel, BorderLayout.WEST);
		rightValsPanel.add(maxVals, BorderLayout.CENTER);
		minMaxPanel.add(leftValsPanel);
		minMaxPanel.add(rightValsPanel);
		
		
		// fill the old histograms in the property dialog
		
		boolean found = false;
		for(int i = 0 ; i < handler.getNumModels() ; i++){
			
			if(handler.getModel(i) instanceof Histogram){
				
				seriesOptions.addItem(handler.getGraphName(i));
				found = true;
			}
			
		}
		
		existing.setEnabled(found);
		seriesOptions.setEnabled(false);
		
		// the bottom panel
		JPanel options = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton ok = new JButton("Plot");
		JButton cancel = new JButton("Cancel");
		
		// bind keyboard keys to plot and cancel buttons to improve usability
		
		ok.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ok");
		ok.getActionMap().put("ok", new AbstractAction() {
			
			private static final long serialVersionUID = -7324877661936685228L;

			@Override
			public void actionPerformed(ActionEvent e) {
				
				ok.doClick();
				
			}
		});
		
		cancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ok");
		cancel.getActionMap().put("ok", new AbstractAction() {
			
			private static final long serialVersionUID = 2642213543774356676L;

			@Override
			public void actionPerformed(ActionEvent e) {
				
				cancel.doClick();
				
			}
		});
		
		
		//Action listener for the new series radio button
		newSeries.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(newSeries.isSelected()){
					
					existing.setSelected(false);
					seriesOptions.setEnabled(false);
					buckets.setEnabled(true);
					buckets.setToolTipText("Select the number of buckets for this Histogram");
					minVals.setEnabled(true);
					maxVals.setEnabled(true);
				}
			}
		});
		
		//Action listener for the existing series radio button
		existing.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(existing.isSelected()){
					
					newSeries.setSelected(false);
					seriesOptions.setEnabled(true);
					buckets.setEnabled(false);
					minVals.setEnabled(false);
					maxVals.setEnabled(false);
					buckets.setToolTipText("Number of buckets can't be changed on an existing Histogram");
				}
				
			}
		});
		
		//Action listener for the plot button
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				dialog.dispose();
				
				if(newSeries.isSelected()){
					
					hist = new Histogram();
					hist.setNumOfBuckets((int)buckets.getValue());
					hist.setIsNew(true);
					
				}
				else if(existing.isSelected()){
					
					String HistName = (String)seriesOptions.getSelectedItem();
					hist = (Histogram)handler.getModel(HistName);
					hist.setIsNew(false);
					
				}
				
				key = hist.addSeries(seriesName.getText());
				
				if(minVals.isEnabled() && maxVals.isEnabled()){
					
					hist.setMinProb((double)minVals.getValue());
					hist.setMaxProb((double) maxVals.getValue());
					
				}
			}
		});
		
		//Action listener for the cancel button
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				hist = null;
			}
		});
		
		dialog.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosed(WindowEvent e) {
		        
		    	hist = null;
		    }
		});
				
		p1.add(buckets, BorderLayout.CENTER);
		
		p2.add(seriesName, BorderLayout.CENTER);
		
	
		
		options.add(ok);
		options.add(cancel);
		
		// add everything to the main panel of the dialog
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(seriesSelectPanel);
		mainPanel.add(p1);
		mainPanel.add(p2);
		mainPanel.add(minMaxPanel);
		
		
		// add main panel to the dialog
		dialog.add(mainPanel, BorderLayout.CENTER);
		dialog.add(options, BorderLayout.SOUTH);
		
		// set dialog properties
		dialog.setSize(320, 290);
		dialog.setLocationRelativeTo(GUIPrism.getGUI());
		dialog.setVisible(true);
		
		// return the user selected Histogram with the properties set
		return new Pair<Histogram, SeriesKey>(hist, key);
	}
	
	
	
	/**
	 * return x axis settings
	 * @return
	 */
	public AxisSettingsHistogram getXAxisSettings() {
		return xAxisSettings;
	}
	
	/**
	 * return y axis settings
	 * @return
	 */

	public AxisSettingsHistogram getYAxisSettings() {
		return yAxisSettings;
	}

	/**
	 * return display settings
	 * @return
	 */
	public DisplaySettings getDisplaySettings() {
		return displaySettings;
	}

	/**
	 * return series settings
	 * @return
	 */
	public SeriesSettingsList getGraphSeriesList(){
		return seriesList;
	}
	
	/**
	 * returns series lock to be used in the synchronized block
	 * @return
	 */
	public XYIntervalSeriesCollection getSeriesLock(){
		return seriesCollection;
	}
	
	/**
	 * Get all the series keys corresponding to the series plotted in the Histogram
	 * @return
	 */
	public java.util.Vector<SeriesKey> getAllSeriesKeys()
	{
		synchronized (seriesCollection)
		{
			java.util.Vector<SeriesKey> result = new java.util.Vector<SeriesKey>();

			for (Map.Entry<SeriesKey, XYIntervalSeries> entries : keyToSeries.entrySet())
			{
				result.add(entries.getKey());
			}

			return result;
		}
	}
	
	/**
	 * Changes the name of a series.
	 * 
	 * @param key The key identifying the series.
	 * @param seriesName New name of series.
	 */
	public void changeSeriesName(SeriesKey key, String seriesName) 
	{
		synchronized (seriesCollection) 
		{
			seriesName = getUniqueSeriesName(seriesName);

			if (keyToSeries.containsKey(key))
			{
				XYIntervalSeries series = keyToSeries.get(key);
				series.setKey(seriesName);
			}			
		}	
	}

	/**
	 * Should always be synchronised on seriesCollection when called.
	 */
	public SeriesSettings getGraphSeries(SeriesKey key)
	{
		synchronized (seriesCollection)
		{
			if (keyToGraphSeries.containsKey(key))
			{
				return keyToGraphSeries.get(key);
			}

			return null;
		}
	}
	
	/**
	 * Should always be synchronised on seriesCollection when called.
	 * @return >0 when series found.
	 */
	public int getJFreeChartIndex(SeriesKey key)
	{
		synchronized (seriesCollection) 
		{
			XYIntervalSeries series = keyToSeries.get(key);

			for (int i = 0; i < seriesCollection.getSeriesCount(); i++)
			{
				if (seriesCollection.getSeries(i).equals((series)))
					return i;
			}

			return -1;
		}
	}
	
	/**
	 * Should always be synchronised on seriesCollection when called.
	 */
	public XYIntervalSeries getXYSeries(SeriesKey key)
	{
		synchronized (seriesCollection)
		{
			if (keyToSeries.containsKey(key))
			{
				return keyToSeries.get(key);
			}

			return null;
		}
	}

	@Override
	public int compareTo(Object o) {
		
		if (o instanceof SettingOwner) {
			SettingOwner po = (SettingOwner) o;
			if (getSettingOwnerID() < po.getSettingOwnerID())
				return -1;
			else if (getSettingOwnerID() > po.getSettingOwnerID())
				return 1;
			else
				return 0;
		} else
			return 0;
	}

	@Override
	public int getSettingOwnerID() {
		return prism.PropertyConstants.MODEL;
	}

	@Override
	public String getSettingOwnerName() {
		return graphTitle.getStringValue();
	}

	@Override
	public String getSettingOwnerClassName() {
		return "Model";
	}

	@Override
	public int getNumSettings() {
		return 5;
	}

	@Override
	public Setting getSetting(int index) {
		switch (index) {
		case 0:
			return graphTitle;
		case 1:
			return titleFont;
		case 2:
			return legendVisible;
		case 3:
			return legendPosition;
		case 4:
			return legendFont;
		default:
			return null;
		}
	}

	@Override
	public void notifySettingChanged(Setting setting) {
		updateGraph();
	}

	@Override
	public SettingDisplay getDisplay() {
		return display;
	}

	@Override
	public void setDisplay(SettingDisplay display) {
		this.display = display;
		
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == xAxisSettings) {
			/* X axis changed */
			super.repaint();
		} else if (o == yAxisSettings) {
			/* Y axis changed */
			super.repaint();
		} else if (o == displaySettings) {
			/* Display settings changed */
			super.repaint();
		} else {
			for (Map.Entry<SeriesKey, SeriesSettings> entry : keyToGraphSeries.entrySet())
			{
				/* Graph series settings changed */
				if (entry.getValue().equals(o))
					repaint();
			}
		}
	}
	
	/**
	 * Getter for property graphTitle.
	 * @return Value of property graphTitle.
	 */
	public String getTitle()
	{
		return graphTitle.getStringValue();
	}

	/**
	 * Setter for property graphTitle.
	 * @param value Value of property graphTitle.
	 */
	public void setTitle(String value)
	{
		try
		{
			graphTitle.setValue(value);
			doEnables();
			updateGraph();
		}
		catch (SettingException e)
		{
			// Shouldn't happen.
		}
	}
	
	/**
	 * Getter for property logarithmic.
	 * @return the legend's position index:
	 * <ul>
	 *	<li>0: LEFT
	 *	<li>1: RIGHT
	 *	<li>2: BOTTOM
	 *  <li>3: TOP
	 * </ul>
	 */
	public int getLegendPosition()
	{
		return legendPosition.getCurrentIndex();
	}

	/**
	 * Setter for property logarithmic.
	 * @param value Represents legend position
	 * <ul>
	 *	<li>0: LEFT
	 *	<li>1: RIGHT
	 *	<li>2: BOTTOM
	 *	<li>4: TOP
	 * </ul>
	 */
	public void setLegendPosition(int value) throws SettingException
	{		
		legendPosition.setSelectedIndex(value);
		doEnables();
		updateGraph();
	}
	
	/**
	 * Update the settings of the graph if the settings changed
	 */
	public void updateGraph(){
	
		/* Update title if necessary. */
		if (!this.chart.getTitle().equals(graphTitle)) {
			this.chart.setTitle(graphTitle.getStringValue());
		}
		
		/* Update title font if necessary. */
		if (!titleFont.getFontColorValue().f.equals(this.chart.getTitle()
				.getFont())) {
			this.chart.getTitle().setFont(titleFont.getFontColorValue().f);
		}

		/* Update title colour if necessary. */
		if (!titleFont.getFontColorValue().c.equals(this.chart.getTitle()
				.getPaint())) {
			this.chart.getTitle().setPaint(titleFont.getFontColorValue().c);
		}

		if (legendVisible.getBooleanValue() != (this.chart.getLegend() != null)) {
			if (!legendVisible.getBooleanValue()) {
				this.chart.removeLegend();
			} else {
				LegendTitle legend = new LegendTitle(plot.getRenderer());
				legend.setBackgroundPaint(Color.white);
				legend.setBorder(1, 1, 1, 1);

				this.chart.addLegend(legend);
			}
		}
		
		if (this.chart.getLegend() != null) {
			LegendTitle legend = this.chart.getLegend();

			/* Put legend on the left if appropriate. */
			if ((legendPosition.getCurrentIndex() == Graph.LEFT)
					&& !legend.getPosition().equals(RectangleEdge.LEFT)) {
				legend.setPosition(RectangleEdge.LEFT);
			}
			/* Put legend on the right if appropriate. */
			if ((legendPosition.getCurrentIndex() == Graph.RIGHT)
					&& !legend.getPosition().equals(RectangleEdge.RIGHT)) {
				legend.setPosition(RectangleEdge.RIGHT);
			}
			/* Put legend on the top if appropriate. */
			if ((legendPosition.getCurrentIndex() == Graph.TOP)
					&& !legend.getPosition().equals(RectangleEdge.TOP)) {
				legend.setPosition(RectangleEdge.TOP);
			}
			/* Put legend on the bottom if appropriate. */
			if ((legendPosition.getCurrentIndex() == Graph.BOTTOM)
					&& !legend.getPosition().equals(RectangleEdge.BOTTOM)) {
				legend.setPosition(RectangleEdge.BOTTOM);
			}

			/* Set legend font. */
			if (!legend.getItemFont().equals(legendFont.getFontColorValue().f)) {
				legend.setItemFont(legendFont.getFontColorValue().f);
			}
			/* Set legend font colour. */
			if (!legend.getItemPaint().equals(legendFont.getFontColorValue().c)) {
				legend.setItemPaint(legendFont.getFontColorValue().c);
			}
		}

		super.repaint();
		doEnables();
	}
	
	/**
	 * Enable / disable some settings
	 */
	public void doEnables() {
		legendPosition.setEnabled(legendVisible.getBooleanValue());
		legendFont.setEnabled(legendVisible.getBooleanValue());

	}
	
	public void exportToGnuplot(File file) throws IOException{
		
		PrintWriter out = new PrintWriter(new FileWriter(file));
		
		out.println("#=========================================");
		out.println("# Generated by PRISM Chart Package");
		out.println("#=========================================");
		out.println("# usage: gnuplot <filename>");
		out.println("# Written by Muhammad Omer Saeed <muhammad.omar555@gmail.com>");
		
		out.println();
		
		out.println("set xtics rotate out");
		out.println("set auto x");
		out.println("set yrange " + "[0:" +getChart().getXYPlot().getRangeAxis().getRange().getUpperBound()*1.2 + "]");
		out.println("set style data histogram");
		out.println("set style fill solid border");
		out.println("set style histogram clustered");
		out.println("set boxwidth 3");
		
		
		synchronized (getSeriesLock()) {
			
			for(int i = 0 ; i < getChart().getXYPlot().getSeriesCount() ; i++){
				
				if(i==0)
					out.print("plot '-' using 2:xticlabels(1)");
				else
					out.print(", '-' using 2:xticlabels(1)");
			}
			
			out.println();
			out.println();
			
			for(int i = 0 ; i < getAllSeriesKeys().size() ; i++){
				
				XYIntervalSeries series = keyToSeries.get(getAllSeriesKeys().get(i));
				
				out.println("max	" + series.getKey());
				
				for(int j = 0 ; j < series.getItemCount() ; j++){

					XYIntervalDataItem item = (XYIntervalDataItem)series.getDataItem(j);
					
					double x = item.getXHighValue();
					x = x*100;
					x = Math.round(x);
					x = x /100;

					out.println( x + "	" + item.getYValue());

				}
				
				out.println("end series");
				out.println();
			}
	
		}
		
		out.println();
		out.println("pause -1");
		
		out.flush();
		out.close();
	}
}
