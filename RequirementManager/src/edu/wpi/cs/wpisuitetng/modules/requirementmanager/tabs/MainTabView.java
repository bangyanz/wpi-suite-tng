

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.tabs;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.dashboard.DashboardView;

/**
 * This tabbed pane will appear as the main content of the Requirments tab.
 * It starts out showing the single Dashboard tab.
 */
@SuppressWarnings("serial")
public class MainTabView extends JTabbedPane {
	
	public MainTabView() {
		setTabPlacement(TOP);
		setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
		setBorder(BorderFactory.createEmptyBorder(5, 3, 3, 3));
		addTab("Dashboard", new ImageIcon(), new DashboardView(),
		       "Your Dashboard - notifications, etc.");
	}
	
	@Override
	public void insertTab(String title, Icon icon, Component component, String tip, int index) {
		super.insertTab(title, icon, component, tip, index);
		// the Dashboard tab cannot be closed
		if(!(component instanceof DashboardView)) {
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
	
}
