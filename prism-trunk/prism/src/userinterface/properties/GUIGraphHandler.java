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
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartPanel;

import com.orsoncharts.Chart3D;
import com.orsoncharts.data.xyz.XYZDataItem;

import prism.PrismException;
import userinterface.GUIPlugin;
import userinterface.GUIPrism;
import userinterface.graph.GUIImageExportDialog;
import userinterface.graph.Graph;
import userinterface.graph.Graph3D;
import userinterface.graph.GraphException;
import userinterface.graph.GraphOptions;
import userinterface.graph.Histogram;
import userinterface.graph.ParametricGraph;
import userinterface.graph.SeriesKey;

@SuppressWarnings("serial")
public class GUIGraphHandler extends JPanel implements MouseListener
{
	private boolean canDelete;

	private JTabbedPane theTabs;
	private JPopupMenu backMenu, graphMenu;

	private java.util.List<JPanel> models;
	private java.util.List<GraphOptions> options;

	private GUIPlugin plug;

	private Action graphOptions, zoomIn, zoomOut, zoomDefault;
	
	private Action printGraph, deleteGraph;
	private Action exportImageJPG, exportImagePNG, exportPDF, exportImageEPS, exportXML, exportMatlab, exportGnuplot;
	private Action importXML;

	private JMenu zoomMenu, exportMenu, importMenu;

	private FileFilter pngFilter, jpgFilter, pdfFilter, epsFilter, graFilter, matlabFilter, gnuplotFilter;

	public GUIGraphHandler(JFrame parent, GUIPlugin plug, boolean canDelete)
	{
		this.plug = plug;
		this.canDelete = canDelete;

		this.graphMenu = new JPopupMenu();
		this.backMenu = new JPopupMenu();

		initComponents();

		pngFilter = new FileNameExtensionFilter("PNG files (*.png)", "png");
		jpgFilter = new FileNameExtensionFilter("JPEG files (*.jpg, *.jpeg)", "jpg", "jpeg"); 
		pdfFilter = new FileNameExtensionFilter("PDF files(*.pdf)", "pdf");
		epsFilter = new FileNameExtensionFilter("Encapsulated PostScript files (*.eps)", "eps");
		graFilter = new FileNameExtensionFilter("PRISM graph files (*.gra, *.xml)", "gra", "xml");
		matlabFilter = new FileNameExtensionFilter("Matlab files (*.m)", "m");
		gnuplotFilter = new FileNameExtensionFilter("GNU plot files (*gnuplot , *.gplot , *.gp , *.plt , *.gpi)", "gnuplot");

		models = new ArrayList<JPanel>();
		options = new ArrayList<GraphOptions>();
	}

