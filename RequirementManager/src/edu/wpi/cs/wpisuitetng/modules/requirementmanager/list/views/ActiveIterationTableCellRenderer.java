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

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.list.views;

import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/** Custom cell renderer for the Iteation List View Jtable
 * This makes the selected iteration blue, past iterations grey, and all others white
 */
@SuppressWarnings("serial")
public class ActiveIterationTableCellRenderer extends DefaultTableCellRenderer {
	
	/** instantiate a new date as a current date to compare the iteration dates to */
	Date currentDate = new Date();

	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus, int row, int column) {  
		//Instantiate the cell and model
		Component cell= super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);  

		TableModel model = table.getModel();
		int modelRow = table.getRowSorter().convertRowIndexToModel(row);

		//Grab that start and end dates
		String startDate = (String)model.getValueAt(modelRow, 2);
		String endDate = (String)model.getValueAt(modelRow, 3);

		//Format the start and end dates
		SimpleDateFormat f = new SimpleDateFormat("MM/dd/yy");
		if((column == 2)) {
			setValue(f.format(new Date(startDate)));
		}
		if((column == 3)) {
			setValue(f.format(new Date(endDate)));
		}

		/*
		 * If the row is selected, change the color to the default blue
		 * If the end date of the iteration is before the current date, make the row grey
		 * else make the row white
		 */
		if (isSelected) {
			Color defaultBlue = new Color(184,207,229);
			setBackground(defaultBlue);                
		}        
		else if((new Date(endDate)).before(currentDate)) {
			Color defaultGrey = new Color(238,238,238);
			setBackground(defaultGrey);
		}
		else {        
			setBackground(Color.white);
		}     

		return cell;  
	}
}


