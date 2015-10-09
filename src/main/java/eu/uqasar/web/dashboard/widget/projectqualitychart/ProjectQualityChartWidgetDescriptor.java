package eu.uqasar.web.dashboard.widget.projectqualitychart;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class ProjectQualityChartWidgetDescriptor implements WidgetDescriptor {

	private static final long serialVersionUID = -7046897884371063291L;

	@Override
	public String getTypeName() {
		return "widget.projectqualitychart";
	}

	@Override
	public String getName() {
		return "Chart (ProjectQualityChart)";
	}

	@Override
	public String getProvider() {
		return "U-QASAR Project Quality Widget";
	}

	@Override
	public String getDescription() {
		return "A chart widget illustrating the overall project quality";
	}

	@Override
	public String getWidgetClassName() {
		return ProjectQualityChartWidget.class.getName();
	}
}
