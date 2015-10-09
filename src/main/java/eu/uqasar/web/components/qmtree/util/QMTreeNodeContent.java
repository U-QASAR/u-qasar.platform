package eu.uqasar.web.components.qmtree.util;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.qmtree.QMTreeNode;

public abstract class QMTreeNodeContent implements IDetachable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1879406229399596019L;

	/**
	 * Create new content.
	 */
	public abstract Component newContentComponent(String id,
			AbstractTree<QMTreeNode> tree,
			IModel<QMTreeNode> model);

	@Override
	public void detach() {
	}
}