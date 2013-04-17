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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement.RequirementAttributePanel;

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
//	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//		raPanel.changeStatusWithIteration( e);
//		raPanel.checkIterationChange();
//	}
//
//
//	/** Watches the "Iteration" box for changes and sets up the "status" field
//	 *  of the requirement appropriately	 */
//	public void popupMenuCanceled(PopupMenuEvent e) {
//		raPanel.changeStatusWithIteration( e);
//		raPanel.checkIterationChange();
//	}
//	
//
//	@Override
//	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		raPanel.changeStatusWithIteration( e);
		raPanel.checkIterationChange();
		
	}
}
