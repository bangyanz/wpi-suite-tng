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

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.observers.ListSaveModelObserver;
import edu.wpi.cs.wpisuitetng.network.Network;
import edu.wpi.cs.wpisuitetng.network.Request;
import edu.wpi.cs.wpisuitetng.network.models.HttpMethod;

/** This controller is designed to save a "model" that was edited in a list view 
 *  specifically. The extra functionality necessary includes handling saving multiple
 *  models at once and handling those extra server messages. See IEditableList for 
 *  descriptions of the methods called by this controller.  */
public class ListSaveModelController implements IController{

	/** The model name, in string form, which will be used for sending messages */
	private final String modelName;

	/** The list that this controller does saving for */
	private final IEditableListPanel theList;

	public ListSaveModelController(IEditableListPanel theList, String modelName){
		this.theList = theList;
		this.modelName = modelName;
	}


	/** provide action listener or other ways to check whether a certain action is performed
	 */
	public void perform(){
		// Get the unique identifiers of the models
		Boolean[] needsSaving = theList.getNeedsSaveFlags();

		// Go through the list of flags and send save requests as necessary
		for(int i = 0; i < needsSaving.length; i++){			
			if (needsSaving[i].booleanValue()){ 
				perform(theList.getUniqueIAtIndex(i) , i, theList.getModelAsJson(i));
			}
		}
	}

	/** provide action listener or other ways to check whether a certain action is performed
	 * @param String
	 */
	public void perform(String uniqueIdentifier, int flagIndex, String modelJSON){
		// Create and send a request for the Model with the given ID
		// Send message for each Model ID for deletion
		Request request;
		request = Network.getInstance().makeRequest("requirementmanager/" + modelName + "/" + uniqueIdentifier, HttpMethod.POST);
		request.setBody(modelJSON); // put the new message in the body of the request
		request.addObserver(new ListSaveModelObserver(this, theList.getNeedsSaveFlags(), flagIndex)); // add an observer to process the response
		request.send();
	}


	/** Upon successful completion of all models being saved, this is triggered to set the 
	 *  list view back to normal
	 * 
	 */
	public void success(String JSONString) {
		// Re-set the table to be in normal mode, and re allow sorting etc


		// Trigger a mass reset

	}

	/** Upon failure, prints to console
	 * @param iReq The request response from the server 
	 */
	public void fail() {
		System.err.println("Fail: Cannot delete the " + modelName);		
	}

	/** Upon failure, prints to console
	 * @param iReq The request response from the server 
	 */
	public void error(String error) {
		System.err.println("Could not delete the " + modelName);		
	}
}