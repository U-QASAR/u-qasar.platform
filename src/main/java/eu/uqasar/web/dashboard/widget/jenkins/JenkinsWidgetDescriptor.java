package eu.uqasar.web.dashboard.widget.jenkins;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

/**
 * 
 *
 *
 */
public class JenkinsWidgetDescriptor implements WidgetDescriptor {

	private static final long serialVersionUID = -7046897884371063291L;

	@Override
	public String getTypeName() {
		return "widget.datadeviation";
	}

	@Override
	public String getName() {
		return "Chart (DataDeviation)";
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
		return JenkinsWidget.class.getName();
	}
	
	
}
