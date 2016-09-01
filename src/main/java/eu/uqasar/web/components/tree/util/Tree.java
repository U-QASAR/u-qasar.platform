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


import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.web.components.tree.util.theme.UQasarTreeTheme;

public abstract class Tree extends NestedTree<TreeNode> {

	private static final long serialVersionUID = -7991762156346710807L;

	private final SelectableTreeFolder nodeContent;

	public Tree(String id, ITreeProvider<TreeNode> provider,
			IModel<Set<TreeNode>> state) {
		super(id, provider, state);
		nodeContent = new SelectableTreeFolder(provider) {
			private static final long serialVersionUID = -3561439318750495007L;

			@Override
			public void onNodeClicked(AjaxRequestTarget target,
					IModel<TreeNode> node) {
				Tree.this.onNodeClicked(target, node);
			}

		};
		add(new UQasarTreeTheme());
	}

	public IModel<TreeNode> getSelectedNode() {
		return nodeContent.getSelectedNode();
	}

	public void selectNode(TreeNode node) {
		nodeContent.select(node);
	}

	public boolean isNodeSelected(IModel<TreeNode> nodeModel) {
		return nodeContent.isSelected(nodeModel.getObject());
	}
	
	public void unselect() {
		nodeContent.unselect();
	}

	public boolean isNodeSelected(TreeNode node) {
		return nodeContent.isSelected(node);
	}

	public abstract void onNodeClicked(AjaxRequestTarget target,
			IModel<TreeNode> node);

	@Override
	protected Component newContentComponent(String id, IModel<TreeNode> model) {
		return nodeContent.newContentComponent(id, this, model);
	}

}
