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
package edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.RequirementStatus;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement.RequirementTab.Mode;

/** An action listener specifically made to watch an Iteration selection combo 
 *  box and on changes between any iteration and the backlog, change the status
 *  of the requirement appropriately.            */
public class IterationChangeListener implements ItemListener {

	/** The panel with the Iteration drop down box to be watched */
	RequirementAttributePanel raPanel;

	/** Basic constructor
	 * 
	 * @param raPanel The panel with the box to watch
	 */
	public IterationChangeListener(RequirementAttributePanel raPanel){
		this.raPanel = raPanel;
	}

//	/** Watches the "Iteration" box for changes and sets up the "status" field
//	 *  of the requirement appropriately	 */
//	@SuppressWarnings("rawtypes")// This warning is necessary because of the current version of Java
//	public void actionPerformed(ActionEvent e) {
//		System.out.println("The assigned iteration has been changed; the status will be changed accordingly.");
//
//		if (raPanel.getMode().equals(Mode.EDIT) ){
//			RequirementStatus currentStatus = raPanel.getCurrentRequirement().getStatus(); 			
//			// You can't change the status while Deleted or Complete anyways, so this is a check.
//			if ( currentStatus == RequirementStatus.Complete 	|| currentStatus == RequirementStatus.Deleted ){
//				return;
//			}
//			
//			if ( 0 == ((JComboBox)e.getSource()).getSelectedIndex()){
//				if ( raPanel.getCurrentRequirement().getStatus() == RequirementStatus.New){
//			//		raPanel.getCurrentRequirement().setStatus(RequirementStatus.Open);
//					raPanel.updateStatusSettings("New");
//					return;
//				}
//				raPanel.getCurrentRequirement().setStatus(RequirementStatus.Open);
//				raPanel.updateStatusSettings("Open");
//			} else {
//	//			raPanel.getCurrentRequirement().setStatus(RequirementStatus.InProgress);
//				raPanel.updateStatusSettings("InProgress");
//			}
//		}
//
//
//	}

	/** Watches the "Iteration" box for changes and sets up the "status" field
	 *  of the requirement appropriately	 */
	@SuppressWarnings("rawtypes")
	public void itemStateChanged(ItemEvent e) {
	//	System.out.println("The assigned iteration has been changed; the status will be changed accordingly.");

		if (raPanel.getMode().equals(Mode.EDIT) ){
			RequirementStatus currentStatus = raPanel.getCurrentRequirement().getStatus(); 			
			// You can't change the status while Deleted or Complete anyways, so this is a check.
			if ( currentStatus == RequirementStatus.Complete 	|| currentStatus == RequirementStatus.Deleted ){
				return;
			}
			
			if ( 0 == ((JComboBox)e.getSource()).getSelectedIndex()){
				if ( raPanel.getCurrentRequirement().getStatus() == RequirementStatus.New){
			//		raPanel.getCurrentRequirement().setStatus(RequirementStatus.Open);
					raPanel.updateStatusSettings("New");
					return;
				}
				raPanel.getCurrentRequirement().setStatus(RequirementStatus.Open);
				raPanel.updateStatusSettings("Open");
			} else {
	//			raPanel.getCurrentRequirement().setStatus(RequirementStatus.InProgress);
				raPanel.updateStatusSettings("InProgress");
			}
		}
		
	}
}