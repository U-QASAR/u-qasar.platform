package eu.uqasar.web.dashboard.widget.testlinkwidget;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class TestLinkWidgetDescriptor implements WidgetDescriptor {


	/**
	 * 
	 */
	private static final long serialVersionUID = -923144248453765767L;

	@Override
	public String getTypeName() {
		return "widget.testlinkwidget";
	}

	@Override
	public String getName() {
		return "TestLink Quality";
	}

	@Override
	public String getProvider() {
		return "U-QASAR";
	}

	@Override
	public String getDescription() {
		return "A widget providing a list of TestLink metrics to the user.";
	}

	@Override
	public String getWidgetClassName() {
		return TestLinkWidget.class.getName();
	}
}
