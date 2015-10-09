package eu.uqasar.web.pages.qmtree.quality.indicator;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.quality.indicator.panels.QualityIndicatorViewPanel;

public class QMQualityIndicatorViewPage extends QMBaseTreePage<QMQualityIndicator> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4478762773367907670L;
	
	private QualityIndicatorViewPanel panel;
	
	public QMQualityIndicatorViewPage(final PageParameters parameters) {
		super(parameters);
	}

	@Override
	public WebMarkupContainer getContent(String markupId, IModel<QMQualityIndicator> model) {
		panel = new QualityIndicatorViewPanel(markupId, model);
		return panel;
	}


}
