/*******************************************************************************
 * Copyright (c) 2013 -- WPI Suite
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Team 5 D13
 * 
 ******************************************************************************/

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import edu.wpi.cs.wpisuitetng.janeway.gui.container.toolbar.IToolbarGroupProvider;
import edu.wpi.cs.wpisuitetng.janeway.gui.container.toolbar.ToolbarGroupView;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers.ListSaveModelController;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers.RefreshRequirementsAction;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers.RetrieveAllModelsController;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers.RetrieveAllRequirementsController;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers.RetrieveRequirementController;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.models.Filter;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.ListTab.Mode;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Iteration;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Requirement;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.tabs.MainTabController;

/** View that contains the entire requirement listing interface
 */
@SuppressWarnings("serial")
public class ListView extends JPanel implements IToolbarGroupProvider {

	/** Panel containing the list interface */
	protected ListTab mainPanel;

	/** The layout manager for this panel */
	protected SpringLayout layout;

	/** The panel containing buttons for the tool bar */
	protected ToolbarGroupView buttonGroup;

	/** The refresh button that reloads the results of the list/filter */
	protected JButton btnRefresh;

	/** The button that enables editing in the list view */
	protected JButton btnEnableEdit;

	/** The button that saves editing in the list view */
	protected JButton btnSave;

	/** The button that cancels editing in the list view */
	protected JButton btnCancel;

	/** The display pie chart button that loads the pie chart tab */
	protected JButton btnDisplayPieChart;

	/** The check box for Default Column Widths */
	protected JCheckBox checkBoxDefault;

	/** Boolean to represent state of the check box */
	protected boolean checkBoxStatus;

	/** Controller to handle list and filter requests from the user */
	protected RetrieveAllRequirementsController controller;

	protected RetrieveAllModelsController filterController;
	protected RetrieveAllModelsController iterationController;

	/** The main tab controller */
	protected MainTabController tabController;

	/** Location of divider before entering edit mode */
	protected int oldDividerLocation;
	
	/** Size of divider before entering edit mode */
	protected int oldDividerSize;

	/** The arrays of models stored in the database */
	protected Filter[] allFilters;
	protected Iteration[] allIterations;
	protected Requirement[] allRequirements;
	protected Requirement[] displayedRequirements;
	
