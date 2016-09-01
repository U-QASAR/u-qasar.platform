package eu.uqasar.service.dataadapter;

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


import java.io.Serializable;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.model.measure.CubesMetricMeasurement;
import eu.uqasar.model.measure.JenkinsMetricMeasurement;
import eu.uqasar.model.measure.JiraMetricMeasurement;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.measure.SonarMetricMeasurement;
import eu.uqasar.model.measure.TestLinkMetricMeasurement;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.tree.TreeNodeService;


public class AdapterDataService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JiraDataService jiraDataService;
	private TestLinkDataService testlinkDataService;
	private SonarDataService sonarDataService;
	private CubesDataService cubesDataService;
	private GitlabDataService gitlabDataService;
	private TreeNodeService treeNodeService;
	private AdapterSettingsService adapterSettingsService;
	private JenkinsDataService jenkinsDataService;

	public AdapterDataService() {
		try {
			InitialContext ic = new InitialContext();
			sonarDataService = (SonarDataService) ic.lookup("java:module/SonarDataService");
			testlinkDataService = (TestLinkDataService) ic.lookup("java:module/TestLinkDataService");
			jiraDataService = (JiraDataService) ic.lookup("java:module/JiraDataService");
			cubesDataService = (CubesDataService) ic.lookup("java:module/CubesDataService");
			treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
			gitlabDataService = (GitlabDataService) ic.lookup("java:module/GitlabDataService");
			adapterSettingsService = (AdapterSettingsService) ic.lookup("java:module/AdapterSettingsService");
			jenkinsDataService = (JenkinsDataService) ic.lookup("java:module/JenkinsDataService");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Obtain a metric value for a specific metric source and metric type (and possibly a project) 
	 * @param metricSource
	 * @param metricType
	 * @param project
	 * @return stored metric value or null if no value exists
	 */
	public Float getMetricValue(MetricSource metricSource, String metricType, Project project) {

		// The value to be stored
		Float storedMetricValue = (float) 0;
		
		// Handle the different cases of adapter types
		if (metricSource != null && metricSource == MetricSource.IssueTracker) {
			List<JiraMetricMeasurement> res = null;
			try {
				res = jiraDataService.getMeasurementsForMetric(metricType);
			} catch (uQasarException e) {
				e.printStackTrace();
			}
			if (res != null && res.size() > 0) {
				storedMetricValue = (float) res.size();
			}
		} else if (metricSource != null && metricSource == MetricSource.TestingFramework) {
			String adapterProj = getAdapterProject(project, metricSource);
			TestLinkMetricMeasurement testRes = 
					testlinkDataService.getLatestMeasurementByProjectAndMetric(adapterProj, metricType);
			if (testRes != null) {
				storedMetricValue = Float.valueOf(testRes.getValue());
			} 		

		} else if (metricSource != null && metricSource == MetricSource.StaticAnalysis) {
			String adapterProj = getAdapterProject(project, metricSource);			
			SonarMetricMeasurement sonarRes = 
					sonarDataService.getLatestMeasurementByProjectAndMetric(adapterProj, metricType);
			if (sonarRes != null) {
				storedMetricValue = Float.valueOf(sonarRes.getValue());
			} 

		} else if (metricSource != null && metricSource == MetricSource.CubeAnalysis){
			String adapterProj = getAdapterProject(project, metricSource);
			CubesMetricMeasurement cubesRes = 
					cubesDataService.getLatestMeasurementByProjectAndMetric(adapterProj, metricType);
			if (cubesRes != null) {
				storedMetricValue = Float.valueOf(cubesRes.getValue());
			}
			
		} else if (metricSource != null && metricSource == MetricSource.VersionControl) {
			// TODO
			
			
		} else if (metricSource != null && metricSource == MetricSource.ContinuousIntegration) {
			String adapterProj = getAdapterProject(project, metricSource);
			JenkinsMetricMeasurement jenkinsRes = 
					jenkinsDataService.getLatestMeasurementByProjectAndMetric(adapterProj, metricType);
			if (jenkinsRes != null) {
				
//				storedMetricValue = Float.valueOf(jenkinsRes.getValue());
			}
			
		}
		


		return storedMetricValue;
	}

	
	/**
	 * 
	 * @param proj
	 * @param metricSource
	 * @return
	 */
	private String getAdapterProject(Project proj, MetricSource metricSource) {
		String adapterProject = null;

		// If a suitable adapter is found, get the internal project configured 
		AdapterSettings adapter = adapterSettingsService.getAdapterByProjectAndMetricSource(proj, metricSource);
		if (adapter != null) {
			adapterProject = adapter.getAdapterProject();
		}

		return adapterProject;
	}
	
}
