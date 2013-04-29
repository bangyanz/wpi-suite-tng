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

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.filter;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers.DeleteModelController;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers.RetrieveAllModelsController;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers.RetrieveModelController;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.models.Filter;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.models.FilterType;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.models.ResultsTableModel;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.ActivateDeleteButton;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.ActiveFilterTableCellRenderer;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.IListPanel;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.ListTab;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.NewModelAction;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Iteration;

/**
 * Panel to contain the list of filters that have been saved by the user
 */
@SuppressWarnings("serial")
public class FilterListTab extends JPanel implements IListPanel{

	/** The table of results */
	protected JTable resultsTable;

	protected JButton btnCreate;
	protected JButton btnDelete;
	private boolean btnCreateIsCancel;

	private Filter[] localFilters = {};

	/** The model containing the data to be displayed in the results table */
	protected ResultsTableModel resultsTableModel;

	private DeleteModelController deleteController;
	private RetrieveModelController retrieveController;
	private RetrieveAllModelsController retrieveAllController;

	private final ListTab parent;
	/**
	 * Construct the panel
	 */

	public FilterListTab(ListTab view) {
		parent = view;
		this.setBtnCreateIsCancel(false);
		// Set the layout manager
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// Construct the table model
		resultsTableModel = new ResultsTableModel();

		// Construct the table and configure it
		resultsTable = new JTable(resultsTableModel);
		resultsTable.setAutoCreateRowSorter(true);
		resultsTable.setFillsViewportHeight(true);
		resultsTable.setDefaultRenderer(String.class, new ActiveFilterTableCellRenderer());
		resultsTable.addMouseListener(new ActivateDeleteButton(this)); // Watches for highlighting
		
		// Put the table in a scroll pane
		JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
		resultsScrollPane.setPreferredSize(new Dimension(175,250));
		this.add(resultsScrollPane);
		resultsScrollPane.setAlignmentX(CENTER_ALIGNMENT);

		this.add(Box.createRigidArea(new Dimension(0,6)));

		btnCreate = new JButton ("New Filter");
		btnDelete = new JButton ("Delete");
		setDeleteEnabled(false); // Initialize

		btnCreate.setMaximumSize(new Dimension(120, 40));
		btnCreate.setMinimumSize(new Dimension(120, 40));
		btnDelete.setMaximumSize(new Dimension(120, 40));
		btnDelete.setMinimumSize(new Dimension(120, 40));

		this.add(btnCreate);
		this.add(Box.createRigidArea(new Dimension(0,6)));
		this.add(btnDelete);
		this.add(Box.createRigidArea(new Dimension(0,6)));
		btnCreate.setAlignmentX(CENTER_ALIGNMENT);
		btnDelete.setAlignmentX(CENTER_ALIGNMENT);

		deleteController = new DeleteModelController(this, parent.getFilterBuilderPanel(),"filter");
		retrieveController = new RetrieveModelController(this, parent.getFilterBuilderPanel(),"filter");
		setRetrieveAllController(new RetrieveAllModelsController(this, parent.getFilterBuilderPanel(),"filter"));

		// Add a listener for row clicks
		resultsTable.addMouseListener(retrieveController);

		// Sets up listener system. Once pressed, changes to CancelFilterAction listener, then back to this.
		btnCreate.addActionListener(new NewModelAction(this, parent.getFilterBuilderPanel()));

		btnDelete.addActionListener(deleteController);
		setDeleteEnabled(false); // Initialize

	}

	/**This method returns an ArrayList of active filters
	 * 
	 * @return activeFilters An ArrayList of the active filters
	 */
	public ArrayList<Filter> getActiveFilters() {
		ArrayList<Filter> activeFilters = new ArrayList<Filter>();

		for(int i = 0; i < localFilters.length; i++){
			if(localFilters[i].isUseFilter()) activeFilters.add(localFilters[i]);
		}

		return activeFilters;
	}

	/**
	 * @return the data model for the table
	 */
	public ResultsTableModel getModel() {
		return resultsTableModel;
	}

