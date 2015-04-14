/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ovgu.featureide.core.roslaunch.graphconf.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.ovgu.featureide.core.roslaunch.graphconf.model.Activity;
import de.ovgu.featureide.core.roslaunch.graphconf.model.StructuredActivity;
import de.ovgu.featureide.core.roslaunch.graphconf.model.Transition;

/**
 * @author hudsonr Created on Jul 16, 2003
 */
public class ActivityPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if (model instanceof StructuredActivity)
			part = new ActivityDiagramPart();
		else if (model instanceof Activity)
			part = new SimpleActivityPart();
		else if (model instanceof Transition)
			part = new TransitionPart();
		part.setModel(model);
		return part;
	}

}
