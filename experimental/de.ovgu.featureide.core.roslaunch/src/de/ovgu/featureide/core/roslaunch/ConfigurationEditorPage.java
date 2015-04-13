package de.ovgu.featureide.core.roslaunch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.SelectableFeature;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.ovgu.featureide.fm.ui.FMUIPlugin;
import de.ovgu.featureide.fm.ui.editors.configuration.ConfigurationTreeEditorPage;

public class ConfigurationEditorPage extends ConfigurationTreeEditorPage{

	private static final String ID = FMUIPlugin.PLUGIN_ID + "ConfigurationPageRos";
	private static final String PAGE_TEXT = "ConfigurationRos";

	protected void createUITree(Composite parent) {
		tree = new Tree(parent, SWT.CHECK);
		tree.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.CHECK) {
					final TreeItem item = (TreeItem) event.item;
					final Object data = item.getData();
					if (data instanceof SelectableFeature) {
						final SelectableFeature feature = (SelectableFeature)item.getData();
						if (updateFeatures.contains(feature)) {
							item.setChecked(true);
						} else {
							switch (feature.getAutomatic()) {
								case SELECTED: item.setChecked(true); break;
								case UNSELECTED: item.setChecked(false); break;
								case UNDEFINED: changeSelection(item, true); break;
							}
						}
					}
				}
			}
		});
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getPageText() {
		return PAGE_TEXT;
	}

	@Override
	public void pageChangeTo(int index) {
		final Configuration configuration = configurationEditor.getConfiguration();
		for (SelectableFeature feature : configuration.getFeatures()) {
			if (feature.getAutomatic() == Selection.UNDEFINED && feature.getManual() == Selection.UNSELECTED) {
				configuration.setManual(feature, Selection.UNDEFINED);
			}
		}
		super.pageChangeTo(index);
	}
}