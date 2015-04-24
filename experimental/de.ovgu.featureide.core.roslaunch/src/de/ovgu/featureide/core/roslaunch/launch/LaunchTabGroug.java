package de.ovgu.featureide.core.roslaunch.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;

public class LaunchTabGroug 
extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		AbstractLaunchConfigurationTab tabs[];

		tabs = new AbstractLaunchConfigurationTab[1];
		tabs[0] = new RosLaunchTab();
		setTabs(tabs);
	}

}
