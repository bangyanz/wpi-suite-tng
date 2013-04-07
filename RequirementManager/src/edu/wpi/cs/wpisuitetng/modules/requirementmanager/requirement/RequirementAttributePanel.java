package edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Requirement;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.RequirementPriority;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.RequirementType;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement.RequirementTab.Mode;
import edu.wpi.cs.wpisuitetng.modules.requirementmanager.views.JNumberTextField;

@SuppressWarnings({"serial","rawtypes","unchecked"})
public class RequirementAttributePanel extends JPanel {

	//The labels
	private  JLabel nameLabel; //The label for the name text field ("txtName")
	private  JLabel descriptionLabel;//The label for the description text area ("txtDescription")
	private  JLabel typeLabel; //The label for the type combo box ("typeBox")
	private  JLabel statusLabel; //The label for the status combo box ("statusBox")
	private  JLabel priorityLabel; //The label for the priority combo box ("priorityBox")
	private  JLabel iterationLabel; //The label for the iteration combo box ("iterationBox")
	private  JLabel releaseNumLabel; //The label for the release number text field ("txtReleaseNum")
	private  JLabel estimateLabel; //The label for the estimate text field ("txtEstimate")
	private  JLabel actualEffortLabel; //The label for the actual effort text field ("txtActualEffort")

	//The fillable components
	private  JTextField txtName;//The name text field 
	private  JTextArea txtDescription;//The description text area
	private  JComboBox typeBox;//The type combo box
	private  JComboBox statusBox;//The status combo box
	private  JComboBox priorityBox;//The priority combo box
	private  JComboBox iterationBox;//The iteration combo box
	private  JNumberTextField txtReleaseNumber;//The release number text field
	private  JNumberTextField txtEstimate;//The estimate text field
	private  JNumberTextField txtActualEffort;//The actual effort text field

	private Requirement currentRequirement;//Stores the requirement currently open for editing or creation
	private RequirementTab parent; //Stores the RequirementTab that contains the panel
	protected boolean inputEnabled;//A boolean indicating if input is enabled on the form 
	private Mode mode;// The variable to store the enum indicating whether or not you are creating at the time


	//The layout manager
	protected GridBagLayout layout; //The layout for the inner panel ("innerPanel")

	//The constraints
	private GridBagConstraints attributePanelConstraints;//The constraints variable for the layout of the innerPanel

