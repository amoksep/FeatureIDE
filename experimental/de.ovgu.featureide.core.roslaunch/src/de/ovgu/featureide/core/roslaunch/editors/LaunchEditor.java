package de.ovgu.featureide.core.roslaunch.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class LaunchEditor extends TextEditor {

	private ColorManager colorManager;

	public LaunchEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