	/** The controller that saves requirements that are edited in list view */
	private ListSaveModelController listSaveRequirementController = null;	
	
	
	/**Construct the view
	 * @param tabController The main tab controller
	 */
	public ListView(final MainTabController tabController) {
		this.tabController = tabController;

		mainPanel = new ListTab(tabController, this);

		allFilters = new Filter[0];
		allIterations = new Iteration[0];
		allRequirements = new Requirement[0];
		displayedRequirements = new Requirement[0];

		// Construct the layout manager and add constraints
		layout = new SpringLayout();
		this.setLayout(layout);
		layout.putConstraint(SpringLayout.NORTH, mainPanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, mainPanel, 0, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.WEST, mainPanel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, mainPanel, 0, SpringLayout.EAST, this);

		// Add the mainPanel to this view
		this.add(mainPanel);

		// Initialize the controllers
		controller = new RetrieveAllRequirementsController(this);
		filterController = new RetrieveAllModelsController(
				mainPanel.getTabPanel().getFilterList(), mainPanel.getFilterBuilderPanel(), "filter");
		iterationController = new RetrieveAllModelsController(
				mainPanel.getTabPanel().getIterationList(), mainPanel.getIterationBuilderPanel(), "iteration");

		// Add a listener for row clicks in the actual table
		mainPanel.getResultsPanel().getResultsTable().addMouseListener(new RetrieveRequirementController(this.getListTab().getResultsPanel()));

		// Instantiate the button panel
		buttonGroup = new ToolbarGroupView("Options for Requirements");

		// Instantiate the refresh button
		btnRefresh = new JButton();
		btnRefresh.setAction(new RefreshRequirementsAction(controller));
		buttonGroup.getContent().add(btnRefresh);
		buttonGroup.setPreferredWidth((int)buttonGroup.getPreferredSize().getWidth());

		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshData();
			}
		});


		// Instantiate the defaultColumnWidths checkbox
		checkBoxDefault = new JCheckBox("Reset Table Layout", true);
		checkBoxStatus = true;
		//Save the state of the checkbox every time it changes, workaround for reseting views when tabs are changed
		checkBoxDefault.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

				checkBoxStatus = checkBoxDefault.isSelected();
			}
		});
		buttonGroup.getContent().add(checkBoxDefault);

		// Create and make button to Enable Editing in list view
		btnEnableEdit = new JButton("Enable Edit Mode");
		btnCancel = new JButton("Cancel");
		btnSave = new JButton("Save Changes");
		btnSave.setVisible(false);
		btnCancel.setVisible(false);
		buttonGroup.getContent().add(btnEnableEdit);
		buttonGroup.getContent().add(btnCancel);
		buttonGroup.getContent().add(btnSave);

		btnEnableEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Instantiate the controller if it hasn't been made yet. This is done here just in case
				// of instantiation path errors.
				if (listSaveRequirementController == null){
					listSaveRequirementController = new ListSaveModelController(mainPanel.getResultsPanel(), "requirement");
				}
				
				
				btnEditNotVisible();
		//		mainPanel.getResultsPanel().getModel().setEditable(true);
		//		mainPanel.getResultsPanel().setComboxforType();
		//		mainPanel.getResultsPanel().setComboxforStatus();
		//		mainPanel.getResultsPanel().setComboxforPriority();
		//		mainPanel.getResultsPanel().setComboxforIteration();
				mainPanel.setMode(Mode.EDIT);
				setListsAndBuildersVisible(false);
				btnRefresh.setEnabled(false);
				

		//		mainPanel.getResultsPanel().disableSorting();
				mainPanel.getResultsPanel().setUpForEditing();
				mainPanel.getResultsPanel().setInEditMode(true);
				mainPanel.getTabController().getView().getJanewayModule().getToolbarView().getCreateRequirementButton().setEnabled(false);//disables the create requirements button
				mainPanel.getTabController().getView().getJanewayModule().getToolbarView().getIDbox().setEnabled(false);//disables the lookup by ID box
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCancelSaveNotVisible();
				mainPanel.getResultsPanel().getModel().setEditable(false);
				mainPanel.setMode(Mode.ITERATION);
				setListsAndBuildersVisible(true);
				btnRefresh.setEnabled(true);
				refreshData();
				mainPanel.getResultsPanel().setInEditMode(false);
				mainPanel.getResultsPanel().enableSorting();
				mainPanel.getResultsPanel().getResultsTable().setDefaultRenderer(String.class, new ResultsTableCellRenderer(null, null, null));
				mainPanel.getTabController().getView().getJanewayModule().getToolbarView().getCreateRequirementButton().setEnabled(true);//enables the create requirements button
				mainPanel.getTabController().getView().getJanewayModule().getToolbarView().getIDbox().setEnabled(true);//enables the lookup by ID box
			}
		});
		
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listSaveRequirementController.perform();				
				
				btnCancelSaveNotVisible();
				mainPanel.getResultsPanel().getModel().setEditable(false);
				mainPanel.setMode(Mode.ITERATION);
				setListsAndBuildersVisible(true);
				btnRefresh.setEnabled(true);
				refreshData();
				mainPanel.getResultsPanel().setInEditMode(false);
				mainPanel.getResultsPanel().enableSorting();
				mainPanel.getResultsPanel().getResultsTable().setDefaultRenderer(String.class, new ResultsTableCellRenderer(null, null, null));
				mainPanel.getTabController().getView().getJanewayModule().getToolbarView().getCreateRequirementButton().setEnabled(true);//enables the create requirements button
				mainPanel.getTabController().getView().getJanewayModule().getToolbarView().getIDbox().setEnabled(true);//enables the lookup by ID box
			}
		});
		
	}

	public boolean getCheckBoxStatus() {
		return checkBoxStatus;
	}

	protected void setCheckBoxStatus(boolean checkBoxStatus) {
		this.checkBoxStatus = checkBoxStatus;
	}

	/**
	 * refresh the Data
	 */
	public void refreshData() {
		filterController.refreshData();
		iterationController.refreshData();
	}

	public RetrieveAllRequirementsController getController() {
		return controller;
	}

	public RetrieveAllModelsController getFilterController() {
		return filterController;
	}

	public RetrieveAllModelsController getIterationController() {
		return iterationController;
	}

	public ListTab getListTab() {
		return mainPanel;
	}

	@Override
	public ToolbarGroupView getGroup() {
		return buttonGroup;
	}

	/**
	 * @return the allFilters
	 */
	public Filter[] getAllFilters() {
		return allFilters;
	}

	/**
	 * @param allFilters the allFilters to set
	 */
	public void setAllFilters(Filter[] allFilters) {
		this.allFilters = allFilters;
	}

	/**
	 * @return the allIterations
	 */
	public Iteration[] getAllIterations() {
		return allIterations;
	}

	/**
	 * @param allIterations the allIterations to set
	 */
	public void setAllIterations(Iteration[] allIterations) {
		this.allIterations = allIterations;
	}

	/**
	 * @return the allRequirements
	 */
	public Requirement[] getAllRequirements() {
		return allRequirements;
	}

	/**
	 * @param allRequirements the allRequirements to set
	 */
	public void setAllRequirements(Requirement[] allRequirements) {
		this.allRequirements = allRequirements;
	}

	/**
	 * @return the displayedRequirements
	 */
	public Requirement[] getDisplayedRequirements() {
		return displayedRequirements;
	}

	/**
	 * @param displayedRequirements the displayedRequirements to set
	 */
	public void setDisplayedRequirements(Requirement[] displayedRequirements) {
		this.displayedRequirements = displayedRequirements;
	}

	/**
	 * get the Edit button
	 * @return btnEnableEdit
	 */
	public JButton getBtnEdit(){
		return btnEnableEdit;
	}

	/**
	 * Set the Save and Cancel buttons to invisible
	 * Set the EnableEdit button to visible
	 */
	public void btnCancelSaveNotVisible() {
		btnCancel.setVisible(false);
		btnSave.setVisible(false);
		btnEnableEdit.setVisible(true);
	}

	/**
	 * Set the Save and Cancel buttons to visible
	 * Set the EnableEdit button to invisible
	 */
	public void btnEditNotVisible() {
		btnEnableEdit.setVisible(false);
		btnSave.setVisible(true);
		btnCancel.setVisible(true);
	}
	
	/**
	 * Set everything else enabled or disabled when changing edit modes
	 */
	public void setListsAndBuildersVisible(boolean enable) {
		if (enable) {
			mainPanel.getSplitPane().setDividerLocation(oldDividerLocation);
			mainPanel.getSplitPane().setEnabled(true);
			mainPanel.getSplitPane().setDividerSize(oldDividerSize);
			mainPanel.getLeftPanel().setMinimumSize(new Dimension (260, 500));
		}
		else {
			oldDividerLocation = mainPanel.getSplitPane().getDividerLocation();
			oldDividerSize = mainPanel.getSplitPane().getDividerSize();
			mainPanel.getSplitPane().setEnabled(false);
			mainPanel.getSplitPane().setDividerLocation(0);
			mainPanel.getSplitPane().setDividerSize(0);
			mainPanel.getLeftPanel().setMinimumSize(new Dimension (0, 500));
		}
		
		if (mainPanel.getMode() == Mode.FILTER) {
			mainPanel.getFilterBuilderPanel().setVisible(enable);
		}
		else if (mainPanel.getMode() == Mode.ITERATION) {
			mainPanel.getIterationBuilderPanel().setVisible(enable);
		}
		else {
			mainPanel.getEditModeBuilderPanel().setVisible(!enable);
		}
	}

	/**
	 * @return the btnSave
	 */
	public JButton getBtnSave() {
		return btnSave;
	}

	/**
	 * @param btnSave the btnSave to set
	 */
	public void setBtnSave(JButton btnSave) {
		this.btnSave = btnSave;
	}

}
