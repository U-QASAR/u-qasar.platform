package eu.uqasar.web.components.qmtree.util;

import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.web.components.qmtree.util.theme.UQasarTreeTheme;

public abstract class QMTree extends NestedTree<QMTreeNode> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6485363198642769165L;
	
	private QMSelectableTreeFolder nodeContent;

	public QMTree(String id, ITreeProvider<QMTreeNode> provider,
			IModel<Set<QMTreeNode>> state) {
		super(id, provider, state);
		nodeContent = new QMSelectableTreeFolder(provider) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -1379367044943131546L;

			@Override
			public void onNodeClicked(AjaxRequestTarget target,
					IModel<QMTreeNode> node) {
				QMTree.this.onNodeClicked(target, node);
			}

		};
		add(new UQasarTreeTheme());
	}

	public IModel<QMTreeNode> getSelectedNode() {
		return nodeContent.getSelectedNode();
	}

	public void selectNode(QMTreeNode node) {
		nodeContent.select(node);
	}

	public boolean isNodeSelected(IModel<QMTreeNode> nodeModel) {
		return nodeContent.isSelected(nodeModel.getObject());
	}

	public void unselect() {
		nodeContent.unselect();
	}
	
	public boolean isNodeSelected(QMTreeNode node) {
		return nodeContent.isSelected(node);
	}

	public abstract void onNodeClicked(AjaxRequestTarget target,
			IModel<QMTreeNode> node);

	@Override
	protected Component newContentComponent(String id, IModel<QMTreeNode> model) {
		return nodeContent.newContentComponent(id, this, model);
	}

}
