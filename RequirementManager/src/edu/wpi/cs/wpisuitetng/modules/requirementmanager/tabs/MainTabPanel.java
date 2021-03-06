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

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.tabs;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.JanewayModule;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.ListView;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement.RequirementView;

/** This tabbed pane will appear as the main content of the Requirements tab.
 * It starts out showing the single Dashboard tab.
 */
@SuppressWarnings("serial")
public class MainTabPanel extends JTabbedPane {
	
	/**
	 * Field parent.
	 */
	private JanewayModule parent;
	
	/**
	 * Field mainTabController.
	 */
	private MainTabController mainTabController;
	
	/**
	 * Constructor for MainTabPanel.
	 * @param _parent JanewayModule
	 */
	public MainTabPanel(JanewayModule _parent) {
		parent = _parent;
		setTabPlacement(TOP);
		setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
		setBorder(BorderFactory.createEmptyBorder(5, 3, 3, 3));
		mainTabController = new MainTabController(this);
		addTab("List Requirements", new ImageIcon(), new ListView(mainTabController),
		       "List of all requirements for this project");
	}
	
	public MainTabController getMainTabController() {
		return mainTabController;
	}
	
	@Override
	public void insertTab(String title, Icon icon, Component component, String tip, int index) {
		super.insertTab(title, icon, component, tip, index);
		// the Dashboard tab cannot be closed
		if(!(component instanceof ListView)) {
			setTabComponentAt(index, new ClosableTabComponent(this));
		}
	}
	
	@Override
	public void removeTabAt(int index) {
		// if a tab does not have the close button UI, it cannot be removed
		if(getTabComponentAt(index) instanceof ClosableTabComponent) {
			super.removeTabAt(index);
		}
	}
	
	@Override
	public void setComponentAt(int index, Component component) {
		super.setComponentAt(index, component);
		fireStateChanged(); // hack to make sure toolbar knows if component changes
	}
	
	public JanewayModule getJanewayModule()
	{
		return parent;
	}
	
	public int getNonRequirementTabCount() {
		int count = 0;
		for (int i = 0; i < getTabCount(); i++) {
			if (getComponentAt(i) instanceof RequirementView) count++;
		}
		return count;
	}
}
