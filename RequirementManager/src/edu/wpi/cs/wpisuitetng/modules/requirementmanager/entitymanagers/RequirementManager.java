package edu.wpi.cs.wpisuitetng.modules.requirementmanager.entitymanagers;

import java.util.List;

import edu.wpi.cs.wpisuitetng.Session;
import edu.wpi.cs.wpisuitetng.database.Data;
import edu.wpi.cs.wpisuitetng.exceptions.BadRequestException;
import edu.wpi.cs.wpisuitetng.exceptions.ConflictException;
import edu.wpi.cs.wpisuitetng.exceptions.NotFoundException;
import edu.wpi.cs.wpisuitetng.exceptions.WPISuiteException;
import edu.wpi.cs.wpisuitetng.modules.EntityManager;
import edu.wpi.cs.wpisuitetng.modules.Model;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Requirement;

/**This is the entity manager for the Requirement in the 
 * RequirementManager module
 * 
 * @author Calder
 * @author Dabrowski
 *
 */
public class RequirementManager implements EntityManager<Requirement> {
	/** The database */
	Data db;

	/** Constructs the entity manager. This constructor is called by
	 * {@link edu.wpi.cs.wpisuitetng.ManagerLayer#ManagerLayer()}. 
	 * To make sure this happens, be sure to place add this entity 
	 * manager to the map in the ManagerLayer file.
	 * 
	 * @param db a reference to the persistent database
	 */	
	public RequirementManager(Data data) {
		db = data;
	}

		
	/** Takes an encoded Requirement(as a string) and converts it back to a 
	 *  Requirement and saves it in the database
	 *  
	 *	@param s The current user session
	 *	@param content The requirement that comes in the form of a string to be recreated
	 *	@return the Requirement that originally came as a string
	 */
	public Requirement makeEntity(Session s, String content)
			throws BadRequestException, ConflictException, WPISuiteException {

		// Parse the requirement from JSON
		final Requirement newRequirement = Requirement.fromJSON(content);

		// Saves the requirement in the database if possible
		this.save(s,newRequirement); 

		// Return the newly created requirement (this gets passed back to the client)
		return newRequirement;
	}
		
	/** Takes a session and returns an array of all the Requirements contained
	 * 
	 * @param s The current user session
	 * @return An array of all requirements in the Database	 * 
	 */
	public Requirement[] getAll(Session s) throws WPISuiteException {
		// Ask the database to retrieve all objects of the type Requirement.
		// Passing a dummy Requirement lets the db know what type of object to retrieve
		// Passing the project makes it only get requirements from that project
		List<Model> requirements = db.retrieveAll(new Requirement("",""), s.getProject());

		// Return the list of requirements as an array
		return requirements.toArray(new Requirement[0]);
	}
	
	
	/** Saves the given Requirement into the database if possible.
	 * 
	 *  @param s The current user session
	 *  @param model The Requirement to be saved to the database
	 * 
	 */
	public void save(Session s, Requirement model) throws WPISuiteException {
		assignUniqueID(model); // Assigns a unique ID to the Req if necessary
		
		// Save the requirement in the database if possible, otherwise throw an exception
		// We want the requirement to be associated with the project the user logged in to
		if (!db.save(model, s.getProject())) {
			throw new WPISuiteException();
		}
	}

    /** Takes a Requirement and assigns a unique id if necessary
     * 
     * @param req The requirement that possibly needs a unique id
     * @throws WPISuiteException If there are no Requirements in the database
     */
    public void assignUniqueID(Requirement req) throws WPISuiteException{
        if (req.getId() == -1){// -1 is a flag that says a unique id is needed            
            req.setId(this.Count() + 1); // Makes first Requirement have id = 1
        }        
    }

	/** Returns the number of Requirements currently in the database. Disregards
	 *  the current user session
	 * 
	 *  @return The number of Requirements currently in the databse
	 */
	public int Count() throws WPISuiteException {
		// Passing a dummy Requirement lets the db know what type of object to retrieve
		return db.retrieveAll(new Requirement("","")).size();
	}

// TODO    THIS NEEDS TESTING!!!!	
	/**  For the current user session, Takes a specific id for a Requirement and returns it 
	 *   in an array.	
	 *  
	 *  @param s  The current user session
	 *  @param id Points to a specific requirement
	 *  @return An array of Requirements 
 	 */
	public Requirement[] getEntity(Session s, String id) throws NotFoundException, WPISuiteException {
		// Ask the database to retrieve all objects of the type Requirement.
		// Passing a dummy Requirement lets the db know what type of object to retrieve
		// Passing the project makes it only get requirements from that project
		List<Model> requirements = db.retrieveAll(new Requirement("",""), s.getProject());
		
		// Iterate through the list to find the Requirement with the correct ID
		// Casting is used because the retrieveAll function returns a list of "Models"
		// even though we know it is returning a list of Requirements here
	    for ( Model r : requirements ) {
	    	if ((   (Requirement) r).getId() == Integer.parseInt( id) ){
	    		// Since the requirement has to be returned as an element in an array,
	    		// we make an array of size one and add the Requirement to the array.
	    		Requirement[] desiredReq = new Requirement[1];
	    		desiredReq[0] = (Requirement) r; 
	    		return desiredReq; 
	    	}
	    }		
		// Throw an exception if an ID was specified but not found
		throw new NotFoundException();
	}

	

	// Unimplemented Manager methods	
			

	
	
	// TODO    figure out how we might update a Requirement..
	// Hint: The "content" argument will hold a unique ID for the requirement to change
	/**  Updates a Requirement already in the database
	 *   
	 *  @param s The current user session
	 *  @param content The requirement to be update + the updates
	 * 	@return the changed requirement 
	 */
	public Requirement update(Session s, String content) throws WPISuiteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/** Deletes a Requirement from the database (not advised)
	 *  
	 *  @param s The current user session
	 *  @param id The unique of the requirement to delete
	 *  @return TRUE if successful or FALSE if it fails
	 */
	public boolean deleteEntity(Session s, String id) throws WPISuiteException {
		// TODO Auto-generated method stub

		// Not implemented- Throw exception
		throw new WPISuiteException();
	}
	

	/** Deletes ALL Requirement from the database (not advised)
	 * 
	 *  @param s The current user session
	 */
	public void deleteAll(Session s) throws WPISuiteException {
		// TODO Auto-generated method stub
		
		// Not implemented- Throw exception
		throw new WPISuiteException();
	}
	

	
// Advanced Manager methods
	/** 
	 * 
	 *  @param s The current user session
	 *  @param args 
	 *  @return 
	 */
	public String advancedGet(Session s, String[] args)
			throws WPISuiteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public String advancedPut(Session s, String[] args, String content)
			throws WPISuiteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String advancedPost(Session s, String string, String content)
			throws WPISuiteException {
		// TODO Auto-generated method stub
		return null;
	}


}
