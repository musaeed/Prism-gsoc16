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

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.util.*;

import prism.*;
import parser.type.*;
import userinterface.*;

public class GUIExperimentPicker extends javax.swing.JDialog
{
	public static final int NO_VALUES = 0;
	public static final int VALUES_DONE = 1;
	public static final int CANCELLED = 2;
	public static final int VALUES_DONE_SHOW_GRAPH = 3;
	public static final int VALUES_DONE_SHOW_GRAPH_AND_SIMULATE = 4;
	public static final int VALUES_DONE_SIMULATE = 5;
	
	static ArrayList remember;
	
	static boolean lastGraph = true;
	static boolean lastSimulation = false;
	    
	private boolean cancelled = true;
	
	private ConstantPickerList propTable;
	private ConstantPickerList modelTable;
	
	private boolean areModel, areProp;
	
	private Action okAction;
	private Action cancelAction;
	
	private javax.swing.JButton okayButton;
	
	private UndefinedConstants undef;
	
	private boolean isParam;
	
	private GUIPrism gui;
	
	static
	{
		remember = new ArrayList();
	}
	
	// Variables declaration - do not modify//GEN-BEGIN:variables
	javax.swing.JCheckBox createGraphCheck;
	javax.swing.JPanel topPanel;
	javax.swing.JCheckBox useSimulationCheck;
	// End of variables declaration//GEN-END:variables
	
