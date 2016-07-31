//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Andrew Hinton <ug60axh@cs.bham.ac.uk> (University of Birmingham)
//	* Dave Parker <david.parker@comlab.ox.ac.uk> (University of Oxford, formerly University of Birmingham)
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

package userinterface.properties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartPanel;

import parser.Values;
import parser.type.TypeInterval;
import prism.DefinedConstant;
import prism.Interval;
import prism.PrismException;
import prism.ResultsCollection;
import userinterface.GUIPlugin;
import userinterface.GUIPrism;
import userinterface.graph.Graph;
import userinterface.graph.Graph3D;
import userinterface.graph.GraphResultListener;
import userinterface.graph.GraphResultListener3D;
import userinterface.graph.ParametricGraph;
import userinterface.graph.PrismXYDataItem;
import userinterface.graph.SeriesKey;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUIGraphPicker extends javax.swing.JDialog
{
	private GUIPrism gui;
	private GUIPlugin plugin;

	private GUIExperiment experiment;
	private GUIGraphHandler graphHandler;
	private ResultsCollection resultsCollection;

	private GraphConstantPickerList pickerList;

	private String rangerX, rangerY;
	private DefinedConstant rangingConstantX, rangingConstantY;

	private Values otherValues;
	private Vector<DefinedConstant> multiSeries;

	//used in case 2d plot is selected
	private userinterface.graph.Graph graphModel2D;
	
	// used in case 3d plot is selected
	private Graph3D graphModel3D;
	
	private boolean graphCancelled;

	private static final int MAX_NUM_SERIES_BEFORE_QUERY = 11;

	
	/** Creates new form GUIGraphPicker 
	 * 	@wbp.parser.constructor
	 * @param parent The parent.
	 * @param plugin The GUIPlugin (GUIMultiProperties)
	 * @param experiment The experiment for which to plot a graph.
	 * @param graphHandler The graph handler in which to display the graph.
	 * @param resultsKnown If true, simply plot existing results (experiment has been done). 
	 * If false, attach listeners to the results such that plot is made when results become available.
	 */

	public GUIGraphPicker(GUIPrism parent, GUIPlugin plugin, GUIExperiment experiment, GUIGraphHandler graphHandler, boolean resultsKnown)
	{
		super(parent, true);
		setTitle("New Graph Series");

		this.gui = parent;
		this.plugin = plugin;

		this.experiment = experiment;
		this.graphHandler = graphHandler;
		this.resultsCollection = experiment.getResults();

		// graphCancelled will be set explicitly to false when the OK button is pressed
		// (this means if the user closes the dialog, this counts as a cancel)
		this.graphCancelled = true;

		this.multiSeries = new Vector<DefinedConstant>();

		initComponents();
		setResizable(false);

		init();
		setLocationRelativeTo(getParent()); // centre
		getRootPane().setDefaultButton(lineOkayButton);

		/* Wait untill OK or Cancel is pressed. */
		setVisible(true);

		/* If OK was pressed. */
		if (!graphCancelled && this.plotType2d.isSelected()) {
			/* Collect series keys. */
			Vector<SeriesKey> seriesKeys = new Vector<SeriesKey>();

			/* Collect series Values */
			ArrayList<Values> seriesValues = new ArrayList<Values>();

			/* Add single constant values to each serie */
			seriesValues.add(otherValues);

			for (int i = 0; i < multiSeries.size(); i++) {
				ArrayList<Values> temp = (ArrayList<Values>) seriesValues.clone();
				seriesValues.clear();

				// For each of the possible value in the range
				for (int j = 0; j < multiSeries.get(i).getNumSteps(); j++) {
					// Clone the list
					ArrayList copy = (ArrayList<Values>) temp.clone();

					// For each element in the list
					for (int k = 0; k < copy.size(); k++) {
						Values v = new Values();
						Values cp = (Values) copy.get(k);
						v.addValues(cp);
						v.addValue(multiSeries.get(i).getName(), multiSeries.get(i).getValue(j));
						seriesValues.add(v);
					}
				}
			}

			/* Do all series settings. */
			for (int serie = 0; serie < seriesValues.size(); serie++) //each combination of series
			{
				Values values = seriesValues.get(serie);
				String seriesName = (seriesValues.size() > 1) ? values.toString() : seriesNameField.getText();
				// For properties that return an interval, we add a pair of series
				// (the pair is stored as a linked list)
				if (experiment.getPropertyType() instanceof TypeInterval) {
					SeriesKey key = graphModel2D.addSeries(seriesName + " (min)");
					key.next = graphModel2D.addSeries(seriesName + " (max)");
					seriesKeys.add(key);
				} else {
					seriesKeys.add(graphModel2D.addSeries(seriesName));
				}
			}

			/* If there are results already, then lets render them! */
			if (resultsKnown && resultsCollection.getCurrentIteration() > 0) {
				for (int series = 0; series < seriesValues.size(); series++) //each combination of series
				{
					Values values = seriesValues.get(series);
					SeriesKey seriesKey = seriesKeys.get(series);

					/** Range over x-axis. */
					for (int i = 0; i < rangingConstantX.getNumSteps(); i++) {
						Object value = rangingConstantX.getValue(i);

						/** Values used in the one experiment for this series. */
						Values useThis = new Values();
						useThis.addValues(values);
						useThis.addValue(rangerX, value);

						/** Get this particular result. **/
						try {
							Object result = resultsCollection.getResult(useThis);

							double x = 0, y = 0;
							boolean validX = true;

							if (value instanceof Double) {
								x = ((Double) value).doubleValue();
							} else if (value instanceof Integer) {
								x = ((Integer) value).intValue();
							} else {
								validX = false;
							}

							// Add point to graph (if of valid type)
							if (validX) {
								if (result instanceof Double) {
									y = ((Double) result).doubleValue();
									graphModel2D.addPointToSeries(seriesKey, new PrismXYDataItem(x, y));
								} else if (result instanceof Integer) {
									y = ((Integer) result).intValue();
									graphModel2D.addPointToSeries(seriesKey, new PrismXYDataItem(x, y));
								} else if (result instanceof Interval) {
									Interval interval = (Interval) result;
									if (interval.lower instanceof Double) {
										y = ((Double) interval.lower).doubleValue();
										graphModel2D.addPointToSeries(seriesKey, new PrismXYDataItem(x, y));
										y = ((Double) interval.upper).doubleValue();
										graphModel2D.addPointToSeries(seriesKey.next, new PrismXYDataItem(x, y));
									} else if (result instanceof Integer) {
										y = ((Integer) interval.lower).intValue();
										graphModel2D.addPointToSeries(seriesKey, new PrismXYDataItem(x, y));
										y = ((Integer) interval.upper).intValue();
										graphModel2D.addPointToSeries(seriesKey.next, new PrismXYDataItem(x, y));
									}
								}
							}
						} catch (PrismException pe) {
							// No result found. 
						}
					}
				}
			} else if (!resultsKnown && resultsCollection.getCurrentIteration() == 0) {
				for (int series = 0; series < seriesValues.size(); series++) //each combination of series
				{
					Values values = seriesValues.get(series);
					SeriesKey seriesKey = seriesKeys.get(series);

					GraphResultListener listener = new GraphResultListener(graphModel2D, seriesKey, rangerX, values);
					resultsCollection.addResultListener(listener);
				}
			}
		}
		else if(!graphCancelled && this.plotType3d.isSelected()){
			
			graphModel3D.setAxisLabels(selectAxisConstantCombo.getSelectedItem().toString(),
					selectYaxisConstantCombo.getSelectedItem().toString(), "Result");
			GraphResultListener3D listener = new GraphResultListener3D(graphModel3D, rangerX, rangerY, seriesNameField.getText());
			resultsCollection.addResultListener(listener);
			
		}
	}
	
	public GUIGraphPicker(GUIPrism parent, GUIPlugin plugin, GUIGraphHandler graphHandler) {
		
		super(parent, true);
		setTitle("New Graph Series");

		this.gui = parent;
		this.plugin = plugin;

		this.experiment = null;
		this.graphHandler = graphHandler;
		this.resultsCollection = null;

		// graphCancelled will be set explicitly to false when the OK button is pressed
		// (this means if the user closes the dialog, this counts as a cancel)
		this.graphCancelled = true;

		this.multiSeries = new Vector<DefinedConstant>();

		initComponents();
		setResizable(false);

		initParametric();
		setLocationRelativeTo(getParent()); // centre
		getRootPane().setDefaultButton(lineOkayButton);

		/* Wait untill OK or Cancel is pressed. */
		setVisible(true);
	}
	
	private void initParametric()
	{
		setTitle("Graph options");
		this.selectAxisConstantCombo.setEnabled(false);
		this.seriesNameField.setEnabled(false);
		this.seriesNameField.setBackground(this.getBackground());

		// default graph option is "new graph"
		this.newGraphRadio.setSelected(true);

		// add existing graphs to choose from
		for (int i = 0; i < graphHandler.getNumModels(); i++) {

			if(graphHandler.getModel(i) instanceof ParametricGraph)
				existingGraphCombo.addItem(graphHandler.getGraphName(i));

		}
		// default to latest one
		if (existingGraphCombo.getItemCount() > 0) {
			existingGraphCombo.setSelectedIndex(existingGraphCombo.getItemCount() - 1);
		}
		// if there are no graphs, disable control
		else {
			existingGraphCombo.setEnabled(false);
			this.existingGraphRadio.setEnabled(false);
		}

		// create a default series name
		resetAutoSeriesName();

		// other enables/disables
		doEnables();

		pack();
		
	}

	/** According to what is stored in 'rc', set up the table to pick the constants
	 */
	private void init()
	{
		// set up "define other constants" table
		// create header
		GraphConstantHeader header = new GraphConstantHeader();
		constantTablePanel.add(header, BorderLayout.NORTH);
		// create scroller
		JScrollPane scroller = new JScrollPane();
		constantTablePanel.add(scroller, BorderLayout.CENTER);
		// create picker list
		pickerList = new GraphConstantPickerList();
		scroller.setViewportView(pickerList);
		
		// determine if 3d charts can be plotted or not
		
		plotType2d.setSelected(true);
		selectYaxisConstantCombo.setEnabled(false);
		
		if(resultsCollection.getRangingConstants().size() == 1){
		
			plotType3d.setEnabled(false);
			
		}

		// for each ranging constant in rc, add:
		// (1) a row in the picker list
		// (2) an item in the "x axis" drop down menu
		for (int i = 0; i < resultsCollection.getRangingConstants().size(); i++) {
			DefinedConstant dc = (DefinedConstant) resultsCollection.getRangingConstants().get(i);
			pickerList.addConstant(new GraphConstantLine(dc, this));
			this.selectAxisConstantCombo.addItem(dc.getName());
		}

		// select the default constant for the x axis
		// (first property if there is one, if not first model one)
		if (selectAxisConstantCombo.getItemCount() > 0) {
			if (resultsCollection.getNumPropertyRangingConstants() > 0)
				selectAxisConstantCombo.setSelectedIndex(resultsCollection.getNumModelRangingConstants());
			else
				selectAxisConstantCombo.setSelectedIndex(0);
		}

		
		// now check if the second axis can be selected or not
		
		if(resultsCollection.getRangingConstants().size() == 1 )
		{
			selectYaxisConstantCombo.setEnabled(false);
		}
		else{
			
			for (int i = 0; i < resultsCollection.getRangingConstants().size(); i++) {
				DefinedConstant dc = (DefinedConstant) resultsCollection.getRangingConstants().get(i);
				this.selectYaxisConstantCombo.addItem(dc.getName());
			}
			
			this.selectYaxisConstantCombo.setSelectedIndex(1);
		}
		
		// and disable it in the picker list
		pickerList.disableLine(0);
		
		// if there is only one ranging constant, disable controls
		if (resultsCollection.getRangingConstants().size() == 1) {
			selectAxisConstantCombo.setEnabled(false);
			pickerList.setEnabled(false);
			header.setEnabled(false);
			this.middleLabel.setEnabled(false);
			this.topComboLabel.setEnabled(false);
		}

		// default graph option is "new graph"
		this.newGraphRadio.setSelected(true);

		// add existing graphs to choose from
		for (int i = 0; i < graphHandler.getNumModels(); i++) {
			
			if(graphHandler.getModel(i) instanceof Graph )
				existingGraphCombo.addItem(graphHandler.getGraphName(i));
			
		}
		// default to latest one
		if (existingGraphCombo.getItemCount() > 0) {
			existingGraphCombo.setSelectedIndex(existingGraphCombo.getItemCount() - 1);
		}
		// if there are no graphs, disable control
		else {
			existingGraphCombo.setEnabled(false);
			this.existingGraphRadio.setEnabled(false);
		}

		// create a default series name
		resetAutoSeriesName();

		// other enables/disables
		doEnables();

		pack();
	}

	public void doEnables()
	{
		this.existingGraphCombo.setEnabled(this.existingGraphRadio.isSelected());
	}

	// create a default series name
	public void resetAutoSeriesName()
	{
		DefinedConstant temp;
		Object value;

		if (selectAxisConstantCombo.getSelectedItem() == null) {
			return;
		}

		// see which constant is on x axis
		rangerX = selectAxisConstantCombo.getSelectedItem().toString();
		// init arrays
		otherValues = new Values();
		multiSeries = new Vector<DefinedConstant>();
		// go through constants in picker list
		for (int j = 0; j < pickerList.getNumConstants(); j++) {
			// get constant
			temp = pickerList.getConstantLine(j).getDC();
			// ignore constant for x-axis
			if (temp.getName().equals(rangerX))
				continue;
			// get value
			value = pickerList.getConstantLine(j).getSelectedValue();
			// if we find any constants selected "All Series", clear name, disable and bail out
			if (value instanceof String) {
				this.seriesNameLabel.setEnabled(false);
				this.seriesNameField.setText("");
				this.seriesNameField.setEnabled(false);
				return;
			}
			// we add other constants to a list
			else {
				otherValues.addValue(temp.getName(), value);
			}
		}
		// use values object string for name
		if (otherValues.getNumValues() != 0) {
			this.seriesNameField.setText(otherValues.toString());
		} else {
			this.seriesNameField.setText("New Series");
		}
		this.seriesNameLabel.setEnabled(true);
		this.seriesNameField.setEnabled(true);
	}
	
	public ParametricGraph getGraphModel(){
		return (ParametricGraph)graphModel2D;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()
	{
		java.awt.GridBagConstraints gridBagConstraints;

		buttonGroup1 = new javax.swing.ButtonGroup();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel1 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		jPanel5 = new javax.swing.JPanel();
		topComboLabel = new javax.swing.JLabel();
		jPanel6 = new javax.swing.JPanel();
		selectAxisConstantCombo = new javax.swing.JComboBox();
		jPanel7 = new javax.swing.JPanel();
		middleLabel = new javax.swing.JLabel();
		constantTablePanel = new javax.swing.JPanel();
		jPanel9 = new javax.swing.JPanel();
		jPanel10 = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		newGraphRadio = new javax.swing.JRadioButton();
		existingGraphRadio = new javax.swing.JRadioButton();
		jPanel11 = new javax.swing.JPanel();
		existingGraphCombo = new javax.swing.JComboBox();
		jPanel12 = new javax.swing.JPanel();
		seriesNameLabel = new javax.swing.JLabel();
		seriesNameField = new javax.swing.JTextField();
		jPanel4 = new javax.swing.JPanel();
		lineOkayButton = new javax.swing.JButton();
		lineCancelButton = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();

		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				closeDialog(evt);
			}
		});

		jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
		jPanel1.setLayout(new java.awt.BorderLayout());

		jPanel1.setBorder(new javax.swing.border.TitledBorder("Line Graph"));
		jPanel1.setFocusable(false);
		jPanel1.setEnabled(false);
		GridBagLayout gbl_jPanel3 = new GridBagLayout();
		gbl_jPanel3.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_jPanel3.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0};
		jPanel3.setLayout(gbl_jPanel3);
		
		lblPlotType = new JLabel("Plot type:");
		GridBagConstraints gbc_lblPlotType = new GridBagConstraints();
		gbc_lblPlotType.anchor = GridBagConstraints.WEST;
		gbc_lblPlotType.insets = new Insets(0, 0, 5, 5);
		gbc_lblPlotType.gridx = 1;
		gbc_lblPlotType.gridy = 0;
		jPanel3.add(lblPlotType, gbc_lblPlotType);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.WEST;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 3;
		gbc_panel.gridy = 0;
		jPanel3.add(panel, gbc_panel);
		
		plotType2d = new JRadioButton("2D");
		plotType2d.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				plotType2DRadioActionPerformed(e);
				
			}
		});
		panel.add(plotType2d);
		
		plotType3d = new JRadioButton("3D");
		plotType3d.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotType3DRadioActionPerformed(e);
			}
		});
		panel.add(plotType3d);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		jPanel3.add(jPanel5, gridBagConstraints);

		topComboLabel.setText("Select x axis constant:");
		gridBagConstraints_1 = new java.awt.GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_1.gridx = 1;
		gridBagConstraints_1.gridy = 2;
		gridBagConstraints_1.anchor = java.awt.GridBagConstraints.WEST;
		jPanel3.add(topComboLabel, gridBagConstraints_1);

		gridBagConstraints_2 = new java.awt.GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_2.gridx = 2;
		gridBagConstraints_2.gridy = 1;
		jPanel3.add(jPanel6, gridBagConstraints_2);

		selectAxisConstantCombo.setPreferredSize(new java.awt.Dimension(100, 24));
		selectAxisConstantCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				selectAxisConstantComboActionPerformed(evt);
			}
		});

		gridBagConstraints_3 = new java.awt.GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_3.gridx = 3;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.fill = java.awt.GridBagConstraints.HORIZONTAL;
		jPanel3.add(selectAxisConstantCombo, gridBagConstraints_3);

		gridBagConstraints_4 = new java.awt.GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 3;
		jPanel3.add(jPanel7, gridBagConstraints_4);
		
		lblSelectYAxis = new JLabel("Select y axis constant:");
		GridBagConstraints gbc_lblSelectYAxis = new GridBagConstraints();
		gbc_lblSelectYAxis.anchor = GridBagConstraints.WEST;
		gbc_lblSelectYAxis.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectYAxis.gridx = 1;
		gbc_lblSelectYAxis.gridy = 4;
		jPanel3.add(lblSelectYAxis, gbc_lblSelectYAxis);
		
		selectYaxisConstantCombo = new JComboBox();
		selectYaxisConstantCombo.setPreferredSize(new java.awt.Dimension(100, 24));
		selectYaxisConstantCombo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectYAxisConstantComboActionPerformed(e);
				
			}
		});
		GridBagConstraints gbc_selectYaxisConstantCombo = new GridBagConstraints();
		gbc_selectYaxisConstantCombo.insets = new Insets(0, 0, 5, 5);
		gbc_selectYaxisConstantCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_selectYaxisConstantCombo.gridx = 3;
		gbc_selectYaxisConstantCombo.gridy = 4;
		jPanel3.add(selectYaxisConstantCombo, gbc_selectYaxisConstantCombo);
		
		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 5;
		jPanel3.add(panel_1, gbc_panel_1);

		middleLabel.setText("Define other constants:");
		gridBagConstraints_5 = new java.awt.GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_5.gridx = 1;
		gridBagConstraints_5.gridy = 6;
		gridBagConstraints_5.anchor = java.awt.GridBagConstraints.WEST;
		jPanel3.add(middleLabel, gridBagConstraints_5);

		constantTablePanel.setLayout(new java.awt.BorderLayout());

		gridBagConstraints_6 = new java.awt.GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_6.gridx = 3;
		gridBagConstraints_6.gridy = 6;
		gridBagConstraints_6.gridwidth = 3;
		gridBagConstraints_6.gridheight = 2;
		gridBagConstraints_6.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints_6.weightx = 1.0;
		gridBagConstraints_6.weighty = 1.0;
		jPanel3.add(constantTablePanel, gridBagConstraints_6);

		gridBagConstraints_7 = new java.awt.GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_7.gridx = 6;
		gridBagConstraints_7.gridy = 1;
		jPanel3.add(jPanel9, gridBagConstraints_7);

		gridBagConstraints_8 = new java.awt.GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_8.gridx = 0;
		gridBagConstraints_8.gridy = 8;
		jPanel3.add(jPanel10, gridBagConstraints_8);

		jLabel3.setText("Add Series to:");
		gridBagConstraints_9 = new java.awt.GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_9.gridx = 1;
		gridBagConstraints_9.gridy = 9;
		gridBagConstraints_9.anchor = java.awt.GridBagConstraints.WEST;
		jPanel3.add(jLabel3, gridBagConstraints_9);

		newGraphRadio.setText("New Graph");
		buttonGroup1.add(newGraphRadio);
		newGraphRadio.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				newGraphRadioActionPerformed(evt);
			}
		});

		gridBagConstraints_10 = new java.awt.GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_10.gridx = 3;
		gridBagConstraints_10.gridy = 9;
		gridBagConstraints_10.anchor = java.awt.GridBagConstraints.WEST;
		jPanel3.add(newGraphRadio, gridBagConstraints_10);

		existingGraphRadio.setText("Existing Graph");
		buttonGroup1.add(existingGraphRadio);
		existingGraphRadio.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				existingGraphRadioActionPerformed(evt);
			}
		});

		gridBagConstraints_11 = new java.awt.GridBagConstraints();
		gridBagConstraints_11.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_11.gridx = 3;
		gridBagConstraints_11.gridy = 10;
		gridBagConstraints_11.anchor = java.awt.GridBagConstraints.WEST;
		jPanel3.add(existingGraphRadio, gridBagConstraints_11);

		gridBagConstraints_12 = new java.awt.GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_12.gridx = 4;
		gridBagConstraints_12.gridy = 1;
		jPanel3.add(jPanel11, gridBagConstraints_12);

		gridBagConstraints_13 = new java.awt.GridBagConstraints();
		gridBagConstraints_13.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_13.gridx = 5;
		gridBagConstraints_13.gridy = 10;
		gridBagConstraints_13.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints_13.anchor = java.awt.GridBagConstraints.WEST;
		jPanel3.add(existingGraphCombo, gridBagConstraints_13);

		gridBagConstraints_14 = new java.awt.GridBagConstraints();
		gridBagConstraints_14.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints_14.gridx = 0;
		gridBagConstraints_14.gridy = 11;
		jPanel3.add(jPanel12, gridBagConstraints_14);

		seriesNameLabel.setText("Series name:");
		gridBagConstraints_15 = new java.awt.GridBagConstraints();
		gridBagConstraints_15.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints_15.gridx = 1;
		gridBagConstraints_15.gridy = 12;
		gridBagConstraints_15.anchor = java.awt.GridBagConstraints.WEST;
		jPanel3.add(seriesNameLabel, gridBagConstraints_15);

		gridBagConstraints_16 = new java.awt.GridBagConstraints();
		gridBagConstraints_16.insets = new Insets(0, 0, 0, 5);
		gridBagConstraints_16.gridx = 3;
		gridBagConstraints_16.gridy = 12;
		gridBagConstraints_16.gridwidth = 3;
		gridBagConstraints_16.fill = java.awt.GridBagConstraints.HORIZONTAL;
		jPanel3.add(seriesNameField, gridBagConstraints_16);

		jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

		jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

		lineOkayButton.setText("Okay");
		lineOkayButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				lineOkayButtonActionPerformed(evt);
			}
		});

		jPanel4.add(lineOkayButton);

		lineCancelButton.setText("Cancel");
		lineCancelButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				lineCancelButtonActionPerformed(evt);
			}
		});

		jPanel4.add(lineCancelButton);

		jPanel1.add(jPanel4, java.awt.BorderLayout.SOUTH);

		//jTabbedPane1.addTab("", GUIPrism.getIconFromImage("lineGraph.png"), jPanel1);

		jPanel2.setBorder(new javax.swing.border.TitledBorder("Bar Graph"));
		jPanel2.setEnabled(false);
		//jTabbedPane1.addTab("", GUIPrism.getIconFromImage("barGraph.png"), jPanel2);

		getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

		pack();
	}

	public boolean isGraphCancelled()
	{
		return graphCancelled;
	}
	
	private void plotType2DRadioActionPerformed(java.awt.event.ActionEvent evt){
		
		this.plotType3d.setSelected(false);
		this.lblSelectYAxis.setEnabled(false);
		this.selectYaxisConstantCombo.setEnabled(false);
		
		if(this.graphHandler.getNumModels() > 0){
			this.existingGraphCombo.setEnabled(true);
			this.existingGraphCombo.setEnabled(true);
		}
		
		if(!this.plotType3d.isEnabled()){
			this.plotType2d.setSelected(true);
		}
		
		pickerList.disableLine(selectAxisConstantCombo.getSelectedIndex());
		seriesNameField.setText("");
	}
	
	private void plotType3DRadioActionPerformed(java.awt.event.ActionEvent evt){
		
		this.plotType2d.setSelected(false);
		this.existingGraphCombo.setEnabled(false);
		this.existingGraphRadio.setEnabled(false);
		this.newGraphRadio.setSelected(true);
		this.selectYaxisConstantCombo.setEnabled(true);
		this.lblSelectYAxis.setEnabled(true);
		
		pickerList.disableLines(selectAxisConstantCombo.getSelectedItem().toString(),
								selectYaxisConstantCombo.getSelectedItem().toString());
		
		seriesNameField.setText(pickerList.getDisableConstantsInfo());
	}

	private void lineCancelButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		graphCancelled = true;
		setVisible(false);
	}

	private void lineOkayButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		
		if(!this.plotType2d.isSelected() && !this.plotType3d.isSelected()){
			this.plugin.error("Please select a plot type!");
			return;
		}
		
		// this is for the parametric case, getselecteditem will be null then
		if(selectAxisConstantCombo.getSelectedItem() == null){
			
			if (newGraphRadio.isSelected()) {
				/* Make new graph. */
				graphModel2D = new ParametricGraph("");
				graphHandler.addGraph(graphModel2D);
				
			} else {
				/* Add to an existing graph. */
				
				if(!(graphHandler.getModel(existingGraphCombo.getSelectedItem().toString()) instanceof ParametricGraph)){
					graphModel2D = null;
					return;
				}
				
				graphModel2D = (Graph)graphHandler.getModel(existingGraphCombo.getSelectedItem().toString());
			}
			
			graphCancelled = false;
			setVisible(false);
		
			return;
		}
		
		
		int numSeries = 1;

		// see which constant is on x axis
		rangerX = selectAxisConstantCombo.getSelectedItem().toString();
		
		if(this.plotType3d.isSelected()){
			
			rangerY = selectYaxisConstantCombo.getSelectedItem().toString();
			
		}

		// init arrays
		otherValues = new Values();
		multiSeries = new Vector<DefinedConstant>();

		// go through all constants in picker list
		for (int j = 0; j < pickerList.getNumConstants(); j++) {
			// get constant
			DefinedConstant tmpConstant = pickerList.getConstantLine(j).getDC();
			// if its the constant for the x-axis, store info about the constant
			if (tmpConstant.getName().equals(rangerX)) {
				rangingConstantX = tmpConstant;
			}
			// otherwise store info about the selected values
			else {
				
				if(this.plotType3d.isSelected()){
					if(tmpConstant.getName().equals(rangerY))
						continue;
				}
				
				// Is this constant just a value, or does it have a range?
				Object value = pickerList.getConstantLine(j).getSelectedValue();
				if (value instanceof String) {
					/* Yes, calculate the numSeries. */
					multiSeries.add(pickerList.getConstantLine(j).getDC());
					numSeries *= tmpConstant.getNumSteps();
				} else {
					/* No, just the one. */
					otherValues.addValue(tmpConstant.getName(), value);
				}
			}
		}
		
		if(this.plotType3d.isSelected()){
			
			//go through all constants in the y axis picker list
			for(int j = 0 ; j < pickerList.getNumConstants() ; j++){
				
				// get constant
				DefinedConstant tmpConstant = pickerList.getConstantLine(j).getDC();
				
				if (tmpConstant.getName().equals(rangerY)) {
					rangingConstantY = tmpConstant;
				}
			}
			
		}

		//sort out which one to add it to
		if (rangingConstantX == null)
			return;
		
		//if 3d plot is selected and y axis is not selected, select it!
		if(this.plotType3d.isSelected() && rangingConstantY == null)
			return;

		// if there are a lot of series, check if this is what the user really wanted
		if (numSeries > MAX_NUM_SERIES_BEFORE_QUERY) {
			String[] choices = { "Yes", "No" };
			int choice = -1;
			choice = plugin.optionPane("Warning: This will plot " + numSeries + " series.\nAre you sure you want to continue?", "Question",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, choices, choices[0]);
			if (choice != 0)
				return;
		}

		if(this.plotType2d.isSelected()){

			if (newGraphRadio.isSelected()) {
				/* Make new graph. */
				//graphModel = new Graph();
				graphModel2D = new ParametricGraph("");
				graphHandler.addGraph(graphModel2D);

				graphModel2D.getYAxisSettings().setHeading(resultsCollection.getResultName());
				graphModel2D.getXAxisSettings().setHeading(rangerX);
			} else {
				/* Add to an existing graph. */

				graphModel2D = (Graph)graphHandler.getModel(existingGraphCombo.getSelectedItem().toString());
				if (!rangerX.equals(graphModel2D.getXAxisSettings().getHeading())) //FIXME: must do this better in future
					if (!roughExists(rangerX, graphModel2D.getXAxisSettings().getHeading()))
						graphModel2D.getXAxisSettings().setHeading(graphModel2D.getXAxisSettings().getHeading() + ", " + rangerX);
			}
		
		}
		else if(this.plotType3d.isSelected()){
			
			// always the new graph radio button will be selected since we can't have another 3d plot on the same plot
			
			if(selectAxisConstantCombo.getSelectedItem().toString().equals(selectYaxisConstantCombo.getSelectedItem().toString())){
				
				plugin.error("Please select diffrent variables for x and y axis!");
				return;
				
			}
			
			graphModel3D = new Graph3D();
			graphHandler.addGraph(graphModel3D);
			
		}

		graphCancelled = false;
		setVisible(false);
	}

	private void existingGraphRadioActionPerformed(java.awt.event.ActionEvent evt)
	{
		doEnables();
	}

	private void newGraphRadioActionPerformed(java.awt.event.ActionEvent evt)
	{
		doEnables();
	}
	
	private void selectYAxisConstantComboActionPerformed(java.awt.event.ActionEvent evt){
		
		pickerList.disableLines(selectAxisConstantCombo.getSelectedItem().toString(),
								selectYaxisConstantCombo.getSelectedItem().toString());
		resetAutoSeriesName();
		
		seriesNameField.setText(pickerList.getDisableConstantsInfo());
	}

	private void selectAxisConstantComboActionPerformed(java.awt.event.ActionEvent evt)
	{
		if(this.plotType2d.isSelected())
		{
			pickerList.disableLine(selectAxisConstantCombo.getSelectedIndex());
		}
		else if(this.plotType3d.isSelected())
		{
			pickerList.disableLines(selectAxisConstantCombo.getSelectedItem().toString(),
					selectYaxisConstantCombo.getSelectedItem().toString());
			
			seriesNameField.setText(pickerList.getDisableConstantsInfo());
		}
	
		resetAutoSeriesName();
	}

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)
	{
		setVisible(false);
		dispose();
	}

	// Variables declaration - do not modify
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JPanel constantTablePanel;
	private javax.swing.JComboBox existingGraphCombo;
	private javax.swing.JRadioButton existingGraphRadio;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel10;
	private javax.swing.JPanel jPanel11;
	private javax.swing.JPanel jPanel12;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel9;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JButton lineCancelButton;
	private javax.swing.JButton lineOkayButton;
	private javax.swing.JLabel middleLabel;
	private javax.swing.JRadioButton newGraphRadio;
	private javax.swing.JComboBox selectAxisConstantCombo;
	private javax.swing.JTextField seriesNameField;
	private javax.swing.JLabel seriesNameLabel;
	private javax.swing.JLabel topComboLabel;
	private JPanel panel;
	private GridBagConstraints gridBagConstraints_1;
	private GridBagConstraints gridBagConstraints_2;
	private GridBagConstraints gridBagConstraints_3;
	private GridBagConstraints gridBagConstraints_4;
	private GridBagConstraints gridBagConstraints_5;
	private GridBagConstraints gridBagConstraints_6;
	private GridBagConstraints gridBagConstraints_7;
	private GridBagConstraints gridBagConstraints_8;
	private GridBagConstraints gridBagConstraints_9;
	private GridBagConstraints gridBagConstraints_10;
	private GridBagConstraints gridBagConstraints_11;
	private GridBagConstraints gridBagConstraints_12;
	private GridBagConstraints gridBagConstraints_13;
	private GridBagConstraints gridBagConstraints_14;
	private GridBagConstraints gridBagConstraints_15;
	private GridBagConstraints gridBagConstraints_16;
	private JRadioButton plotType2d;
	private JRadioButton plotType3d;
	private JLabel lblPlotType;
	private JLabel lblSelectYAxis;
	private JComboBox selectYaxisConstantCombo;
	private JPanel panel_1;

	// End of variables declaration

	public static int factorial(int i)
	{
		if (i < 0)
			return 1;
		if (i == 0)
			return 1;
		else
			return i * factorial(i - 1);
	}

	public static boolean roughExists(String test, String inThis)
	{
		int i = inThis.indexOf(test);
		if (i == -1)
			return false;
		if (!((i == 0) || (inThis.charAt(i - 1) == ' ')))
			return false;
		if (!((inThis.length() == i + 1) || (inThis.charAt(i + 1) == ',')))
			return false;
		return true;
	}
}