	/**
	 * @return the results table
	 */
	public JTable getResultsTable() {
		return resultsTable;
	}

	/**
	 * Replace the results table with the given table
	 * @param newTable the new results table
	 */
	public void setResultsTable(JTable newTable) {
		resultsTable = newTable;
	}

	/**
	 * @return the parent
	 */
	public ListTab getParent() {
		return parent;
	}

	/**
	 * @return the localFilters
	 */
	public Filter[] getLocalFilters() {
		return localFilters;
	}

	/**
	 * @param localFilters the localFilters to set
	 */
	public void setLocalFilters(Filter[] localFilters) {
		this.localFilters = localFilters;
	}

	public JButton getBtnCreate(){
		return btnCreate;
	}

	/**
	 * @return the btnCreateIsCancel
	 */
	public boolean isBtnCreateIsCancel() {
		return btnCreateIsCancel;
	}

	/**
	 * @param btnCreateIsCancel the btnCreateIsCancel to set
	 */
	public void setBtnCreateIsCancel(boolean btnCreateIsCancel) {
		this.btnCreateIsCancel = btnCreateIsCancel;
	}

	/** Takes whatever model(s) is(are) stored in the the current panel,
	 *  and returns the unique identifier(s) in an array. Generally
	 *  pulls the highlighted identifiers from a table view.
	 * 
	 * @return An array of unique identifiers in the form of strings
	 */
	public String[] getSelectedUniqueIdentifiers() {
		// get highlighted rows 
		int[] rowNumbers = resultsTable.getSelectedRows();

		String[] ids = new String [rowNumbers.length];
		// get array of row numbers, if there are any highlighted rows
		for(int i=0; i<rowNumbers.length;i++){
			ids[i] = (String) resultsTable.getValueAt(rowNumbers[i], 0);
		}
		return ids;
	}

	/** Sets the New button to clear/cancel	 */
	public void setNewBtnToCancel() {
		// set the New/Cancel button to cancel
		getBtnCreate().setText("Cancel"); 
		setBtnCreateIsCancel(true);
	}

	/** Sets the "Cancel" button back to "New Filter" 	 */
	public void setCancelBtnToNew() {
		// Set the cancel button back to New Filter if it was in cancel mode 
		this.getBtnCreate().setText("New Filter"); 
		this.setBtnCreateIsCancel(false);

	}

	/** Toggles between "New Model" and "Cancel" mode */
	public void toggleNewCancelMode() {
		btnCreateIsCancel = !btnCreateIsCancel;
		if(btnCreateIsCancel)
			this.getBtnCreate().setText("Cancel"); 			
		else
			this.getBtnCreate().setText("New Filter");
	}


	/** Begins refresh process, starting with Filters
	 * 
	 * @return true on success, false on failure
	 */
	public boolean refreshAll() {
		retrieveAllController.refreshData();
		return true;
	}


	/** Gets the unique identifier of the list entry that was double clicked
	 * 
	 * @param me The mouse event that was triggered by a double click
	 * @return The unique identifier, either name or ID number
	 */
	public String getSelectedUniqueIdentifier(MouseEvent me) {

		JTable filters = parent.getTabPanel().getFilterList().getResultsTable();

		int row = filters.rowAtPoint(me.getPoint());

		String filterId=null;
		// make sure the user actually clicked on a row
		if (row > -1) {
			filterId = (String) resultsTable.getValueAt(row, 0);
		}

		return filterId;
	}

