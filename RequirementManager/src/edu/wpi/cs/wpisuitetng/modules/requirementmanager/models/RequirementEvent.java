package edu.wpi.cs.wpisuitetng.modules.requirementmanager.models;

import java.util.Date;

import com.google.gson.GsonBuilder;

import edu.wpi.cs.wpisuitetng.modules.AbstractModel;
import edu.wpi.cs.wpisuitetng.modules.core.models.User;

/**
 * Implementations of this interface represent some kind of event in a Requirement.
 * For example, the addition of a comment or the modification of fields.
 */
public abstract class RequirementEvent extends AbstractModel {
	
	public enum EventType {
		CHANGESET
	};
	
	protected Date date = new Date();
	protected User user = new User("", "", "", -1);
	
	/**
	 * The type of event this is.  Subclasses must specify this in order to be deserialized properly.
	 */
	protected EventType type;
	
	/**
	 * @return The Date when this event happened
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @param date The Date of the Event to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return The User responsible for this event
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @param user The User responsible for the event to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Given a builder, add anything to it that's necessary for Gson to interact with this class.
	 * 
	 * @param builder The builder to modify
	 */
	public static void addGsonDependencies(GsonBuilder builder) {
		builder.registerTypeAdapter(RequirementEvent.class, new RequirementEventDeserializer());
		builder.registerTypeAdapter(RequirementChangeset.class, new RequirementChangesetDeserializer());
	}
	
	@Override
	public void save() {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
	}
	
	// this model will only be created server side and then retrieved as part of a Requirement in the future
	// so I'm not sure if this is necessary
	@Override
	public Boolean identify(Object o) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
