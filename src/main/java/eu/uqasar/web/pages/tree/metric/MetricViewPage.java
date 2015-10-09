package eu.uqasar.web.pages.tree.metric;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.tree.Metric;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.metric.panels.MetricViewPanel;

public class MetricViewPage extends BaseTreePage<Metric> {

	private static final long serialVersionUID = 7845962691810820533L;

	private MetricViewPanel panel;

	public MetricViewPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public WebMarkupContainer getContent(String markupId, IModel<Metric> model) {
		panel = new MetricViewPanel(markupId, model);
		return panel;
	}

}
