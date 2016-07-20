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

package userinterface.graph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartPanel;

import com.orsoncharts.Chart3DPanel;

import settings.SettingTable;
import userinterface.GUIPlugin;
import userinterface.GUIPrism;

public class GraphOptionsPanel extends JPanel implements ListSelectionListener
{
	private SettingTable graphPropertiesTable, axisPropertiesTable, displayPropertiesTable, seriesPropertiesTable;
	
	private JList seriesList, axesList;
	
	private JPanel theModel;
	private Observable xAxisSettings;
	private Observable yAxisSettings;
	private Observable zAxisSettings;
	private Observable displaySettings;
	
	private JFrame parent;
	private GUIPlugin plugin;
	
	/** Creates new form GraphOptionsPanel */
	public GraphOptionsPanel(GUIPlugin plugin, JFrame parent, JPanel theModel)
	{
		this.plugin = plugin;
		this.parent = parent;
		this.theModel = theModel;
		
		/* TODO: Use generic container. */ 
		ArrayList own = new ArrayList();
		own.add(theModel);
		
		graphPropertiesTable = new SettingTable(parent);
		graphPropertiesTable.setOwners(own);
		
		if(theModel instanceof Graph){
			
			((Graph)theModel).setDisplay(graphPropertiesTable);
			xAxisSettings = ((Graph)theModel).getXAxisSettings();
			yAxisSettings = ((Graph)theModel).getYAxisSettings();
			zAxisSettings = null;
			displaySettings = ((Graph)theModel).getDisplaySettings();
			
		}
		else if(theModel instanceof Histogram){
			
			((Histogram)theModel).setDisplay(graphPropertiesTable);
			xAxisSettings = ((Histogram)theModel).getXAxisSettings();
			yAxisSettings = ((Histogram)theModel).getYAxisSettings();
			zAxisSettings = null;
			displaySettings = ((Histogram)theModel).getDisplaySettings();
		}
		else if(theModel instanceof Graph3D){
			
			((Graph3D)theModel).setDisplay(graphPropertiesTable);
			xAxisSettings = ((Graph3D)theModel).getxAxisSetting();
			yAxisSettings = ((Graph3D)theModel).getyAxisSetting();
			zAxisSettings = null;
			//zAxisSettings = ((Graph3D)theModel).getzAxisSetting();
			displaySettings = ((Graph3D)theModel).getDisplaySettings();
		}

		
		
		
		String[] axes = {"x-Axis", "y-Axis"};
		axesList = new JList(axes);
		axesList.setSelectedIndex(0);
		
		axesList.addListSelectionListener(this);
		
		own = new ArrayList();		
		own.add(xAxisSettings);		
		axisPropertiesTable = new SettingTable(parent);
		axisPropertiesTable.setOwners(own);
		
		if(theModel instanceof Graph){
			
			((AxisSettings)xAxisSettings).setDisplay(axisPropertiesTable);
			((AxisSettings)yAxisSettings).setDisplay(axisPropertiesTable);
			
		}
		else if(theModel instanceof Histogram){
			
			((AxisSettingsHistogram)xAxisSettings).setDisplay(axisPropertiesTable);
			((AxisSettingsHistogram)yAxisSettings).setDisplay(axisPropertiesTable);
		}
		
		
		own = new ArrayList();		
		own.add(displaySettings);
		displayPropertiesTable = new SettingTable(parent);
		displayPropertiesTable.setOwners(own);
		
		if(theModel instanceof ChartPanel)
			((DisplaySettings)displaySettings).setDisplay(displayPropertiesTable);
		else if(theModel instanceof Graph3D)
			((DisplaySettings3D)displaySettings).setDisplay(displayPropertiesTable);
		
		
		if(theModel instanceof Graph){
			seriesList = new JList(((Graph)theModel).getGraphSeriesList());
		}
		else if(theModel instanceof Histogram){
			
			seriesList = new JList(((Histogram)theModel).getGraphSeriesList());
		}
		else if(theModel instanceof Graph3D){
			// we dont have any series for a 3d graph
			seriesList = new JList<>();
		}

		
		seriesList.addListSelectionListener(this);
		
		seriesList.setCellRenderer(new ListCellRenderer() {
					
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
			{
				JLabel label = new JLabel((value == null) ? "undefined" : value.toString());
				JPanel panel = new JPanel();
				
				panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
												
				if (isSelected) 
				{
					panel.setBackground(list.getSelectionBackground());
					panel.setForeground(list.getSelectionForeground());
		        } 
				else 
		        {
					panel.setBackground(list.getBackground());
		        	panel.setForeground(list.getForeground());
		        }
				
				if (value instanceof SeriesSettings)
				{
					SeriesSettings graphSeries = (SeriesSettings)value;
					panel.add(graphSeries.getIcon());
				}
				
				panel.add(label);
								
				return panel;
			}
		});
			
		seriesPropertiesTable = new SettingTable(parent);
		
		
		/*seriesList = theModel.getSeriesList();
		seriesList.addListSelectionListener(this);
		//seriesModel = new PropertyTableModel();
		ArrayList ss = seriesList.getSelectedSeries();
		//seriesModel.setOwners(ss);
		
		seriesProperties = new SettingTable(parent);
		seriesProperties.setOwners(ss);*/
		initComponents();
		//addSeries.setEnabled(ss.size() > 0);
		/*removeSeries.setEnabled(ss.size() > 0);
		moveUp.setEnabled(ss.size() > 0);
		moveDown.setEnabled(ss.size() > 0);
		viewData.setEnabled(ss.size() > 0);*/
		
		
	}
	
