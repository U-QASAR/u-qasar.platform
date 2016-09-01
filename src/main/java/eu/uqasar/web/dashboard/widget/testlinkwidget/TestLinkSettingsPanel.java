package eu.uqasar.web.dashboard.widget.testlinkwidget;

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

import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.service.dataadapter.TestLinkDataService;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.dashboard.DashboardViewPage;
import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import ro.fortsoft.wicket.dashboard.web.DashboardContextAware;
import ro.fortsoft.wicket.dashboard.web.DashboardPanel;
import ro.fortsoft.wicket.dashboard.web.WidgetPanel;

public class TestLinkSettingsPanel extends GenericPanel<TestLinkWidget> 
implements DashboardContextAware {

	private static final long serialVersionUID = 1L;

	private transient DashboardContext dashboardContext;
	private String project;
    private final String timeInterval;
    private final String individualMetric;

	public TestLinkSettingsPanel(String id, 
			IModel<TestLinkWidget> model) {
		super(id, model);

		setOutputMarkupPlaceholderTag(true);

		Form<Widget> form = new Form<>("form");
		project = getModelObject().getSettings().get("project");
		timeInterval = getModelObject().getSettings().get("period");
		individualMetric = getModelObject().getSettings().get("individualMetric");

		List<String> projects = new ArrayList<>();
		try {
			InitialContext ic = new InitialContext();
			TestLinkDataService dataService = (TestLinkDataService) ic.lookup("java:module/TestLinkDataService");
			projects = dataService.getTestLinkProjects();
		} catch (NamingException e) {
			e.printStackTrace();
		}

		form.add(new DropDownChoice<>("project",
                new PropertyModel<String>(this, "project"), projects));

		List<String> individualMetricGroups = UQasarUtil.getTestLinkMetricNames();        
		DropDownChoice<String> dropDown = new DropDownChoice<>("individualMetric", new PropertyModel<String>(this,
                "individualMetric"), individualMetricGroups);
		dropDown.setNullValid(true);
		form.add(dropDown);

		form.add(new AjaxSubmitLink("submit") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

				Dashboard dashboard = findParent(DashboardPanel.class).getDashboard();
				if (project != null && dashboard != null) {
					getModelObject().getSettings().put("project", project);
					getModelObject().getSettings().put("period", timeInterval);
					getModelObject().getSettings().put("individualMetric", individualMetric);
					
					DbDashboard dbdb = (DbDashboard) dashboard;
					hideSettingPanel(target);

					WidgetPanel widgetPanel = findParent(WidgetPanel.class);
					TestLinkWidget tasksWidget = 
							(TestLinkWidget) widgetPanel.getModelObject();
					tasksWidget.setTitle("Source code quality in " +project);
					TestLinkWidgetView widgetView = 
							(TestLinkWidgetView) widgetPanel.getWidgetView();
					target.add(widgetView);

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
		List<String> timeIntervals = Arrays.asList("Last Year", "Last 6 Months", "Last Month", "Last Week");
		form.add(new DropDownChoice<>("time", new PropertyModel<String>(this, "timeInterval"), timeIntervals));


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
