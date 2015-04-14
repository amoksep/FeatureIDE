package de.ovgu.featureide.core.roslaunch.graphconf;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.jface.resource.ImageDescriptor;

import de.ovgu.featureide.core.roslaunch.RosComposer;
import de.ovgu.featureide.core.roslaunch.graphconf.model.Activity;
import de.ovgu.featureide.fm.core.Feature;

public class GraphEditPallette extends PaletteRoot {
	private PaletteDrawer drawer;
	private List<CombinedTemplateCreationEntry> features;

	public GraphEditPallette() {
		super();
		this.addAll(createCategories());
	}

	private List createCategories() {
		List categories = new ArrayList();
		categories.add(createControlGroup());
		categories.add(createComponentsDrawer());
		return categories;
	}

	private PaletteContainer createComponentsDrawer() {
		CombinedTemplateCreationEntry feature;

		drawer = new PaletteDrawer("Features", null);

		features = new ArrayList<CombinedTemplateCreationEntry>();

		if (RosComposer.IFeatureProject != null
				&& RosComposer.IFeatureProject.getFeatureModel() != null) {
			for (Feature f : RosComposer.IFeatureProject.getFeatureModel()
					.getFeatures()) {
				feature = new CombinedTemplateCreationEntry(f.getDisplayName(),
						f.getDescription(), Activity.class, new SimpleFactory(
								Activity.class),
						ImageDescriptor.createFromFile(GraphEditPallette.class,
								"images/gear16.gif"),
						ImageDescriptor.createFromFile(Activity.class,
								"images/gear16.gif"));
				features.add(feature);
			}
			drawer.addAll(features);
		} else {
			// feature = new CombinedTemplateCreationEntry(
			// "Activity", "Create a new Activity Node", Activity.class,
			// new SimpleFactory(Activity.class),
			// ImageDescriptor.createFromFile(GraphEditPallette.class,
			// "images/gear16.gif"), ImageDescriptor.createFromFile(
			// Activity.class, "images/gear16.gif"));
			drawer.add(new PaletteEntry("Features", "kein Feature Model gefunden",
					PaletteEntry.PALETTE_TYPE_UNKNOWN));
		}

		return drawer;
	}

	public PaletteContainer createControlGroup() {
		PaletteGroup controlGroup = new PaletteGroup("Control Group");

		List entries = new ArrayList();

		ToolEntry tool = new SelectionToolEntry();
		entries.add(tool);
		this.setDefaultEntry(tool);

		tool = new MarqueeToolEntry();
		entries.add(tool);

		PaletteSeparator sep = new PaletteSeparator(
				"org.eclipse.gef.examples.flow.flowplugin.sep2");
		sep.setUserModificationPermission(PaletteEntry.PERMISSION_HIDE_ONLY);
		entries.add(sep);

		tool = new ConnectionCreationToolEntry("Connection Creation",
				"Creating connections", null, ImageDescriptor.createFromFile(
						GraphEditPallette.class, "images/connection16.gif"),
				ImageDescriptor.createFromFile(Activity.class,
						"images/connection16.gif"));
		entries.add(tool);
		controlGroup.addAll(entries);
		return controlGroup;
	}

	public void deaktivateFeature() {
		// feature.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		features.get(0).setVisible(false);
		PaletteEntry entry = new PaletteEntry("Feature2",
				"nur ein neues Featuire", PaletteEntry.PALETTE_TYPE_UNKNOWN);
		entry.setUserModificationPermission(PERMISSION_HIDE_ONLY);
		this.drawer.add(entry);
	}
}
