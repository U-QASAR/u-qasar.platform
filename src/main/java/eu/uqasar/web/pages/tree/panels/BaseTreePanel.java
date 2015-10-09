package eu.uqasar.web.pages.tree.panels;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.tree.ITreeNode;

public abstract class BaseTreePanel<T extends ITreeNode<String>> extends Panel {

	private static final long serialVersionUID = 8251835089980525461L;

	public BaseTreePanel(String id, IModel<T> model) {
		super(id, model);
	}

}
