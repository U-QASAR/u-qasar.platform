package eu.uqasar.web.dashboard.widget.uqasardatavisualization;

import org.apache.wicket.model.IModel;

import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

/**
 * 
 *
 *
 */
public class UqasarDataVisualizationWidgetView extends WidgetView {

	private static final long serialVersionUID = 7504539323135550980L;
	public UqasarDataVisualizationWidgetView(String id, IModel<Widget> model) {
		super(id, model);

		UqasarDataVisualizationWidget widget = (UqasarDataVisualizationWidget) model.getObject();

		Chart chart = new Chart("chart", widget.getQualityParameterOptions()); 
		add(chart);
	}
}
