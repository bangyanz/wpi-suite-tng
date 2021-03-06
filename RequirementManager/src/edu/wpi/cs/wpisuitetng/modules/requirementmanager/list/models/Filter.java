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

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.wpi.cs.wpisuitetng.modules.AbstractModel;
import edu.wpi.cs.wpisuitetng.modules.core.models.User;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Requirement;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.RequirementStatus;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.RequirementPriority;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.RequirementType;

/** A filter is a single set of constraints that a user can use to filter a list of requirements.
 */
public class Filter extends AbstractModel {
	/** Unique identifier for database extraction */
	private int uniqueID;
	/**	What to filter by */
	private FilterType type;
	/**	Operator to use for comparison */
	private OperatorType comparator;
	/**	String, integer, or RequirementStatus, RequirementPriority */
	private String value;
	/**	Whether we want this filter to be applied */
	private boolean useFilter;
	/** The owner of the filter */
	private User user;

	/**	Basic constructor.  Sets ID to -1 as a flag to the entity manager
	 */
	public Filter () {
		this.setUniqueID(-1); 		// default as a flag to entity manager
	}

	/**	Full constructor for Filter. 
	 * 
	 * @param type What we are filtering by
	 * @param comparator How we are searching
	 * @param value     Could be anything, preferably Integer, String, RequirementStatus, RequirementType, RequirementPriority
	 * @param useFilter  Field that says whether or not to use the filter
	 */
	public Filter( FilterType type, OperatorType comparator, Object value, boolean useFilter) {
		this();
		this.setType(type);
		this.setComparator(comparator);
		this.setValue(value);  		// Calls correctly overloaded setValue method
		this.setUseFilter(useFilter);

	}

	/** Converts this Filter to a JSON string
	 * @return a string in JSON representing this Filter
	 */
	public String toJSON() {
		String json;
		Gson gson = new Gson();
		json = gson.toJson(this, Filter.class);
		return json;
	}

	/** Converts the given list of Filters to a JSON string
	 * 
	 * @param dlist A list of Filters
	 * @return A string in JSON representing the list of Filters
	 */
	public static String toJSON(Filter[] dlist) {
		String json;
		Gson gson = new Gson();
		json = gson.toJson(dlist, Filter.class);
		return json;
	}

	@Override
	/** Alternate call to convert class to a JSON
	 * @return Same as {@link toJON()}
	 */
	public String toString() {
		return toJSON();
	}

	/** Converts a given JSON string to a Filter
	 * 
	 * @param json JSON string to parse containing Filter
	 * @return The Filter given by JSON
	 */
	public static Filter fromJSON(String json) {
		GsonBuilder builder = new GsonBuilder();
		return builder.create().fromJson(json, Filter.class);
	}

	/** Converts a JSON string of an array of filters to
	 *  an array of filters.
	 *  
	 * @param json JSON string to parse containing Filter array
	 * @return The Filter array given by JSON
	 */
	public static Filter[] fromJSONArray(String json) {
		GsonBuilder builder = new GsonBuilder();
		return builder.create().fromJson(json, Filter[].class);
	}

	/**
	 * @see edu.wpi.cs.wpisuitetng.modules.Model#identify(java.lang.Object)
	 */
	public Boolean identify(Object o) {
		Boolean returnValue = false;
		if(o instanceof Filter && uniqueID == ((Filter) o).getUniqueID()) {
			returnValue = true;
		}	
		if(o instanceof String && Integer.toString(uniqueID).equals(o)) {
			returnValue = true;
		}
		return returnValue;
	}

	/** Changes all fields in the current Filter to equal the fields of the
	 * filter passed in as "filterUpdate".
	 * User and UniqueID are ignored as they should never be updated.
	 * 
	 * @param filterUpdate Filter holding the updates
	 */
	public void updateFilter(Filter filterUpdate){	
		this.setType(filterUpdate.getType());
		this.setComparator(filterUpdate.getComparator());
		this.setValue(filterUpdate.getValue());
		this.setUseFilter(filterUpdate.isUseFilter());
		// User does not need to be set, as it cannot be changed anyways
		// Unique ID does not need to be set, as it cannot be changed anyways
	}

	/** Compares two filters. Intended for use in the makeEntity method.
	 * 
	 * @param toCompareTo The Filter to compare to
	 * @return Whether the two Filters are equal or not
	 */
	public boolean equals(Filter toCompareTo){ // $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.obeyEqualsContract.obeyGeneralContractOfEquals
		if (this.getType() != toCompareTo.getType()) return false;
		if (this.getComparator() != toCompareTo.getComparator()) return false;	
		if (!this.getValue().equals(toCompareTo.getValue())  ) return false;		
		return true;
	}	

