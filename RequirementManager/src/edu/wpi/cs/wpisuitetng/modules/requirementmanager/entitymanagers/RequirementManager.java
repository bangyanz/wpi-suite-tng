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

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.entitymanagers;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonSyntaxException;

import edu.wpi.cs.wpisuitetng.Session;
import edu.wpi.cs.wpisuitetng.database.Data;
import edu.wpi.cs.wpisuitetng.exceptions.BadRequestException;
import edu.wpi.cs.wpisuitetng.exceptions.ConflictException;
import edu.wpi.cs.wpisuitetng.exceptions.NotFoundException;
import edu.wpi.cs.wpisuitetng.exceptions.NotImplementedException;
import edu.wpi.cs.wpisuitetng.exceptions.WPISuiteException;
import edu.wpi.cs.wpisuitetng.modules.EntityManager;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.AcceptanceTest;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Iteration;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Note;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Requirement;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.changeset.AcceptanceTestUpdate;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.changeset.FieldChange;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.changeset.RequirementChangeset;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.changeset.RequirementCreation;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.changeset.UserChange;

/** This is the entity manager for requirements in the RequirementManager module. The provided
 *  methods include functionality for creating, updating, getting specific requirements, and 
 *  getting all requirements. Current, requirements are project specific, so
 *  requirements pulled from the database will only be for the current current project.   
 */
public class RequirementManager implements EntityManager<Requirement> {
	/** The database */
	private Data db;

	/** The ModelMapper */
	ModelMapper updateMapper;

	/** This is for advanced logging and debugging of the server interactions */
	private static final Logger logger = Logger.getLogger(RequirementManager.class.getName());

	/** Constructs the entity manager. This constructor is called by
	 * {@link edu.wpi.cs.wpisuitetng.ManagerLayer#ManagerLayer()}. 
	 * To make sure this happens, be sure to place add this entity 
	 * manager to the map in the ManagerLayer file.
	 * 
	 * NOTE: This expects that the data passed is valid and does no error checking!
	 *
	 * @param data Database in the core
	 */	
	public RequirementManager(Data data) {
		db = data;
		updateMapper = new ModelMapper();
		updateMapper.getBlacklist().add("project"); // never allow project changing
	}

	/** Takes an encoded Requirement(as a string) and converts it back to a 
	 *  Requirement and saves it in the database
	 *  
	 *	@param s The current user session
	 *	@param content The requirement that comes in the form of a string to be recreated
	 *	@return the Requirement that originally came as a string
	 * 	@throws BadRequestException "The Requirement creation string had invalid formatting. Entity String: " + content
	 * 	@throws ConflictException "A Requirement with the given ID already exists. Entity String: " + content
	 * 	@throws WPISuiteException "Unable to save Requirement."
	 * 	@see edu.wpi.cs.wpisuitetng.modules.EntityManager#makeEntity(Session, String)
	 */
	public Requirement makeEntity(Session s, String content)
			throws BadRequestException, ConflictException, WPISuiteException {

		// Parse the requirement from JSON
		final Requirement newRequirement;
		logger.log(Level.FINER, "Attempting new Requirement creation...");
		try {
			newRequirement = Requirement.fromJSON(content);
		} catch(JsonSyntaxException e){ // the JSON conversion failed
			logger.log(Level.WARNING, "Invalid Requirement entity creation string.");
			throw new BadRequestException("The Requirement creation string had invalid formatting. Entity String: " + content);			
		}

		// Adds the creation event for this requirement to its history log
		RequirementCreation creation = new RequirementCreation(newRequirement, s.getUser().getName());
		newRequirement.getEvents().add(creation);

		// Saves the requirement in the database
		this.save(s,newRequirement); // An exception may be thrown here if we can't save it

		// Return the newly created requirement (this gets passed back to the client)
		logger.log(Level.FINER, "Requirement creation success!");
		return newRequirement;
	}