	public RequirementAttributePanel(RequirementTab parentTab, Requirement requirement, Mode editMode){

		parent = parentTab;//Set the RequirementView that contains this instance of this panel
		currentRequirement = requirement; //Set the requirement that is currently open for editing or creation
		mode = editMode;//Set the indicated mode

		// Indicate that input is enabled
		inputEnabled = true;

		//Create and set the layout manager that controls the positions of the components
		layout = new GridBagLayout();//Create the layout
		setLayout(layout); //Set the layout

		//add a border of blank space
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30)); 

		addComponents();//Add the components to the panel
		updateFields();//Update the fields to those given in the currentRequirement, if necessary.

	}


	/**
	 * Adds the components to both the innerPanel and 
	 * this instance of a RequirementPanel and places
	 * constraints on the components within the innerPanel
	 * for the GridBagLayout.
	 */
	protected void addComponents(){
		//Construct the labels
		nameLabel = new JLabel("Name:");
		descriptionLabel = new JLabel("Description:");
		typeLabel = new JLabel("Type:");
		statusLabel = new JLabel("Status:");
		priorityLabel = new JLabel("Priority:");
		iterationLabel = new JLabel("Iteration:");
		releaseNumLabel = new JLabel("ReleaseNumber:");
		estimateLabel = new JLabel("Estimate:");
		actualEffortLabel = new JLabel("ActualEffort:");

		//Construct the misc components
		txtName = new JTextField("");
		txtDescription = new JTextArea("", 2, 2);
		txtReleaseNumber = new JNumberTextField();
		txtEstimate = new JNumberTextField();
		txtActualEffort = new JNumberTextField();
		txtReleaseNumber.setAllowNegative(false);
		txtEstimate.setAllowNegative(false);
		txtActualEffort.setAllowNegative(false);

		//Set the txtDescription component to wrap
		txtDescription.setLineWrap(true);
		txtDescription.setWrapStyleWord(true);

		//Create the strings for the boxes
		String[] typeStrings = { "", "Epic", "Theme", "UserStory", "NonFunctional", "Scenario" };
		String[] statusStrings = { "New", "InProgress", "Open", "Complete", "Deleted" };
		String[] priorityStrings = { "", "High", "Medium", "Low"};
		String[] iterationStrings = { "Backlog"};

		//Construct the boxes 
		typeBox = new JComboBox(typeStrings);
		statusBox = new JComboBox(statusStrings);
		priorityBox = new JComboBox(priorityStrings);
		iterationBox = new JComboBox(iterationStrings);


		if (mode == Mode.EDIT)//If we are editing an existing requirement
		{
			String oldStatus = (currentRequirement.getStatus()).toString();//grab the string version of the status passed in with "requirement"

			//if the oldStatus is InProgress or Completed, disable editing of the Estimate
			if (   (oldStatus.equals("InProgress"))    ||    (oldStatus.equals("Complete"))   )
				toggleEnabled(txtEstimate, false);
		}
		else//We are creating a new requirement
		{
			//Set the estimate and actual effort to 0 since this is a new requirement
			txtEstimate.setText("0");
			txtActualEffort.setText("0");

			//Set the initial selections for the boxes
			typeBox.setSelectedIndex(0);
			statusBox.setSelectedIndex(0);
			priorityBox.setSelectedIndex(0);
			iterationBox.setSelectedIndex(0);

			//Enables the fields upon creation
			setInputEnabled(inputEnabled);


			//Set the following fields to be initially grayed out
			toggleCreationDisable();

		}

		//Set up the description scroll pane
		JScrollPane scrollPane = new JScrollPane(txtDescription);// Put the txtDescription in a scroll pane
		scrollPane.setPreferredSize(new Dimension(450,250)); //Set the initial size of the txtDescription scroll panel

		//In this last section we adjust the size and alignments of all the components and add them to the innerPanel.
		//Please read all the comments in this section if you are having trouble understanding what is going on.

		//create the constraints variable for the layout of the innerPanel
		attributePanelConstraints = new GridBagConstraints(); 

		//A quick note about constraints:
		//A constraint variable is a single instance of an object that stores constraints,
		//  so if you set variables within it, they persist to the next time it is used.
		//This means you *must* reset variables within the constraint if you do not want
		//  the previously stored value to effect the next component that uses it.

		//Name:
		//Set the constraints for "nameLabel" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.07;//This sets the horizontal (x axis) "weight" of the component, which tells the layout how big to make this component in respect to the other components on it's line
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;//This sets the anchor of the field, here we have told it to anchor the component to the top right of it's field
		attributePanelConstraints.insets = new Insets(15,5,0,0);  //Set the top padding to 15 units of blank space, left padding to 5 units of space
		attributePanelConstraints.gridx = 0; //set the x coord of the cell of the layout we are describing
		attributePanelConstraints.gridy = 0;//set the y coord of the cell of the layout we are describing
		add(nameLabel, attributePanelConstraints);//Actually add the "nameLabel" to the layout given the previous constraints
		//Set the constraints for "txtName" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.77;
		attributePanelConstraints.fill = GridBagConstraints.HORIZONTAL;//This tells the layout to stretch this field horizontally to fit it's cell
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;//Anchor the component to the top left center of it's field
		attributePanelConstraints.gridx = 1;
		attributePanelConstraints.gridwidth = 3; //This tells the layout that this component will be 3 cells wide
		attributePanelConstraints.gridy = 0;
		add(txtName, attributePanelConstraints);
		//end Name

		//Description:		
		//Set the constraints for the "descriptionLabel" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.07;
		attributePanelConstraints.fill = GridBagConstraints.NONE;//This tells the layout to *not* stretch this field horizontally (or vertically, for that matter) to fit it's cell
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END; 
		attributePanelConstraints.insets = new Insets(15,15,0,0);  //Set the top padding to 15 units of blank space, left padding to 15 units of space
		attributePanelConstraints.gridx = 0;
		attributePanelConstraints.gridwidth = 1;//This tells the layout to reset the amount of cells the component will be wide, from the previous 3 cells wide down to the default of 1
		attributePanelConstraints.gridy = 1;
		add(descriptionLabel, attributePanelConstraints);
		//Set the constraints for the "scrollPane" containing the "txtDescription" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.77;
		attributePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		attributePanelConstraints.insets = new Insets(15,5,10,0);  //Set the top padding to 15 units of blank space, left padding to 5 units of space, right padding to 10 units
		attributePanelConstraints.gridx = 1;
		attributePanelConstraints.gridwidth = 3;
		attributePanelConstraints.gridy = 1;
		add(scrollPane, attributePanelConstraints);
		//end Description

		//Type:
		//Set the constraints for the "typeLabel" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.07;
		attributePanelConstraints.fill = GridBagConstraints.NONE;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		attributePanelConstraints.insets = new Insets(15,15,0,0); 
		attributePanelConstraints.gridx = 0;
		attributePanelConstraints.gridwidth = 1;
		attributePanelConstraints.gridy = 2;
		add(typeLabel, attributePanelConstraints);
		//Set the constraints for the "typeBox"  and add it to the innerPanel
		attributePanelConstraints.weightx = 0.43;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		attributePanelConstraints.insets = new Insets(15,5,10,0);
		attributePanelConstraints.gridx = 1;
		attributePanelConstraints.gridy = 2;
		add(typeBox, attributePanelConstraints);
		//end Type

		//Priority:
		//Set the constraints for the "priorityLabel" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.07;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		attributePanelConstraints.insets = new Insets(15,15,0,0);
		attributePanelConstraints.gridx = 0;
		attributePanelConstraints.gridy = 3;
		add(priorityLabel, attributePanelConstraints);
		//Set the constraints for the "priorityBox" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.43;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		attributePanelConstraints.insets = new Insets(15,5,10,0);
		attributePanelConstraints.gridx = 1;
		attributePanelConstraints.gridy = 3;
		add(priorityBox, attributePanelConstraints);
		//end Priority

		//Release number:
		//Set the constraints for the "releaseNumLabel" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.07;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		attributePanelConstraints.insets = new Insets(15,15,0,0);
		attributePanelConstraints.gridx = 0;
		attributePanelConstraints.gridy = 4;
		add(releaseNumLabel, attributePanelConstraints);
		//Set the constraints for the "txtReleaseNum" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.43;
		attributePanelConstraints.ipadx = 80;//This tells the layout to pad the interior of this field horizontally with 80 units of space
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		attributePanelConstraints.insets = new Insets(15,5,10,0);
		attributePanelConstraints.gridx = 1;
		attributePanelConstraints.gridy = 4;
		add(txtReleaseNumber, attributePanelConstraints);
		//end Release number


		//Status:		
		//Set the constraints for the "statusLabel" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.07;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END; 
		attributePanelConstraints.insets = new Insets(15,5,0,0);
		attributePanelConstraints.ipadx = 0;//This tells the layout to reset the horizontal ipad from the previously defined 80 units to now 0 units
		attributePanelConstraints.gridx = 2;
		attributePanelConstraints.gridy = 2;
		add(statusLabel, attributePanelConstraints);
		//Set the constraints for the "statusBox" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.43;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		attributePanelConstraints.gridx = 3;
		attributePanelConstraints.gridy = 2;
		add(statusBox, attributePanelConstraints);
		//end Status

		//Estimate:		
		//Set the constraints for the "estimateLabel" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.07;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		attributePanelConstraints.gridx = 2;
		attributePanelConstraints.gridy = 3;
		add(estimateLabel, attributePanelConstraints);
		//Set the constraints for the "txtEstimate" and add it to the innerPanel
		attributePanelConstraints.ipadx = 80;
		attributePanelConstraints.weightx = 0.43;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		attributePanelConstraints.gridx = 3;
		attributePanelConstraints.gridy = 3;
		add(txtEstimate, attributePanelConstraints);
		//end Estimate

		//Actual effort:
		//Set the constraints for the "actualEffortLabel" and add it to the innerPanel
		attributePanelConstraints.ipadx = 0;//This tells the layout to reset the horizontal ipad from the previously defined 80 units to now 0 units
		attributePanelConstraints.weightx = 0.07;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		attributePanelConstraints.gridx = 2;
		attributePanelConstraints.gridy = 4;
		add(actualEffortLabel, attributePanelConstraints);
		//Set the constraints for the "txtActualEffort" and add it to the innerPanel
		attributePanelConstraints.ipadx = 80;
		attributePanelConstraints.weightx = 0.43;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		attributePanelConstraints.gridx = 3;
		attributePanelConstraints.gridy = 4;
		add(txtActualEffort, attributePanelConstraints);
		//end Actual effort

		//Iteration
		//Set the constraints for the "iterationLabel" and add it to the innerPanel
		attributePanelConstraints.ipadx = 0;//This tells the layout to reset the horizontal ipad from the previously defined 80 units to now 0 units
		attributePanelConstraints.weightx = 0.07;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		attributePanelConstraints.gridx = 2;
		attributePanelConstraints.gridy = 5;
		add(iterationLabel, attributePanelConstraints);
		//Set the constraints for the "iterationBox" and add it to the innerPanel
		attributePanelConstraints.weightx = 0.43;
		attributePanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		attributePanelConstraints.gridx = 3;
		attributePanelConstraints.gridy = 5;
		add(iterationBox, attributePanelConstraints);
		//end Iteration

	}

	/**
	 * Sets whether input is enabled for this panel and its children. This should be used instead of 
	 * JComponent#setEnabled because setEnabled does not affect its children.
	 * 
	 * @param enabled Whether or not input is enabled.
	 */
	protected void setInputEnabled(boolean enabled){
		inputEnabled = enabled;

		toggleEnabled(txtName, enabled);
		toggleEnabled(txtDescription, enabled);
		toggleEnabled(typeBox, enabled);
		toggleEnabled(statusBox, enabled);
		toggleEnabled(priorityBox, enabled);
		toggleEnabled(txtReleaseNumber, enabled);
		toggleEnabled(txtEstimate, enabled);
		toggleEnabled(txtActualEffort, enabled);
		toggleEnabled(iterationBox, enabled);

	}
	
	/**
	 * Sets the appropriate fields disabled upon creation
	 * 
	 */
	protected void toggleCreationDisable(){
		//Set the following fields to be initially grayed out
		toggleEnabled(txtEstimate, false);
		toggleEnabled(txtActualEffort, false);
		toggleEnabled(statusBox, false);
		toggleEnabled(iterationBox, false);

	}

	/**
	 * Updates the RequirementPanel's model ("currentRequirement") to contain the values of the given Requirement and sets the 
	 * RequirementPanel's "mode" to {@link Mode#EDIT}.
	 * 
	 * @param requirement	The Requirement which contains the new values for the model ("currentRequirement").
	 */
	protected void updateModel(Requirement requirement) {
		updateModel(requirement, Mode.EDIT);
	}

	/**
	 * Updates the RequirementPanel's model ("currentRequirement") to contain the values of the given Requirement.
	 * 
	 * @param requirement	The Requirement which contains the new values for the model ("currentRequirement").
	 * @param newMode		The new "mode"
	 */
	protected void updateModel(Requirement requirement, Mode newMode){
		mode = newMode;

		currentRequirement.setId(requirement.getId());
		currentRequirement.setName(requirement.getName());
		currentRequirement.setDescription(requirement.getDescription());
		currentRequirement.setType(requirement.getType());
		currentRequirement.setStatus(requirement.getStatus());
		currentRequirement.setPriority(requirement.getPriority());
		currentRequirement.setReleaseNumber(requirement.getReleaseNumber());
		currentRequirement.setEstimate(requirement.getEstimate());
		currentRequirement.setActualEffort(requirement.getActualEffort());
		currentRequirement.setNotes(requirement.getNotes());

		updateFields();
		revalidate();

		layout.invalidateLayout(this);
		layout.layoutContainer(this);
		repaint();
	}

	/**
	 * Enables or disables a given JComponent and sets is color accordingly
	 * 
	 * @param box Box to be enabled / disabled
	 * @param enabled True for enable, false for disable
	 */
	public void toggleEnabled(JComponent box, boolean enabled) {
		if (enabled) {
			box.setEnabled(true);
			box.setBackground(Color.WHITE);
		}
		else {
			box.setEnabled(false);
			box.setBackground(new Color(238,238,238));
		}
	}


	/**
	 * Updates the RequirementPanel's fields to match those in the current model (stored in "currentRequirement").
	 */
	private void updateFields(){

		if (mode == Mode.EDIT)//If we are editing an existing requirement
		{
			//Enable all fields
			setInputEnabled(true);

			//Set the fields to the values passed in with "requirement"
			txtName.setText(currentRequirement.getName());
			txtDescription.setText(currentRequirement.getDescription());
			txtReleaseNumber.setText( String.valueOf(currentRequirement.getReleaseNumber()) );
			txtEstimate.setText( String.valueOf(currentRequirement.getEstimate()) );
			txtActualEffort.setText(String.valueOf(currentRequirement.getActualEffort()) );

			String oldType = (currentRequirement.getType()).toString();//grab the string version of the type passed in with "requirement"
			String oldStatus = (currentRequirement.getStatus()).toString();//grab the string version of the status passed in with "requirement"
			String oldPriority = (currentRequirement.getPriority()).toString();//grab the string version of the priority passed in with "requirement"

			//Set the selected index of the typeBox to the correct value, based on the oldType
			if (oldType.equals("Epic"))
				typeBox.setSelectedIndex(1);
			else if (oldType.equals("Theme"))
				typeBox.setSelectedIndex(2);
			else if (oldType.equals("UserStory"))
				typeBox.setSelectedIndex(3);
			else if (oldType.equals("NonFunctional"))
				typeBox.setSelectedIndex(4);
			else if (oldType.equals("Scenario"))
				typeBox.setSelectedIndex(5);
			else// oldType = "NoType"
				typeBox.setSelectedIndex(0);

			//Set the selected index of the statusBox to the correct value, based on the oldStatus
			if (oldStatus.equals("New"))
				statusBox.setSelectedIndex(0);
			else if (oldStatus.equals("InProgress"))
				statusBox.setSelectedIndex(1);
			else if (oldStatus.equals("Open"))
				statusBox.setSelectedIndex(2);
			else if (oldStatus.equals("Complete"))
				statusBox.setSelectedIndex(3);
			else // oldStatus = "Deleted"
				statusBox.setSelectedIndex(4);


			//Set the selected index of the priorityBox to the correct value, based on the oldPriority
			if (oldPriority.equals("High"))
				priorityBox.setSelectedIndex(1);
			else if (oldPriority.equals("Medium"))
				priorityBox.setSelectedIndex(2);
			else if (oldPriority.equals("Low"))
				priorityBox.setSelectedIndex(3);
			else // oldPriority = "NoPriority"
				priorityBox.setSelectedIndex(0);


			//if the oldStatus is InProgress or Completed, disable editing of the Estimate
			if (   (oldStatus.equals("InProgress"))    ||    (oldStatus.equals("Complete"))   )
				toggleEnabled(txtEstimate, false);
		}
	}

	/**
	 * Returns a boolean representing whether or not input is enabled for the RequirementPanel.
	 * @return the inputEnabled boolean 	A boolean representing whether or not input is enabled for the RequirementPanel.
	 */
	public boolean getInputEnabled() {
		return inputEnabled;
	}

	/**
	 * This returns the JTextField "txtName"
	 * @return the txtName JTextField
	 */
	public JTextField getRequirementName() {
		return txtName;
	}

	/**
	 * This returns the JTextArea "txtDescription"
	 * @return the txtDescription JTextArea
	 */
	public JTextArea getRequirementDescription() {
		return txtDescription;
	}


	/**
	 * This returns the JComboBox "typeBox"
	 * @return the typeBox JComboBox
	 */
	public JComboBox getRequirementType() {
		return typeBox;
	}

	/**
	 * This returns the JComboBox "statusBox"
	 * @return the statusBox JComboBox
	 */
	public JComboBox getRequirementStatus() {
		return statusBox;
	}

	/**
	 * This returns the JComboBox "priorityBox"
	 * @return the priorityBox JComboBox
	 */
	public JComboBox getRequirementPriority() {
		return priorityBox;
	}

	/**
	 * This returns the JTextField "txtReleaseNum"
	 * @return the txtReleaseNum JTextField
	 */
	public JTextField getRequirementReleaseNumber() {
		return txtReleaseNumber;
	}

	/**
	 * This returns the JTextField "txtEstimate"
	 * @return the txtEstimate JTextField
	 */
	public JTextField getRequirementEstimate() {
		return txtEstimate;
	}

	/**
	 * This returns the JTextField "txtActualEffort"
	 * @return the txtActualEffort JTextField
	 */
	public JTextField getRequirementActualEffort() {
		return txtActualEffort;
	}

	/**
	 * Gets a requirement from the current text fields
	 * TODO error check
	 * 
	 * @return Requiremnt made from the text fields
	 */
	public Requirement getRequirement()
	{
		// get the fields from the UI
		String name = this.getRequirementName().getText();
		String description = this.getRequirementDescription().getText();
		int releaseNumber = Integer.parseInt((this.getRequirementReleaseNumber().getText().equals("")) ? "-1" : (this.getRequirementReleaseNumber().getText()));
		RequirementPriority priority = RequirementPriority.toPriority(this.getRequirementPriority().getSelectedItem().toString());
		RequirementType type = RequirementType.toType(this.getRequirementType().getSelectedItem().toString());

		return new Requirement(name, description, type, priority,  releaseNumber);

	}

	/**
	 * This returns the "mode" of this panel (Mode.EDIT or Mode.CREATE)
	 * @return the mode Mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * This sets the Mode "mode" of this panel
	 * @param Mode m to set mode to (Mode.EDIT or Mode.CREATE)
	 */
	public void setMode(Mode m) {
		mode = m;
	}

	/**
	 * This returns the Requirement "currentRequirement" 
	 * @return the currentRequirement Requirement
	 */
	public Requirement getCurrentRequirement() {
		return currentRequirement;
	}

	/**
	 * This sets the Requirement "currentRequirement" 
	 * @param Requirement currentRequirement the currentRequirement to set
	 */
	public void setCurrentRequirement(Requirement currentRequirement) {
		this.currentRequirement = currentRequirement;
	}

	/**
	 * This returns the "parent" (a RequirementView) of this panel
	 * @return the parent RequirementTab
	 */
	public RequirementTab getParent() {
		return parent;
	}
	
	/**
	 * @return the iterationBox
	 */
	public JComboBox getIterationBox() {
		return iterationBox;
	}

	/**
	 * @param iterationBox the iterationBox to set
	 */
	public void setIterationBox(JComboBox iterationBox) {
		this.iterationBox = iterationBox;
	}


}