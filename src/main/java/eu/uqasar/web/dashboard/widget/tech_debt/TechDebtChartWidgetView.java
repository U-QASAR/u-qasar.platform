package eu.uqasar.web.dashboard.widget.tech_debt;

import org.apache.wicket.model.IModel;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

public class TechDebtChartWidgetView extends WidgetView {

	private static final long serialVersionUID = 7504539323135550980L;

	public TechDebtChartWidgetView(String id, IModel<Widget> model) {
		super(id, model);

		TechDebtChartWidget widget = 
				(TechDebtChartWidget) model.getObject();
		Chart techDebtChart = new Chart("techDebtChart", widget.getOptions());
		add(techDebtChart);
		
		// TODO: Replace the wicked-chart with this kind of an implementation or with a similar one 
		// when going open source.
		//add(widget.createChart());
	}
}
