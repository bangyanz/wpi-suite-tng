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

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.lowagie.text.Font;

import edu.wpi.cs.wpisuitetng.modules.core.models.User;
import edu.wpi.cs.wpisuitetng.network.Network;
import edu.wpi.cs.wpisuitetng.network.Request;
import edu.wpi.cs.wpisuitetng.network.models.HttpMethod;

/** This panel is added to the RequirementTabPanel and 
 * contains all the GUI components involving assigning/removing a user to/from a requirement:
 * -a list of available users (in a scrollpane) not assigned to this requirement
 * -a list of users (in a scrollpane) assigned to this requirement
 * -two buttons to enable transfer of selected users between the lists (in an inner panel)
 */
@SuppressWarnings({"serial","rawtypes","unchecked"})
public class UserChooserTab extends JPanel {

	/** The actual list of Users used to create the JList unassignedList */
	private UserListModel unassignedUserListModel;
	
	/** The actual list of Users used to create the JList assignedList */
	private UserListModel assignedUserListModel;
	
	/** The JList that displays the available unassigned users */
	private JList unassignedList;
	
	/** The JList that displays the users assigned to this requirement */
	private JList assignedList;

	/** A boolean indicating if input is enabled on the form  */
	private boolean inputEnabled;

	/** Stores the RequirementTab that contains this panel (within a RequirementTabPanel)*/
	private RequirementTab parent; 

	/** The scroll pane to hold the JList of unassigned users ("unassignedList") */
	private JScrollPane unassignedScroll;
	
	/** The scroll pane to hold the JList of users assigned to this requirement ("assignedList") */
	private JScrollPane assignedScroll;

	/** The button to trigger the assignment of a selected user to this requirement from the "unassignedList" */
	private JButton addSelectedUserButton;
	
	/** The button to trigger the unassignment of a selected user to this requirement from the "assignedList" */
	private JButton removeSelectedUserButton;

	/** A label to tell the user that changes in this panel are automatically saved */
	private JLabel changesSavedLabel;

	/** An array list of all the users in the current project (assigned or unassigned to this requirement) */
	private ArrayList<User> users;

	/** Used to store the ListSelectionModel of the unassignedList JList. This is used to respond to changes in the selection of users within the unassignedList */
	private ListSelectionModel unassignedListSelectionModel;
	
	/** Used to store the ListSelectionModel of the assignedList JList. This is used to respond to changes in the selection of users within the assignedList */
	private ListSelectionModel assignedListSelectionModel;

	/** The constructor for UserChooserTab;
	 * Construct the panel, the components, and add the
	 * components to the panel.
	 * @param reqTabParent	The RequirementTab parent of this tab
	 */
	public UserChooserTab(final RequirementTab reqTabParent) {

		parent = reqTabParent; //Set the RequirementPanel that contains this instance of this panel

		// Set the layout manager for this tab to a new Box layout with a line orientation (left to right)
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		//Set an empty border around this panel for spacing purposes (10 units wide in all directions)
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		//Create the buttons
		addSelectedUserButton = new JButton("Add Users ->");
		removeSelectedUserButton= new JButton("<- Remove Users");

		//Set the buttons to be initially disabled because there will be nothing selected in the lists upon creation
		addSelectedUserButton.setEnabled(false);
		removeSelectedUserButton.setEnabled(false);

		//Create the label
		changesSavedLabel = new JLabel("(saves automatically)");
		changesSavedLabel.setFont(changesSavedLabel.getFont().deriveFont(7));//set the size (7 point) of the text of this label
		changesSavedLabel.setFont(changesSavedLabel.getFont().deriveFont(Font.ITALIC));//set the text of this label to italic

		//Create the UserListModels
		unassignedUserListModel = new UserListModel();
		assignedUserListModel = new UserListModel();

		//Create the JLists using the appropriate UserListModel
		unassignedList = new JList(unassignedUserListModel);
		assignedList = new JList(assignedUserListModel);

		//List of users
		users = new ArrayList<User>();

		Request request;
		request = Network.getInstance().makeRequest("core/user", HttpMethod.GET);
		request.addObserver(new RetrieveAllUsersObserver(this));
		request.send();
	}
	
