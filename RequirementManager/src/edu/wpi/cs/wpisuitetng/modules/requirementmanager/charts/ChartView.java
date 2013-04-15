/*******************************************************************************
 * Copyright (c) 2013 -- WPI Suite
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Robert Dabrowski
 *		Danielle LaRose
 *		Edison Jimenez
 *		Christian Gonzalez
 *		Mike Calder
 *		John Bosworth
 *		Paula Rudy
 *		Gabe Isko
 *		Bangyan Zhang
 *		Cassie Hudson
 *		Robert Smieja
 *		Alex Solomon
 *		Brian Hetherman
 ******************************************************************************/

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.charts;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.wpi.cs.wpisuitetng.janeway.gui.container.toolbar.IToolbarGroupProvider;
import edu.wpi.cs.wpisuitetng.janeway.gui.container.toolbar.ToolbarGroupView;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.ListTab;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Iteration;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Requirement;

/**
 * View that contains the charts
 */

@SuppressWarnings("serial")
public class ChartView extends JPanel implements IToolbarGroupProvider{

	/** The panel containing buttons for the tool bar */
	protected ToolbarGroupView buttonGroup;

	/** The refresh button that reloads the results of the list/filter */
	protected JButton btnRefresh;

	/** The panel containing the actual status pie chart */
	private PieChartPanel statusPiePanel;

	/** The panel containing the actual iteration pie chart */
	private PieChartPanel iterationPiePanel;

	/** The panel containing the actual status bar chart */
	private BarChartPanel statusBarPanel;

	/** The panel containing the actual iteration bar chart */
	private BarChartPanel iterationBarPanel;

	/** The panel containing various chart options on the left of the view */
	private ChartOptionsPanel optionsPanel; 

	private boolean isFiltered;

	/** Internal variables related to what type of chart should be displayed */
	private String chartDataType;
	private String chartType;

	private ListTab view;


	/**
	 * Construct the view and initialize necessary internal variables
	 * @param view A ListTab that this view will be used in
	 */
	public ChartView(ListTab view) {
		/** Init all the variables */
		//Charts
		this.statusPiePanel = new PieChartPanel();
		this.iterationPiePanel = new PieChartPanel();
		this.statusBarPanel = new BarChartPanel();
		this.iterationBarPanel = new BarChartPanel();

		//Toolbar and buttons on the top
		this.buttonGroup = new ToolbarGroupView("All Charts");
		this.btnRefresh = new JButton("Refresh");
		this.buttonGroup.getContent().add(btnRefresh);
		this.buttonGroup.setPreferredWidth(150);

		//Internal variables
		this.view = view;
		this.isFiltered = false;
		this.chartDataType = "Requirement Status"; //TODO: Make this an enum?
		this.chartType = "Pie Chart"; //TODO: Make this an enum?

		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshData();
			}
		});

		//Set up the options panel to configure the charts
		this.optionsPanel = new ChartOptionsPanel(this);		

		// Construct the layout 
		this.setLayout(new BorderLayout());

		/** Initial setup */
		//Bar Charts
		this.add(iterationBarPanel, BorderLayout.CENTER);
		this.add(statusBarPanel, BorderLayout.CENTER);
		
		//Pie Charts
		this.add(iterationPiePanel, BorderLayout.CENTER);
		this.add(statusPiePanel, BorderLayout.CENTER);
		
		//Visibility for all charts
		this.iterationBarPanel.setVisible(true);
		this.statusBarPanel.setVisible(true);
		this.iterationPiePanel.setVisible(true);
		this.statusPiePanel.setVisible(true);
	
		//Options
		this.add(optionsPanel, BorderLayout.WEST);
		this.optionsPanel.setVisible(true);

		//Initial visibility and data
		this.refreshData();
	}

	/** Refresh and reload data in the pie chart */
	public void refreshData() {
		// Load initial data
		view.getParent().refreshData();

		//Grab requirements
		Requirement[] requirements;
		if(!isFiltered){
			//TODO: make the bool in the PieChartPanel to be less redundant?
			requirements = view.getParent().getAllRequirements();
		}else{
			requirements = view.getParent().getDisplayedRequirements();
		}

		//Grab iterations
		Iteration[] iterations = view.getParent().getAllIterations();

		//Pass the status of the filter to each chart
		iterationPiePanel.enableFilter(isFiltered);
		statusPiePanel.enableFilter(isFiltered);
		iterationBarPanel.enableFilter(isFiltered);
		statusBarPanel.enableFilter(isFiltered);

		//Refresh the chart data
		statusPiePanel.refreshStatusChart(requirements);
		statusBarPanel.refreshStatusChart(requirements);
		iterationPiePanel.refreshIterationChart(requirements, iterations);
		iterationBarPanel.refreshIterationChart(requirements, iterations);
	}

	/** Sets the type of chart to use
	 * @param chartType the type of chart to use
	 */
	public void setChartType(String chartType) {
		this.chartType = chartType;
		//So it starts displaying immediately without a delay
		this.refreshChartVisibility();
	}

	/** Sets the type of data visible
	 * @param dataType the type of data to make visible
	 */
	public void setDataTypeVisible(String dataType) {
		this.chartDataType = dataType;
		//So it starts displaying immediately without a delay
		this.refreshChartVisibility();
	}

	/** Sets whether the data is filtered
	 * @param filterStatus the status of the filter, "Applied" or "Not Applied"
	 */
	public void setDataFiltered(String filterStatus) {
		if(filterStatus == "Applied")
			isFiltered = true;
		else if(filterStatus == "Not Applied")
			isFiltered = false;
	}

	/**
	 * @return the button group to place on the top of the toolbar 
	 */
	@Override
	public ToolbarGroupView getGroup() {
		return buttonGroup;
	}

	/** Sets visibility of charts based on chartDataType and chartType */
	private void refreshChartVisibility(){
		//Remove all charts then add the one we want
		this.iterationBarPanel.setVisible(false);
		this.statusBarPanel.setVisible(false);
		this.iterationPiePanel.setVisible(false);
		this.statusPiePanel.setVisible(false);

		//Pie Chart
		if (this.chartType.equals("Pie Chart")){
			if(this.chartDataType =="Requirement Status"){
				this.add(statusPiePanel, BorderLayout.CENTER);
				this.statusPiePanel.setVisible(true);
			}
			else if(this.chartDataType == "Requirement Iteration"){
				this.add(iterationPiePanel, BorderLayout.CENTER);
				this.iterationPiePanel.setVisible(true);
			}

		//BarChart	
		} else if (this.chartType.equals("Bar Chart")){
			if(this.chartDataType =="Requirement Status"){
				this.add(statusBarPanel, BorderLayout.CENTER);
				this.statusBarPanel.setVisible(true);
			}
			else if(this.chartDataType == "Requirement Iteration"){
				this.add(iterationBarPanel, BorderLayout.CENTER);
				this.iterationBarPanel.setVisible(true);
			}
		}
		
		//Always repaint!
		this.repaint();
	}

}