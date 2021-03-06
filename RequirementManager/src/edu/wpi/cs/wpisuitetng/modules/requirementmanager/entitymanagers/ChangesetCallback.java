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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.wpi.cs.wpisuitetng.modules.Model;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.entitymanagers.ModelMapper.MapCallback;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.changeset.FieldChange;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.changeset.RequirementChangeset;

/** Responsible for filling in a changeset after being passed to
 * {@link ModelMapper#map(Model, Model, MapCallback)} 
 */
class ChangesetCallback implements MapCallback {

	/** The RequirementChangeset to store changes in */
	private final RequirementChangeset changeset;

	/** Set of field Strings not to add as changes */
	private static final Set<String> dontRecord =
			new HashSet<String>(Arrays.asList("events", "lastModifiedDate"));

	/** Create a callback that will fill in the given changeset.
	 * @param changeset The changeset to add changes to
	 */
	ChangesetCallback(RequirementChangeset changeset) {
		this.changeset = changeset;
	}

	/** Method call that checks and records a change for a field in an object
	 * @param source The updated model initiating the call
	 * @param destination The old model that is being targeted by the call
	 * @param fieldName The field that is being changed
	 * @param sourceValue The updated value of the field
	 * @param destinationValue The old value of the field
	 * @return The object passed in to source field
	 * @see edu.wpi.cs.wpisuitetng.modules.requirementmanager.entitymanagers.ModelMapper$MapCallback#call(Model, Model, String, Object, Object)
	 */
	@Override
	public Object call(Model source, Model destination, String fieldName,
			Object sourceValue, Object destinationValue) {
		if(!dontRecord.contains(fieldName)) {
			if(!objectsEqual(sourceValue, destinationValue)) {

				/* This field has changed - indicate the change in the changeset
				 * remember that fields from updated requirement are being copied to old one
				 * destinationValue is the old value
				 */
				changeset.getChanges().put(fieldName, new FieldChange<Object>(destinationValue, sourceValue));
			}
		}
		return sourceValue;
	}

	/** Method to compare two objects
	 * @param a Object
	 * @param b Object
	 * @return boolean
	 */
	private boolean objectsEqual(Object a, Object b) {
		// Java 7 has Objects.equals... we're on Java 6
		if(a == b) {
			return true;
		}
		if(a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}
}