	/** Saves the given Requirement into the database if possible.
	 * 
	 *  @param s The current user session
	 *  @param model The Requirement to be saved to the database
	 *  @throws WPISuiteException  "Unable to save Requirement."
	 */
	public void save(Session s, Requirement model) throws WPISuiteException {
		assignUniqueID(model); // Assigns a unique ID to the Req if necessary

		// Save the requirement in the database if possible, otherwise throw an exception
		// We want the requirement to be associated with the project the user logged in to
		if (!db.save(model, s.getProject())) {
			throw new WPISuiteException("Unable to save Requirement.");
		}
		logger.log(Level.FINE, "Requirement Saved :" + model);
	}

	/** Takes a Requirement and assigns a unique id if necessary
	 * 
	 * @param req The requirement that possibly needs a unique id
	 * @throws WPISuiteException "Count failed"
	 */
	public void assignUniqueID(Requirement req) throws WPISuiteException{
		if (req.getId() == -1){// -1 is a flag that says a unique id is needed            
			req.setId(Count() + 1); // Makes first Requirement have id = 1
		}        
	}

	/** Returns the number of Requirements currently in the database. Disregards
	 *  the current user session
	 * 
	 * @return The number of Requirements currently in the database
	 * @throws WPISuiteException "Retrieve all failed"
	 * @see edu.wpi.cs.wpisuitetng.modules.EntityManager#Count()
	 */
	public int Count() throws WPISuiteException {
		// Passing a dummy Requirement lets the db know what type of object to retrieve
		return db.retrieveAll(new Requirement()).size();
	}

	/** Takes a session and returns an array of all the Requirements contained
	 * 
	 * @param s The current user session
	 * @return An array of all requirements in the Database
	 * @see edu.wpi.cs.wpisuitetng.modules.EntityManager#getAll(Session)
	 */
	public Requirement[] getAll(Session s)  {
		// Ask the database to retrieve all objects of the type Requirement.
		// Passing a dummy Requirement lets the db know what type of object to retrieve
		// Passing the project makes it only get requirements from that project
		// Return the list of requirements as an array
		return db.retrieveAll(new Requirement(), s.getProject()).toArray(new Requirement[0]);
	}	

	/**  For the current user session, Takes a specific id for a Requirement and returns it 
	 *   in an array.	
	 *  
	 *  @param s  The current user session
	 *  @param id Points to a specific requirement
	 *  @return An array of Requirements
	 *	@throws NotFoundException  "The Requirement with the specified id was not found:" + intId
	 *	@throws WPISuiteException  "There was a problem retrieving from the database."
	 *	@see edu.wpi.cs.wpisuitetng.modules.EntityManager#getEntity(Session, String)
	 */
	public Requirement[] getEntity(Session s, String id) throws NotFoundException, WPISuiteException {

		final int intId = Integer.parseInt(id);
		if(intId < 1) {
			throw new NotFoundException("The Requirement with the specified id was not found:" + intId);
		}
		Requirement[] requirements = null;

		// Try to retrieve the specific Requirement
		try {
			requirements = db.retrieve(Requirement.class, "id", intId, s.getProject()).toArray(new Requirement[0]);
		} catch (WPISuiteException e) { // caught and re-thrown with a new message
			e.printStackTrace();
			throw new WPISuiteException("There was a problem retrieving from the database." );
		}

		// If a requirement was pulled, but has no content
		if(requirements.length < 1 || requirements[0] == null) {
			throw new NotFoundException("The Requirement with the specified id was not found:" + intId);
		}
		return requirements;
	}

