package eu.uqasar.web.dashboard.widget.datadeviation;

import org.apache.wicket.model.IModel;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

/**
 * 
 *
 *
 */
public class DataDeviationWidgetView extends WidgetView {

	private static final long serialVersionUID = 7504539323135550980L;
	public DataDeviationWidgetView(String id, IModel<Widget> model) {
		super(id, model);

		DataDeviationWidget widget = (DataDeviationWidget) model.getObject();

		Chart chart = new Chart("chart", widget.getQualityParameterOptions()); 
		add(chart);
	}
}
