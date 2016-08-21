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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartPanel;

import com.orsoncharts.graphics3d.ExportUtils;
import com.orsoncharts.graphics3d.ViewPoint3D;

import cern.jet.math.Functions;
import param.Function;
import parser.Values;
import parser.ast.Expression;
import parser.ast.ExpressionConstant;
import parser.ast.ExpressionIdent;
import parser.type.TypeDouble;
import parser.visitor.ASTTraverseModify;
import prism.PrismException;
import prism.PrismLangException;
import userinterface.GUIPlugin;
import userinterface.GUIPrism;
import userinterface.graph.GUIImageExportDialog;
import userinterface.graph.Graph;
import userinterface.graph.Graph3D;
import userinterface.graph.GraphException;
import userinterface.graph.GraphOptions;
import userinterface.graph.Histogram;
import userinterface.graph.ParametricGraph;
import userinterface.graph.ParametricGraph3D;
import userinterface.graph.PrismXYDataItem;
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
	private Action importXML, addFunction;

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
		theTabs = new JTabbedPane(){

			@Override
			public String getTitleAt(int index) {
				try{
					TabClosePanel panel = (TabClosePanel)getTabComponentAt(index);
					return panel.getTitle();
				}
				catch(Exception e){
					return "";
				}
			}

			@Override
			public String getToolTipTextAt(int index) {
				return ((TabClosePanel)getTabComponentAt(index)).getToolTipText();
			}

			@Override
			public void setTitleAt(int index, String title) {
				
				if(((TabClosePanel)getTabComponentAt(index)) == null){
					return;
				}
				
				((TabClosePanel)getTabComponentAt(index)).setTitle(title);
			}

			@Override
			public void setIconAt(int index, Icon icon) {
				((TabClosePanel)getTabComponentAt(index)).setIcon(icon);
			}

			@Override
			public void setToolTipTextAt(int index, String toolTipText) {
				((TabClosePanel)getTabComponentAt(index)).setToolTip(toolTipText);
			}
			
			
			
		};
		
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
				else if(mgm instanceof Graph3D)
				{
					double rho = ((Graph3D)mgm).getChart3DPanel().getViewPoint().getRho();
					((Graph3D)mgm).getChart3DPanel().getViewPoint().setRho(rho-5);
					((Graph3D)mgm).getChart3DPanel().repaint();
				}
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
				else if(mgm instanceof Graph3D)
				{
					double rho = ((Graph3D)mgm).getChart3DPanel().getViewPoint().getRho();
					((Graph3D)mgm).getChart3DPanel().getViewPoint().setRho(rho+5);
					((Graph3D)mgm).getChart3DPanel().repaint();			
				}
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
				else if(mgm instanceof Graph3D)
				{
					((Graph3D)mgm).getChart3DPanel().zoomToFit();
					((Graph3D)mgm).getChart3DPanel().getDrawable().setViewPoint(new ViewPoint3D(-Math.PI / 2, Math.PI * 1.124, 70.0, 0.0));
					((Graph3D)mgm).getChart3DPanel().repaint();
					
				}
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
		
		addFunction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				plotNewFunction();
			}
		};
		addFunction.putValue(Action.NAME, "Plot function");
		addFunction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		addFunction.putValue(Action.SMALL_ICON, GUIPrism.getIconFromImage("smallFunction.png"));
		addFunction.putValue(Action.LONG_DESCRIPTION, "Plots a new specified function on the current graph");

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
				
					GUIImageExportDialog imageDialog = new GUIImageExportDialog(plug.getGUI(), model, GUIImageExportDialog.JPEG);
					saveImage(imageDialog);
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
				GUIImageExportDialog imageDialog = new GUIImageExportDialog(plug.getGUI(), model, GUIImageExportDialog.PNG);
				saveImage(imageDialog);
				
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
				else if(mgm instanceof Graph3D){

					Graph3D.exportToPDF(plug.getChooserFile(), ((Graph3D)mgm).getChart3DPanel());
				}
				
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
				
				
				JPanel mgm = models.get(theTabs.getSelectedIndex());
				
				if(mgm instanceof Graph){
					
					try {
						
						((Graph)mgm).exportToMatlab(plug.getChooserFile());
					
					} catch (IOException ex) {
						plug.error("Could not export Matlab file:\n" + ex.getMessage());
					}
					
				}
				else if(mgm instanceof Graph3D){
					
					try {
						
						((Graph3D)mgm).exportToMatlab(plug.getChooserFile());
					
					} catch (IOException e1) {
						
						plug.error("Could not export Matlab file:\n" + e1.getMessage());
						e1.printStackTrace();
					}
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
				
				else if(mgm instanceof Graph3D){
					
					try{
						
						((Graph3D)mgm).exportToGnuplot(plug.getChooserFile());
					
					} catch(IOException ex){
						
						plug.error("Could not export Gnuplot file:\n" + ex.getMessage());
						ex.printStackTrace();
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
				
				if(graph instanceof Graph3D){
					
					((Graph3D)graph).createPrintJob();
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
		graphMenu.add(addFunction);

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
			
			else if(graph instanceof Graph3D){
				
				Graph3D g3d = (Graph3D)graph;
				
				if (imageDialog.getImageType() == GUIImageExportDialog.JPEG) {
					
					if (plug.showSaveFileDialog(jpgFilter) != JFileChooser.APPROVE_OPTION)
						return;
					
					try {

						File file = null;


						if(!plug.getChooserFile().getName().contains(".jpg") || !plug.getChooserFile().getName().contains(".jpeg")){

							file = new File(plug.getChooserFile().getAbsolutePath() + ".jpg");

						}
						else{

							file = plug.getChooserFile();
						}

						ExportUtils.writeAsJPEG(g3d.getChart3DPanel().getDrawable(), imageDialog.getExportWidth(), imageDialog.getExportHeight(), file);
						
					} catch (IOException e) {
						JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
					
				}
				else if(imageDialog.getImageType() == GUIImageExportDialog.PNG){
					
					if (plug.showSaveFileDialog(pngFilter) != JFileChooser.APPROVE_OPTION)
						return;
					
					File file = null;
					
					try{
						
						if(!plug.getChooserFile().getName().contains(".png")){
							
							file = new File(plug.getChooserFile().getAbsolutePath() + ".png");
							
						}
						else{
							
							file = plug.getChooserFile();
						}
						
						ExportUtils.writeAsPNG(g3d.getChart3DPanel().getDrawable(), imageDialog.getExportWidth(), imageDialog.getHeight(), file);
						
					}catch (IOException e) {
						
						JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
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
		
		//set the tabclosepanel component so we see the close button
		TabClosePanel closePanel = new TabClosePanel(tabName);
		closePanel.addMouseListener(this);
		theTabs.setTabComponentAt(index, closePanel);

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
				
				exportImageEPS.setEnabled(true);
				exportMatlab.setEnabled(getModel(i) instanceof Graph);
				exportXML.setEnabled(getModel(i) instanceof Graph);

				theTabs.setSelectedIndex(i);
				this.graphMenu.show(models.get(i), e.getX(), e.getY());
				return;
			}
		}
	}
	
	public void doGraph3DEnables(){
		
		exportImageEPS.setEnabled(false);
		exportXML.setEnabled(false);
		printGraph.setEnabled(false);
		
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
	
	public void plotNewFunction(){
		
		JDialog dialog;
		JRadioButton radio2d, radio3d, newGraph, existingGraph;
		JTextField functionField, seriesName;
		JButton ok, cancel;
		JComboBox<String> chartOptions;
		JLabel example;
		
		//init all the fields of the dialog
		dialog = new JDialog(GUIPrism.getGUI());
		radio2d = new JRadioButton("2D");
		radio3d = new JRadioButton("3D");
		newGraph = new JRadioButton("New Graph");
		existingGraph = new JRadioButton("Exisiting");
		chartOptions = new JComboBox<String>();
		functionField = new JTextField();
		ok = new JButton("Plot");
		cancel = new JButton("Cancel");
		seriesName = new JTextField();
		example = new JLabel("<html><font size=3 color=red>Example:</font><font size=3>x/2 + 5</font></html>");
		example.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				example.setCursor(new Cursor(Cursor.HAND_CURSOR));
				example.setForeground(Color.BLUE);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				example.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				example.setForeground(Color.BLACK);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(e.getButton() == MouseEvent.BUTTON1){
					
					if(radio2d.isSelected()){
						functionField.setText("x/2 + 5");
					}
					else{
						functionField.setText("x+y+5");
					}
					
					functionField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
					functionField.setForeground(Color.BLACK);
					
				}
			}
			
			
			
		});
		
		//set dialog properties
		dialog.setSize(400, 350);
		dialog.setTitle("Plot a new function");
		dialog.setModal(true);
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
		dialog.setLocationRelativeTo(GUIPrism.getGUI());
		
		//add every component to their dedicated panels
		JPanel graphTypePanel = new JPanel(new FlowLayout());
		graphTypePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Function type"));
		graphTypePanel.add(radio2d);
		graphTypePanel.add(radio3d);
		
		JPanel functionFieldPanel = new JPanel(new BorderLayout());
		functionFieldPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Function"));
		functionFieldPanel.add(functionField, BorderLayout.CENTER);
		functionFieldPanel.add(example, BorderLayout.SOUTH);
		
		JPanel chartSelectPanel = new JPanel();
		chartSelectPanel.setLayout(new BoxLayout(chartSelectPanel, BoxLayout.Y_AXIS));
		chartSelectPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Plot function to"));
		JPanel radioPlotPanel = new JPanel(new FlowLayout());
		radioPlotPanel.add(newGraph);
		radioPlotPanel.add(existingGraph);
		JPanel chartOptionsPanel = new JPanel(new FlowLayout());
		chartOptionsPanel.add(chartOptions);
		chartSelectPanel.add(radioPlotPanel);
		chartSelectPanel.add(chartOptionsPanel);
		
		JPanel bottomControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomControlPanel.add(ok);
		bottomControlPanel.add(cancel);
		
		JPanel seriesNamePanel = new JPanel(new BorderLayout());
		seriesNamePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Series name"));
		seriesNamePanel.add(seriesName, BorderLayout.CENTER);
		
		// add all the panels to the dialog
		
		dialog.add(graphTypePanel);
		dialog.add(functionFieldPanel);
		dialog.add(chartSelectPanel);
		dialog.add(seriesNamePanel);
		dialog.add(bottomControlPanel);
		
		// do all the enables and set properties
		
		radio2d.setSelected(true);
		newGraph.setSelected(true);
		chartOptions.setEnabled(false);
		functionField.setText("Add function expression here....");
		functionField.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 15));
		functionField.setForeground(Color.GRAY);
		seriesName.setText("New function");
		ok.setMnemonic('P');
		cancel.setMnemonic('C');
		example.setToolTipText("click to try out");
		
		ok.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ok");
		ok.getActionMap().put("ok", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ok.doClick();
			}
		});
		
		cancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		cancel.getActionMap().put("cancel", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel.doClick();
			}
		});
		
		boolean found = false;
		
		for(int i = 0 ; i < theTabs.getTabCount() ; i++){
			
			if(theTabs.getComponentAt(i) instanceof Graph){
				chartOptions.addItem(getGraphName(i));
				found = true;
			}
		}
		
		if(!found){
			
			existingGraph.setEnabled(false);
			chartOptions.setEnabled(false);
		}
		
		//add all the action listeners
		
		radio2d.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(radio2d.isSelected()){
					
					radio3d.setSelected(false);
					
					if(chartOptions.getItemCount() > 0){
						existingGraph.setEnabled(true);
						chartOptions.setEnabled(true);
					}
					
					example.setText("<html><font size=3 color=red>Example:</font><font size=3>x/2 + 5</font></html>");
				}
			}
		});
		
		radio3d.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(radio3d.isSelected()){
					
					radio2d.setSelected(false);
					newGraph.setSelected(true);
					existingGraph.setEnabled(false);
					chartOptions.setEnabled(false);
					example.setText("<html><font size=3 color=red>Example:</font><font size=3>x+y+5</font></html>");
					
				}
				
			}
		});
		
		newGraph.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(newGraph.isSelected()){
					existingGraph.setSelected(false);
					chartOptions.setEnabled(false);
				}
			}
		});
		
		existingGraph.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(existingGraph.isSelected()){
					
					newGraph.setSelected(false);
					chartOptions.setEnabled(true);
				}
			}
		});
		
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String function = functionField.getText();
				
				Expression expr = null;
				
				try {
					
					expr = GUIPrism.getGUI().getPrism().parseSingleExpressionString(function);
					expr = (Expression) expr.accept(new ASTTraverseModify()
					{

						@Override
						public Object visit(ExpressionIdent e) throws PrismLangException 
						{
							return new ExpressionConstant(e.getName(), TypeDouble.getInstance());
						}
						
					});
					
					expr.typeCheck();
					expr.semanticCheck();
					
				} catch (PrismLangException e1) {
					
					// for copying style
				    JLabel label = new JLabel();

				    // html content in our case the error we want to show
				    JEditorPane ep = new JEditorPane("text/html","<html> There was an error parsing the function. To read about what built-in"
							+ " functions are supported <br>and some more information on the functions, visit "
							+ "<a href='http://www.prismmodelchecker.org/manual/ThePRISMLanguage/Expressions'>Prism expressions site</a>."
							+ "<br><br><font color=red>Error: </font>" + e1.getMessage() +" </html>"); 

				    // handle link events
				    ep.addHyperlinkListener(new HyperlinkListener()
				    {
				        @Override
				        public void hyperlinkUpdate(HyperlinkEvent e)
				        {
				            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)){
								try {
									Desktop.getDesktop().browse(e.getURL().toURI());
								} catch (IOException | URISyntaxException e1) {
									
									e1.printStackTrace();
								}
				            }
				        }
				    });
				    ep.setEditable(false);
				    ep.setBackground(label.getBackground());

				    // show the error dialog
				    JOptionPane.showMessageDialog(dialog, ep, "Parse Error", JOptionPane.ERROR_MESSAGE);					
					return;
				}
				
				
				if(radio2d.isSelected()){
					
					ParametricGraph graph = null;
					
					if(newGraph.isSelected()){
						
						graph = new ParametricGraph("");
					}
					else{
						
						for(int i = 0 ; i < theTabs.getComponentCount() ; i++){
							
							if(theTabs.getTitleAt(i).equals(chartOptions.getSelectedItem())){
								
								graph = (ParametricGraph)theTabs.getComponent(i);
							}
						}
						
					}
					
					dialog.dispose();
					defineConstantsAndPlot(expr, graph,seriesName.getText(), newGraph.isSelected(),  true);
					
				}
				else if(radio3d.isSelected()){
					
					try {

						expr = (Expression) expr.accept(new ASTTraverseModify()
						{
							@Override
							public Object visit(ExpressionIdent e) throws PrismLangException
							{
								return new ExpressionConstant(e.getName(), TypeDouble.getInstance());
							}

						});

						expr.semanticCheck();
						expr.typeCheck();
						
					} catch (PrismLangException e1) {
						e1.printStackTrace();
					}
					
					if(expr.getAllConstants().size() < 2){
						
						JOptionPane.showMessageDialog(dialog, "There are not enough variables in the function to plot a 3D chart!", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					// its always a new graph
					ParametricGraph3D graph = new ParametricGraph3D(expr);
					dialog.dispose();
					defineConstantsAndPlot(expr, graph,seriesName.getText(), true, false);
				}
				
				dialog.dispose();
			}
		});
		
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		
		// we will show info about the function when field is out of focus
		functionField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				
				if(!functionField.getText().equals("")){
					return;
				}
				
				functionField.setText("Add function expression here....");
				functionField.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 15));
				functionField.setForeground(Color.GRAY);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				
				if(!functionField.getText().equals("Add function expression here....")){
					return;
				}
				
				functionField.setForeground(Color.BLACK);
				functionField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
				functionField.setText("");
			}
		});
		
		// show the dialog
		dialog.setVisible(true);
	}
	
	public void defineConstantsAndPlot(Expression expr, JPanel graph, String seriesName, Boolean isNewGraph, boolean is2D){
		
		JDialog dialog;
		JButton ok, cancel;
		JScrollPane scrollPane;
		ConstantPickerList constantList;
		JComboBox<String> xAxis, yAxis;
		
		//init everything
		dialog = new JDialog(GUIPrism.getGUI());
		dialog.setTitle("Define constants and select axes");
		dialog.setSize(500,400);
		dialog.setLocationRelativeTo(GUIPrism.getGUI());
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
		dialog.setModal(true);
		
		constantList = new ConstantPickerList();
		scrollPane = new JScrollPane(constantList);
		
		xAxis = new JComboBox<String>();
		yAxis = new JComboBox<String>();
		
		ok = new JButton("Ok");
		ok.setMnemonic('O');
		cancel = new JButton("Cancel");
		cancel.setMnemonic('C');
		
		//add all components to their dedicated panels
		JPanel exprPanel = new JPanel(new FlowLayout());
		exprPanel.setBorder(new TitledBorder("Function"));
		exprPanel.add(new JLabel(expr.toString()));
		
		JPanel axisPanel = new JPanel();
		axisPanel.setBorder(new TitledBorder("Select axis constants"));
		axisPanel.setLayout(new BoxLayout(axisPanel, BoxLayout.Y_AXIS));
		JPanel xAxisPanel = new JPanel(new FlowLayout());
		xAxisPanel.add(new JLabel("X axis:"));
		xAxisPanel.add(xAxis);
		JPanel yAxisPanel = new JPanel(new FlowLayout());
		yAxisPanel.add(new JLabel("Y axis:"));
		yAxisPanel.add(yAxis);
		axisPanel.add(xAxisPanel);
		axisPanel.add(yAxisPanel);
		
		JPanel constantPanel = new JPanel(new BorderLayout());
		constantPanel.setBorder(new TitledBorder("Please define the following constants"));
		constantPanel.add(scrollPane, BorderLayout.CENTER);
		constantPanel.add(new ConstantHeader(), BorderLayout.NORTH);
		
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.add(ok);
		bottomPanel.add(cancel);
		
		//fill the axes components
		
		for(int i = 0 ; i < expr.getAllConstants().size() ; i++){
			
			xAxis.addItem(expr.getAllConstants().get(i));
			
			if(!is2D)
				yAxis.addItem(expr.getAllConstants().get(i));
		}
		
		if(!is2D){
			yAxis.setSelectedIndex(1);
		}
		
		
		//fill the constants table
		
		for(int i = 0 ; i < expr.getAllConstants().size() ; i++){
			
			ConstantLine line = new ConstantLine(expr.getAllConstants().get(i), TypeDouble.getInstance());
			constantList.addConstant(line);
			
			if(!is2D){
				if(line.getName().equals(xAxis.getSelectedItem().toString()) ||
						line.getName().equals(yAxis.getSelectedItem().toString())){

					line.doFuncEnables(false);
				}
				else{
					line.doFuncEnables(true);
				}
			}
			
		}
		
		//do enables
		
		if(is2D){
			yAxis.setEnabled(false);
		}
		
		ok.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ok");
		ok.getActionMap().put("ok", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ok.doClick();
			}
		});
		
		cancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		cancel.getActionMap().put("cancel", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel.doClick();
			}
		});
		
		//add action listeners
		
		xAxis.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if(is2D){
					return;
				}
				
				String item = xAxis.getSelectedItem().toString();

				if(item.equals(yAxis.getSelectedItem().toString())){

					int index = yAxis.getSelectedIndex();

					if(index != yAxis.getItemCount()-1){
						yAxis.setSelectedIndex(++index);
					}
					else{
						yAxis.setSelectedIndex(--index);
					}


				}

				for(int i = 0 ; i < constantList.getNumConstants() ; i++){

					ConstantLine line = constantList.getConstantLine(i);

					if(line.getName().equals(xAxis.getSelectedItem().toString())
							|| line.getName().equals(yAxis.getSelectedItem().toString())){

						line.doFuncEnables(false);	
					}
					else{

						line.doFuncEnables(true);
					}

				}
			}
		});

		yAxis.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String item = yAxis.getSelectedItem().toString();

				if(item.equals(xAxis.getSelectedItem().toString())){

					int index = xAxis.getSelectedIndex();

					if(index != xAxis.getItemCount()-1){
						xAxis.setSelectedIndex(++index);
					}
					else{
						xAxis.setSelectedIndex(--index);
					}
				}

				for(int i = 0 ; i < constantList.getNumConstants() ; i++){

					ConstantLine line = constantList.getConstantLine(i);

					if(line.getName().equals(xAxis.getSelectedItem().toString())
							|| line.getName().equals(yAxis.getSelectedItem().toString())){

						line.doFuncEnables(false);	
					}
					else{

						line.doFuncEnables(true);
					}

				}
			}
		});

		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					
					constantList.checkValid();
			
				} catch (PrismException e2) {
					JOptionPane.showMessageDialog(dialog, "<html> One or more of the defined constants are invalid. <br><br> <font color=red>Error: </font>"+ 
							e2.getMessage() + "</html>", "Parse Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				

				if(is2D){

					// it will always be a parametric graph in 2d case
					ParametricGraph pGraph = (ParametricGraph)graph;

					ConstantLine xAxisConstant = constantList.getConstantLine(xAxis.getSelectedItem().toString());

					if(xAxisConstant == null){
						//should never happen
						JOptionPane.showMessageDialog(dialog, "The selected axis is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					double xStartValue = Double.parseDouble(xAxisConstant.getStartValue());
					double xEndValue = Double.parseDouble(xAxisConstant.getEndValue());
					double xStepValue = Double.parseDouble(xAxisConstant.getStepValue());


					int seriesCount = 0;

					for(int i = 0 ; i < constantList.getNumConstants() ; i++){

						ConstantLine line = constantList.getConstantLine(i);

						if(line.getName().equals(xAxis.getSelectedItem().toString())){
							seriesCount++;
						}
						else{

							seriesCount += line.getTotalNumOfValues();

						}
					}

					// if there will be too many series to be plotted, ask the user if they are sure they know what they are doing
					if(seriesCount > 10){

						int res = JOptionPane.showConfirmDialog(dialog, "This configuration will create " + seriesCount + " series. Continue?", "Confirm", JOptionPane.YES_NO_OPTION);

						if(res == JOptionPane.NO_OPTION){
							return;
						}

					}

					Values singleValuesGlobal = new Values();

					//add the single values to a global values list

					for(int i = 0 ; i < constantList.getNumConstants() ; i++){

						ConstantLine line = constantList.getConstantLine(i);

						if(line.getTotalNumOfValues() == 1){

							double singleValue = Double.parseDouble(line.getSingleValue());
							singleValuesGlobal.addValue(line.getName(), singleValue);
						}
					}

					// we just have one variable so we can plot directly
					if(constantList.getNumConstants() == 1 || singleValuesGlobal.getNumValues() == constantList.getNumConstants()-1){


						SeriesKey key = pGraph.addSeries(seriesName);
						
						pGraph.hideShape(key);
						pGraph.getXAxisSettings().setHeading(xAxis.getSelectedItem().toString());
						pGraph.getYAxisSettings().setHeading("function");

						for(double x = xStartValue ; x <= xEndValue ; x+= xStepValue){

							double ans = -1;

							Values vals = new Values();
							vals.addValue(xAxis.getSelectedItem().toString(), x);
							
							for(int ii = 0 ; ii < singleValuesGlobal.getNumValues() ; ii++){
								
								try {
									vals.addValue(singleValuesGlobal.getName(ii), singleValuesGlobal.getDoubleValue(ii));
								} catch (PrismLangException e1) {
									e1.printStackTrace();
								}
								
							}

							try {
								ans = expr.evaluateDouble(vals);
							} catch (PrismLangException e1) {
								e1.printStackTrace();
							}

							pGraph.addPointToSeries(key, new PrismXYDataItem(x, ans));

						}

						if(isNewGraph){
							addGraph(pGraph);
						}

						dialog.dispose();	
						return;
					}
					
					for(int i = 0 ; i < constantList.getNumConstants() ; i++){
						
						ConstantLine line = constantList.getConstantLine(i);
						
						if(line == xAxisConstant || singleValuesGlobal.contains(line.getName())){
							continue;
						}
						
						double lineStart = Double.parseDouble(line.getStartValue());
						double lineEnd = Double.parseDouble(line.getEndValue());
						double lineStep = Double.parseDouble(line.getStepValue());
						
						for(double j = lineStart ; j < lineEnd ; j+=lineStep){
							
							SeriesKey key = pGraph.addSeries(seriesName);
							pGraph.hideShape(key);
							
							for(double x = xStartValue ; x <= xEndValue ; x+= xStepValue){
								
								double ans = -1;

								Values vals = new Values();
								vals.addValue(xAxis.getSelectedItem().toString(), x);
								vals.addValue(line.getName(), j);
								
								for(int ii = 0 ; ii < singleValuesGlobal.getNumValues() ; ii++){
									
									try {
										vals.addValue(singleValuesGlobal.getName(ii), singleValuesGlobal.getDoubleValue(ii));
									} catch (PrismLangException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									
								}
								
								for(int ii = 0 ; ii < constantList.getNumConstants() ; ii++){
									
									if(constantList.getConstantLine(ii) == xAxisConstant 
											|| singleValuesGlobal.contains(constantList.getConstantLine(ii).getName())
											|| constantList.getConstantLine(ii) == line){
										
										continue;
									}
									
									ConstantLine temp = constantList.getConstantLine(ii);
									double val = Double.parseDouble(temp.getStartValue());
									
									vals.addValue(temp.getName(), val);
								}
								
								try {
									ans = expr.evaluateDouble(vals);
								} catch (PrismLangException e1) {
									e1.printStackTrace();
								}

								pGraph.addPointToSeries(key, new PrismXYDataItem(x, ans));
							}
						}
						
					}

					if(isNewGraph){
						addGraph(graph);
					}
				}
				else{
					
					// this will always be a parametric graph for the 3d case
					ParametricGraph3D pGraph = (ParametricGraph3D)graph;
					
					//add the graph to the gui
					addGraph(pGraph);
					
					//Get the constants we want to put on the x and y axis
					ConstantLine xAxisConstant = constantList.getConstantLine(xAxis.getSelectedItem().toString());
					ConstantLine yAxisConstant = constantList.getConstantLine(yAxis.getSelectedItem().toString());

					//add the single values to a global values list

					Values singleValuesGlobal = new Values();
					
					for(int i = 0 ; i < constantList.getNumConstants() ; i++){

						ConstantLine line = constantList.getConstantLine(i);

						if(line.getTotalNumOfValues() == 1){

							double singleValue = Double.parseDouble(line.getSingleValue());
							singleValuesGlobal.addValue(line.getName(), singleValue);
						}
					}
					
					pGraph.setGlobalValues(singleValuesGlobal);
					pGraph.setAxisConstants(xAxis.getSelectedItem().toString(), yAxis.getSelectedItem().toString());
					pGraph.setBounds(xAxisConstant.getStartValue(), xAxisConstant.getEndValue(), yAxisConstant.getStartValue(), yAxisConstant.getEndValue());

					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							//plot the graph
							pGraph.plot(expr.toString(), xAxis.getSelectedItem().toString(), "Function", yAxis.getSelectedItem().toString());
							
							//calculate the sampling rates from the step sizes as given by the user
							
							int xSamples = (int)((Double.parseDouble(xAxisConstant.getEndValue()) - Double.parseDouble(xAxisConstant.getStartValue())) / Double.parseDouble(xAxisConstant.getStepValue()));
							int ySamples = (int)((Double.parseDouble(xAxisConstant.getEndValue()) - Double.parseDouble(xAxisConstant.getStartValue())) / Double.parseDouble(xAxisConstant.getStepValue()));
							
							pGraph.setSamplingRates(xSamples, ySamples);	
						}
					});

				}
				
				dialog.dispose();
			}
		});
		
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		
		//add everything to the dialog show the dialog
		
		dialog.add(exprPanel);
		dialog.add(axisPanel);
		dialog.add(constantPanel);
		dialog.add(bottomPanel);
		dialog.setVisible(true);
		
	}
	
	/**
	 * Class for adding a close button to the tabs
	 * @author Muhammad Omer Saeed
	 */
	private class TabClosePanel extends JPanel{
		
		private static final long serialVersionUID = 3375988660604064366L;
		private JLabel label;
		private JButton close;
		
		public TabClosePanel(String title){
			
			init(title);
			addToPanel();
			addListeners();
			addActions();

		}

		public void init(String title){
			
			setOpaque(false);
			label = new JLabel(title);
			label.setOpaque(false);
			close = new JButton("<html><font size=2><b>X</b></font></html>");
			close.setContentAreaFilled(false);
			close.setPreferredSize(new Dimension(20, 10));
			close.setBorderPainted(false);
			close.setToolTipText("close this tab");
			
			
		}
		
		public void addToPanel(){
			setLayout(new BorderLayout());
			add(label, BorderLayout.CENTER);
			add(close, BorderLayout.EAST);
			setPreferredSize(getPreferredSize());
			validate();
			
		}
		
		public void addActions(){
			close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					
					String title = label.getText();
					
					int index = -1;
					
					for(int i = 0 ; i < theTabs.getComponentCount() ; i++){
						
						if(theTabs.getTitleAt(i).equals(title)){
							index = i;
						}
					}
					
					if(index == -1){
						return;
					}
					
					JPanel graph = models.get(index);

					models.remove(index);
					options.remove(index);
					theTabs.remove(graph);
					
				}
			});
		}
		
		public void addListeners(){
			
			close.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseEntered(MouseEvent e) {
					close.setForeground(Color.red);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					close.setForeground(Color.BLACK);
				}

			});
			}
		
		@Override
		public void addMouseListener(MouseListener l){}
		
		public void setIcon(Icon icon){
			label.setIcon(icon);
		}

		public void setTitle(String title){
			label.setText(title);
		}
		
		public void setToolTip(String tip) {
			label.setToolTipText(tip);
		}
		
		public String getTitle(){
			return label.getText();
		}

		@Override
		public String getToolTipText() {
			return label.getToolTipText();
		}
		
		
		
	}

}
