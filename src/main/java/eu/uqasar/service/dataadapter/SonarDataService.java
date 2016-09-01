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
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;

import com.google.gson.Gson;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.model.measure.SonarMetricMeasurement;
import eu.uqasar.model.measure.SonarMetricMeasurement_;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.qualifier.Conversational;
import eu.uqasar.service.AbstractService;
import eu.uqasar.sonar.adapter.SonarAdapter;
import eu.uqasar.util.UQasarUtil;

@Stateless
@Conversational
public class SonarDataService extends AbstractService<SonarMetricMeasurement> {

	private static final long serialVersionUID = 5133322819970758382L;

	@Inject
	private AdapterSettingsService settingsService;
	
	public SonarDataService() {
		super(SonarMetricMeasurement.class);
	}

	/**
	 * 
	 * @return
	 */
    private List<SonarMetricMeasurement> getAllSonarMetricObjects() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SonarMetricMeasurement> query = 
				cb.createQuery(SonarMetricMeasurement.class);
		query.from(SonarMetricMeasurement.class);
        return em.createQuery(query).getResultList();
	}	

	/**
	 * 
	 * @param adapter
	 * @param first
	 * @param count
	 * @return
	 */
	public List<SonarMetricMeasurement> getAllByAdapter(int first, int count, AdapterSettings adapter) {
		logger.info("Get measurements for adapter: " +adapter);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SonarMetricMeasurement> query = cb.createQuery(SonarMetricMeasurement.class);
		Root<SonarMetricMeasurement> root = query.from(SonarMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(SonarMetricMeasurement_.adapter), adapter);
		query.orderBy(cb.asc(root.get(SonarMetricMeasurement_.sonarKey)));
		query.where(condition);
		return em.createQuery(query).setFirstResult(first).setMaxResults(count).getResultList();
	}

	/**
	 * 
	 * @param adapter
	 * @return
	 */
	public List<SonarMetricMeasurement> getAllByAdapter(AdapterSettings adapter) {
		logger.info("Get measurements for adapter: " +adapter);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SonarMetricMeasurement> query = cb.createQuery(SonarMetricMeasurement.class);
		Root<SonarMetricMeasurement> root = query.from(SonarMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(SonarMetricMeasurement_.adapter), adapter);
		query.where(condition);
		return em.createQuery(query).getResultList();
	}

	/**
	 * Count the measurements belonging to an adapter
	 * @param adapter
	 * @return
	 */
	public int countAllByAdapter(AdapterSettings adapter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SonarMetricMeasurement> query = cb.createQuery(SonarMetricMeasurement.class);
		Root<SonarMetricMeasurement> root = query.from(SonarMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(SonarMetricMeasurement_.adapter), adapter);
		query.where(condition);		
		return em.createQuery(query).getResultList().size();
	}
	
	
	
	/**
	 *
     */
	public void delete(Collection<SonarMetricMeasurement> metrics) {
		for (SonarMetricMeasurement m : metrics) {
			delete(m);
		}
	}

	/**
	 * 
	 * @param first
	 * @param count
	 * @return
	 */
	public List<SonarMetricMeasurement> getAllByAscendingName(int first, 
			int count) {
		logger.infof("loading all SonarMetricMeasurement ordered by " 
				+"ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SonarMetricMeasurement> criteria = 
				cb.createQuery(SonarMetricMeasurement.class);
		Root<SonarMetricMeasurement> root = 
				criteria.from(SonarMetricMeasurement.class);
		criteria.orderBy(cb.asc(root.get(SonarMetricMeasurement_.name)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}	

	/**
	 * 
	 * @param project
	 * @return
	 */
    private List<SonarMetricMeasurement> getMeasurementsForProject(
            String project) {
		logger.info("Obtaining measurements for the project: " +project);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SonarMetricMeasurement> query = 
				cb.createQuery(SonarMetricMeasurement.class);
		Root<SonarMetricMeasurement> root = 
				query.from(SonarMetricMeasurement.class);
		Predicate condition = cb.equal(
				root.get(SonarMetricMeasurement_.name), project);
		query.where(condition);
		query.orderBy(cb.desc(root.get(SonarMetricMeasurement_.timeStamp)));
		return em.createQuery(query).getResultList();
	}


	/**
	 * 
	 * @param project
	 * @param date
	 * @return
	 */
    private List<SonarMetricMeasurement> getMeasurementsForProjectByDate(
            String project, Date date) {
		logger.info("Obtaining measurements for the project: " +project);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SonarMetricMeasurement> query = 
				cb.createQuery(SonarMetricMeasurement.class);
		Root<SonarMetricMeasurement> root = 
				query.from(SonarMetricMeasurement.class);
		Predicate condition = cb.equal(
				root.get(SonarMetricMeasurement_.name), project);
		Predicate condition2 = cb.equal(
				root.get(SonarMetricMeasurement_.timeStamp), date);
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(SonarMetricMeasurement_.timeStamp)));
		return em.createQuery(query).getResultList();
	}
	
	/**
	 * 
	 * @param project
	 * @return
	 */
	public List<SonarMetricMeasurement> getMeasurementsForProjectByPeriod(String project, String period) {
		logger.info("Obtaining measurements for the project: " +project +" within the: " + period);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SonarMetricMeasurement> query = 
				cb.createQuery(SonarMetricMeasurement.class);
		Root<SonarMetricMeasurement> root = 
				query.from(SonarMetricMeasurement.class);
		
		Predicate condition = cb.equal(
				root.get(SonarMetricMeasurement_.name), project);
		
		Date from = getDateForPeriod(period);		
		Date to = DateTime.now().toDate();
		Predicate condition2 = cb.between(root.get(SonarMetricMeasurement_.timeStamp), from, to);
		
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(SonarMetricMeasurement_.timeStamp)));
		return em.createQuery(query).getResultList();
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
	public SonarMetricMeasurement getLatestMeasurementByProjectAndMetric(
			String project, String metric) {
		logger.info("Obtaining measurements for the project: " +project 
				+" and metric: " +metric);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SonarMetricMeasurement> query = 
				cb.createQuery(SonarMetricMeasurement.class);
		Root<SonarMetricMeasurement> root = 
				query.from(SonarMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(SonarMetricMeasurement_.name), 
				project);
		Predicate condition2 = cb.equal(root.get(
				SonarMetricMeasurement_.sonarMetric), metric);
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(SonarMetricMeasurement_.timeStamp)));
		List<SonarMetricMeasurement> measurements = 
				em.createQuery(query).getResultList();
		SonarMetricMeasurement measurement = null;
		if (measurements != null && measurements.size() > 0 
				&& measurements.get(0) != null) {
			measurement = measurements.get(0); // Get the most "fresh" result 
		}
		return measurement;
	}
	
	
	/**
     * 
     * @param project
     * @return
     */
    public List<SonarMetricMeasurement> getLatestMeasurementByProject(
            String project) {
        List<SonarMetricMeasurement> resultList = getMeasurementsForProject(project);
        Date newestDate = null;
        for(SonarMetricMeasurement smm : resultList){
            if (newestDate == null || smm.getTimeStamp().compareTo(newestDate) > 0){
                newestDate = smm.getTimeStamp();
            }  
        }
        return getMeasurementsForProjectByDate(project,newestDate);
    }

	
	/**
	 * Get a list of available Sonar project names 
	 * @return
	 */
	public List<String> getSonarProjects() {
		logger.info("Getting Sonar projects...");
		List<SonarMetricMeasurement> measurements = getAllSonarMetricObjects();
		List<String> projects = new ArrayList<>();
		for (SonarMetricMeasurement sonarMetricMeasurement : measurements) {
			String project = sonarMetricMeasurement.getName();
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
		CriteriaQuery<SonarMetricMeasurement> query = 
				cb.createQuery(SonarMetricMeasurement.class);
		Root<SonarMetricMeasurement> root = 
				query.from(SonarMetricMeasurement.class);
		query.select(root);
		query.orderBy(cb.desc(root.get(SonarMetricMeasurement_.timeStamp)));
		Date latest;
		try {
			SonarMetricMeasurement m = em.createQuery(query).setMaxResults(1).getSingleResult(); 
			latest = m.getTimeStamp();
		} catch (NoResultException nre) {
			latest = null;
		}
		
		return latest;
	}
	
	
	/**
	 * Fetch an updated set of adapter data.
	 * @param adapterSettings Settings of the adapter to be used
	 * @throws uQasarException
	 */
	public void updateAdapterData(AdapterSettings adapterSettings) 
			throws uQasarException {		
		Date snapshotTimeStamp = new Date();
		logger.info("Get measurements for all Sonar metrics at " 
		+snapshotTimeStamp +adapterSettings.toString());
		SonarAdapter adapter = new SonarAdapter(); 
		// Timestamp for the measurement snapshot
		String boundSystemURL = adapterSettings.getUrl();
		String username = null;
		String password = null;
		String credentials = ""; 
		if (adapterSettings.getAdapterUsername() != null) {
			username = adapterSettings.getAdapterUsername();
		} 
		if (adapterSettings.getAdapterPassword() != null) {
			password = adapterSettings.getAdapterPassword();
		}
		if (username != null && password != null) {
			if (!username.isEmpty() && !password.isEmpty()) {
				credentials = username +":" +password;
			}
		}
		
		// Iterate the metric names and fetch results for each of these
		// create a persistent entity for each measurement
		for (String metric : UQasarUtil.getSonarMetricNames()) {
			List<Measurement> metricMeasurements = 
					adapter.query(boundSystemURL, credentials, metric);
			// Iterate the results for a metric
			for (Measurement m : metricMeasurements) {
				// Read the measurement to a JSON array and check whether it 
				// contains results for the desired project
				if (m.getMeasurement() != null) {
//					logger.info("measurement: " +m.getMeasurement());
					String json = m.getMeasurement();
					Gson gson = new Gson();
					SonarMetricMeasurement[] measurement = gson.fromJson(json, 
							SonarMetricMeasurement[].class);
                    for (SonarMetricMeasurement aMeasurement : measurement) {
//						logger.info(measurement[i].toString());
                        // Add a timestamp and metric name to the object´
                        aMeasurement.setTimeStamp(snapshotTimeStamp);
                        aMeasurement.setSonarMetric(metric);
                        aMeasurement.setProject(adapterSettings.getProject());
                        aMeasurement.setAdapter(adapterSettings);
                        // Create an entity
                        create(aMeasurement);
                    }
				}					
			}
		}
		
		adapterSettings.setLatestUpdate(snapshotTimeStamp);
		settingsService.update(adapterSettings);		

	}
}

