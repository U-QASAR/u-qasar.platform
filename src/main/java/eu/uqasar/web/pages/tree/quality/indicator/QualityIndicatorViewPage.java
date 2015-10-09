package eu.uqasar.web.pages.tree.quality.indicator;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.quality.indicator.panels.QualityIndicatorViewPanel;

public class QualityIndicatorViewPage extends BaseTreePage<QualityIndicator> {

	private static final long serialVersionUID = -7308986839340127545L;
	
	private QualityIndicatorViewPanel panel;
	
	public QualityIndicatorViewPage(final PageParameters parameters) {
		super(parameters);
	}

	@Override
	public WebMarkupContainer getContent(String markupId, IModel<QualityIndicator> model) {
		panel = new QualityIndicatorViewPanel(markupId, model);
		return panel;
	}


}