	/** Determines whether a Requirement passes this filter
	 *
	 *	@param req The Requirement in question
	 *	@return True if the Requirement passes, false if it does not
	 */
	public boolean passesFilter(Requirement req){
		if (!this.isUseFilter()) return true; // If filter is turned off, the Requirement passes
		try{
			switch (type){	
			// The following are strings
			case Name:
				return OperatorType.perform(comparator,value.toLowerCase(), req.getName().toLowerCase(), false);
			case Description:
				return OperatorType.perform(comparator, value.toLowerCase(), req.getDescription().toLowerCase(), false);		
			case ReleaseNumber:
				return OperatorType.perform(comparator, value.toLowerCase(), req.getReleaseNumber().toLowerCase(), false);
			case AssignedUsers:
				if (comparator == OperatorType.Contains ){
					return req.getUserNames().contains(value) || (req.getUserNames().size() == 0 && value.equals(""));			
				} else { // The operator will be DoesNotContain
					return (!req.getUserNames().contains(value)  && !value.equals("") )|| (req.getUserNames().size() > 0 && value.equals(""));
				}
				// The following five are Integers
			case Id: 
				return OperatorType.perform(comparator, Integer.parseInt(value), req.getId());
			case Iteration:
				return OperatorType.perform(comparator, Integer.parseInt(value), req.getIteration());
			case ActualEffort:
				return OperatorType.perform(comparator, Integer.parseInt(value), req.getActualEffort());		
			case Estimate:
				return OperatorType.perform(comparator, Integer.parseInt(value), req.getEstimate());	

				// The following three are different enums
			case Status:
				return OperatorType.perform(comparator, RequirementStatus.toStatus(value), req.getStatus());
			case Type:
				return OperatorType.perform(comparator, RequirementType.toType(value), req.getType());
			case Priority:
				return OperatorType.perform(comparator, RequirementPriority.toPriority(value), req.getPriority());

				// Default
			default:
				return true;  // default to not filter out stuff
			}
		} catch (NumberFormatException nfe){
			return false; // If parseInt is given a string with no numbers, the filter is set to pass the filter
		}
	}

	/** This method is unused, but is required by the AbstractModel interface
	 * @see edu.wpi.cs.wpisuitetng.modules.Model#save()
	 */
	public void save() {
	}

	/** This method is unused, but is required by the AbstractModel interface
	 * @see edu.wpi.cs.wpisuitetng.modules.Model#delete()
	 */
	public void delete() {
	}
	
	/** Get the uniqueID for this Filter
	 * @return uniqueID The "uniqueID" int of this Filter
	 */
	public int getUniqueID() {
		return uniqueID;
	}

	/** Set the uniqueID for this Filter
	 * Be careful using this method
	 * @param uniqueID The "uniqueID" (an int) to set
	 */
	public void setUniqueID(int uniqueID) {
		this.uniqueID = uniqueID;
	}

	/** Get what field this filters by
	 * @return type The "type" FilterType of this Filter
	 */
	public FilterType getType() {
		return type;
	}

	/** Set what field this filters by
	 * @param type The "type" FilterType of this Filter to set
	 */
	public void setType(FilterType type) {
		this.type = type;
	}

	/** Get how we compare when filtering
	 * @return comparator The "comparator" OperatorType
	 */
	public OperatorType getComparator() {
		return comparator;
	}

	/** Set how we compare when filtering
	 * @param comparator The "comparator" OperatorType to set
	 */
	public void setComparator(OperatorType comparator) {
		this.comparator = comparator;
	}

	/** Whether we want to use this filter
	 * @return useFilter The "useFilter" boolean
	 */
	public boolean isUseFilter() {
		return useFilter;
	}

	/** Set if we want to use this filter
	 * @param useFilter The "useFilter" boolean to set
	 */
	public void setUseFilter(boolean useFilter) {
		this.useFilter = useFilter;
	}

	/** Get the User who created this Filter
	 * @return user The "user" (a User) that created this Filter
	 */
	public User getUser() {
		return user;
	}

	/** Set the User who created this Filter
	 * @param user The "user" (a User) that created this Filter to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/** Get the "value" String that we use for comparison in this Filter
	 * @return value The "value" String
	 */
	public String getValue() {
		return value;
	}

	/** Sets the value of the Filter when the input is an Object
	 * @param o The "value" to set (an Object)
	 */
	public void setValue(Object o){
		value = o.toString();		
	}
}
