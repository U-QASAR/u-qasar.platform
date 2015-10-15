package eu.uqasar.web.dashboard.projectqualitygooglechart;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class ProjectQualityGoogleChartWidgetDescriptor implements WidgetDescriptor {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7161860268112095733L;

	@Override
	public String getTypeName() {
		return "widget.projectqualitygooglechart";
	}

	@Override
	public String getName() {
		return "Chart (ProjectQualityGoogleChart)";
	}

	@Override
	public String getProvider() {
		return "U-QASAR Project Quality Widget (Google Charts)";
	}

	@Override
	public String getDescription() {
		return "A chart widget illustrating the overall project quality";
	}

	@Override
	public String getWidgetClassName() {
		return ProjectQualityGoogleChartWidget.class.getName();
	}
}
