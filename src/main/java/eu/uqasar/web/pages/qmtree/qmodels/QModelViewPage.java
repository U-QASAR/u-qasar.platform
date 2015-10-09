package eu.uqasar.web.pages.qmtree.qmodels;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.qmodels.panels.QModelViewPanel;

public class QModelViewPage extends QMBaseTreePage<QModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2772863493569900988L;

	private QModelViewPanel panel;

	public QModelViewPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public WebMarkupContainer getContent(String markupId, IModel<QModel> model) {
		panel = new QModelViewPanel(markupId, model);
		return panel;
	}

}
