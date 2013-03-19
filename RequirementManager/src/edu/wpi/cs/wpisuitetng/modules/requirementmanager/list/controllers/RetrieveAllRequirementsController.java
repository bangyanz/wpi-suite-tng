/*******************************************************************************
 * Copyright (c) 2013 -- WPI Suite
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chris Casola
 *    JPage
 ******************************************************************************/

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers;

import java.util.Date;

import javax.swing.JOptionPane;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Requirement;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.observers.RetrieveAllRequirementsRequestObserver;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.ListRequirementsView;
import edu.wpi.cs.wpisuitetng.network.Network;
import edu.wpi.cs.wpisuitetng.network.Request;
import edu.wpi.cs.wpisuitetng.network.RequestObserver;
import edu.wpi.cs.wpisuitetng.network.models.HttpMethod;

/**
 * Controller to handle retrieving all requirements from the server and
 * displaying them in the {@link SearchRequirementsView}
 */
public class RetrieveAllRequirementsController {

	/** The search requirements view */
	protected ListRequirementsView view;

	/** The requirements data retrieved from the server */
	protected Requirement[] data = null;

	/**
	 * Constructs a new RetrieveAllRequirementsController
	 * 
	 * @param view the search requirements view
	 */
	public RetrieveAllRequirementsController(ListRequirementsView view) {
		this.view = view;
	}

	/**
	 * Sends a request for all of the requirements
	 */
	public void refreshData() {		
		final RequestObserver requestObserver = new RetrieveAllRequirementsRequestObserver(this);
		Request request;
		request = Network.getInstance().makeRequest("requirementtracker/requirement", HttpMethod.GET);
		request.addObserver(requestObserver);
		request.send();
	}

	/**
	 * This method is called by the {@link RetrieveAllRequirementsRequestObserver} when the
	 * response is received
	 * 
	 * @param requirements an array of requirements returned by the server
	 */
	public void receivedData(Requirement[] requirements) {	
		if (requirements.length > 0) {
			// save the data
			this.data = requirements;

			// set the column names
			String[] columnNames = {"ID", "Name", "Description", "Status", "Priority", "ReleaseNumber", "Estimate", "ActualEffort"};
			view.getListPanel().getResultsPanel().getModel().setColumnNames(columnNames);

			// put the data in the table
			Object[][] entries = new Object[requirements.length][columnNames.length];
			for (int i = 0; i < requirements.length; i++) {
				entries[i][0] = String.valueOf(requirements[i].getId());
				entries[i][1] = requirements[i].getName();
				entries[i][2] = requirements[i].getDescription();
				entries[i][3] = requirements[i].getStatus().toString();
				entries[i][4] = requirements[i].getPriority().toString();
				entries[i][5] = String.valueOf(requirements[i].getReleaseNumber());
				entries[i][6] = String.valueOf(requirements[i].getEstimate());
				entries[i][7] = String.valueOf(requirements[i].getActualEffort());
			}
			view.getListPanel().getResultsPanel().getModel().setData(entries);
			view.getListPanel().getResultsPanel().getModel().fireTableStructureChanged();
		}
		else {
			// do nothing, there are no requirements
		}
	}

	/**
	 * This method is called by the {@link RetrieveAllRequirementsRequestObserver} when an
	 * error occurs retrieving the requirements from the server.
	 */
	public void errorReceivingData(String error) {
		JOptionPane.showMessageDialog(view, "An error occurred retrieving requirements from the server. " + error, 
				"Error Communicating with Server", JOptionPane.ERROR_MESSAGE);
	}
}