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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.tree.TreeNode;

public abstract class SelectableTreeFolder extends TreeNodeContent {

	private static final long serialVersionUID = 1L;

	private final ITreeProvider<TreeNode> provider;

	private IModel<TreeNode> selected;

	public SelectableTreeFolder(ITreeProvider<TreeNode> provider) {
		this.provider = provider;
	}

	@Override
	public void detach() {
		if (selected != null) {
			selected.detach();
		}
	}

	public IModel<TreeNode> getSelectedNode() {
		return selected;
	}

	public boolean isSelected(TreeNode node) {
		IModel<TreeNode> model = provider.model(node);

		try {
			return selected != null && selected.equals(model);
		} finally {
			model.detach();
		}
	}
	
	public void unselect() {
		this.selected = null;
	}

	public void select(TreeNode node) {
		if (selected != null) {
			selected.detach();
			selected = null;
		}
		selected = provider.model(node);
	}

	private void select(TreeNode node, AbstractTree<TreeNode> tree,
                        final AjaxRequestTarget target) {
		if (selected != null) {
			tree.updateNode(selected.getObject(), target);

			selected.detach();
			selected = null;
		}

		selected = provider.model(node);

		tree.updateNode(node, target);
	}

	@Override
	public Component newContentComponent(String id,
			final AbstractTree<TreeNode> tree,
			final IModel<TreeNode> model) {
		return new TreeFolder(id, tree, model) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target) {
				onNodeClicked(target, model);
				SelectableTreeFolder.this
						.select(getModelObject(), tree, target);
			}

			@Override
			protected boolean isSelected() {
				return SelectableTreeFolder.this.isSelected(getModelObject());
			}
		};
	}

	public abstract void onNodeClicked(AjaxRequestTarget target,
			IModel<TreeNode> node);
}