	/**  Updates a Requirement already in the database
	 *   
	 *  @param s The current user session
	 *  @param content The requirement to be update + the updates
	 * 	@return the changed requirement
	 *  @throws WPISuiteException  "There was a problem retrieving from the database."   or "Null session."
	 *  @throws NotFoundException  "The Requirement with the specified id was not found:" + intId
	 *	@see edu.wpi.cs.wpisuitetng.modules.EntityManager#update(Session, String)
	 */
	public Requirement update(Session s, String content) throws WPISuiteException {
		// If there is no session
		if(s == null){
			throw new WPISuiteException("Null session.");
		} 

		// Try to parse the message
		final Requirement reqUpdate;
		logger.log(Level.FINER, "Attempting to update a Requirement...");
		try {
			reqUpdate = Requirement.fromJSON(content);
		} catch(JsonSyntaxException e){ // the JSON conversion failed
			logger.log(Level.WARNING, "Invalid Requirement entity update string.");
			throw new BadRequestException("The Requirement update string had invalid formatting. Entity String: " + content);			
		}
		// Pull out the name of the current user
		String currentUser = s.getUser().getName();
		// Attempt to get the entity, NotFoundException or WPISuiteException may be thrown	    	
		Requirement oldReq = getEntity(s, Integer.toString(  reqUpdate.getId()  )  )[0];

		// If the update was adding a note, set the user and update appropriately
		if (reqUpdate.getNotes().size() > oldReq.getNotes().size())
		{
			ArrayList<Note> notes = reqUpdate.getNotes();
			Note lastNote = notes.get(notes.size() -  1);
			lastNote.setUser(currentUser);
			reqUpdate.getEvents().add(lastNote);
		}
		// If the update was adding an acceptance test, set the user and update appropriately
		else if (reqUpdate.getAcceptanceTests().size() > oldReq.getAcceptanceTests().size())
		{
			ArrayList<AcceptanceTest> acceptanceTests = reqUpdate.getAcceptanceTests();
			AcceptanceTest lastAcceptanceTest = acceptanceTests.get(acceptanceTests.size() -  1);
			lastAcceptanceTest.setUser(currentUser);
			reqUpdate.getEvents().add(lastAcceptanceTest);
		}
		// If the update was editing an acceptance test status, find which one it was and update accordingly
		else if (!hasSameTestResults(oldReq, reqUpdate)) {
			int testNumber = 0;
			for (int i = 0; i < reqUpdate.getAcceptanceTests().size(); i++) {
				AcceptanceTest oldTest = oldReq.getAcceptanceTests().get(i);
				AcceptanceTest newTest = reqUpdate.getAcceptanceTests().get(i);
				if (oldTest.getAcceptanceTestResult() != newTest.getAcceptanceTestResult())
					testNumber = i;
			}
			AcceptanceTestUpdate acceptanceTestUpdate = new AcceptanceTestUpdate(oldReq, reqUpdate, testNumber, currentUser);
			reqUpdate.getEvents().add(acceptanceTestUpdate);
		}
		// If the update was a user change, set the user and update accordingly
		else if (reqUpdate.getUserNames().size() != oldReq.getUserNames().size()) {
			UserChange userChange = new UserChange(oldReq, reqUpdate, currentUser);
			reqUpdate.getEvents().add(userChange);
		}
		else { // this update is a changeset
			RequirementChangeset changeset = new RequirementChangeset(currentUser);
			ChangesetCallback callback = new ChangesetCallback(changeset);

			// copy values to old requirement and fill in our changeset appropriately
			updateMapper.map(reqUpdate, oldReq, callback);
			
			// make the event contain the iteration name (at the time) instead of the id
			if (changeset.getChanges().containsKey("iteration")) {
				int oldId = (Integer)changeset.getChanges().get("iteration").getOldValue();
				int newId = (Integer)changeset.getChanges().get("iteration").getNewValue();
				String oldName = ((Iteration) db.retrieve(Iteration.class, "id", oldId, s.getProject()).get(0)).getName();
				String newName = ((Iteration) db.retrieve(Iteration.class, "id", newId, s.getProject()).get(0)).getName();
				if (oldName.equals("")) oldName = "Backlog";
				if (newName.equals("")) newName = "Backlog";
				changeset.getChanges().remove("iteration");
				changeset.getChanges().put("iteration", new FieldChange<String>(oldName, newName));
			}
			
			reqUpdate.getEvents().add(changeset);
		}
		
		// Copy new field values into old Requirement. This is because the "same" model must
		// be saved back into the database
		oldReq.updateReq(reqUpdate);

		// Attempt to save. WPISuiteException may be thrown
		this.save(s,oldReq);

		return oldReq;
	}

