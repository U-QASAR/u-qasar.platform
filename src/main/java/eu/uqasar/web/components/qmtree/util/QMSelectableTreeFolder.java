package eu.uqasar.web.components.qmtree.util;

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

import eu.uqasar.model.qmtree.QMTreeNode;

public abstract class QMSelectableTreeFolder extends QMTreeNodeContent {

	private static final long serialVersionUID = 1L;

	private final ITreeProvider<QMTreeNode> provider;

	private IModel<QMTreeNode> selected;

	public QMSelectableTreeFolder(ITreeProvider<QMTreeNode> provider) {
		this.provider = provider;
	}

	@Override
	public void detach() {
		if (selected != null) {
			selected.detach();
		}
	}

	public IModel<QMTreeNode> getSelectedNode() {
		return selected;
	}

	public boolean isSelected(QMTreeNode node) {
		IModel<QMTreeNode> model = provider.model(node);

		try {
			return selected != null && selected.equals(model);
		} finally {
			model.detach();
		}
	}

	public void unselect() {
		this.selected = null;
	}

	public void select(QMTreeNode node) {
		if (selected != null) {
			selected.detach();
			selected = null;
		}
		selected = provider.model(node);
	}

	private void select(QMTreeNode node, AbstractTree<QMTreeNode> tree,
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
			final AbstractTree<QMTreeNode> tree,
			final IModel<QMTreeNode> model) {
		return new QMTreeFolder(id, tree, model) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target) {
				onNodeClicked(target, model);
				QMSelectableTreeFolder.this
						.select(getModelObject(), tree, target);
			}

			@Override
			protected boolean isSelected() {
				return QMSelectableTreeFolder.this.isSelected(getModelObject());
			}
		};
	}

	public abstract void onNodeClicked(AjaxRequestTarget target,
			IModel<QMTreeNode> node);
}