	public void stopEditors()
	{
		graphPropertiesTable.stopEditing();
		axisPropertiesTable.stopEditing();
		seriesPropertiesTable.stopEditing();
		displayPropertiesTable.stopEditing();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//javax.swing.JPanel jPanel6;
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        javax.swing.JTabbedPane tabbedPanel;

        tabbedPanel = new javax.swing.JTabbedPane();
        graphOptionsPanel = new javax.swing.JPanel();
        innerGraphOptionsPanel = new javax.swing.JPanel();
        axisOptionsPanel = new javax.swing.JPanel();
        innerAxesOptionsPanel = new javax.swing.JPanel();
        axesOptionPanelSplitPane = new javax.swing.JSplitPane();
        axesTopPanel = new javax.swing.JPanel();
        axesInnerTopPanel = new javax.swing.JPanel();
        axesLabel = new javax.swing.JLabel();
        axesListPanel = new javax.swing.JPanel();
        axesInnerListPanel = new javax.swing.JPanel();
        axesListScrollPane = new javax.swing.JScrollPane();
        axesBottomPanel = new javax.swing.JPanel();
        seriesOptionsPanel = new javax.swing.JPanel();
        seriesOptionPanelSplitPane = new javax.swing.JSplitPane();
        seriesTopPanel = new javax.swing.JPanel();
        seriesInnerTopPanel = new javax.swing.JPanel();
        seriesLabel = new javax.swing.JLabel();
        seriesListPanel = new javax.swing.JPanel();
        seriesInnerListPanel = new javax.swing.JPanel();
        seriesListScrollPane = new javax.swing.JScrollPane();
        seriesButtonPanel = new javax.swing.JPanel();
        addSeries = new javax.swing.JButton();
        removeSeries = new javax.swing.JButton();
        moveUp = new javax.swing.JButton();
        moveDown = new javax.swing.JButton();
        viewData = new javax.swing.JButton();
        seriesBottomPanel = new javax.swing.JPanel();
        displayOptionsPanel = new javax.swing.JPanel();
        innerDisplayOptionsPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        tabbedPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPanelStateChanged(evt);
            }
        });

        graphOptionsPanel.setLayout(new java.awt.BorderLayout());

        graphOptionsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        innerGraphOptionsPanel.setLayout(new java.awt.BorderLayout());

        innerGraphOptionsPanel.add(graphPropertiesTable, java.awt.BorderLayout.CENTER);
        graphOptionsPanel.add(innerGraphOptionsPanel, java.awt.BorderLayout.CENTER);

        tabbedPanel.addTab("Graph", graphOptionsPanel);

        axisOptionsPanel.setLayout(new java.awt.BorderLayout());

        axisOptionsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        innerAxesOptionsPanel.setLayout(new java.awt.BorderLayout());

        axesOptionPanelSplitPane.setDividerLocation(80);
        axesOptionPanelSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        axesTopPanel.setLayout(new java.awt.BorderLayout());

        axesTopPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        axesInnerTopPanel.setLayout(new java.awt.BorderLayout());

        axesLabel.setText("Select Axis:");
        axesInnerTopPanel.add(axesLabel, java.awt.BorderLayout.NORTH);

        axesListPanel.setLayout(new java.awt.GridBagLayout());

        axesInnerListPanel.setLayout(new java.awt.BorderLayout());

        axesListScrollPane.setMaximumSize(new java.awt.Dimension(32767, 120));
        axesListScrollPane.setMinimumSize(new java.awt.Dimension(20, 22));
        axesListScrollPane.setPreferredSize(new java.awt.Dimension(3, 120));
        axesListScrollPane.setViewportView(axesList);
        axesInnerListPanel.add(axesListScrollPane, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.weighty = 1.0;
        axesListPanel.add(axesInnerListPanel, gridBagConstraints);

        axesInnerTopPanel.add(axesListPanel, java.awt.BorderLayout.CENTER);

        axesTopPanel.add(axesInnerTopPanel, java.awt.BorderLayout.CENTER);

        axesOptionPanelSplitPane.setLeftComponent(axesTopPanel);

        axesBottomPanel.setLayout(new java.awt.BorderLayout());

        axesBottomPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        axesBottomPanel.add(axisPropertiesTable, BorderLayout.CENTER);

        axesOptionPanelSplitPane.setRightComponent(axesBottomPanel);

        innerAxesOptionsPanel.add(axesOptionPanelSplitPane, java.awt.BorderLayout.CENTER);

        axisOptionsPanel.add(innerAxesOptionsPanel, java.awt.BorderLayout.CENTER);

        tabbedPanel.addTab("Axes", axisOptionsPanel);

        seriesOptionsPanel.setLayout(new java.awt.BorderLayout());

        seriesOptionsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        seriesOptionPanelSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        seriesTopPanel.setLayout(new java.awt.BorderLayout());

        seriesTopPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        seriesInnerTopPanel.setLayout(new java.awt.BorderLayout());

        seriesLabel.setText("Select Series:");
        seriesInnerTopPanel.add(seriesLabel, java.awt.BorderLayout.NORTH);

        seriesListPanel.setLayout(new java.awt.GridBagLayout());

        seriesInnerListPanel.setLayout(new java.awt.BorderLayout());

        seriesListScrollPane.setMaximumSize(new java.awt.Dimension(32767, 120));
        seriesListScrollPane.setMinimumSize(new java.awt.Dimension(20, 22));
        seriesListScrollPane.setPreferredSize(new java.awt.Dimension(3, 120));
        seriesListScrollPane.setViewportView(seriesList);

        seriesInnerListPanel.add(seriesListScrollPane, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.weighty = 1.0;
        seriesListPanel.add(seriesInnerListPanel, gridBagConstraints);

        seriesButtonPanel.setLayout(new java.awt.GridLayout(5, 1, 5, 5));

        seriesButtonPanel.setMaximumSize(new java.awt.Dimension(2147483647, 105));
        addSeries.setText("Add");
        addSeries.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addSeries.setMinimumSize(new java.awt.Dimension(5, 25));
        addSeries.setPreferredSize(new java.awt.Dimension(5, 25));
        addSeries.setIcon(GUIPrism.getIconFromImage("smallAdd.png"));
        addSeries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSeriesActionPerformed(evt);
            }
        });

        seriesButtonPanel.add(addSeries);

        removeSeries.setText("Remove");
        removeSeries.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        removeSeries.setMinimumSize(new java.awt.Dimension(5, 25));
        removeSeries.setPreferredSize(new java.awt.Dimension(5, 25));
        removeSeries.setIcon(GUIPrism.getIconFromImage("smallRemove.png"));
        removeSeries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSeriesActionPerformed(evt);
            }
        });

        seriesButtonPanel.add(removeSeries);

        moveUp.setText("Move Up");
        moveUp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        moveUp.setMinimumSize(new java.awt.Dimension(5, 25));
        moveUp.setPreferredSize(new java.awt.Dimension(5, 25));
        moveUp.setIcon(GUIPrism.getIconFromImage("smallArrowUp.png"));
        moveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpActionPerformed(evt);
            }
        });

        seriesButtonPanel.add(moveUp);

        moveDown.setText("Move Down");
        moveDown.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        moveDown.setMinimumSize(new java.awt.Dimension(5, 25));
        moveDown.setPreferredSize(new java.awt.Dimension(5, 25));
        moveDown.setIcon(GUIPrism.getIconFromImage("smallArrowDown.png"));
        moveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownActionPerformed(evt);
            }
        });

        seriesButtonPanel.add(moveDown);
        
        if(theModel instanceof Graph)
        	viewData.setText("Edit Data");
        else if(theModel instanceof Histogram)
        	viewData.setText("View Data");
        
        viewData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        viewData.setIcon(GUIPrism.getIconFromImage("smallEditData.png"));
        viewData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewDataActionPerformed(evt);
            }
        });

        seriesButtonPanel.add(viewData);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        seriesListPanel.add(seriesButtonPanel, gridBagConstraints);

        seriesInnerTopPanel.add(seriesListPanel, java.awt.BorderLayout.CENTER);

        seriesTopPanel.add(seriesInnerTopPanel, java.awt.BorderLayout.CENTER);

        seriesOptionPanelSplitPane.setLeftComponent(seriesTopPanel);

        seriesBottomPanel.setLayout(new java.awt.BorderLayout());

        seriesBottomPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        seriesBottomPanel.add(seriesPropertiesTable, BorderLayout.CENTER);
        seriesOptionPanelSplitPane.setRightComponent(seriesBottomPanel);

        seriesOptionsPanel.add(seriesOptionPanelSplitPane, java.awt.BorderLayout.CENTER);

        tabbedPanel.addTab("Series", seriesOptionsPanel);

        displayOptionsPanel.setLayout(new java.awt.BorderLayout());

        displayOptionsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        innerDisplayOptionsPanel.setLayout(new java.awt.BorderLayout());

        innerDisplayOptionsPanel.add(displayPropertiesTable, java.awt.BorderLayout.CENTER);

        displayOptionsPanel.add(innerDisplayOptionsPanel, java.awt.BorderLayout.CENTER);

        tabbedPanel.addTab("Display", displayOptionsPanel);

        add(tabbedPanel, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents
	
    private void viewDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewDataActionPerformed
		
    	Object lock = null;
    	
    	if(theModel instanceof Graph){
    		lock = ((Graph)theModel).getSeriesLock();
    	}
    	else if(theModel instanceof Histogram){
    		lock = ((Histogram)theModel).getSeriesLock();
    	}
    	
    	
    	synchronized (lock)
		{
			int[] sel = seriesList.getSelectedIndices();
			
			java.util.List<SeriesKey> selected = new ArrayList<SeriesKey>();
		
			for (int i = 0; i < sel.length; i++)
			{
				if(theModel instanceof Graph)
					selected.add(((SeriesSettings)((Graph)theModel).getGraphSeriesList().getElementAt(sel[i])).getSeriesKey());
				else if(theModel instanceof Histogram)
					selected.add(((SeriesSettings)((Histogram)theModel).getGraphSeriesList().getElementAt(sel[i])).getSeriesKey());
			}
			
			if(theModel instanceof ChartPanel)
				SeriesEditorDialog.makeSeriesEditor(plugin, parent, (ChartPanel)theModel, selected);
		}
    	
	/*	ArrayList ss = seriesList.getSelectedSeries();
		if(ss.size() > 1)
		{
			GraphListEditor.showEditors(parent, seriesList.getEditors());
		}
		else if(seriesList.getSelectedSeries().size() == 1)
			((GraphList)ss.get(0)).getEditor().showEditor(parent);*/
    	
    }//GEN-LAST:event_viewDataActionPerformed
	
    private void tabbedPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPanelStateChanged
		
		stopEditors();
    }//GEN-LAST:event_tabbedPanelStateChanged
	
	private void moveDownActionPerformed(java.awt.event.ActionEvent evt)                                         
	{		
		Object lock = null;
    	
    	if(theModel instanceof Graph){
    		lock = ((Graph)theModel).getSeriesLock();
    	}
    	else if(theModel instanceof Histogram){
    		lock = ((Histogram)theModel).getSeriesLock();
    	}
    	
		synchronized (lock)
		{
			int[] sel = seriesList.getSelectedIndices();
			
			SeriesSettingsList listModel = null;
			
			if(theModel instanceof Graph){
				
				listModel = ((Graph)theModel).getGraphSeriesList();
				
			}
			else if(theModel instanceof Histogram){
				
				listModel = ((Histogram)theModel).getGraphSeriesList();
			}

			
			Vector<SeriesKey> toMove = new Vector<SeriesKey>();
			
			for (int i = 0; i < sel.length; i++)
			{
				SeriesSettings series = (SeriesSettings)listModel.getElementAt(sel[i]);
				toMove.add(series.getSeriesKey());
			}
			
			if(theModel instanceof Graph)
				((Graph)theModel).moveDown(toMove);
			
			int[] newSel = new int[sel.length];
			                       
		    for (int i = 0; i < sel.length; i++)
		    {
		    	newSel[i] = (sel[i] < listModel.getSize() - 1) ? (sel[i] + 1) : (0);
		    }
			
			seriesList.setSelectedIndices(newSel);
		}
	}
	
	
	private void moveUpActionPerformed(java.awt.event.ActionEvent evt)                                       
	{
		Object lock = null;
    	
    	if(theModel instanceof Graph){
    		lock = ((Graph)theModel).getSeriesLock();
    	}
    	else if(theModel instanceof Histogram){
    		lock = ((Histogram)theModel).getSeriesLock();
    	}
    	
		synchronized (lock)
		{
			int[] sel = seriesList.getSelectedIndices();
			
			SeriesSettingsList listModel = null;
			
			if(theModel instanceof Graph){
				
				listModel = ((Graph)theModel).getGraphSeriesList();
				
			}
			else if(theModel instanceof Histogram){
				
				listModel = ((Histogram)theModel).getGraphSeriesList();
			}
			
			Vector<SeriesKey> toMove = new Vector<SeriesKey>();
			
			for (int i = 0; i < sel.length; i++)
			{
				SeriesSettings series = (SeriesSettings)listModel.getElementAt(sel[i]);
				toMove.add(series.getSeriesKey());
			}
			
			if(theModel instanceof Graph)
				((Graph)theModel).moveUp(toMove);
			
			int[] newSel = new int[sel.length];
			                       
		    for (int i = 0; i < sel.length; i++)
		    {
		    	newSel[i] = (sel[i] > 1) ? (sel[i] -1) : (0);
		    }
			
			seriesList.setSelectedIndices(newSel);			
		}
	}
	
	private void removeSeriesActionPerformed(java.awt.event.ActionEvent evt)                                             
	{
		
		Object lock = null;
    	
    	if(theModel instanceof Graph){
    		lock = ((Graph)theModel).getSeriesLock();
    	}
    	else if(theModel instanceof Histogram){
    		lock = ((Histogram)theModel).getSeriesLock();
    	}
    	
		synchronized (lock)
		{
			int[] sel = seriesList.getSelectedIndices();
			
			SeriesSettingsList listModel = null;
			
			if(theModel instanceof Graph){
				
				listModel = ((Graph)theModel).getGraphSeriesList();
				
			}
			else if(theModel instanceof Histogram){
				
				listModel = ((Histogram)theModel).getGraphSeriesList();
			}
			
			Vector<SeriesKey> toRemove = new Vector<SeriesKey>();
			
			for (int i = 0; i < sel.length; i++)
			{
				SeriesSettings series = (SeriesSettings)listModel.getElementAt(sel[i]);
				toRemove.add(series.getSeriesKey());
			}
			
			for (SeriesKey key : toRemove)
			{
				if(theModel instanceof Graph)
				{
					((Graph)theModel).removeSeries(key);
					
				}
				else if(theModel instanceof Histogram)
				{
					
					((Histogram)theModel).removeSeries(key);
					
				}

			}
			
			listModel.updateSeriesList();
		}
		
		seriesList.clearSelection();
	}
	
	private void addSeriesActionPerformed(java.awt.event.ActionEvent evt)                                          
	{
		if(theModel instanceof Graph)
			((Graph)theModel).addSeries("New Series");
		else if(theModel instanceof Histogram)
			((Histogram)theModel).addSeries("New Series");
	}
	
	public void doEnables()
	{
		boolean hasFirst = false;
		boolean hasLast = false;
		
		for (int i = 0; i < seriesList.getSelectedIndices().length; i++)
		{
			if (seriesList.getSelectedIndices()[i] == 0)
				hasFirst = true;
			if(theModel instanceof Graph)
				if (seriesList.getSelectedIndices()[i] == ((Graph)theModel).getGraphSeriesList().getSize() - 1)
					hasLast = true;
			if(theModel instanceof Histogram)
				if (seriesList.getSelectedIndices()[i] == ((Histogram)theModel).getGraphSeriesList().getSize() - 1)
					hasLast = true;
		}
		
		removeSeries.setEnabled(seriesList.getSelectedIndices().length >= 1);
				
		moveUp.setEnabled(!hasFirst && seriesList.getSelectedIndices().length >= 1);
		moveDown.setEnabled(!hasLast && seriesList.getSelectedIndices().length >= 1);
		
		viewData.setEnabled(seriesList.getSelectedIndices().length >= 1);
	}
		
	public void valueChanged(ListSelectionEvent e)
	{	
		stopEditors();
		doEnables();
		
		if (e.getSource() == axesList)
		{
			int [] sel = axesList.getSelectedIndices();
			
			ArrayList own = new ArrayList();
			
			for (int i = 0; i < sel.length; i++)
			{
				if (sel[i] == 0)
					own.add(xAxisSettings);
				else if (sel[i] == 1)
					own.add(yAxisSettings);
			}
			axisPropertiesTable.setOwners(own);
		}
		else if (e.getSource() == seriesList)
		{
			Object lock = null;
	    	
	    	if(theModel instanceof Graph){
	    		lock = ((Graph)theModel).getSeriesLock();
	    	}
	    	else if(theModel instanceof Histogram){
	    		lock = ((Histogram)theModel).getSeriesLock();
	    	}
	    	
			synchronized (lock)
			{
				int[] sel = seriesList.getSelectedIndices();
				
				ArrayList own = new ArrayList();
				//seriesPropertiesTable.setOwners(own);
				
				for (int i = 0; i < sel.length; i++)
				{
					if(theModel instanceof Graph)
						own.add(((Graph)theModel).getGraphSeriesList().getElementAt(sel[i]));
				}
				
				//seriesPropertiesTable = new SettingTable(parent);
				//seriesPropertiesTable.setOwners(own);
			}
		}
	}
	
	public void errorDialog(String error, String title)
	{
		JOptionPane.showMessageDialog(parent,
		error,
		title, JOptionPane.ERROR_MESSAGE);
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addSeries;
    private javax.swing.JPanel axesBottomPanel;
    private javax.swing.JPanel axesInnerListPanel;
    private javax.swing.JPanel axesInnerTopPanel;
    private javax.swing.JLabel axesLabel;
    private javax.swing.JPanel axesListPanel;
    private javax.swing.JScrollPane axesListScrollPane;
    private javax.swing.JSplitPane axesOptionPanelSplitPane;
    private javax.swing.JPanel axesTopPanel;
    private javax.swing.JPanel axisOptionsPanel;
    private javax.swing.JPanel displayOptionsPanel;
    private javax.swing.JPanel graphOptionsPanel;
    private javax.swing.JPanel innerAxesOptionsPanel;
    private javax.swing.JPanel innerDisplayOptionsPanel;
    private javax.swing.JPanel innerGraphOptionsPanel;
    private javax.swing.JButton moveDown;
    private javax.swing.JButton moveUp;
    private javax.swing.JButton removeSeries;
    private javax.swing.JPanel seriesBottomPanel;
    private javax.swing.JPanel seriesButtonPanel;
    private javax.swing.JPanel seriesInnerListPanel;
    private javax.swing.JPanel seriesInnerTopPanel;
    private javax.swing.JLabel seriesLabel;
    private javax.swing.JPanel seriesListPanel;
    private javax.swing.JScrollPane seriesListScrollPane;
    private javax.swing.JSplitPane seriesOptionPanelSplitPane;
    private javax.swing.JPanel seriesOptionsPanel;
    private javax.swing.JPanel seriesTopPanel;
    private javax.swing.JButton viewData;
    // End of variables declaration//GEN-END:variables
	
	
   /* public static void main(String[]args)
	{
		javax.swing.JFrame f = new javax.swing.JFrame("Graph Options Test");
	
		GraphOptionsPanel gop = new GraphOptionsPanel();
	
		f.getContentPane().add(gop);
	
		f.pack();
		f.show();
		f.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	
	}*/
}
