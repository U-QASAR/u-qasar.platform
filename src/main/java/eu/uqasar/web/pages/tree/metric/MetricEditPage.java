package eu.uqasar.web.pages.tree.metric;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.tree.Metric;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.metric.panels.MetricEditPanel;

public class MetricEditPage extends BaseTreePage<Metric> {

	private static final long serialVersionUID = 8670486720912723983L;
	
	private MetricEditPanel panel;
	private boolean isNew = false;
	
	public MetricEditPage(PageParameters parameters) {
		super(parameters);
		if (parameters !=null && parameters.get("isNew") !=null && 
				parameters.get("isNew").toBoolean() == true) {
			isNew = true;
		}
	}

	@Override
	public WebMarkupContainer getContent(String markupId, IModel<Metric> model) {
		panel = new MetricEditPanel(markupId, model, isNew);
		return panel;
	}


}
