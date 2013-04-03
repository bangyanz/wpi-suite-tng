/*******************************************************************************
 * Copyright (c) 2013 -- WPI Suite
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Robert Dabrowski
 * Danielle LaRose
 * Edison Jimenez
 * Christian Gonzalez
 * Mike Calder
 * John Bosworth
 * Paula Rudy
 * Gabe Isko
 * Bangyan Zhang
 * Cassie Hudson
 * Robert Smieja
 * Alex Solomon
 * Brian Hetherman
 ******************************************************************************/

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.iteration;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.toedter.calendar.JDateChooser;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.controllers.SaveModelController;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.IBuilderPanel;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views.ListPanel;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Iteration;


@SuppressWarnings({"serial","unused"})
public class IterationBuilderPanel extends JPanel implements ActionListener, IBuilderPanel {

	//the labels
	private final JLabel startDateLabel;
	private final JLabel endDateLabel;
	private final JLabel nameLabel;

	//the fillable components
	private JTextField nameValue;
	private JDateChooser startDateChooser;
	private JDateChooser endDateChooser;

	//button
	private final JButton btnSave;

	private final ListPanel parent;

	private Mode currentMode;

	private String curType = "Id";

	private Iteration currentIteration;
	private boolean isBuilderActive;

	private SaveModelController saveController;

	/**
	 * Construct the panel
	 */
	public IterationBuilderPanel(ListPanel view) {
		parent = view;
		currentMode = Mode.CREATE;
		currentIteration = null;
		isBuilderActive = false;

		//construct the panels
		nameLabel = new JLabel("Name:");
		startDateLabel = new JLabel("Start Date:");
		endDateLabel = new JLabel("End Date:");
		btnSave = new JButton("Save");

		//construct the components
		nameValue = new JTextField();
		enable(nameValue, false);
		startDateChooser = new JDateChooser(new Date());
		endDateChooser = new JDateChooser(new Date());

		// The action listener for these are below
		btnSave.setEnabled(false);
		startDateChooser.setEnabled(false);
		endDateChooser.setEnabled(false);

		//Add a titled boarder to this panel
		setBorder(BorderFactory.createTitledBorder("Iteration Builder"));

		//set the layout for this panel
		setLayout(new GridBagLayout());
		GridBagConstraints IterationBuilderConstraints = new GridBagConstraints();

		//Set the preferred sizes of the components
		startDateChooser.setPreferredSize(new Dimension (125,20));
		endDateChooser.setPreferredSize(new Dimension (125,20));
		btnSave.setPreferredSize(new Dimension (75,30));


		//Iteration Name
		//Set the constraints for the "nameLabel" and add it to the view
		IterationBuilderConstraints.anchor = GridBagConstraints.LINE_END; //This sets the anchor of the field, here we have told it to anchor the component to the center right of it's field
		IterationBuilderConstraints.insets = new Insets(10,10,10,0); //Set the top padding to 10 units of blank space, set left padding to 10 units, bottom padding to 10 units
		IterationBuilderConstraints.gridx = 0;//Set the x coord of the cell of the layout we are describing
		IterationBuilderConstraints.gridy = 0;//Set the y coord of the cell of the layout we are describing
		add(nameLabel, IterationBuilderConstraints);//Actually add the "nameLabel" to the layout given the previous constraints
		//Set the constraints for the "nameValue" and add it to the view
		IterationBuilderConstraints.anchor = GridBagConstraints.LINE_START;//This sets the anchor of the field, here we have told it to anchor the component to the center left of it's field
		IterationBuilderConstraints.insets = new Insets(10,10,10,25); //Set the top padding to 10 units of blank space, set left padding to 10 units, bottom padding to 10 units, right padding to 25 units
		IterationBuilderConstraints.ipadx=80;//stretch this field horizontally by 80 units
		IterationBuilderConstraints.gridx = 1;
		IterationBuilderConstraints.gridy = 0;
		add(nameValue, IterationBuilderConstraints);//Actually add the "nameValue" to the layout given the previous constraints

		//Start Date
		//Set the constraints for the "startDateLabel" and add it to the view
		IterationBuilderConstraints.anchor = GridBagConstraints.LINE_END;
		IterationBuilderConstraints.insets = new Insets(10,10,10,0);
		IterationBuilderConstraints.ipadx=0;//This resets the horizontal padding from the previously defined 80 units back to 0 units
		IterationBuilderConstraints.gridx = 2;
		IterationBuilderConstraints.gridy = 0;
		add(startDateLabel, IterationBuilderConstraints);
		//Set the constraints for the "startDateChooser" and add it to the view
		IterationBuilderConstraints.anchor = GridBagConstraints.LINE_START;
		IterationBuilderConstraints.insets = new Insets(10,10,10,25);
		IterationBuilderConstraints.gridx = 3;
		IterationBuilderConstraints.gridy = 0;
		add(startDateChooser, IterationBuilderConstraints);

		//End Date
		//Set the constraints for the "endDateLabel" and add it to the view
		IterationBuilderConstraints.anchor = GridBagConstraints.LINE_END;
		IterationBuilderConstraints.insets = new Insets(10,10,10,0);
		IterationBuilderConstraints.gridx = 4;
		IterationBuilderConstraints.gridy = 0;
		add(endDateLabel, IterationBuilderConstraints);
		//Set the constraints for the "endDateChooser" and add it to the view
		IterationBuilderConstraints.anchor = GridBagConstraints.LINE_START;
		IterationBuilderConstraints.insets = new Insets(10,10,10,40);
		IterationBuilderConstraints.gridx = 5;
		IterationBuilderConstraints.gridy = 0;
		add(endDateChooser, IterationBuilderConstraints);


		//Save button:
		//Set the constraints for the "Save" button and add it to the view
		IterationBuilderConstraints.anchor = GridBagConstraints.LINE_END;
		IterationBuilderConstraints.gridx = 6;
		IterationBuilderConstraints.gridy = 0;
		add(btnSave, IterationBuilderConstraints);//Actually add the "Save" button to the layout given the previous constraints


	}

