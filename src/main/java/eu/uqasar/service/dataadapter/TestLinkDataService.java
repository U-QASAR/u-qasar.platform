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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.model.measure.TestLinkMetricMeasurement;
import eu.uqasar.model.measure.TestLinkMetricMeasurement_;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.qualifier.Conversational;
import eu.uqasar.service.AbstractService;
import eu.uqasar.testlink.adapter.TestLinkAdapter;
import eu.uqasar.util.UQasarUtil;

@Stateless
@Conversational
public class TestLinkDataService extends AbstractService<TestLinkMetricMeasurement> {

	private static final long serialVersionUID = 5133322819970758382L;

	@Inject
	private AdapterSettingsService settingsService;

	public TestLinkDataService() {
		super(TestLinkMetricMeasurement.class);
	}

	/**
	 * 
	 * @return
	 */
    private List<TestLinkMetricMeasurement> getAllTestLinkMetricObjects() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestLinkMetricMeasurement> query = 
				cb.createQuery(TestLinkMetricMeasurement.class);
		query.from(TestLinkMetricMeasurement.class);
        return em.createQuery(query).getResultList();
	}	

	/**
	 * 
	 * @param adapter
	 * @return
	 */
	public List<TestLinkMetricMeasurement> getAllByAdapter(AdapterSettings adapter) {
		logger.info("Get measurements for adapter: " +adapter);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestLinkMetricMeasurement> query = cb.createQuery(TestLinkMetricMeasurement.class);
		Root<TestLinkMetricMeasurement> root = query.from(TestLinkMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(TestLinkMetricMeasurement_.adapter), adapter);
		query.where(condition);
		return em.createQuery(query).getResultList();
	}

	/**
	 * 
	 * @param first
	 * @param count
	 * @param adapter
	 * @return
	 */
	public List<TestLinkMetricMeasurement> getAllByAdapter(int first, int count, AdapterSettings adapter) {
		logger.info("Get measurements for adapter: " +adapter);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestLinkMetricMeasurement> query = cb.createQuery(TestLinkMetricMeasurement.class);
		Root<TestLinkMetricMeasurement> root = query.from(TestLinkMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(TestLinkMetricMeasurement_.adapter), adapter);
		query.orderBy(cb.asc(root.get(TestLinkMetricMeasurement_.name)));
		query.where(condition);
		return em.createQuery(query).setFirstResult(first).setMaxResults(count).getResultList();
	}

	/**
	 * 
	 * @param adapter
	 * @return
	 */
	public int countAllByAdapter(AdapterSettings adapter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestLinkMetricMeasurement> query = cb.createQuery(TestLinkMetricMeasurement.class);
		Root<TestLinkMetricMeasurement> root = query.from(TestLinkMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(TestLinkMetricMeasurement_.adapter), adapter);
		query.where(condition);		
		return em.createQuery(query).getResultList().size();
	}	


	/**
	 *
     */
	public void delete(Collection<TestLinkMetricMeasurement> metrics) {
		for (TestLinkMetricMeasurement m : metrics) {
			delete(m);
		}
	}

	/**
	 * 
	 * @param first
	 * @param count
	 * @return
	 */
	public List<TestLinkMetricMeasurement> getAllByAscendingName(int first, int count) {
		logger.infof("loading all TestLinkMetricMeasurement ordered by ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestLinkMetricMeasurement> criteria = 
				cb.createQuery(TestLinkMetricMeasurement.class);
		Root<TestLinkMetricMeasurement> root = criteria.from(TestLinkMetricMeasurement.class);
		criteria.orderBy(cb.asc(root.get(TestLinkMetricMeasurement_.name)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}	

	/**
	 * 
	 * @param project
	 * @return
	 */
    private List<TestLinkMetricMeasurement> getMeasurementsForProject(String project) {
		logger.info("Obtaining measurements for the project: " +project);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestLinkMetricMeasurement> query = cb.createQuery(TestLinkMetricMeasurement.class);
		Root<TestLinkMetricMeasurement> root = query.from(TestLinkMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(TestLinkMetricMeasurement_.name), project);
		query.where(condition);
		query.orderBy(cb.desc(root.get(TestLinkMetricMeasurement_.timeStamp)));
		return em.createQuery(query).getResultList();
	}


	/**
	 * 
	 * @param project
	 * @param date
	 * @return
	 */
    private List<TestLinkMetricMeasurement> getMeasurementsForProjectByDate(String project, Date date) {
		logger.info("Obtaining measurements for the project: " +project);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestLinkMetricMeasurement> query = cb.createQuery(TestLinkMetricMeasurement.class);
		Root<TestLinkMetricMeasurement> root = query.from(TestLinkMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(TestLinkMetricMeasurement_.name), project);
		Predicate condition2 = cb.equal(root.get(TestLinkMetricMeasurement_.timeStamp), date);
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(TestLinkMetricMeasurement_.timeStamp)));
		return em.createQuery(query).getResultList();
	}

	/**
	 * 
	 * @param project
	 * @return
	 */
	public List<TestLinkMetricMeasurement> getMeasurementsForProjectByPeriod(String project, String period) {
		logger.info("Obtaining measurements for the project: " +project +" within the: " + period);
		List<TestLinkMetricMeasurement> measurements = new ArrayList<>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestLinkMetricMeasurement> query = 
				cb.createQuery(TestLinkMetricMeasurement.class);
		Root<TestLinkMetricMeasurement> root = 
				query.from(TestLinkMetricMeasurement.class);

		Predicate condition = cb.equal(
				root.get(TestLinkMetricMeasurement_.name), project);

		Date from = getDateForPeriod(period);		
		Date to = DateTime.now().toDate();
		Predicate condition2 = cb.between(root.get(TestLinkMetricMeasurement_.timeStamp), from, to);

		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(TestLinkMetricMeasurement_.timeStamp)));
		if (em.createQuery(query).getResultList() != null && !em.createQuery(query).getResultList().isEmpty()) {
			measurements = em.createQuery(query).getResultList();
		}
		return measurements;
	}

	public List<TestLinkMetricMeasurement> getMeasurementsForProjectByLatestDate(String project) {
		List<TestLinkMetricMeasurement> results = getMeasurementsForProject(project);

		Date newDate = null;
		for(TestLinkMetricMeasurement tMM : results){

			if (newDate == null ||  tMM.getTimeStamp().compareTo(newDate) > 0){
				newDate = tMM.getTimeStamp();
			}  
		}

        return getMeasurementsForProjectByDate(project,newDate);
	}


	/**
	 * 
	 * @param period
	 * @return
	 */
	private Date getDateForPeriod(String period) {
		DateTime now = DateTime.now();
		Date date;

        switch (period) {
            case "Last Year":
                date = now.minusMonths(12).toDate();
                break;
            case "Last 6 Months":
                date = now.minusMonths(6).toDate();
                break;
            case "Last Month":
                date = now.minusMonths(1).toDate();
                break;
            case "Last Week":
                date = now.minusWeeks(1).toDate();
                break;
            default:
                date = now.minusYears(5).toDate();
                break;
        }
		//System.out.println("datedatedate:"+date);

		return date;
	}

	/**
	 * 
	 * @param project
	 * @return
	 */
	public TestLinkMetricMeasurement getLatestMeasurementByProjectAndMetric(String project, String metric) {
		logger.info("Obtaining measurements for the project: " +project +" and metric: " +metric);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestLinkMetricMeasurement> query = cb.createQuery(TestLinkMetricMeasurement.class);
		Root<TestLinkMetricMeasurement> root = query.from(TestLinkMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(TestLinkMetricMeasurement_.name), project);
		Predicate condition2 = cb.equal(root.get(TestLinkMetricMeasurement_.testLinkMetric), metric);
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(TestLinkMetricMeasurement_.timeStamp)));
		List<TestLinkMetricMeasurement> measurements = em.createQuery(query).getResultList();
		TestLinkMetricMeasurement measurement = null;
		if (measurements != null && measurements.size() > 0 && measurements.get(0) != null) {
			measurement = measurements.get(0); // Get the most "fresh" result 
		}
		return measurement;
	}


	/**
	 * Get a list of available TestLink project names 
	 * @return
	 */
	public List<String> getTestLinkProjects() {
		logger.info("Getting TestLink projects...");
		List<TestLinkMetricMeasurement> measurements = getAllTestLinkMetricObjects();
		List<String> projects = new ArrayList<>();
		for (TestLinkMetricMeasurement testLinkMetricMeasurement : measurements) {
			String project = testLinkMetricMeasurement.getName();
			if (!projects.contains(project)) {
				projects.add(project);
			}
		}

		return projects;
	}

	/**
	 * Get the latest date of measurement snapshots
	 * @return
	 */
	public Date getLatestDate() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestLinkMetricMeasurement> query = cb.createQuery(TestLinkMetricMeasurement.class);
		Root<TestLinkMetricMeasurement> root = query.from(TestLinkMetricMeasurement.class);
		query.select(root);
		query.orderBy(cb.desc(root.get(TestLinkMetricMeasurement_.timeStamp)));
		Date latest;
		try {
			TestLinkMetricMeasurement m = em.createQuery(query).setMaxResults(1).getSingleResult();
			latest = m.getTimeStamp();			
		} catch (NoResultException nre) {
			latest = null;
		}
		return latest;
	}

	/**
	 *
	 * @param adapterSettings Adaptersettings for the adapter used for updating the data
	 * @throws eu.uqasar.adapter.exception.uQasarException
	 */
	public void updateAdapterData(AdapterSettings adapterSettings) throws uQasarException {
		logger.info("Get measurements for all TestLink metrics " +new Date());

		// Timestamp for the measurement snapshot
		Date snapshotTimeStamp = new Date();

		String boundSystemURL = adapterSettings.getUrl();
		String credentials = adapterSettings.getAdapterPassword();
		TestLinkAdapter adapter = new TestLinkAdapter(boundSystemURL, credentials);

		String testProjectName = adapterSettings.getAdapterProject();
		String testPlanName = adapterSettings.getAdapterTestPlan();

		for (String metric : UQasarUtil.getTestLinkMetricNames()) {

			logger.info("String metric "+ metric);

			Map<String, String> params = new HashMap<>();
			params.put("testProjectName", testProjectName);
			params.put("testPlanName", testPlanName);

			List<Measurement> metricMeasurements = adapter.getMeasurement(metric, params);
			logger.info("metricMeasurments : "+ metricMeasurements.get(0));
			// Iterate the results for a metric
			for (Measurement m : metricMeasurements) {
				// Read the measurement to a JSON array and check whether it contains
				// results for the desired project
				if (m.getMeasurement() != null) {
					logger.info("Testlink measurement: " +m.getMeasurement());
					TestLinkMetricMeasurement measurement = new TestLinkMetricMeasurement();
					measurement.setName(testProjectName);
					measurement.setValue(m.getMeasurement());
					measurement.setTimeStamp(snapshotTimeStamp);
					measurement.setTestLinkMetric(metric);
					measurement.setProject(adapterSettings.getProject());
					measurement.setAdapter(adapterSettings);
					create(measurement);
				}
			}
			adapterSettings.setLatestUpdate(snapshotTimeStamp);
			settingsService.update(adapterSettings);		
		}
	}
}