	private void initComponents()
	{
		theTabs = new JTabbedPane();
		theTabs.addMouseListener(this);
		theTabs.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getPreciseWheelRotation() > 0.0){
					
					if(theTabs.getSelectedIndex() != (theTabs.getTabCount() - 1))
						theTabs.setSelectedIndex(theTabs.getSelectedIndex() + 1);
				}
				else{
					
					if(theTabs.getSelectedIndex() != 0)
						theTabs.setSelectedIndex(theTabs.getSelectedIndex() - 1);
				}
			}
		});

		setLayout(new BorderLayout());
		add(theTabs, BorderLayout.CENTER);
		
		graphOptions = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				GraphOptions graphOptions = options.get(theTabs.getSelectedIndex());
				graphOptions.setVisible(true);
			}
		};

		graphOptions.putValue(Action.NAME, "Graph options");
		graphOptions.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
		graphOptions.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallOptions.png"));
		graphOptions.putValue(Action.LONG_DESCRIPTION, "Displays the options dialog for the graph.");

		zoomIn = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JPanel mgm = models.get(theTabs.getSelectedIndex());
				
				if(mgm instanceof ChartPanel)
					((ChartPanel)mgm).zoomInBoth(-1, -1);
			}
		};

		zoomIn.putValue(Action.NAME, "In");
		zoomIn.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
		zoomIn.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallPlayerFwd.png"));
		zoomIn.putValue(Action.LONG_DESCRIPTION, "Zoom in on the graph.");

		zoomOut = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JPanel mgm = models.get(theTabs.getSelectedIndex());
				
				if(mgm instanceof ChartPanel)
					((ChartPanel)mgm).zoomOutBoth(-1, -1);
			}
		};

		zoomOut.putValue(Action.NAME, "Out");
		zoomOut.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		zoomOut.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallPlayerRew.png"));
		zoomOut.putValue(Action.LONG_DESCRIPTION, "Zoom out of the graph.");

		zoomDefault = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JPanel mgm = models.get(theTabs.getSelectedIndex());
				
				if(mgm instanceof ChartPanel)
					((ChartPanel)mgm).restoreAutoBounds();
			}
		};

		zoomDefault.putValue(Action.NAME, "Default");
		zoomDefault.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		zoomDefault.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallPlayerStart.png"));
		zoomDefault.putValue(Action.LONG_DESCRIPTION, "Set the default zoom for the graph.");

		importXML = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (plug.showOpenFileDialog(graFilter) != JFileChooser.APPROVE_OPTION)
					return;
				try {
					Graph mgm = Graph.load(plug.getChooserFile());
					addGraph(mgm);
				} catch (GraphException ex) {
					plug.error("Could not import PRISM graph file:\n" + ex.getMessage());
				}
			}
		};
		importXML.putValue(Action.NAME, "PRISM graph (*.gra)");
		importXML.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
		importXML.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallFileGraph.png"));
		importXML.putValue(Action.LONG_DESCRIPTION, "Imports a saved PRISM graph from a file.");

		exportXML = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (plug.showSaveFileDialog(graFilter) != JFileChooser.APPROVE_OPTION)
					return;
				Graph mgm = (Graph)models.get(theTabs.getSelectedIndex());
				try {
					mgm.save(plug.getChooserFile());
				} catch (PrismException ex) {
					plug.error("Could not export PRISM graph file:\n" + ex.getMessage());
				}
			}
		};
		exportXML.putValue(Action.NAME, "PRISM graph (*.gra)");
		exportXML.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_X));
		exportXML.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallFileGraph.png"));
		exportXML.putValue(Action.LONG_DESCRIPTION, "Export graph as a PRISM graph file.");

		exportImageJPG = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JPanel model = getModel(theTabs.getSelectedIndex());
				
				if(model instanceof ChartPanel){
				
					GUIImageExportDialog imageDialog = new GUIImageExportDialog(plug.getGUI(), (ChartPanel)model, GUIImageExportDialog.JPEG);

					saveImage(imageDialog);
				}
			}
		};
		exportImageJPG.putValue(Action.NAME, "JPEG Interchange Format (*.jpg, *.jpeg)");
		exportImageJPG.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_J));
		exportImageJPG.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallFileImage.png"));
		exportImageJPG.putValue(Action.LONG_DESCRIPTION, "Export graph as a JPEG file.");

		exportImagePNG = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JPanel model = getModel(theTabs.getSelectedIndex());
				
				if(model instanceof ChartPanel){
				
					GUIImageExportDialog imageDialog = new GUIImageExportDialog(plug.getGUI(), (ChartPanel)model, GUIImageExportDialog.PNG);

					saveImage(imageDialog);
				}
			}
		};
		exportImagePNG.putValue(Action.NAME, "Portable Network Graphics (*.png)");
		exportImagePNG.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		exportImagePNG.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallFileImage.png"));
		exportImagePNG.putValue(Action.LONG_DESCRIPTION, "Export graph as a Portable Network Graphics file.");
		
		
		exportPDF = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (plug.showSaveFileDialog(pdfFilter) != JFileChooser.APPROVE_OPTION)
					return;
				
				JPanel mgm = models.get(theTabs.getSelectedIndex());
				
				if(mgm instanceof ChartPanel)
					Graph.exportToPDF(plug.getChooserFile(), ((ChartPanel)mgm).getChart());
				
			}
		};
		exportPDF.putValue(Action.NAME, "Portable document format (*.pdf)");
		exportPDF.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		exportPDF.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallFilePdf.png"));
		exportPDF.putValue(Action.LONG_DESCRIPTION, "Export the graph as a Portable document format file.");

		exportImageEPS = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JPanel model = getModel(theTabs.getSelectedIndex());
				
				if(model instanceof ChartPanel){
				
					GUIImageExportDialog imageDialog = new GUIImageExportDialog(plug.getGUI(), (ChartPanel)model, GUIImageExportDialog.EPS);

					saveImage(imageDialog);
					
				}
			}
		};
		exportImageEPS.putValue(Action.NAME, "Encapsulated PostScript (*.eps)");
		exportImageEPS.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
		exportImageEPS.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallFilePdf.png"));
		exportImageEPS.putValue(Action.LONG_DESCRIPTION, "Export graph as an Encapsulated PostScript file.");

		exportMatlab = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (plug.showSaveFileDialog(matlabFilter) != JFileChooser.APPROVE_OPTION)
					return;
				
				Graph mgm = (Graph)models.get(theTabs.getSelectedIndex());

				try {
					mgm.exportToMatlab(plug.getChooserFile());
				} catch (IOException ex) {
					plug.error("Could not export Matlab file:\n" + ex.getMessage());
				}
			}
		};
		exportMatlab.putValue(Action.NAME, "Matlab file (*.m)");
		exportMatlab.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
		exportMatlab.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallFileMatlab.png"));
		exportMatlab.putValue(Action.LONG_DESCRIPTION, "Export graph as a Matlab file.");
		
		exportGnuplot = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (plug.showSaveFileDialog(gnuplotFilter) != JFileChooser.APPROVE_OPTION)
					return;
				
				JPanel mgm = models.get(theTabs.getSelectedIndex());

				if(mgm instanceof ChartPanel)
				{
			
					try 
					{
						if(mgm instanceof Graph && !(mgm instanceof ParametricGraph)){

							((Graph)mgm).exportToGnuplot(plug.getChooserFile());
						}
						else if(mgm instanceof ParametricGraph){

							((ParametricGraph)mgm).exportToGnuplot(plug.getChooserFile());
						}
						else if(mgm instanceof Histogram){
							((Histogram)mgm).exportToGnuplot(plug.getChooserFile());
						}


					} catch (IOException ex) {
						plug.error("Could not export Gnuplot file:\n" + ex.getMessage());
					}
				}
				
				
			}
		};
		
		exportGnuplot.putValue(Action.NAME, "GNU Plot file(*.gnuplot)");
		exportGnuplot.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
		exportGnuplot.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallgnuplot.png"));
		exportGnuplot.putValue(Action.LONG_DESCRIPTION, "Export graph as a GNU plot file.");

		printGraph = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JPanel graph = models.get(theTabs.getSelectedIndex());

				if(graph instanceof ChartPanel)
				{

					if(graph instanceof Graph)
					{

						if (!((Graph)graph).getDisplaySettings().getBackgroundColor().equals(Color.white)) 
						{
							if (plug.questionYesNo("Your graph has a coloured background, this background will show up on the \n"
									+ "printout. Would you like to make the current background colour white?") == 0) 
							{
								
								((Graph)graph).getDisplaySettings().setBackgroundColor(Color.white);
							}
						}

					}
					else if(graph instanceof Histogram)
					{

						if (!((Histogram)graph).getDisplaySettings().getBackgroundColor().equals(Color.white)) 
						{
							if (plug.questionYesNo("Your graph has a coloured background, this background will show up on the \n"
									+ "printout. Would you like to make the current background colour white?") == 0) 
							{
								((Histogram)graph).getDisplaySettings().setBackgroundColor(Color.white);
							}
						}

					}



					((ChartPanel)graph).createChartPrintJob();
				}
			}
		};
		
		printGraph.putValue(Action.NAME, "Print graph");
		printGraph.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		printGraph.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallPrint.png"));
		printGraph.putValue(Action.LONG_DESCRIPTION, "Print the graph to a printer or file");

		deleteGraph = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				JPanel graph = models.get(theTabs.getSelectedIndex());

				models.remove(theTabs.getSelectedIndex());
				options.remove(theTabs.getSelectedIndex());
				theTabs.remove(graph);
			}
		};
		deleteGraph.putValue(Action.NAME, "Delete graph");
		deleteGraph.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		deleteGraph.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallDelete.png"));
		deleteGraph.putValue(Action.LONG_DESCRIPTION, "Deletes the graph.");

		zoomMenu = new JMenu("Zoom");
		zoomMenu.setMnemonic('Z');
		zoomMenu.setIcon(GUIPrism.getIconFromImage("smallView.png"));
		zoomMenu.add(zoomIn);
		zoomMenu.add(zoomOut);
		zoomMenu.add(zoomDefault);

		exportMenu = new JMenu("Export graph");
		exportMenu.setMnemonic('E');
		exportMenu.setIcon(GUIPrism.getIconFromImage("smallExport.png"));
		exportMenu.add(exportXML);
		exportMenu.add(exportImagePNG);
		exportMenu.add(exportPDF);
		exportMenu.add(exportImageEPS);
		exportMenu.add(exportImageJPG);

		exportMenu.add(exportMatlab);
		exportMenu.add(exportGnuplot);

		importMenu = new JMenu("Import graph");
		importMenu.setMnemonic('I');
		importMenu.setIcon(GUIPrism.getIconFromImage("smallImport.png"));
		importMenu.add(importXML);

		graphMenu.add(graphOptions);
		graphMenu.add(zoomMenu);
		graphMenu.addSeparator();
		graphMenu.add(printGraph);
		graphMenu.add(deleteGraph);
		graphMenu.addSeparator();
		graphMenu.add(exportMenu);
		graphMenu.add(importMenu);

		/* Tab context menu */
		backMenu.add(importXML);
	}

	public void saveImage(GUIImageExportDialog imageDialog)
	{
		if (!imageDialog.isCancelled()) {

			JPanel graph = getModel(theTabs.getSelectedIndex());

			if(graph instanceof ChartPanel){

				if(graph instanceof Graph){
					/* If background is not white, and it will show up, then lets warn everyone. */
					if (!((Graph)graph).getDisplaySettings().getBackgroundColor().equals(Color.white)
							&& (imageDialog.getImageType() != GUIImageExportDialog.PNG || !imageDialog.getAlpha())) {
						if (plug.questionYesNo("Your graph has a coloured background, this background will show up on the \n"
								+ "exported image. Would you like to make the current background colour white?") == 0) {
							((Graph)graph).getDisplaySettings().setBackgroundColor(Color.white);
						}
					}
				}
				else if(graph instanceof Histogram){

					/* If background is not white, and it will show up, then lets warn everyone. */
					if (!((Histogram)graph).getDisplaySettings().getBackgroundColor().equals(Color.white)
							&& (imageDialog.getImageType() != GUIImageExportDialog.PNG || !imageDialog.getAlpha())) {
						if (plug.questionYesNo("Your graph has a coloured background, this background will show up on the \n"
								+ "exported image. Would you like to make the current background colour white?") == 0) {
							((Histogram)graph).getDisplaySettings().setBackgroundColor(Color.white);
						}
					}

				}

				if (imageDialog.getImageType() == GUIImageExportDialog.JPEG) {
					if (plug.showSaveFileDialog(jpgFilter) != JFileChooser.APPROVE_OPTION)
						return;
					try {
						Graph.exportToJPEG(plug.getChooserFile(), ((ChartPanel)graph).getChart(), imageDialog.getExportWidth(), imageDialog.getExportHeight());
					} catch (GraphException ex) {
						plug.error("Could not export JPEG file:\n" + ex.getMessage());
					} catch (IOException ex) {
						plug.error("Could not export JPEG file:\n" + ex.getMessage());
					}
				} else if (imageDialog.getImageType() == GUIImageExportDialog.PNG) {
					if (plug.showSaveFileDialog(pngFilter) != JFileChooser.APPROVE_OPTION)
						return;
					try {

						Graph.exportToPNG(plug.getChooserFile(), ((ChartPanel)graph).getChart(), imageDialog.getExportWidth(), imageDialog.getExportHeight(), imageDialog.getAlpha());
					} catch (GraphException ex) {
						plug.error("Could not export PNG file:\n" + ex.getMessage());
					} catch (IOException ex) {
						plug.error("Could not export PNG file:\n" + ex.getMessage());
					}
				} else if (imageDialog.getImageType() == GUIImageExportDialog.EPS) {
					if (plug.showSaveFileDialog(epsFilter) != JFileChooser.APPROVE_OPTION)
						return;
					try {
						Graph.exportToEPS(plug.getChooserFile(), imageDialog.getExportWidth(), imageDialog.getExportHeight(), ((ChartPanel)graph).getChart());
					} catch (GraphException ex) {
						plug.error("Could not export EPS file:\n" + ex.getMessage());
					} catch (IOException ex) {
						plug.error("Could not export EPS file:\n" + ex.getMessage());
					}
				}
			}
		}
	}

	public Action getPrintGraph()
	{
		return printGraph;
	}

	public Action getDeleteGraph()
	{
		if (canDelete)
			return deleteGraph;
		return null;
	}

	public int addGraph(JPanel m)
	{
		String name = "";

		boolean nameNew;
		int counter = 1;

		while (true) {
			name = "Graph " + (counter);
			nameNew = true;

			for (int i = 0; i < theTabs.getComponentCount(); i++) {
				if (theTabs.getTitleAt(i).equals(name))
					nameNew = false;
			}

			if (nameNew)
				return addGraph(m, name);

			counter++;
		}
	}

	public int addGraph(JPanel m, String tabName)
	{
		// add the model to the list of models
		models.add(m);

		// make the graph appear as a tab
		theTabs.add(m);
		
		options.add(new GraphOptions(plug, m, plug.getGUI(), "Options for graph " + tabName));

		if(m instanceof ChartPanel)
			// anything that happens to the graph should propagate
			m.addMouseListener(this);
		else if(m instanceof Graph3D)
			((Graph3D)m).addMouseListener(this);

		// get the index of this model in the model list
		int index = models.indexOf(m);

		// increase the graph count and title the tab
		theTabs.setTitleAt(index, tabName);

		// make this new tab the default selection
		theTabs.setSelectedIndex(theTabs.indexOfComponent(m));

		// return the index of the component
		return index;
	}

	public void jumpToGraph(ChartPanel m)
	{
		for (int i = 0; i < models.size(); i++) {
			if (m == models.get(i)) {
				theTabs.setSelectedComponent(m);
				break;
			}
		}
	}

	public JPanel getModel(int i)
	{	
		return models.get(i);
	}

	public JPanel getModel(String tabHeader)
	{
		for (int i = 0; i < theTabs.getComponentCount(); i++) {
			if (theTabs.getTitleAt(i).equals(tabHeader)) {
				return getModel(i);
			}
		}
		return null;
	}

	public int getNumModels()
	{
		return models.size();
	}

	public String getGraphName(int i)
	{
		return theTabs.getTitleAt(i);
	}

	// User right clicked on a tab
	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger()) {
			popUpTriggered(e);
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		// Zoom out on double click
		if (e.getClickCount() == 2) {
			if (e.getSource() instanceof Graph) {
				((Graph) e.getSource()).restoreAutoBounds();
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger()) {
			popUpTriggered(e);
		}
	}

	private void popUpTriggered(MouseEvent e)
	{
		if (e.getSource() == theTabs)//just show the background popup
		{
			int index = theTabs.indexAtLocation(e.getX(), e.getY());
			if (index != -1) {
				graphOptions.setEnabled(true);
				zoomMenu.setEnabled(true);

				exportMenu.setEnabled(true);
				importMenu.setEnabled(true);

				printGraph.setEnabled(true);
				deleteGraph.setEnabled(true);

				theTabs.setSelectedIndex(index);

				this.graphMenu.show(theTabs, e.getX(), e.getY());
			} else {
				graphOptions.setEnabled(false);
				zoomMenu.setEnabled(false);

				exportMenu.setEnabled(false);
				importMenu.setEnabled(true);

				printGraph.setEnabled(false);
				deleteGraph.setEnabled(false);

				this.graphMenu.show(theTabs, e.getX(), e.getY());
			}
			return;
		}

		for (int i = 0; i < models.size(); i++) {
			
		if (e.getSource() == models.get(i)) {
				
				graphOptions.setEnabled(true);
				zoomMenu.setEnabled(true);

				exportMenu.setEnabled(true);
				importMenu.setEnabled(true);

				printGraph.setEnabled(true);
				deleteGraph.setEnabled(true);
				
				exportMatlab.setEnabled(getModel(i) instanceof Graph);
				exportXML.setEnabled(getModel(i) instanceof Graph);

				theTabs.setSelectedIndex(i);
				this.graphMenu.show(models.get(i), e.getX(), e.getY());
				return;
			}
		}
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
	}

	// don't implement these for tabs
	//public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}
	
	public JPopupMenu getGraphMenu(){
		return this.graphMenu;
	}

}