	/** Deletes a Requirement from the database permanently. It is not advised that this
	 *  implementation be used because the current unique ID system depends on all requirements
	 *  existing in the DB forever. In the future, a stronger unique ID system could be implemented
	 *  and this method would be valuable at that time. In the current GUI implementation, there is 
	 *  no way to delete a requirement other than setting its status, in which case it still 
	 *  exists, but has a deleted status. 
	 *  
	 *  @param s The current user session
	 *  @param id The unique of the requirement to delete
	 *  @return TRUE if successful or FALSE if it fails
	 *	@throws WPISuiteException  "There was a problem retrieving from the database."   or "Null session."
	 *  @throws NotFoundException  "The Requirement with the specified id was not found:" + intId
	 *	@see edu.wpi.cs.wpisuitetng.modules.EntityManager#deleteEntity(Session, String)
	 */
	public boolean deleteEntity(Session s, String id) throws WPISuiteException {
		// Attempt to get the entity, NotFoundException or WPISuiteException may be thrown	    	
		Requirement oldReq = getEntity(s,   id    )[0];

		if (db.delete(oldReq) == oldReq){
			return true; // the deletion was successful
		}	    
		return false; // The deletion was unsuccessful
	}

	/** Deletes ALL Requirement from the database permanently. It is not advised that this
	 *  implementation be used because the current unique ID system depends on all requirements
	 *  existing in the DB forever. In the future, a stronger unique ID system could be implemented
	 *  and this method would be valuable at that time. In the current GUI implementation, there is 
	 *  no way to delete a requirement other than setting its status, in which case it still 
	 *  exists, but has a deleted status. 
	 * 
	 *  @param s The current user session
	 *  @see edu.wpi.cs.wpisuitetng.modules.EntityManager#deleteAll(Session)
	 */
	public void deleteAll(Session s)  {
		db.deleteAll(new Requirement(), s.getProject());
	}

	//The following methods are not implemented but required by the "EntityManager" interface:
	
	/** Method advancedGet. (Not implemented but required by the "EntityManager" interface)
	 * @param s Session
	 * @param args String[]
	 * @return String
	 * @throws WPISuiteException
	 * @throws WPISuiteException, NotImplementedException
	 * @see edu.wpi.cs.wpisuitetng.modules.EntityManager#advancedPut(Session, String[], String)
	 */
	public String advancedGet(Session s, String[] args)
			throws WPISuiteException {
		throw new NotImplementedException();
	}

	/** Method advancedPut. (Not implemented but required by the "EntityManager" interface)
	 * @param s Session
	 * @param args String[]
	 * @param content String
	 * @return String
	 * @throws WPISuiteException
	 * @throws WPISuiteException, NotImplementedException
	 * @see edu.wpi.cs.wpisuitetng.modules.EntityManager#advancedPut(Session, String[], String)
	 */
	public String advancedPut(Session s, String[] args, String content)
			throws WPISuiteException {
		throw new NotImplementedException();
	}

	/** Method advancedPost. (Not implemented but required by the "EntityManager" interface)
	 * @param s Session
	 * @param string String
	 * @param content String
	 * @return String
	 * @throws WPISuiteException
	 * @throws WPISuiteException, NotImplementedException
	 * @see edu.wpi.cs.wpisuitetng.modules.EntityManager#advancedPost(Session, String, String)
	 */
	public String advancedPost(Session s, String string, String content)
			throws WPISuiteException {
		throw new NotImplementedException();
	}
	
	/** Checks if the two requirements have identical acceptance tests
	 * 
	 * @param oldReq The old requirement to check against
	 * @param reqUpdate a requirement holding the updates
	 * @return true if the tests haven't changed, false if they have
	 */
	public boolean hasSameTestResults(Requirement oldReq, Requirement reqUpdate) {
		// Loop through acceptance tests and see if there are any changes
		for (int i = 0; i < reqUpdate.getAcceptanceTests().size(); i++) {
			AcceptanceTest oldTest = oldReq.getAcceptanceTests().get(i);
			AcceptanceTest newTest = reqUpdate.getAcceptanceTests().get(i);
			if (oldTest.getAcceptanceTestResult() != newTest.getAcceptanceTestResult())
				return false;
		}
		return true;
	}

}
