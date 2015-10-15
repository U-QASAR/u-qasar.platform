package eu.uqasar.web.dashboard.projectqualitygooglechart;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class ProjectQualityGoogleChartWidgetView extends WidgetView {

	private static final long serialVersionUID = 7504539323135550980L;
	private Double value;
	
	public ProjectQualityGoogleChartWidgetView(String id, IModel<Widget> model) {
		super(id, model);

		ProjectQualityGoogleChartWidget widget = (ProjectQualityGoogleChartWidget) model.getObject();
		value = widget.returnProjectQualityValue();
	}
	
	@Override
    public void renderHead(IHeaderResponse response) {
	    super.renderHead(response);
	    response.render(JavaScriptHeaderItem.forScript("$(document).ready(function() {google.setOnLoadCallback(drawChart("+value+"));});", "idJavaScript"));
	}
}
