package eu.uqasar.web.pages.tree.quality.objective;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.quality.objective.panels.QualityObjectiveViewPanel;

public class QualityObjectiveViewPage extends BaseTreePage<QualityObjective> {

	private static final long serialVersionUID = -8653833384370041792L;

	private QualityObjectiveViewPanel panel;

	public QualityObjectiveViewPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public WebMarkupContainer getContent(String markupId,
			IModel<QualityObjective> model) {
		panel = new QualityObjectiveViewPanel(markupId, model);
		return panel;
	}

}
