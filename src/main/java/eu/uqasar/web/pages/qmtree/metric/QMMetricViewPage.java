package eu.uqasar.web.pages.qmtree.metric;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.metric.panels.MetricViewPanel;

public class QMMetricViewPage extends QMBaseTreePage<QMMetric> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4674857770843000915L;

	private MetricViewPanel panel;

	public QMMetricViewPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public WebMarkupContainer getContent(String markupId, IModel<QMMetric> model) {
		panel = new MetricViewPanel(markupId, model);
		return panel;
	}

}
