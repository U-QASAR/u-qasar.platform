package eu.uqasar.web.components.qmtree.util;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.qmtree.QMTreeNode;

public abstract class QMSelectableTreeFolder extends QMTreeNodeContent {

	private static final long serialVersionUID = 1L;

	private ITreeProvider<QMTreeNode> provider;

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

	protected void select(QMTreeNode node, AbstractTree<QMTreeNode> tree,
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