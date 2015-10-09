package eu.uqasar.web.dashboard.widget.tech_debt;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class TechDebtChartWidgetDescriptor implements WidgetDescriptor {

	private static final long serialVersionUID = -7046897884371063291L;

	@Override
	public String getTypeName() {
		return "widget.techdebtchart";
	}

	@Override
	public String getName() {
		return "Chart (TechDebtChart)";
	}

	@Override
	public String getProvider() {
		return "U-QASAR tech debt widget";
	}

	@Override
	public String getDescription() {
		return "A chart widget";
	}

	@Override
	public String getWidgetClassName() {
		return TechDebtChartWidget.class.getName();
	}
}