	/** Creates new form GUIConstantsPicker */
	public GUIExperimentPicker(GUIPrism parent, UndefinedConstants undef, boolean areModel, boolean areProp, boolean offerGraph, boolean offerSimulation, boolean isParam)
	{
		super(parent, "Define Constants", true);
		this.areModel = areModel;
		this.areProp  = areProp;
		this.undef = undef;
		this.gui = parent;
		this.isParam = isParam;
		//setup tables
		propTable = new ConstantPickerList();
		modelTable = new ConstantPickerList();
		
		//initialise
		initComponents();
		this.getRootPane().setDefaultButton(okayButton);
		if (offerGraph)
		{
			createGraphCheck.setEnabled(true);
			createGraphCheck.setSelected(lastGraph);
		} else
		{
			createGraphCheck.setEnabled(false);
			createGraphCheck.setSelected(false);
		}

		if(offerSimulation)
		{
			useSimulationCheck.setEnabled(true);
			useSimulationCheck.setSelected(lastSimulation);
		}
		else
		{
			useSimulationCheck.setEnabled(false);
			useSimulationCheck.setSelected(false);
		}


		
		initTables(areModel, areProp);
		initValues(undef);
		
		//setResizable(false);
		
		pack();
		setLocationRelativeTo(getParent()); // centre
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()//GEN-BEGIN:initComponents
	{
		javax.swing.JButton cancelButton;
		java.awt.GridBagConstraints gridBagConstraints;
		javax.swing.JLabel jLabel1;
		javax.swing.JPanel jPanel1;
		javax.swing.JPanel jPanel2;
		javax.swing.JPanel jPanel3;
		javax.swing.JPanel jPanel4;
		javax.swing.JPanel jPanel5;
		javax.swing.JPanel jPanel6;
		
		jPanel1 = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		jPanel4 = new javax.swing.JPanel();
		jPanel5 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		topPanel = new javax.swing.JPanel();
		createGraphCheck = new javax.swing.JCheckBox();
		useSimulationCheck = new javax.swing.JCheckBox();
		jPanel6 = new javax.swing.JPanel();
		okayButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				closeDialog(evt);
			}
		});

		jPanel1.setLayout(new java.awt.GridBagLayout());

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		jPanel1.add(jPanel2, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		jPanel1.add(jPanel3, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		jPanel1.add(jPanel4, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		jPanel1.add(jPanel5, gridBagConstraints);

		jLabel1.setText("Please define the following constants:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel1.add(jLabel1, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel1.add(topPanel, gridBagConstraints);

		createGraphCheck.setText("Create Graph");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel1.add(createGraphCheck, gridBagConstraints);

		useSimulationCheck.setText("Use Simulation");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		jPanel1.add(useSimulationCheck, gridBagConstraints);

		getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
		
		
		jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

		okayButton.setText("Okay");
		okayButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				okayButtonActionPerformed(evt);
			}
		});

		jPanel6.add(okayButton);

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				cancelButtonActionPerformed(evt);
			}
		});

		jPanel6.add(cancelButton);

		getContentPane().add(jPanel6, java.awt.BorderLayout.SOUTH);

		pack();
	}//GEN-END:initComponents
	
	private void initTables(boolean areModel, boolean areProp)
	{
		if(areModel && areProp)
		{
			topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
			JPanel topTopPanel = new JPanel();
			topTopPanel.setBorder(new TitledBorder("Model Constants"));
			topTopPanel.setLayout(new BorderLayout());
			JScrollPane sp = new JScrollPane();
			sp.setViewportView(modelTable);
			topTopPanel.add(sp);
			topTopPanel.add(new ConstantHeader(), BorderLayout.NORTH);
			
			JPanel bottomTopPanel = new JPanel();
			bottomTopPanel.setBorder(new TitledBorder("Property Constants"));
			bottomTopPanel.setLayout(new BorderLayout());
			JScrollPane sp2 = new JScrollPane();
			
			sp2.setViewportView(propTable);
			bottomTopPanel.add(sp2);
			bottomTopPanel.add(new ConstantHeader(), BorderLayout.NORTH);
			topPanel.add(topTopPanel);
			topPanel.add(bottomTopPanel);
		}
		else if(areModel)
		{
			topPanel.setBorder(new TitledBorder("Model Constants"));
			topPanel.setLayout(new BorderLayout());
			JScrollPane sp = new JScrollPane();
			sp.setViewportView(modelTable);
			topPanel.add(sp);
			topPanel.add(new ConstantHeader(), BorderLayout.NORTH);
		}
		else if(areProp)
		{
			topPanel.setBorder(new TitledBorder("Property Constants"));
			topPanel.setLayout(new BorderLayout());
			JScrollPane sp = new JScrollPane();
			
			sp.setViewportView(propTable);
			topPanel.add(sp);
			topPanel.add(new ConstantHeader(), BorderLayout.NORTH);
		}
		
		topPanel.setPreferredSize(new Dimension(500,300));
	}
	
	private void initValues(UndefinedConstants undef)
	{
		
		if(isParam){
			
			this.useSimulationCheck.setEnabled(false);
			this.useSimulationCheck.setToolTipText("Not available for parametric operation");
		}
		
		for(int i = 0; i < undef.getMFNumUndefined(); i++)
		{
			ConstantLine line = new ConstantLine(undef.getMFUndefinedName(i), undef.getMFUndefinedType(i));
			
			if(isParam){
				
				line.singleValueField.setEditable(false);
				line.singleValueCombo.setEnabled(false);
				line.stepValueField.setText("");
				line.stepValueField.setToolTipText("doesn't matter for the parametric option!");
				line.stepValueField.setEditable(false);
				line.rangeCombo.doClick();
			}
			
			modelTable.addConstant(line);
		}
		for(int i = 0; i < undef.getPFNumUndefined(); i++)
		{
			ConstantLine line = new ConstantLine(undef.getPFUndefinedName(i), undef.getPFUndefinedType(i));
			propTable.addConstant(line);
			
			if(isParam){
				
				line.startValueField.setText("1");
				line.endValueField.setText("5");
			}
		}
		
		// go through list of remembered values and see if we can use them
		for(int i = 0; i < remember.size(); i++)
		{
			Rememberance r = (Rememberance)remember.get(i);
			for(int j = 0; j < propTable.getNumConstants(); j++)
			{
				ConstantLine cl = propTable.getConstantLine(j);
				if(cl.getName().equals(r.varName) && (cl.getType() == r.type))
				{
					cl.setSingleValue(r.singleValue);
					cl.setStartValue(r.start);
					cl.setEndValue(r.end);
					cl.setStepValue(r.step);
					cl.setIsRange(r.isRange);
				}
			}
			for(int j = 0; j < modelTable.getNumConstants(); j++)
			{
				ConstantLine cl = modelTable.getConstantLine(j);
				if(cl.getName().equals(r.varName) && (cl.getType() == r.type))
				{
					cl.setSingleValue(r.singleValue);
					cl.setStartValue(r.start);
					cl.setEndValue(r.end);
					cl.setStepValue(r.step);
					cl.setIsRange(r.isRange);
				}
			}
		}
	}
	
	/** Call this static method to construct a new GUIConstantsPicker to define undef. */
	public static int defineConstantsWithDialog(GUIPrism parent, UndefinedConstants undef, boolean offerGraph, boolean offerSimulation, boolean isParam)
	{
		boolean areModel = undef.getMFNumUndefined() > 0;
		boolean areProp  = undef.getPFNumUndefined() > 0;
		if(areModel || areProp)
		{
			return new GUIExperimentPicker(parent, undef, areModel, areProp, offerGraph, offerSimulation, isParam).defineValues();
		}
		else return NO_VALUES;
	}
	
	public int defineValues()
	{
		show();
		if(cancelled)
			return CANCELLED;
		else
			if(createGraphCheck.isSelected())
				if(useSimulationCheck.isSelected())
					return VALUES_DONE_SHOW_GRAPH_AND_SIMULATE;
				else
					return VALUES_DONE_SHOW_GRAPH;
			else
				if(useSimulationCheck.isSelected())
					return VALUES_DONE_SIMULATE;
				else 
					return VALUES_DONE;
	}
	
	public void rememberValues()
	{
		int i, j;
		ConstantLine cl;
		Rememberance r, rNew;
		
		for(i = 0; i < propTable.getNumConstants(); i++)
		{
			cl = propTable.getConstantLine(i);
			// store info about this constant
			rNew = new GUIExperimentPicker.Rememberance();
			rNew.varName = cl.getName();
			rNew.type = cl.getType();
			rNew.isRange = cl.isRange();
			rNew.singleValue = cl.getSingleValue();
			rNew.end = cl.getEndValue();
			rNew.start = cl.getStartValue();
			rNew.step = cl.getStepValue();
			
			// see if we got this constant remembered already
			for (j = 0; j < remember.size(); j++)
			{
				r = (Rememberance)remember.get(j);
				// if so, replace it
				if (cl.getName().equals(r.varName) && (cl.getType() == r.type))
				{
					remember.set(j, rNew);
					break;
				}
			}
			// if not, add it
			if (j == remember.size()) remember.add(rNew);
		}
		for(i = 0; i < modelTable.getNumConstants(); i++)
		{
			cl = modelTable.getConstantLine(i);
			// store info about this constant
			rNew = new GUIExperimentPicker.Rememberance();
			rNew.varName = cl.getName();
			rNew.type = cl.getType();
			rNew.isRange = cl.isRange();
			rNew.singleValue = cl.getSingleValue();
			rNew.end = cl.getEndValue();
			rNew.start = cl.getStartValue();
			rNew.step = cl.getStepValue();
			// see if we got this constant remembered already
			for (j = 0; j < remember.size(); j++)
			{
				r = (Rememberance)remember.get(j);
				// if so, replace it
				if (cl.getName().equals(r.varName) && (cl.getType() == r.type))
				{
					remember.set(j, rNew);
					break;
				}
			}
			// if not, add it
			if (j == remember.size()) remember.add(rNew);
		}
	}
	
	private void okayButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okayButtonActionPerformed
	{//GEN-HEADEREND:event_okayButtonActionPerformed
			int i, n;
			ConstantLine c;
			
			try
			{
				// passing info to UndefinedConstants object
				n = modelTable.getNumConstants();
				for (i = 0; i < n; i++)
				{
					c = modelTable.getConstantLine(i);
					
					if(isParam)
						c.stepValueField.setText("0.1"); //just a dummy value here, it doesn;t matter in the parametric case
														// It will be ignored later
					
					c.checkValid();
					if(c.isRange())
					{
						undef.defineConstant(c.getName(), c.getStartValue(), c.getEndValue(), c.getStepValue());
					}
					else
					{
						undef.defineConstant(c.getName(), c.getSingleValue());
					}
				}
				n = propTable.getNumConstants();
				for (i = 0; i < n; i++)
				{
					c = propTable.getConstantLine(i);
					c.checkValid();
					if(c.isRange())
					{
						undef.defineConstant(c.getName(), c.getStartValue(), c.getEndValue(), c.getStepValue());
					}
					else
					{
						undef.defineConstant(c.getName(), c.getSingleValue());
					}
				}
				undef.checkAllDefined();
				undef.initialiseIterators();
				
				cancelled = false;
				rememberValues();
				lastGraph = this.createGraphCheck.isSelected();
				lastSimulation = this.useSimulationCheck.isSelected();
				dispose();
			}
			catch(PrismException e)
			{
				gui.errorDialog(e.getMessage());
			}
	}//GEN-LAST:event_okayButtonActionPerformed
		
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
	{//GEN-HEADEREND:event_cancelButtonActionPerformed
			dispose();
	}//GEN-LAST:event_cancelButtonActionPerformed
		
		/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)//GEN-FIRST:event_closeDialog
		{
			setVisible(false);
			dispose();
	}//GEN-LAST:event_closeDialog
		
		// remembered values for a single constant
		
		static class Rememberance
		{
			String varName;
			parser.type.Type type;
			boolean isRange;
			String singleValue;
			String start;
			String end;
			String step;
			public String toString()
			{ return varName+"("+type.getTypeString()+") : "+isRange+","+singleValue; }
		}
}
