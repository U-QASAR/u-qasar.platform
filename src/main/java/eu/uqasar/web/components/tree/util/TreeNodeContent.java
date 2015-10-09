package eu.uqasar.web.components.tree.util;


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