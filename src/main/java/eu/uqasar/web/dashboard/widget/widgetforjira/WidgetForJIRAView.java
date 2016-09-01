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


import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.model.IModel;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.model.measure.JiraMetricMeasurement;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.dataadapter.JiraDataService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;

public class WidgetForJIRAView extends WidgetView {

	private static final long serialVersionUID = 2880002420318496717L;
	private transient List<JiraMetricMeasurement> measurements = new ArrayList<>();
	private final String chartType;
    private final String timeInterval;
    private final String projectName;
    private final String individualMetric;
	private final Project project;
	private final Chart jiraChart;

	public WidgetForJIRAView(String id, IModel<Widget> model) {
		super(id, model);

		final WidgetForJira jiraWidget = (WidgetForJira) model.getObject();
	
		// Add JIRA sample image
		add(new ContextImage("jiraimg", "assets/img/jiraimg.png"));

		if (jiraWidget.getSettings().get("chartType") != null) {
			chartType = jiraWidget.getSettings().get("chartType");
			projectName = jiraWidget.getSettings().get("project");
			project = getProject(projectName);
			timeInterval = jiraWidget.getSettings().get("timeInterval");
		} else {
			chartType = "BAR";
			projectName = "U-QASAR Platform Development";
			timeInterval = "Latest";
			project = getProject(projectName);
		}
		
		// Get the current measurement data
		measurements = jiraWidget.getMeasurements(project, timeInterval);	
		individualMetric = jiraWidget.getSettings().get("individualMetric");
		
        if (individualMetric  != null){
            jiraChart = new Chart("jira_chart", getChartOptionsDifferently());
            jiraChart.setOutputMarkupId(true);
            jiraChart.setOptions(getChartOptionsDifferently());
        } 
        else {
            jiraChart = new Chart("jira_chart", getChartOptions());
            jiraChart.setOutputMarkupId(true);
            jiraChart.setOptions(getChartOptions());
        }
		
		add(jiraChart);
	} 
	
    /**
     * 
     * @param
     * @return
     */
    private Options getChartOptions() {

        Options options = new Options();
        options.setChartOptions(new ChartOptions().setType(SeriesType.valueOf(chartType)));
        options.setTitle(new Title("Jira Metrics (" + timeInterval + ")"));
        options.setxAxis(new Axis().setCategories(UQasarUtil.getJiraMetricNamesAbbreviated()));
        options.setyAxis(new Axis().setTitle(new Title("Number of issues")));

        List<Number> resItems = new ArrayList<>();
        for (JiraMetricMeasurement jiraMeasurement : measurements) {
            int count;
            try {
                if (timeInterval.compareToIgnoreCase("Latest") == 0) {
                    count = getDataService().countMeasurementsPerProjectByMetricWithLatestDate(project,
                        jiraMeasurement.getJiraMetric());
                }
                count = getDataService().countMeasurementsPerProjectByMetricWithinPeriod(project,
                    jiraMeasurement.getJiraMetric(), timeInterval);
                resItems.add(count);
            } catch (uQasarException e1) {
                e1.printStackTrace();
            }
        }

        options.addSeries(new SimpleSeries().setName("Jira Data").setData(resItems));

        return options;
    }

    /**
     * 
     * @param
     * @return
     */
    private Options getChartOptionsDifferently() {

        Options options = new Options();
        options.setChartOptions(new ChartOptions().setType(SeriesType.valueOf(chartType)));
        options.setTitle(new Title("Jira Metrics (" + timeInterval + ")"));
        options.setxAxis(new Axis().setCategories(UQasarUtil.getJiraMetricNamesAbbreviated()));
        options.setyAxis(new Axis().setTitle(new Title("Number of issues")));

        List<Number> resItems = new ArrayList<>();
        for (JiraMetricMeasurement jiraMeasurement : measurements) {

            if (jiraMeasurement.getJiraMetric().equals(individualMetric)) {
                int count;
                try {
                    if (timeInterval.compareToIgnoreCase("Latest") == 0) {
                        count = getDataService().countMeasurementsPerProjectByMetricWithLatestDate(project,
                            jiraMeasurement.getJiraMetric());
                    }
                    count = getDataService().countMeasurementsPerProjectByMetricWithinPeriod(project,
                        jiraMeasurement.getJiraMetric(), timeInterval);
                    resItems.add(count);
                } catch (uQasarException e1) {
                    e1.printStackTrace();
                }
            }
        }

        options.addSeries(new SimpleSeries().setName("Jira Data").setData(resItems));

        return options;
    }

	/**
	 * 
	 * @return
	 */
	private JiraDataService getDataService() {
		JiraDataService dataService = null;
		try {
			InitialContext ic = new InitialContext();
			dataService = (JiraDataService) ic.lookup("java:module/JiraDataService");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return dataService;
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
