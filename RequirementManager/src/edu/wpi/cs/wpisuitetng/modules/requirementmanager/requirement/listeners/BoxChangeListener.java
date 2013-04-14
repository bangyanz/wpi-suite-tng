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
package edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JComboBox;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Requirement;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement.RequirementAttributePanel;

/** This listener is used on text fields/areas and turns those
 *  boxes yellow when changes are detected.
 * 
 */
@SuppressWarnings("rawtypes")
public class BoxChangeListener implements KeyListener{

	/** The panel with the field being watched */
	private RequirementAttributePanel thePanel;
	/** The field to watch */
	private JComboBox toWatch;
	/** The old values to compare against */
	private Requirement reference;
	/** The field of the reference to watch */
	private String fieldToCheck ;

	/** Index in the array of booleans*/
	int indexOfBoolean;

	/** The getter method that is used on the reference to get the appropriate value to check against */
	private Method getOldFieldValue;

	/** Constructor that takes all the references it needs
	 * 
	 * @param thePanel
	 * @param toWatch
	 * @param reference
	 * @param fieldToCheck
	 */
	public BoxChangeListener(RequirementAttributePanel thePanel,JComboBox toWatch, 
			Requirement reference, String fieldToCheck, int indexOfBoolean ){
		this.thePanel = thePanel;
		this.toWatch = toWatch;
		this.reference = reference;
		this.fieldToCheck = fieldToCheck;
		this.indexOfBoolean =  indexOfBoolean;
		// Get the getter method for the requirement
		Method[] allMethods = (Requirement.class).getMethods();
		for(Method m: allMethods){//Cycles through all of the methods in the requirement Class
			if(m.getName().equalsIgnoreCase("get"+fieldToCheck)){
				getOldFieldValue = m; //saves the method called "get" + aFieldName
			}
		}


	}

	/** Checks the field for changes and sets it to yellow on changes or white otherwise */
	private void checkForChanges(){
		// The value to compare against
		Object oldValue;
		System.out.print("Checking: " + fieldToCheck);
		// Since the invoke stuff can throw exceptions, we check and print here as necessary
		try {
			oldValue = getOldFieldValue.invoke(reference);
		} catch (IllegalArgumentException iae) {
			System.err.println("FieldChangeListener problem: "+ fieldToCheck);
			return;
		} catch (IllegalAccessException iae2) {
			System.err.println("FieldChangeListener problem: "+ fieldToCheck);
			return;
		} catch (InvocationTargetException ite) {
			System.err.println("FieldChangeListener problem: "+ fieldToCheck);
			return;         
		}


		// Check the old value and set the box yellow as necessary
		if (!toWatch.getSelectedItem().toString().equals(oldValue + "")) {
			thePanel.changeField(toWatch, 4, true);
			System.out.println("  Result: activate");
		} else {
			System.out.println("  Result: deactivate");
			thePanel.changeField(toWatch, 4, false);
		}
	}


	/** This is called when the user types then releases the key. If
	 *  changes are made, the box is turned yellow. 
	 */
	public void keyReleased(KeyEvent e) {
		checkForChanges();		
	}


	/** This method is unused but required by the interface   */
	public void keyTyped(KeyEvent e) {		
		return;
	}


	/** This method is unused but required by the interface    */
	public void keyPressed(KeyEvent e) {
		return;
	}
}
