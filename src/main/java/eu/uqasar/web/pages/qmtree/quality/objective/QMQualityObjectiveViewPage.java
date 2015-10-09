package eu.uqasar.web.pages.qmtree.quality.objective;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.quality.objective.panels.QualityObjectiveViewPanel;

public class QMQualityObjectiveViewPage extends QMBaseTreePage<QMQualityObjective> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1219627006053289042L;

	private QualityObjectiveViewPanel panel;

	public QMQualityObjectiveViewPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public WebMarkupContainer getContent(String markupId,
			IModel<QMQualityObjective> model) {
		panel = new QualityObjectiveViewPanel(markupId, model);
		return panel;
	}

}
