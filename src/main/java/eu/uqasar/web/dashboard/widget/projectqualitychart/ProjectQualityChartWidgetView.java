package eu.uqasar.web.dashboard.widget.projectqualitychart;

import org.apache.wicket.model.IModel;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

public class ProjectQualityChartWidgetView extends WidgetView {

	private static final long serialVersionUID = 7504539323135550980L;

	public ProjectQualityChartWidgetView(String id, IModel<Widget> model) {
		super(id, model);

		ProjectQualityChartWidget widget = 
				(ProjectQualityChartWidget) model.getObject();
		Chart projectCurrentQualityChart = new Chart("currentProjectQualityChart", widget.getOptions());
		add(projectCurrentQualityChart);
		Chart projectHistoricalQualityChart = new Chart("historicalProjectQualityChart", widget.getOptionsForHistoricalChart());
		add(projectHistoricalQualityChart);
	}
}
