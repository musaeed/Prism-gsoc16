//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Mark Kattenbelt <mark.kattenbelt@comlab.ox.ac.uk> (University of Oxford, formerly University of Birmingham)
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

import javax.swing.*;

import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jfree.chart.ChartPanel;

import userinterface.*;

public class GUIImageExportDialog extends JDialog implements DocumentListener
{         
	//ATTRIBUTES    
	private Action okAction;
	private Action cancelAction;
		
	private GUIPrism gui;
	 
	private int exportWidth;
	private int exportHeight;
	
	private boolean cancelled;	
	
	public static final int JPEG = 0;
	public static final int PNG = 1;
	public static final int EPS = 2;
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel allPanel;
    private javax.swing.JCheckBox alphaInputField;
    private javax.swing.JLabel alphaInputLabel;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField heightInputField;
    private javax.swing.JLabel heightInputLabel;
    private javax.swing.JComboBox imageTypeInputField;
    private javax.swing.JLabel imageTypeInputLabel;
    private javax.swing.JPanel innerTopPanel;
    private javax.swing.JButton okayButton;
    private javax.swing.JPanel topPanel;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JTextField widthInputField;
    private javax.swing.JLabel widthInputLabel;
    // End of variables declaration//GEN-END:variables
    
	/** Creates new form GUIConstantsPicker */
	public GUIImageExportDialog(GUIPrism parent, JPanel graph, int defaultImageType)
	{
		super(parent, "Provide rendering information", true);
		
		this.exportWidth = graph.getWidth();
		this.exportHeight = graph.getHeight();
        		
		initComponents();
		
		this.getRootPane().setDefaultButton(okayButton);
		
		this.cancelled = false;
		
		this.warningLabel.setIcon(GUIPrism.getIconFromImage("smallError.png"));
		this.warningLabel.setVisible(false);
		
		this.widthInputField.getDocument().addDocumentListener(this);
		this.heightInputField.getDocument().addDocumentListener(this);
		
		this.widthInputField.setText("" + exportWidth);
		this.heightInputField.setText("" + exportHeight);
		
		this.imageTypeInputField.setSelectedIndex(defaultImageType);
		
		this.alphaInputField.setSelected(false);
		this.alphaInputField.setEnabled(defaultImageType == PNG);
		this.alphaInputLabel.setEnabled(defaultImageType == PNG);
				
		super.setBounds(new Rectangle(550, 300));
		setResizable(true);
		setLocationRelativeTo(getParent()); // centre
				
		
		this.setVisible(true);
	}
    
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        allPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        okayButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        topPanel = new javax.swing.JPanel();
        innerTopPanel = new javax.swing.JPanel();
        widthInputLabel = new javax.swing.JLabel();
        widthInputField = new javax.swing.JTextField();
        heightInputLabel = new javax.swing.JLabel();
        heightInputField = new javax.swing.JTextField();
        imageTypeInputLabel = new javax.swing.JLabel();
        imageTypeInputField = new javax.swing.JComboBox();
        alphaInputLabel = new javax.swing.JLabel();
        alphaInputField = new javax.swing.JCheckBox();

        setMinimumSize(new java.awt.Dimension(550, 350));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        allPanel.setLayout(new java.awt.BorderLayout());

        allPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bottomPanel.setLayout(new java.awt.BorderLayout());

        warningLabel.setText("Please enter positive integers for width and height.");
        warningLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        bottomPanel.add(warningLabel, java.awt.BorderLayout.CENTER);
        warningLabel.getAccessibleContext().setAccessibleName("Please enter a positive integer for both width and height.");

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okayButton.setText("Okay");
        okayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okayButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(okayButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(cancelButton);

        bottomPanel.add(buttonPanel, java.awt.BorderLayout.EAST);

        allPanel.add(bottomPanel, java.awt.BorderLayout.SOUTH);

        topPanel.setLayout(new java.awt.BorderLayout());

        topPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Image properties"));
        topPanel.setMinimumSize(new java.awt.Dimension(400, 200));
        topPanel.setPreferredSize(new java.awt.Dimension(400, 200));
        innerTopPanel.setLayout(new java.awt.GridLayout(4, 2, 5, 5));

        innerTopPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        widthInputLabel.setText("Width:");
        innerTopPanel.add(widthInputLabel);

        innerTopPanel.add(widthInputField);

        heightInputLabel.setText("Height:");
        innerTopPanel.add(heightInputLabel);

        innerTopPanel.add(heightInputField);

        imageTypeInputLabel.setText("Image format:");
        innerTopPanel.add(imageTypeInputLabel);

        imageTypeInputField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "JPEG", "PNG", "EPS" }));
        imageTypeInputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageTypeInputFieldActionPerformed(evt);
            }
        });

        innerTopPanel.add(imageTypeInputField);

        alphaInputLabel.setText("Transparent background:");
        innerTopPanel.add(alphaInputLabel);

        alphaInputField.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        alphaInputField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        innerTopPanel.add(alphaInputField);

        topPanel.add(innerTopPanel, java.awt.BorderLayout.NORTH);

        allPanel.add(topPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(allPanel, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

    private void imageTypeInputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageTypeInputFieldActionPerformed

    	boolean canHandleAlpha = (imageTypeInputField.getSelectedIndex() == PNG);
    	
    	alphaInputField.setEnabled(canHandleAlpha);
    	alphaInputLabel.setEnabled(canHandleAlpha);
    	    		
    }//GEN-LAST:event_imageTypeInputFieldActionPerformed

	private void okayButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okayButtonActionPerformed
	{//GEN-HEADEREND:event_okayButtonActionPerformed
		dispose();
	}//GEN-LAST:event_okayButtonActionPerformed
        
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
	{//GEN-HEADEREND:event_cancelButtonActionPerformed
		cancelled = true;
		dispose();
	}//GEN-LAST:event_cancelButtonActionPerformed
        
	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)//GEN-FIRST:event_closeDialog
	{
		setVisible(false);
		dispose();
	}//GEN-LAST:event_closeDialog
		
	public int getImageType()
	{
		return imageTypeInputField.getSelectedIndex();
	}
	
	public boolean isCancelled() 
	{
		return cancelled;
	}

	public int getExportWidth() 
	{
		return exportWidth;
	}
	
	public int getExportHeight() 
	{
		return exportHeight;
	}
	
	public boolean getAlpha() 
	{
		return alphaInputField.isSelected();
	}
	
	public void changedUpdate(DocumentEvent e) 
	{
		try
		{
			exportWidth = Integer.parseInt(widthInputField.getText());
			if (exportWidth <= 0) throw new NumberFormatException();
			exportHeight = Integer.parseInt(heightInputField.getText());
			if (exportHeight <= 0) throw new NumberFormatException();
			GUIImageExportDialog.this.warningLabel.setVisible(false);
			GUIImageExportDialog.this.okayButton.setEnabled(true);
		}
		catch (NumberFormatException nfe)
		{
			GUIImageExportDialog.this.warningLabel.setVisible(true);
			GUIImageExportDialog.this.okayButton.setEnabled(false);
		}				
	}
		
	public void removeUpdate(DocumentEvent e) {changedUpdate(e);}			
	public void insertUpdate(DocumentEvent e) {changedUpdate(e);}
}


