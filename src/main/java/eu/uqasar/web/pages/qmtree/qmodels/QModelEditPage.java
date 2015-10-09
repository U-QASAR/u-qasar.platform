package eu.uqasar.web.pages.qmtree.qmodels;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.qmodels.panels.QModelEditPanel;

public class QModelEditPage extends QMBaseTreePage<QModel> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 451083867628959546L;
	
	private QModelEditPanel panel;
	
	/**
	 * isNew = true if accessing in "create" mode
	 */
	private boolean isNew = false;
	
	public QModelEditPage(PageParameters parameters) {
		super(parameters);
		if (parameters!=null && parameters.get("isNew").toString()!=null && parameters.get("isNew").toString().equals("true")){
					 isNew = true;
		}
	}

	@Override
	public WebMarkupContainer getContent(final String markupId,
			final IModel<QModel> model) {
		panel = new QModelEditPanel(markupId, model, isNew);
		return panel;
	}
}
