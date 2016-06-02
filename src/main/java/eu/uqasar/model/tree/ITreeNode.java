package eu.uqasar.model.tree;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.Serializable;
import java.util.List;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.Persistable;

public interface ITreeNode<Key extends Serializable> extends Persistable<Long>, Serializable {

	public Key getNodeKey();
	
	public ITreeNode<Key> getParent();
	
	public List<? extends ITreeNode<Key>> getSiblings();
	
	public List<? extends ITreeNode<Key>> getSiblings(boolean includeSelf);
	
	public boolean canChangePositionWithNextSibling();

	public boolean canChangePositionWithNextSibling(boolean changeParents);

	public boolean canChangePositionWithPreviousSibling();

	public boolean canChangePositionWithPreviousSibling(boolean changeParents);
	
	public boolean changePositionWithNextSibling();

	public boolean changePositionWithNextSibling(boolean changeParents);

	public boolean changePositionWithPreviousSibling();

	public boolean changePositionWithPreviousSibling(boolean changeParents);
	
	public List<? extends ITreeNode<Key>> getChildren();
	
	public Class<? extends ITreeNode<Key>> getChildType();

	public void addChild(ITreeNode<Key> child);
	
	public String getName();
	
	public IconType getIconType();
	
	public QualityStatus getQualityStatus();

}
