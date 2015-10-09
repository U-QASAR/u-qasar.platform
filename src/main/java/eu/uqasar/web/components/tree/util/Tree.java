package eu.uqasar.web.components.tree.util;

import java.io.Serializable;
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

	private SelectableTreeFolder nodeContent;

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
