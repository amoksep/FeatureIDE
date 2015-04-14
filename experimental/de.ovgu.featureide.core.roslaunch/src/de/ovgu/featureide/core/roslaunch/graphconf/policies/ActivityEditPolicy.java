/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ovgu.featureide.core.roslaunch.graphconf.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import de.ovgu.featureide.core.roslaunch.graphconf.model.Activity;
import de.ovgu.featureide.core.roslaunch.graphconf.model.StructuredActivity;
import de.ovgu.featureide.core.roslaunch.graphconf.model.commands.DeleteCommand;
import org.eclipse.gef.requests.GroupRequest;

/**
 * @author Daniel Lee
 */
public class ActivityEditPolicy extends ComponentEditPolicy {

	/**
	 * @see ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		StructuredActivity parent = (StructuredActivity) (getHost().getParent()
				.getModel());
		DeleteCommand deleteCmd = new DeleteCommand();
		deleteCmd.setParent(parent);
		deleteCmd.setChild((Activity) (getHost().getModel()));
		return deleteCmd;
	}

}