	public void setUp() {
		setupControllersAndListeners();
	}

	public JButton getButton()
	{
		return btnSave;
	}

	public JTextField getIterationValue()
	{
		return nameValue;
	}

	/**
	 * @return the currentMode
	 */
	public Mode getCurrentMode() {
		return currentMode;
	}

	/**
	 * @param currentMode the currentMode to set
	 */
	public void setCurrentMode(Mode currentMode) {
		this.currentMode = currentMode;
	}

	public Iteration getCurrentIteration()
	{
		return currentIteration;
	}

	public void setCurrentIteration(Iteration newIteration)
	{
		currentIteration = newIteration;
	}

	/**
	 * @return the grandpa
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void setInputEnabled(boolean setTo) {
		isBuilderActive = setTo;
		enable(this.nameValue, setTo);
		this.startDateChooser.setEnabled(setTo);
		this.endDateChooser.setEnabled(setTo);
		this.btnSave.setEnabled(setTo);
		if (!setTo){
			this.nameValue.setText("");
		}
	}


	@Override
	public void toggleNewCancelMode() {
		currentMode = Mode.CREATE; // default for this function
		isBuilderActive = !isBuilderActive;
		setInputEnabled(isBuilderActive);
	}



	// New methods for the refactor.
	public void resetFields() {
		this.nameValue.setText("");
		startDateChooser.setDate(new Date());	// Set the two date-choosers to today
		endDateChooser.setDate(new Date());
	}


	public void setModeAndBtn(Mode mode) {
		this.currentMode = mode;
		if (mode == Mode.CREATE) {
			this.btnSave.setText("Create");
		}
		else if (mode == Mode.EDIT) {
			this.btnSave.setText("Save");
		}
	}

	public String convertCurrentModelToJSON(){
		Iteration toSend = new Iteration();
	
		if(!isValid())
		   //return null;
		
		toSend.setName(this.nameValue.getText());
		toSend.setStartDate(this.startDateChooser.getDate());
		toSend.setEndDate(this.endDateChooser.getDate());

		System.out.println(toSend.toJSON());

		return toSend.toJSON();
	}
	
	public boolean isValid(){
	    ArrayList<Iteration> iters = parent.getTabPanel().getIterationList().getIterations();
	    
	    String error = "";
	    
	    if(this.nameValue.getText().length() <=0){
		return false;
		//error += "The name field of the iteration must be non-blank.\n";
	    }
	    
	    for(int i = 0; i < iters.size(); i++){
		if(this.nameValue.getText().equals(iters.get(i).getName()))
		{
		    return false;
		    //error += "The name field of the iteration cannot be the same as other iterations\n";
		}
		
		if(this.startDateChooser.getDate().before(iters.get(i).getEndDate()) ||
			this.endDateChooser.getDate().after(iters.get(i).getEndDate()))
		{
		    return false;
		    //error += "The Start date and end date of the iteration cannot fall within another iterations dates.\n";
		}
	    }
	    
	    /*
	    if(error.length() > 0)
		throw new InvalidModelException(error);
	    */
	    return true;
	}


	public void displayModelFromJSONArray(String jsonArray) {
		Iteration toDisplay = Iteration.fromJSONArray(jsonArray)[0];

		this.nameValue.setText(toDisplay.getName());
		this.startDateChooser.setDate(toDisplay.getStartDate());
		this.endDateChooser.setDate(toDisplay.getEndDate());

		setInputEnabled(true);
	}


	public void setupControllersAndListeners() {
		saveController = new SaveModelController(parent.getTabPanel().getIterationList(),this,"iteration");
		btnSave.addActionListener(saveController);

		startDateChooser.getDateEditor().addPropertyChangeListener("date",
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if (startDateChooser.getDate().compareTo(new Date()) > 0) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							endDateChooser.setMinSelectableDate(startDateChooser.getDate());
						}
					});
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							endDateChooser.setMinSelectableDate(new Date());
						}
					});
				}
			}
		}
				);

		endDateChooser.getDateEditor().addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								startDateChooser.setMaxSelectableDate(endDateChooser.getDate());
							}
						});
					}
				}
				);
	}

	public void enable(JTextField box, boolean enabled) {
		if (enabled) {
			box.setEnabled(true);
			box.setBackground(Color.WHITE);
		}
		else {
			box.setEnabled(false);
			box.setBackground(new Color(238,238,238));
		}
	}
}