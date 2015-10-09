package eu.uqasar.web.pages.qmtree.metric;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.metric.panels.MetricEditPanel;

public class QMMetricEditPage extends QMBaseTreePage<QMMetric> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5976137207324559789L;

	private MetricEditPanel panel;
	
	/**
	 * isNew = true if accessing in "create" mode
	 */
	private boolean isNew = false;
	
	public QMMetricEditPage(PageParameters parameters) {
		super(parameters);
		if (parameters!=null && parameters.get("isNew").toString()!=null && parameters.get("isNew").toString().equals("true")){
			 isNew = true;
		}
	}

	@Override
	public WebMarkupContainer getContent(String markupId, IModel<QMMetric> model) {
		panel = new MetricEditPanel(markupId, model, isNew);
		return panel;
	}


}
