package eu.uqasar.web.components.tree.util;

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



import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.tree.TreeNode;

public abstract class TreeNodeContent implements IDetachable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7041506070029131494L;

	/**
	 * Create new content.
	 */
	public abstract Component newContentComponent(String id,
			AbstractTree<TreeNode> tree,
			IModel<TreeNode> model);

	@Override
	public void detach() {
	}
}