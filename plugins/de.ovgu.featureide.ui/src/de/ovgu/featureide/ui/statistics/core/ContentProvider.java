/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2015  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.ui.statistics.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.UIJob;

import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.core.builder.IComposerExtensionClass;
import de.ovgu.featureide.core.fstmodel.FSTModel;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.ui.statistics.core.composite.LazyParent;
import de.ovgu.featureide.ui.statistics.core.composite.Parent;
import de.ovgu.featureide.ui.statistics.core.composite.lazyimplementations.ConfigParentNode;
import de.ovgu.featureide.ui.statistics.core.composite.lazyimplementations.DirectivesNode;
import de.ovgu.featureide.ui.statistics.core.composite.lazyimplementations.StatisticsContractComplexityNew;
import de.ovgu.featureide.ui.statistics.core.composite.lazyimplementations.StatisticsFeatureComplexity;
import de.ovgu.featureide.ui.statistics.core.composite.lazyimplementations.StatisticsProgramSizeNew;
import de.ovgu.featureide.ui.statistics.ui.helper.JobDoneListener;

/**
 * Content provider for the {@link TreeViewer}.
 * 
 * @see ContentProvider#calculateContent(IResource)
 * @see ITreeContentProvider
 * 
 * @author Dominik Hamann
 * @author Patrick Haese
 */
public class ContentProvider implements ITreeContentProvider, StatisticsIds {

	private static final Parent DEFAULT_TEXT = new Parent(OPEN_FILE, null);
	private TreeViewer viewer;
	public Parent godfather = new Parent("godfather", null);
	private IFeatureProject project;
	private boolean canceled;

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	@Override
	public void dispose() {
		this.godfather = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public ContentProvider(TreeViewer viewer) {
		super();
		this.viewer = viewer;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement.equals(viewer)) {
			return getChildren(godfather);
		}
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof Parent) {
			return ((Parent) parent).getChildren();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Parent) {
			return ((Parent) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Parent) {
			return ((Parent) element).hasChildren();
		}
		return false;
	}

	/**
	 * Calculates content to be shown. If the current editor is not editing a
	 * file out of a feature project a default message is being displayed. Every
	 * node is responsible for its own content. So for further information see
	 * the classes in "lazyimplementations"-package.
	 * 
	 * @see Parent
	 * @see LazyParent
	 * @param res
	 *            Any file out of the current feature-project.
	 */
	public void calculateContent(IResource res) {
		IFeatureProject newProject = CorePlugin.getFeatureProject(res);
		boolean hasChanged = newProject != null && project != null && !newProject.equals(project);
		calculateContent(res, hasChanged);
	}

	public void calculateContent(IResource res, boolean hasChanged) {
		IFeatureProject newProject = CorePlugin.getFeatureProject(res);
		boolean isEntirelyNewProject = project == null && newProject != null;

		if (isEntirelyNewProject || hasChanged) {
			this.project = newProject;
			addNodes();
		} else if (newProject == null) {
			project = null;
			defaultContent();
		}
	}

	private synchronized void addNodes() {
		IComposerExtensionClass composer = project.getComposer();
		FSTModel fstModel = getFSTModel(composer);
		FeatureModel featModel = project.getFeatureModel();
		JobDoneListener.getInstance().init(viewer);

		godfather = new Parent("GODFATHER", null);
		String composerName = composer.getName();
		Parent composerParent = new Parent(DESC_COMPOSER_NAME, composerName);

		godfather.addChild(new Parent(PROJECT_NAME, project.getProjectName()));
		godfather.addChild(composerParent);
		Parent featureModelStatistics = new Parent(STATISTICS_OF_THE_FEATURE_MODEL);
		featureModelStatistics.addChild(new StatisticsFeatureComplexity(NUMBER_OF_FEATURE, featModel));
		featureModelStatistics.addChild(new ConfigParentNode(VALID_CONFIGURATIONS, featModel));
		godfather.addChild(featureModelStatistics);

		if (composer.getGenerationMechanism() == IComposerExtensionClass.Mechanism.FEATURE_ORIENTED_PROGRAMMING) {
			godfather.addChild(new StatisticsProgramSizeNew(PRODUCT_LINE_IMPLEMENTATION, fstModel));
			godfather.addChild(new StatisticsContractComplexityNew(CONTRACT_COMPLEXITY, fstModel, featModel, project.getContractComposition()));
		}
		if (composer.getGenerationMechanism() == IComposerExtensionClass.Mechanism.PREPROCESSOR) {
			godfather.addChild(new DirectivesNode(PRODUCT_LINE_IMPLEMENTATION, fstModel));
		}
		refresh();
	}

	private FSTModel getFSTModel(IComposerExtensionClass composer) {
		FSTModel fstModel = project.getFSTModel();
		if (fstModel == null || fstModel.getClasses().isEmpty() || fstModel.getFeatures().isEmpty()) {
			composer.buildFSTModel();
			fstModel = project.getFSTModel();
		}
		return fstModel;
	}

	/**
	 * Prints a default message when the plug-in can't find necessary
	 * information.
	 */
	public void defaultContent() {
		godfather = new Parent("GODFATHER");
		godfather.addChild(DEFAULT_TEXT);
		refresh();
	}

	/**
	 * Refreshes the {@link ContentProvider#view} using a UI-Job with highest
	 * priority.
	 */
	protected void refresh() {
		UIJob job_setColor = new UIJob("Refresh statistics view") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (!viewer.getControl().isDisposed()) {
					viewer.refresh();
				}
				return Status.OK_STATUS;
			}
		};
		job_setColor.setPriority(Job.INTERACTIVE);
		job_setColor.schedule();
	}

}
