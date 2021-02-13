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


import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.http.Http;

import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.settings.adapter.AdapterSettings_;
import eu.uqasar.model.tree.Project;
import eu.uqasar.qualifier.Conversational;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.PlatformSettingsService;
import eu.uqasar.util.UQasarUtil;

/**
 * Service entity for obtaining adapter settings
 *
 */
@Stateless
@Conversational
public class AdapterSettingsService extends AbstractService<AdapterSettings> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1689794621305780967L;

	@Inject
	@Http
	private ConversationContext context;

	@Inject
	@Bound
	private BoundConversationContext boundContext;

	@Resource
	private TimerService timerService;
	
	@Inject
	private PlatformSettingsService platformSettingsService;

	private String adapterDataUpdateInterval;

	private int updateInterval = 1440; // update interval in minutes

	private final String ADAPTERDATA_TIMER = "Adapter-data-update-timer";

	public AdapterSettingsService() {
		super(AdapterSettings.class);
	}

	public List<AdapterSettings> getAllByAscendingName(int first, int count) {
		logger.infof("loading all AdapterSettings ordered by ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AdapterSettings> criteria = cb.createQuery(AdapterSettings.class);
		Root<AdapterSettings> root = criteria.from(AdapterSettings.class);
		criteria.orderBy(cb.asc(root.get(AdapterSettings_.name)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}

	/**
	 * Get an adapter belonging to a specific project by the metric source
	 * @param project
	 * @param source
	 * @return
	 */
	public AdapterSettings getAdapterByProjectAndMetricSource(Project project, MetricSource source) {
		AdapterSettings settings = null;
		logger.infof("loading AdapterSettings belonging to a specific project and metric source ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AdapterSettings> query = cb.createQuery(AdapterSettings.class);
		Root<AdapterSettings> root = query.from(AdapterSettings.class);
		Predicate condition = cb.equal(root.get(AdapterSettings_.project), project);
		Predicate condition2 = cb.equal(root.get(AdapterSettings_.metricSource), source);
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);        
		if (!em.createQuery(query).getResultList().isEmpty()) {
			settings = em.createQuery(query).getResultList().get(0);
		}
		return settings;
	}

	/**
	 * Initialize the timer for taking care of data updates
	 */
	public void initAdapterDataTimer() {

		adapterDataUpdateInterval = platformSettingsService.getValueByKey("adapterdata_update_interval");
		System.out.println("adapterdata_update_interval: " +adapterDataUpdateInterval);
		
		// Attempt to read the value of update interval from the properties file
		if (adapterDataUpdateInterval != null && !adapterDataUpdateInterval.isEmpty()) {
			this.updateInterval = Integer.valueOf(adapterDataUpdateInterval);
		}
		
		System.out.println("initAdapterDataTimer() [" + new Date() + "]");
		// Cancel the existing timer if exists
		cancelTimer();
		// Convert the default value to ms
		Integer updateInterval = UQasarUtil.minsToMs(this.updateInterval);
		// Create a timer for executing data updates
		TimerConfig tc = new TimerConfig();
		tc.setPersistent(false);
		tc.setInfo(ADAPTERDATA_TIMER);
		// Do not start the timer immediately, but only after 60 s, 
		// to ensure the platform starts properly without an unnecessary delay 
		timerService.createIntervalTimer(60000, updateInterval, tc);
	}


	/**	
	 * Cancel an existing timer
	 */
    private void cancelTimer() {
		if (timerService.getTimers() != null && timerService.getTimers().size() > 0) {
			System.out.println("Cancelling U-QASAR adapter data update timer ");

			for (Timer timer : timerService.getTimers()) {
				String timerId  = timer.getInfo().toString();
				System.out.println("Existing timers:[TimerID: " +timerId +"]");
				if (timerId.equals(ADAPTERDATA_TIMER)) {
					timer.cancel();
				}
			}
		}
	}

	/**
	 * Get a new snapshot of adapter data
     */
	@Timeout
	public void updateAdapterData() {

		try {
			InitialContext ic = new InitialContext();
			AdapterSettingsService adapterSettingsService = (AdapterSettingsService) ic.lookup("java:module/AdapterSettingsService");

			List<AdapterSettings> adapters = adapterSettingsService.getAll();
			boolean updateTree = false;
			Set<Project> projectsToUpdate = new HashSet<>();
			for (AdapterSettings adapter : adapters) {
				if (adapter != null) {            
					if (adapter.getMetricSource() != null && adapter.getMetricSource() == MetricSource.IssueTracker) {
						JiraDataService jiraDataService = (JiraDataService) ic.lookup("java:module/JiraDataService");
						jiraDataService.updateAdapterData(adapter);
						updateTree = true;
						projectsToUpdate.add(adapter.getProject());
					} else if (adapter.getMetricSource() != null && adapter.getMetricSource() == MetricSource.StaticAnalysis) {
						SonarDataService sonarDataService = (SonarDataService) ic.lookup("java:module/SonarDataService");
						sonarDataService.updateAdapterData(adapter);
						updateTree = true;
						projectsToUpdate.add(adapter.getProject());
					} else if (adapter.getMetricSource() != null && adapter.getMetricSource() == MetricSource.TestingFramework) {
						TestLinkDataService testLinkDataService = (TestLinkDataService) ic.lookup("java:module/TestLinkDataService");
						testLinkDataService.updateAdapterData(adapter);
						updateTree = true;
						projectsToUpdate.add(adapter.getProject());
					} else if (adapter.getMetricSource() != null && adapter.getMetricSource() == MetricSource.CubeAnalysis) {
						CubesDataService cubesDataService = (CubesDataService) ic.lookup("java:module/CubesDataService");
						cubesDataService.updateAdapterData(adapter);
						updateTree = true;
						projectsToUpdate.add(adapter.getProject());
					} else if (adapter.getMetricSource() != null && adapter.getMetricSource() == MetricSource.ContinuousIntegration) {
						JenkinsDataService jenkinsDataService = (JenkinsDataService) ic.lookup("java:module/JenkinsDataService");
						jenkinsDataService.updateAdapterData(adapter);
						updateTree = true;
						projectsToUpdate.add(adapter.getProject());
					}
				}
			}

			// Only update project tree for the projects whose adapters were used to fetch data updates 
			if (updateTree) {
                for (Project projectToUpdate : projectsToUpdate) {
                    // Update the project tree
                    UQasarUtil.updateTree(projectToUpdate);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
