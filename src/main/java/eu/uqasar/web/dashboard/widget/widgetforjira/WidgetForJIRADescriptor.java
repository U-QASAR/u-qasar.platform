package eu.uqasar.web.dashboard.widget.widgetforjira;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class WidgetForJIRADescriptor implements WidgetDescriptor {

	private static final long serialVersionUID = -8991404810105901232L;

	@Override
	public String getTypeName() {
		return "widget.widgetForJIRA";
	}

	@Override
	public String getName() {
		return "Widget for JIRA(R)";
	}

	@Override
	public String getProvider() {
		return "U-QASAR issues widget";
	}

	@Override
	public String getDescription() {
		return "A widget providing a list of current JIRA(R) issues to the user.";
	}

	@Override
	public String getWidgetClassName() {
		return WidgetForJira.class.getName();
	}

}
