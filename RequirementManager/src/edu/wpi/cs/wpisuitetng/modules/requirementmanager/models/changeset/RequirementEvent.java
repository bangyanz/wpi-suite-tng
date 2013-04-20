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

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.changeset;

import java.util.Date;

import com.google.gson.GsonBuilder;

import edu.wpi.cs.wpisuitetng.modules.AbstractModel;
import edu.wpi.cs.wpisuitetng.modules.core.models.User;

/**
 * Implementations of this interface represent some kind of event in a requirement.
 * For example, the addition of a comment or the modification of fields.
 */
public abstract class RequirementEvent extends AbstractModel {
	
	/** The types of possible events */
	public enum EventType {
		CREATION,
		CHANGESET,
		NOTE,
		USER,
		ATTACHMENT
	};
	
	/** The date that this event was made */
	protected Date date = new Date();
	/** The user that caused this event */
	protected String userName = "";	

	/** The type of event this is.  Subclasses must specify this in order to be deserialized properly.	 */
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
	public String getUser() {
		return userName;
	}
	
	/**
	 * @param userName The User responsible for the event to set
	 */
	public void setUser( String userName) {
		this.userName = userName;
	}
	
	/**
	 * @param userName The User responsible for the event to set
	 */
	public void setUser( User user) {
		this.userName = user.getName();
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
	
	/**
	 * @return the body of the history log message
	 */
	public abstract String getBodyString();
	
	/**
	 * @return the label of the history log message
	 */
	public abstract String getLabelString();
	
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