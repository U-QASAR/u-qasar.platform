package eu.uqasar.web.dashboard.widget.jenkins;

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


import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import eu.uqasar.model.tree.Project;
import eu.uqasar.service.tree.TreeNodeService;

/**
 * 
 *
 *
 */
public class JenkinsWidget extends AbstractWidget{

	private final Project project;
	private static JenkinsFactory chartDataFactory;	
	private static final long serialVersionUID = -2447400380886027022L;
		
	public JenkinsWidget() {
		super();
		title = "Jenkins Continuous Integration";	
		project = getProject(settings.get("project"));
	}

	@Override
	public void init() {
		
	}

	public static JenkinsFactory getChartDataFactory() {
		return chartDataFactory;
	}

	public static void setChartDataFactory(JenkinsFactory chartDataFactory) {
		JenkinsWidget.chartDataFactory = chartDataFactory;
	}

	public String getChartData() {
		if (chartDataFactory == null) {
			throw new RuntimeException("ChartDataFactory cannot be null. Use ChartWidget.setChartDataFactory(...)");
		}

		return chartDataFactory.createChart(this);
	}

	public WidgetView createView(String viewId) {
		return new JenkinsWidgetView(viewId, new Model<Widget>(this));
	}

	@Override
	public boolean hasSettings() {
		return true;
	}

	@Override
	public Panel createSettingsPanel(String settingsPanelId) {
		return new JenkinsSettingsPanel(settingsPanelId, new Model<>(this));
	}
		

	private Project getProject(String projName) {
		Project pro = null;
		if (projName == null || projName.isEmpty()) {
			projName = "U-QASAR Platform Development";
		}
		TreeNodeService treeNodeService = null;
		try {
			InitialContext ic = new InitialContext();
			treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
			pro = treeNodeService.getProjectByName(projName);		
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return pro;
	}


	
}
