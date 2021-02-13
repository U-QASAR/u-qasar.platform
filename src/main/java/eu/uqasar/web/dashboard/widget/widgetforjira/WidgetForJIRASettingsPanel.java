package eu.uqasar.web.dashboard.widget.widgetforjira;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.dashboard.DashboardViewPage;

public class WidgetForJIRASettingsPanel extends GenericPanel<WidgetForJira> implements DashboardContextAware {

	private static final long serialVersionUID = 1L;

	private transient DashboardContext dashboardContext;
	private Project project;
	private String projectName;
    private final String timeInterval;
    private String chartType;
    private final String individualMetric;
	private final DropDownChoice<Project> projectChoice;
	private final WebMarkupContainer wmcGeneral;
	private List<Project> projects;

	public WidgetForJIRASettingsPanel(final String id, IModel<WidgetForJira> model) {
		super(id, model);

		setOutputMarkupPlaceholderTag(true);

		final WidgetForJira tasksWidget = model.getObject();
		// Get the project from the settings
		if (tasksWidget.getSettings().get("project") != null) {
			chartType = tasksWidget.getSettings().get("chartType");
			projectName = tasksWidget.getSettings().get("project");
			timeInterval = tasksWidget.getSettings().get("timeInterval");
		} else {
			// Otherwise use the first project of the existing ones
			chartType = "BAR";
			timeInterval = "Latest";
		}

		individualMetric = getModelObject().getSettings().get("individualMetric");
		// DropDown select for Projects
		TreeNodeService treeNodeService = null;
		try {
			InitialContext ic = new InitialContext();
			treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService"); 
			projects = treeNodeService.getAllProjectsOfLoggedInUser();
			// Select the first project if no project is provided
			if (projects != null && projects.size() != 0) {
				if (projectName == null || projectName.isEmpty() && projects.size() != 0) {
					projectName = projects.get(0).getName();
				}
			}
			project = treeNodeService.getProjectByName(projectName);
		} catch (NamingException e) {
			e.printStackTrace();
		}

		Form<Widget> form = new Form<>("form");
		wmcGeneral = new WebMarkupContainer("wmcGeneral");
		form.add(wmcGeneral);

		chartType = getModelObject().getSettings().get("chartType");
		List<String> chartTypes = Arrays.asList("AREA", "BAR", "LINE", "COLUMN");
		List<String> timeIntervals = Arrays.asList("Last Year", "Last 6 Months", "Last Month", "Last Week", "Latest");

		wmcGeneral.add(new DropDownChoice<>("chartType", new PropertyModel<String>(this, "chartType"), chartTypes));

		wmcGeneral.add(new DropDownChoice<>("time", new PropertyModel<String>(this, "timeInterval"), timeIntervals));

		// project
		projectChoice = new DropDownChoice<>("project", new PropertyModel<Project>(this, "project"), projects);
		if (project != null) {
			projectChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					System.out.println(project);

				}
			});

		}
		List<String> individualMetricGroups = UQasarUtil.getJiraMetricNames();
		DropDownChoice<String> dropDown = new DropDownChoice<>("individualMetric", new PropertyModel<String>(this,
                "individualMetric"), individualMetricGroups);
		dropDown.setNullValid(true);
		wmcGeneral.add(dropDown);
		wmcGeneral.setOutputMarkupId(true);
		wmcGeneral.add(projectChoice);
		form.add(new AjaxSubmitLink("submit") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Dashboard dashboard = findParent(DashboardPanel.class).getDashboard();
				if (project != null && dashboard != null ) {
					getModelObject().getSettings().put("chartType", chartType);
					getModelObject().getSettings().put("project", project.getName());
					getModelObject().getSettings().put("timeInterval", timeInterval);
					getModelObject().getSettings().put("individualMetric", individualMetric);
					hideSettingPanel(target);

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
		form.add(new AjaxLink<Void>("cancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				hideSettingPanel(target);
			}
		});

		add(form);
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
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
