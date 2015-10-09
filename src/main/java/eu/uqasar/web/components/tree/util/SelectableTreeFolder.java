package eu.uqasar.web.components.tree.util;

import java.io.Serializable;

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

	protected void select(TreeNode node, AbstractTree<TreeNode> tree,
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