	/** Show the filters in the list view
	 * 
	 * @param jsonString An array of models in the form of a JSON string
	 */
	public void showRecievedModels(String jsonString) {
		// Setup data structures
		String[] emptyColumns = {};
		Object[][] emptyData = {};

		// Fire blanks so that the old contents are removed
		this.getModel().setColumnNames(emptyColumns);
		this.getModel().setData(emptyData);
		this.getModel().fireTableStructureChanged();

		Filter[] filters = Filter.fromJSONArray(jsonString);
		
		// Check for invalid filters- Cancel upload and refresh again if necessary
		for (Filter filter: filters)
		{
			// Only filter out filters that have Iteration as their type
			if (filter.getType() == FilterType.Iteration){
				// Only filter out filters that reference deleted iterations
				boolean foundTheIter = false;
				for (Iteration iter : parent.getParent().getAllIterations()) {				
					// Check to see if the filter references a currently valid Iteration
					if (filter.getValue().equals(iter.getID() + "") ){
						foundTheIter = true; // means that the filter is valid and we can continue
					}
				}
				// Indicates an invalid filter if the iteration referenced was not found
				if (!foundTheIter){
					// Delete the filter. A retrieve all command will be sent after the deletion occurs
					deleteController.perform(Integer.toString(filter.getUniqueID()));
				}	
			}
		}
		
		// The new list of filters has passed basic validation, so it is saved
		this.setLocalFilters(filters);
		parent.getParent().setAllFilters(filters);

		// Add the list of filters to the FilterListPanel object
		if (filters.length > 0) {
			// set the column names
			String[] columnNames = {"Id", "Type", "Op", "Value", "Active"};

			// put the data in the table
			Object[][] entries = new Object[filters.length][columnNames.length];
			for (int i = 0; i < filters.length; i++) {
				entries[i][0] = String.valueOf(filters[i].getUniqueID());
				entries[i][1] = filters[i].getType().toString();

				if (filters[i].getComparator().toString().equals("Contains")) {
					entries[i][2] = "c";
				} else if (filters[i].getComparator().toString().equals("DoesNotContain")) {
					entries[i][2] = "!c";
				} else {
					entries[i][2] = filters[i].getComparator().toString();
				}

				String typeString = filters[i].getType().toString();
				if (typeString.equals("Iteration")) {
					String strId = filters[i].getValue();
					for (Iteration iter : parent.getParent().getAllIterations()) {
						if (strId.equals(iter.getID() + "")) {
							entries[i][3] = iter.getName();
						}
					}
				}
				else {
					entries[i][3] = filters[i].getValue();
				}

				if (filters[i].isUseFilter()) {
					entries[i][4] = "yes";
				} else {
					entries[i][4] = "no";
				}
			}

			// fill the table
			this.getModel().setColumnNames(columnNames);
			this.getModel().setData(entries);
			this.getModel().fireTableStructureChanged();

			//Hide the Id column
			resultsTable.getColumn("Id").setMinWidth(0);
			resultsTable.getColumn("Id").setMaxWidth(0);
			resultsTable.getColumn("Id").setWidth(0);

			//Set preferred column widths
			//Type
			resultsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
			//Op
			resultsTable.getColumnModel().getColumn(2).setPreferredWidth(50);
			//Value
			resultsTable.getColumnModel().getColumn(3).setPreferredWidth(75);
			//Active
			resultsTable.getColumnModel().getColumn(4).setPreferredWidth(75);
		}

		refreshRequirements();
		setDeleteEnabled(false);
	}

	/**
	 * @return the retrieveAllController
	 */
	public RetrieveAllModelsController getRetrieveAllController() {
		return retrieveAllController;
	}

	/**
	 * @param retrieveAllController the retrieveAllController to set
	 */
	public void setRetrieveAllController(RetrieveAllModelsController retrieveAllController) {
		this.retrieveAllController = retrieveAllController;
	}

	/** Refresh all the requirements    */
	public void refreshRequirements() {
		parent.getParent().getController().refreshData();
	}

	/** Sets the delete button to either activated or deactivated 
	 * 
	 * @param setActive True to activate and false to deactivate
	 */
	public void setDeleteEnabled(boolean setActive) {
		btnDelete.setEnabled(setActive);		
	}
	
	/** Checks if the selected items can ALL be deleted or not. Currently, all 
	 *  filters may be deleted, so only true is returned
	 *  
	 * @return false if any item selected cannot be deleted.
	 */
	public boolean areSelectedItemsDeletable(){
		return true;
	}	
}

