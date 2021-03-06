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
package de.ovgu.featureide.core.signature;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import de.ovgu.featureide.core.signature.ProjectSignatures.SignatureIterator;
import de.ovgu.featureide.core.signature.base.AClassCreator;
import de.ovgu.featureide.core.signature.base.AbstractClassFragment;
import de.ovgu.featureide.core.signature.base.AbstractClassSignature;
import de.ovgu.featureide.core.signature.base.AbstractSignature;
import de.ovgu.featureide.core.signature.java.JavaClassCreator;

/** 
 * Representation of {@link ProjectSignatures} as a list of classes.
 * 
 * @author Sebastian Krieter
 */
public class ProjectStructure {

	protected final HashMap<String, AbstractClassFragment> classList = new HashMap<String, AbstractClassFragment>();
	
	protected int hashCode = 0;
	protected boolean hasHashCode = false;
	
	public ProjectStructure(SignatureIterator it) {
		construct(it, new JavaClassCreator());
	}
	
	public void construct(SignatureIterator allMembers, AClassCreator aClassCreator) {
		classList.clear();
		LinkedList<AbstractClassSignature> parents = new LinkedList<AbstractClassSignature>();
		allMembers.reset();
		while (allMembers.hasNext()) {
			AbstractSignature sig = allMembers.next();
			AbstractClassSignature parent = sig.getParent();
			parents.clear();
			final boolean isMember;
			if (sig instanceof AbstractClassSignature) {
				parents.addFirst((AbstractClassSignature) sig);
				isMember = false;
			} else {
				isMember = true;
			}
			while (parent != null) {
				parents.addFirst(parent);
				parent = parent.getParent();
			}
			
			parent = parents.removeFirst();
			AbstractClassFragment parentClass = getClass(parent.getFullName());
			if (parentClass == null) {
				parentClass = aClassCreator.create(parent);
				addClass(parentClass);
			} else {
				if (sig instanceof AbstractClassSignature) {
					AbstractClassSignature parentSig = parentClass.getSignature();
					for (String newImport : ((AbstractClassSignature)sig).getImportList()) {
						parentSig.addImport(newImport);
					}
				}
			}
			
			for (AbstractClassSignature child : parents) {
				AbstractClassFragment childClass = parentClass.getInnerClass(child.getFullName());
				
				if (childClass == null) {
					childClass = aClassCreator.create(child);
					parentClass.addInnerClass(childClass);
				}
				parentClass = childClass;
			}
			if (isMember) {
				parentClass.addMember(sig);
			}
		}
	}

	public AbstractClassFragment getClass(String id) {
		return classList.get(id);
	}
	
	public Collection<AbstractClassFragment> getClasses() {
		return classList.values();
	}
	
	private void addClass(AbstractClassFragment classSig) {
		classList.put(classSig.getSignature().getFullName(), classSig);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		ProjectStructure otherSig = (ProjectStructure) obj;
		
		if (otherSig == null 
				|| classList.size() != otherSig.classList.size()) {
			return false;
		}
		for (Entry<String, AbstractClassFragment> entrySet : classList.entrySet()) {
			AbstractClassFragment otherClassSig = otherSig.classList.get(entrySet.getKey());
			if (otherClassSig == null || !otherClassSig.equals(entrySet.getValue())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (!hasHashCode) {
			hashCode = 1;
			for (AbstractClassFragment cls : classList.values()) {
				hashCode = hashCode + cls.hashCode();
			}
			hasHashCode = true;
		}
		return hashCode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (AbstractClassFragment cls : classList.values()) {
			sb.append(cls.toString());
		}
		return sb.toString();
	}
}
