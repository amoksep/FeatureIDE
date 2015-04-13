package de.ovgu.featureide.core.roslaunch.launch;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import de.ovgu.featureide.core.CorePlugin;

public class LaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		// TODO Auto-generated method stub
		CorePlugin.getDefault().logInfo("launch");

	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		// TODO Auto-generated method stub
		CorePlugin.getDefault().logInfo("launch");

	}

}
