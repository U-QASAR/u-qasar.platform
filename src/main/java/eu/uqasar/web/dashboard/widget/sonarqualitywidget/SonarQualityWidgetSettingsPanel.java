package eu.uqasar.web.dashboard.widget.sonarqualitywidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import ro.fortsoft.wicket.dashboard.web.DashboardContextAware;
import ro.fortsoft.wicket.dashboard.web.DashboardPanel;
import ro.fortsoft.wicket.dashboard.web.WidgetPanel;
import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.service.dataadapter.SonarDataService;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.dashboard.DashboardViewPage;

public class SonarQualityWidgetSettingsPanel extends GenericPanel<SonarQualityWidget> implements DashboardContextAware {

    private static final long serialVersionUID = 1L;

    private transient DashboardContext dashboardContext;
    private String project, timeInterval, metric, individualMetric;
    private SonarDataService dataService;

    public SonarQualityWidgetSettingsPanel(String id, IModel<SonarQualityWidget> model) {
        super(id, model);

        setOutputMarkupPlaceholderTag(true);

        Form<Widget> form = new Form<Widget>("form");
        project = getModelObject().getSettings().get("project");
        metric = getModelObject().getSettings().get("metric");
        individualMetric = getModelObject().getSettings().get("individualMetric");
        if (getModelObject().getSettings().get("period") != null){
            timeInterval = getModelObject().getSettings().get("period");
        } else {
            // if there is no selection of period then it should be the latest values 
            timeInterval = "Latest";
        }
        
        

        List<String> projects = new ArrayList<String>();
        try {
            InitialContext ic = new InitialContext();
            dataService = (SonarDataService) ic.lookup("java:module/SonarDataService");
            projects = dataService.getSonarProjects();
        } catch (NamingException e) {
            e.printStackTrace();
        }

        form.add(new DropDownChoice<String>("project", new PropertyModel<String>(this, "project"), projects));

        List<String> metricGroups = Arrays.asList(new String[] { "Code Lines related", "Complexity related",
                "Structure related", "Density related", "Test related" });
        form.add(new DropDownChoice<String>("metric", new PropertyModel<String>(this, "metric"), metricGroups));
        
        List<String> individualMetricGroups = UQasarUtil.getSonarMetricNames();
        DropDownChoice<String> dropDown = new DropDownChoice<String>("individualMetric", new PropertyModel<String>(this,
            "individualMetric"), individualMetricGroups);
        dropDown.setNullValid(true);
        form.add(dropDown);

        form.add(new AjaxSubmitLink("submit") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Dashboard dashboard = findParent(DashboardPanel.class).getDashboard();
                if (project != null && dashboard != null ) {
                    getModelObject().getSettings().put("project", project);
                    getModelObject().getSettings().put("metric", metric);
                    getModelObject().getSettings().put("period", timeInterval);
                    getModelObject().getSettings().put("individualMetric", individualMetric);
                    hideSettingPanel(target);

                    WidgetPanel widgetPanel = findParent(WidgetPanel.class);
                    SonarQualityWidget tasksWidget = (SonarQualityWidget) widgetPanel.getModelObject();
                    tasksWidget.setTitle("Source code quality in " + project);
                    SonarQualityWidgetView widgetView = (SonarQualityWidgetView) widgetPanel.getWidgetView();
                    target.add(widgetView);
                    DbDashboard dbdb = (DbDashboard) dashboard;

                    // Do not save the default dashboard
					if (dbdb.getId() != null && dbdb.getId() != 0) {
						dashboardContext.getDashboardPersiter().save(dashboard);
						PageParameters params = new PageParameters();
						params.add("id", dbdb.getId());
						setResponsePage(DashboardViewPage.class, params);
					}
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }

        });

        // Period
        List<String> timeIntervals = Arrays.asList(new String[] { "Last Year", "Last 6 Months", "Last Month", "Last Week","Latest" });

        form.add(new DropDownChoice<String>("time", new PropertyModel<String>(this, "timeInterval"), timeIntervals));

        form.add(new AjaxLink<Void>("cancel") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                hideSettingPanel(target);
            }
        });

        add(form);
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public void setDashboardContext(DashboardContext dashboardContext) {
        this.dashboardContext = dashboardContext;
    }

    private void hideSettingPanel(AjaxRequestTarget target) {
        setVisible(false);
        target.add(this);
    }

}
