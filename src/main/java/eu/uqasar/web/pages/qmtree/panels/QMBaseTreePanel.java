package eu.uqasar.web.pages.qmtree.panels;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import eu.uqasar.model.qmtree.IQMTreeNode;

public abstract class QMBaseTreePanel<T extends IQMTreeNode<String>> extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1471525857010455058L;

	public QMBaseTreePanel(String id, IModel<T> model) {
		super(id, model);
	}

}
