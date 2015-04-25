package de.ovgu.featureide.core.roslaunch;

import java.util.LinkedHashSet;

import org.eclipse.core.resources.IFile;

import de.ovgu.featureide.core.builder.ComposerExtensionClass;

public class FeatureRosComposer extends ComposerExtensionClass {

	private RosComposer composer;

	public FeatureRosComposer() {
		this.composer = new RosComposer();
	}

	@Override
	public void performFullBuild(IFile configFile) {
		this.composer.compose(configFile, this.featureProject);
	}

	@Override
	public Mechanism getGenerationMechanism() {
		return null;
	}

	@Override
	public LinkedHashSet<String> extensions() {
		return this.composer.extensions();
	}
}
