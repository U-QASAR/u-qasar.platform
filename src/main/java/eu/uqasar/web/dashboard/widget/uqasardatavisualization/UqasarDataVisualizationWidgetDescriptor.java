package eu.uqasar.web.dashboard.widget.uqasardatavisualization;

import com.googlecode.wickedcharts.highcharts.options.Options;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

/**
 * 
 *
 *
 */
public class UqasarDataVisualizationWidgetDescriptor implements WidgetDescriptor {

	private static final long serialVersionUID = -7046897884371063291L;

	@Override
	public String getTypeName() {
		return "widget.uqasardatavisualization";
	}

	@Override
	public String getName() {
		return "Chart (UqasarDataVisualization)";
	}

	@Override
	public String getProvider() {
		return "Philipp Spindler";
	}

	@Override
	public String getDescription() {
		return "A chart widget based on WickedChart";
	}

	@Override
	public String getWidgetClassName() {
		return UqasarDataVisualizationWidget.class.getName();
	}
	
	
}