	/** 
	 * This method is a hack-around to prevent potential errors, 
	 * so instead of using a while loop to check if we have received a response
	 * It resumes initialization after receiving a success response in the RetrieveAllUsersObserver
	 */
	public void resumeInitialization(){
		//Add the users to the unassignedUserListModel
		for (int i = 0; i < users.size(); i++) {
			if (parent.getCurrentRequirement().getUserNames().contains(users.get(i).getName()))
				assignedUserListModel.addUser(users.get(i).getName());
			else
				unassignedUserListModel.addUser(users.get(i).getName());
		}

		//Create the scrollpanes to hold their corresponding lists
		unassignedScroll = new JScrollPane(unassignedList);
		assignedScroll = new JScrollPane(assignedList);

		//Find the maximum preferred width and height of the two lists
		double maxPrefHeight= 0;//Used to store the maximum preferred height
		double maxPrefWidth = 0;//Used to store the maximum preferred width

		if ((unassignedUserListModel.getSize() == 0) && (assignedUserListModel.getSize() == 0)) //If both lists are empty
		{
			//Set the maximum preferred width and height to a default
			maxPrefHeight = 300;
			maxPrefWidth = 140;
		}
		else if (unassignedUserListModel.getSize() == 0) //If only the unassigned user list is empty
		{
			//Use the preferred width and height of the assigned list
			maxPrefHeight = assignedScroll.getPreferredSize().getHeight();
			maxPrefWidth = assignedScroll.getPreferredSize().getWidth();
		}
		else if (assignedUserListModel.getSize() == 0)//If only the assigned list is empty
		{
			//Use the preferred width and height of the unassigned list
			maxPrefHeight = unassignedScroll.getPreferredSize().getHeight();
			maxPrefWidth = unassignedScroll.getPreferredSize().getWidth();
		}
		else //If neither list is empty
		{
			if (unassignedScroll.getPreferredSize().getHeight()>= assignedScroll.getPreferredSize().getHeight())//If the unassigned list has a equal or greater preferred height to the assigned list
				maxPrefHeight = unassignedScroll.getPreferredSize().getHeight();//Use the preferred height of the unassigned list
			else//The assigned list has a greater preferred height
				maxPrefHeight = assignedScroll.getPreferredSize().getHeight();//So use the preferred height of the assigned list

			if (unassignedScroll.getPreferredSize().getWidth()>= assignedScroll.getPreferredSize().getWidth())//If the unassigned list has a equal or greater preferred width to the assigned list
				maxPrefWidth = unassignedScroll.getPreferredSize().getWidth();//Use the preferred width of the unassigned list
			else//The assigned list has a greater preferred width
				maxPrefWidth = assignedScroll.getPreferredSize().getWidth();//So use the preferred width of the assigned list
		}
		
		//Add 10 to each value to account for the border
		maxPrefHeight += 10;
		maxPrefWidth += 10;

		//Set the preferred size of both of the scroll panes holding the lists to the same value (the maximum of the preferred widths and heights of the lists)
		unassignedScroll.setPreferredSize(new Dimension((int)maxPrefWidth,(int)maxPrefHeight));
		assignedScroll.setPreferredSize(new Dimension((int)maxPrefWidth,(int)maxPrefHeight));

		//Create and set the titled borders for the lists
		//create the titled border for the list of assigned users
		TitledBorder assignedUsersTitleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Assigned Users");
		assignedUsersTitleBorder.setTitleFont(addSelectedUserButton.getFont().deriveFont(12));//set the title font to the same font used on the "addSelectedUsersButton" in a size 12
		assignedUsersTitleBorder.setTitleColor(Color.gray);//set the color of the title to grey
		//create the titled border for the list of unassigned users
		TitledBorder unassignedUsersTitleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Unassigned Users"); 
		unassignedUsersTitleBorder.setTitleFont(assignedUsersTitleBorder.getTitleFont());//Use the same font as the "assignedUsersTitleBorder"
		unassignedUsersTitleBorder.setTitleColor(Color.gray);//set the color of the title to grey

		//add an inner border padding of 5 units all around to each titled border, and set the appropriate border to the corresponding scroll pane
		assignedScroll.setBorder(BorderFactory.createCompoundBorder(assignedUsersTitleBorder, (BorderFactory.createEmptyBorder(5, 5, 5, 5)) )  );
		unassignedScroll.setBorder(BorderFactory.createCompoundBorder(unassignedUsersTitleBorder, (BorderFactory.createEmptyBorder(5, 5, 5, 5)) )  );

		//Set the background of both of the scroll panes to white
		assignedScroll.setBackground(Color.WHITE);
		unassignedScroll.setBackground(Color.WHITE);

		//Create and add a selection listener to the unassignedList to grey out the add user button when nothing is selected
		unassignedListSelectionModel = unassignedList.getSelectionModel();
		unassignedListSelectionModel.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (lsm.isSelectionEmpty()) {
					addSelectedUserButton.setEnabled(false);

				} else {
					addSelectedUserButton.setEnabled(true);
				}

			}
		});

		//Create and add a selection listener to the assignedList to grey out the remove user button when nothing is selected
		assignedListSelectionModel = assignedList.getSelectionModel();
		assignedListSelectionModel.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (lsm.isSelectionEmpty()) {
					removeSelectedUserButton.setEnabled(false);

				} else {
					removeSelectedUserButton.setEnabled(true);
				}

			}
		});

		//Create and add an action listener to the "addSelectedUserButton"
		addSelectedUserButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!(unassignedList.isSelectionEmpty()))//if a user is selected in the unassigned user list at the time the button is pushed...
				{
					int oldIndicies[] = assignedList.getSelectedIndices();//Grab an array of the selected users' indexes in the assigned user list (in increasing order). This will be used to restore the selection(s) later if needed.

					int indices[] = unassignedList.getSelectedIndices();//Grab an array of the selected users' indexes in the unassigned user list (in increasing order)

					for (int i = 0; i<indices.length; i++ )//For each index in the array "indices"
					{
						int nextIndex = indices[i];//Grab the index stored at the index "i" in "indices"
						assignedUserListModel.addUser(unassignedUserListModel.getUserAt(nextIndex - i));//add the appropriate user from the unassignedUserListModel to the assignedUserListModel, taking care to account for previously removed users
						unassignedUserListModel.removeElementAt(nextIndex - i);//remove the appropriate user from the unassignedUserListModel, taking care to account for previously removed users
					}

					unassignedList.clearSelection();//Clear the selection in the unassignedList. This helps prevent errors, as the selected indices are only stored by swing upon mouse clicks

					//This section prevents an error whereby if the top user in the assignedList was selected at the time a user was added to the list, that new user will be selected too
					if( (!(assignedList.isSelectionEmpty())) && (assignedList.getMinSelectionIndex() == 0))//if a user is selected in the assigned user list at the time the button was pushed and the first selected index in the assigned user list is zero
					{
						//Alter the old array of selected indices in the assignedList to account for the newly added users
						for (int i =0; i< oldIndicies.length; i++)
						{
							oldIndicies[i]+=indices.length;
						}
						assignedList.setSelectedIndices(oldIndicies);//Restore the old selection(s) appropriately
					}

					//Revalidate and repaint both lists to ensure the change shows on the GUI
					unassignedList.revalidate();
					unassignedList.repaint();
					assignedList.revalidate();
					assignedList.repaint();

					parent.getParent().getController().saveUsers(); //Actually save the changes to the list of users assigned to this requirement
				}
				else{}//No users were selected in the unassigned user list at the time the button was pushed
			}
		});

		//Create and add an action listener to the "removeSelectedUserButton"
		removeSelectedUserButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!(assignedList.isSelectionEmpty()))//if a user is selected in the assigned user list at the time the button is pushed...
				{
					int oldIndicies[] = unassignedList.getSelectedIndices();//Grab an array of the selected users' indexes in the unassigned user list (in increasing order). This will be used to restore the selection(s) later if needed.
					int indices[] = assignedList.getSelectedIndices();//Grab an array of the selected users' indexes in the assigned user list (in increasing order)

					for (int i = 0; i<indices.length; i++ )//For each index in the array "indices"
					{
						int nextIndex = indices[i];//Grab the index stored at the index "i" in "indices"
						unassignedUserListModel.addUser(assignedUserListModel.getUserAt(nextIndex - i));//add the appropriate user from the assignedUserListModel to the unassignedUserListModel, taking care to account for previously removed users
						assignedUserListModel.removeElementAt(nextIndex - i);//remove the appropriate user from the assignedUserListModel, taking care to account for previously removed users
					}

					assignedList.clearSelection();//Clear the selection in the assignedList. This helps prevent errors, as the selected indices are only stored by swing upon mouse clicks

					//This section prevents an error whereby if the top user in the unassignedList was selected at the time a user was added to the list, that new user will be selected too
					if( (!(unassignedList.isSelectionEmpty())) && (unassignedList.getMinSelectionIndex() == 0))//if a user is selected in the unassigned user list at the time the button was pushed and the first selected index in the assigned user list is zero
					{
						//Alter the old array of selected indices in the assignedList to account for the newly added users
						for (int i =0; i< oldIndicies.length; i++)
						{
							oldIndicies[i]+=indices.length;
						}
						unassignedList.setSelectedIndices(oldIndicies);//Restore the old selection(s) appropriately
					}

					//Revalidate and repaint both lists to ensure the change shows on the GUI
					unassignedList.revalidate();
					unassignedList.repaint();
					assignedList.revalidate();
					assignedList.repaint();

					parent.getParent().getController().saveUsers(); //Actually save the changes to the list of users assigned to this requirement
				}
				else{}//No users were selected in the assigned user list at the time the button was pushed
			}
		});

		//Create the buttonPanel using a grid layout with 3 rows, 1 column, 0 horizontal spacing, and 6 units of vertical spacing between the components
		/** This is a small inner panel to hold the buttons and the changesSavedLabel */
		JPanel buttonPanel = new JPanel(new GridLayout(3,1,0,6));
		
		//Set the border of the buttonPanel to an empty boarder for spacing purposes
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 3, 3, 3));

		//Set the alignment of the changesSavedLabel to the center of it's area
		changesSavedLabel.setHorizontalAlignment(SwingConstants.CENTER);

		//add the buttons and the changesSavedLabel to the buttonPanel
		buttonPanel.add(addSelectedUserButton);
		buttonPanel.add(removeSelectedUserButton);
		buttonPanel.add(changesSavedLabel);

		//set the maximum size of the buttonPanel. This is especially important because a grid layout will stretch it's components to fill whatever space it can
		buttonPanel.setMaximumSize(new Dimension(140, 90));
		//End button panel

		//Add the components to this panel
		this.add(Box.createHorizontalGlue());//Add a horizontal glue component to keep the lists in the center of the panel
		this.add(unassignedScroll);//Add the list of unassigned users, in the "unassignedScroll" scroll pane to this panel
		this.add(Box.createRigidArea(new Dimension(15,0)));//Add a rigid area for spacing
		this.add(buttonPanel);//Add the inner "buttonPanel" containing the buttons to this panel
		this.add(Box.createRigidArea(new Dimension(15,0)));//Add a rigid area for spacing
		this.add(assignedScroll);//Add the list of assigned users, in the "assignedScroll" scroll pane to this panel
		this.add(Box.createHorizontalGlue());//Add a horizontal glue component to keep the lists in the center of the panel
	}

	/**	
	 * @return unassignedList The "unassignedList" JList
	 */
	public JList getUnassignedList() {
		return unassignedList;
	}

	/**
	 * @param unassignedList The "unassignedList" (an instance of JList) to set
	 */
	public void setUnassignedList(JList unassignedList) {
		this.unassignedList = unassignedList;
	}

	/**
	 * @return assignedList The "assignedList" JList
	 */
	public JList getAssignedList() {
		return assignedList;
	}

	/**
	 * @param assignedList The "assignedList" (an instance of JList) to set
	 */
	public void setAssignedList(JList assignedList) {
		this.assignedList = assignedList;
	}


	/**
	 * @return addSelectedUserButton The "addSelectedUserButton" JButton
	 */
	public JButton getAddSelectedUserButton() {
		return addSelectedUserButton;
	}

	/**
	 * @param addSelectedUserButton The "addSelectedUserButton" (an instance of JButton) to set
	 */
	public void setAddSelectedUserButton(JButton addSelectedUserButton) {
		this.addSelectedUserButton = addSelectedUserButton;
	}

	/**
	 * @return removeSelectedUserButton The "removeSelectedUserButton" JButton
	 */
	public JButton getRemoveSelectedUserButton() {
		return removeSelectedUserButton;
	}

	/**
	 * @param removeSelectedUserButton The "removeSelectedUserButton" (an instance of JButton) to set
	 */
	public void setRemoveSelectedUserButton(JButton removeSelectedUserButton) {
		this.removeSelectedUserButton = removeSelectedUserButton;
	}

	/**
	 * @return unassignedUserListModel The "unassignedUserListModel" UserListModel
	 */
	public UserListModel getUnassignedUserListModel() {
		return unassignedUserListModel;
	}

	/**
	 * @param unassignedUserListModel The "unassignedUserListModel" (an instance of UserListModel) to set
	 */
	public void setUnassignedUserListModel(UserListModel unassignedUserListModel) {
		this.unassignedUserListModel = unassignedUserListModel;
	}

	/**
	 * @return assignedUserListModel The "assignedUserListModel" UserListModel
	 */
	public UserListModel getAssignedUserListModel() {
		return assignedUserListModel;
	}

	/**
	 * @param assignedUserListModel The "assignedUserListModel" (an instance of UserListModel) to set
	 */
	public void setAssignedUserListModel(UserListModel assignedUserListModel) {
		this.assignedUserListModel = assignedUserListModel;
	}

	/**
	 * @return parent The "parent" RequirementTab
	 */
	public RequirementTab getReqTabParent() {
		return parent;
	}

	/**
	 * @return users The "users" ArrayList
	 */
	public ArrayList<User> getUsers() {
		return users;
	}

	/**
	 * @param users The "users" (an ArrayList of Users) to set
	 */
	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	/**
	 * @return inputEnabled The "inputEnabled" boolean that represents whether or not input is enabled in this tab
	 */
	public boolean isInputEnabled() {
		return inputEnabled;
	}

	/** Sets whether input is enabled for this panel.
	 * @param inputEnabled A boolean representing whether or not input is to be enabled for this panel.
	 */
	public void setInputEnabled(boolean inputEnabled) {
		this.inputEnabled = inputEnabled;

		//If the unassignedList has nothing selected in it, set the addSelectedUserButton to disabled.
		if (unassignedList.isSelectionEmpty())
			addSelectedUserButton.setEnabled(false);
		else //Set the addSelectedUserButton to whatever was passed in as inputEnabled
			addSelectedUserButton.setEnabled(inputEnabled);

		//If the assignedList has nothing selected in it, set the removeSelectedUserButton to disabled.
		if (assignedList.isSelectionEmpty())
			removeSelectedUserButton.setEnabled(false);
		else //Set the removeSelectedUserButton to whatever was passed in as inputEnabled
			removeSelectedUserButton.setEnabled(inputEnabled);

		assignedList.setEnabled(inputEnabled);
		unassignedList.setEnabled(inputEnabled);
	}

}