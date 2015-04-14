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
package de.ovgu.featureide.core.roslaunch.graphconf.model.commands;

import org.eclipse.gef.commands.Command;

import de.ovgu.featureide.core.roslaunch.graphconf.model.Activity;
import de.ovgu.featureide.core.roslaunch.graphconf.model.StructuredActivity;

/**
 * @author Daniel Lee
 */
public class CreateCommand extends Command {

	private StructuredActivity parent;
	private Activity child;
	private int index = -1;

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (index > 0)
			parent.addChild(child, index);
		else
			parent.addChild(child);
	}

	/**
	 * Sets the index to the passed value
	 * 
	 * @param i
	 *            the index
	 */
	public void setIndex(int i) {
		index = i;
	}

	/**
	 * Sets the parent ActivityDiagram
	 * 
	 * @param sa
	 *            the parent
	 */
	public void setParent(StructuredActivity sa) {
		parent = sa;
	}

	/**
	 * Sets the Activity to create
	 * 
	 * @param activity
	 *            the Activity to create
	 */
	public void setChild(Activity activity) {
		child = activity;
		child.setName("a " + (parent.getChildren().size() + 1));
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		parent.removeChild(child);
	}

}
