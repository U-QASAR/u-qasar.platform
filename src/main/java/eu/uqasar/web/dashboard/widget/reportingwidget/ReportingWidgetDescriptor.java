package eu.uqasar.web.dashboard.widget.reportingwidget;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class ReportingWidgetDescriptor implements WidgetDescriptor {

    private static final long serialVersionUID = -8991404810105901232L;

    @Override
    public String getTypeName() {
        return "widget.reportingwidget";
    }

    @Override
    public String getName() {
        return "Reporting Quality";
    }

    @Override
    public String getProvider() {
        return "Abhijit Vyas";
    }

    @Override
    public String getDescription() {
        return "A widget providing Reporting analysis data to the user.";
    }

    @Override
    public String getWidgetClassName() {
        return ReportingWidget.class.getName();
    }

}
