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

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.charts;

//import java.awt.Graphics;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PieChartPanel extends JPanel{
	
	private PieChart chart;
	
	public PieChartPanel() {
//		super();
		this.chart = new PieChart();
	}
	
	/** Function to refresh and redraw pie chart */
	public void refreshChart() {
		chart.refresh();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		chart.paint(g);
	}
}
