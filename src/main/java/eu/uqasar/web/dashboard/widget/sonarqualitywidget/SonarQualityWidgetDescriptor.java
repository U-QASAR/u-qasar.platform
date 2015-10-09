package eu.uqasar.web.dashboard.widget.sonarqualitywidget;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class SonarQualityWidgetDescriptor implements WidgetDescriptor {

	private static final long serialVersionUID = -8991404810105901232L;

	@Override
	public String getTypeName() {
		return "widget.sonarqualitywidget";
	}

	@Override
	public String getName() {
		return "Sonar Code Quality";
	}

	@Override
	public String getProvider() {
		return "U-Qasar widget for static code analysis";
	}

	@Override
	public String getDescription() {
		return "A widget providing Sonar code analysis data to the user.";
	}

	@Override
	public String getWidgetClassName() {
		return SonarQualityWidget.class.getName();
	}

}
