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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.DateTableCellRenderer;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Note;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.SubRequirementTabModel;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement.RequirementTab.Mode;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.tabs.MainTabController;

@SuppressWarnings("serial")
public class SubRequirementTab extends JPanel {

	private RequirementTab parent;
	
	protected SubRequirementTabModel submodel;
	
	protected JTable subtable;
	
	//protected final RetrieveRequirmentsIntoSubController subController;
	
	public SubRequirementTab(RequirementTab rparent){
		//this.subController=subController;
		setParent(rparent);
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 3, 3, 3)); 
		submodel = new SubRequirementTabModel();
		subtable = new JTable(submodel);
		subtable.setAutoCreateRowSorter(true);
		subtable.setFillsViewportHeight(true);
		subtable.setDefaultRenderer(Date.class, new DateTableCellRenderer());
		
		// Put the table in a scroll pane
		JScrollPane subScrollPane = new JScrollPane(subtable);
		
		this.add(subScrollPane, BorderLayout.CENTER);
		//this.subController.refreshData();
		
	}

	public RequirementTab getParent() {
		return parent;
	}

	public void setParent(RequirementTab parent) {
		this.parent = parent;
	}

	public SubRequirementTabModel getModel() {
		return this.submodel;
	}

	public JTable getResultsTable() {
	
		return subtable;
	}
	
}