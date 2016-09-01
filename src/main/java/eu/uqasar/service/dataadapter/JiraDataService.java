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


import java.io.IOException;
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

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;

import com.google.gson.Gson;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.jira.adapter.JiraAdapter;
import eu.uqasar.model.measure.JiraMetricMeasurement;
import eu.uqasar.model.measure.JiraMetricMeasurement_;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;
import eu.uqasar.qualifier.Conversational;
import eu.uqasar.service.AbstractService;
import eu.uqasar.util.UQasarUtil;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;


@Stateless
@Conversational
public class JiraDataService extends AbstractService<JiraMetricMeasurement> {

	private static final long serialVersionUID = 5133322819970758382L;

	@Inject
	AdapterSettingsService settingsService;

	public JiraDataService() {
		super(JiraMetricMeasurement.class);
	}

	/**
	 * 
	 * @return
	 */
	public List<JiraMetricMeasurement> getAllJiraMetricObjects() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JiraMetricMeasurement> query = 
				cb.createQuery(JiraMetricMeasurement.class);
		query.from(JiraMetricMeasurement.class);
        return em.createQuery(query).getResultList();
	}	

	/**
	 * 
	 * @param first
	 * @param count
	 * @param adapter
	 * @return
	 */
	public List<JiraMetricMeasurement> getAllByAdapter(int first, int count, AdapterSettings adapter) {
		logger.info("Get measurements for adapter: " +adapter);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JiraMetricMeasurement> query = cb.createQuery(JiraMetricMeasurement.class);
		Root<JiraMetricMeasurement> root = query.from(JiraMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(JiraMetricMeasurement_.adapter), adapter);
		query.orderBy(cb.asc(root.get(JiraMetricMeasurement_.jiraKey)));
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
		CriteriaQuery<JiraMetricMeasurement> query = cb.createQuery(JiraMetricMeasurement.class);
		Root<JiraMetricMeasurement> root = query.from(JiraMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(JiraMetricMeasurement_.adapter), adapter);
		query.where(condition);		
		return em.createQuery(query).getResultList().size();
	}	


	/**
	 * 
	 * @param adapter
	 * @return
	 */
	public List<JiraMetricMeasurement> getAllByAdapter(AdapterSettings adapter) {
		logger.info("Get measurements for adapter: " +adapter);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JiraMetricMeasurement> query = cb.createQuery(JiraMetricMeasurement.class);
		Root<JiraMetricMeasurement> root = query.from(JiraMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(JiraMetricMeasurement_.adapter), adapter);
		query.where(condition);
		return em.createQuery(query).getResultList();
	}


	/**
	 *
     */
	public void delete(Collection<JiraMetricMeasurement> metrics) {
		for (JiraMetricMeasurement m : metrics) {
			delete(m);
		}
	}

	/**
	 * 
	 * @param first
	 * @param count
	 * @return
	 */
	public List<JiraMetricMeasurement> getAllByAscendingName(int first, int count) {
		logger.infof("loading all JiraMetricMeasurement ordered by ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JiraMetricMeasurement> criteria = 
				cb.createQuery(JiraMetricMeasurement.class);
		Root<JiraMetricMeasurement> root = criteria.from(JiraMetricMeasurement.class);
		criteria.orderBy(cb.asc(root.get(JiraMetricMeasurement_.self)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}	


	/**
	 * Get an updated set of adapter data. In addition to the basic data set, get the payload stored to a JSON string
	 * @param settings Settings for the adapter used for fetching data
	 * @throws uQasarException
	 */
	public void updateAdapterData(AdapterSettings settings) throws uQasarException {

		String credentials = settings.getAdapterUsername() +":" +settings.getAdapterPassword();
		String boundSystemURL = settings.getUrl();

		JiraAdapter adapter = new JiraAdapter();
		List<String> metricNames = UQasarUtil.getJiraMetricNames();
		Date snapshotTimeStamp = new Date();

		// Init resty for fetching the JSON content
		Resty resty = new Resty();
		// encoding byte array into base64
		byte[] encoded = Base64.encodeBase64(credentials.getBytes());     
		String encodedString = new String(encoded);
		resty.alwaysSend("Authorization", "Basic " + encodedString);

		// Iterate the metric names and fetch results for each of these
		// create a persistent entity for each measurement 
		for (String metric : metricNames) {
			logger.info("Obtaining JIRA measurements for metric: "+metric);
			List<Measurement> metricMeasurements = adapter.query(boundSystemURL, credentials, metric);
			for (Measurement measurement : metricMeasurements) {
				String json = measurement.getMeasurement();
				Gson gson = new Gson();
				JiraMetricMeasurement[] jiraMetricMeasurement = gson.fromJson(json, 
						JiraMetricMeasurement[].class);
                for (JiraMetricMeasurement aJiraMetricMeasurement : jiraMetricMeasurement) {
                    // Add a timestamp and metric name to the object
                    aJiraMetricMeasurement.setTimeStamp(snapshotTimeStamp);
                    aJiraMetricMeasurement.setJiraMetric(metric);
                    aJiraMetricMeasurement.setProject(settings.getProject());
                    aJiraMetricMeasurement.setAdapter(settings);

                    // Get the url from the measurement for fetching the JSON content
                    String url = aJiraMetricMeasurement.getSelf();
                    String jsonContent = "";
                    try {
                        JSONResource res = resty.json(url);
                        JSONObject jobj = res.toObject();
                        //						logger.info(jobj.toString());
                        jsonContent = jobj.toString();
                        //						logger.info("JSON Content: " +jsonContent);
                        aJiraMetricMeasurement.setJsonContent(jsonContent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // persist the measurement
                    create(aJiraMetricMeasurement);
                }
			}
		}
		// Set timestamp to the adapter settings
		settings.setLatestUpdate(snapshotTimeStamp);
		settingsService.update(settings);				
	}

	/**
	 * 
	 * @param metric
	 * @return
	 * @throws uQasarException
	 */
	public List<JiraMetricMeasurement> getMeasurementsForMetric(String metric) 
			throws uQasarException {
		logger.info("Get measurements for metric: " +metric);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JiraMetricMeasurement> query = cb.createQuery(JiraMetricMeasurement.class);
		Root<JiraMetricMeasurement> root = query.from(JiraMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(JiraMetricMeasurement_.jiraMetric), metric);
		query.where(condition);
		query.orderBy(cb.desc(root.get(JiraMetricMeasurement_.timeStamp)));
		return em.createQuery(query).getResultList();
	}

	/**
	 * Get the latest date of measurement snapshots
	 * @return
	 */
	public Date getLatestDate() {
		logger.info("Get the latest date from JIRA measurements...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JiraMetricMeasurement> query = cb.createQuery(JiraMetricMeasurement.class);
		Root<JiraMetricMeasurement> root = query.from(JiraMetricMeasurement.class);
		query.select(root);
		query.orderBy(cb.desc(root.get(JiraMetricMeasurement_.timeStamp)));
		Date latest;
		try {
			JiraMetricMeasurement m = em.createQuery(query).setMaxResults(1).getSingleResult();
			latest = m.getTimeStamp();
		} catch (NoResultException nre) {
			latest = null;
		}
		return latest;
	}

	/**
	 * 
	 * @param metric
	 * @return
	 * @throws uQasarException
	 */
    private List<JiraMetricMeasurement> getMeasurementsByMetricAndDate(String metric, Date date)
			throws uQasarException {
		logger.info("Get measurements for metric: " +metric);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JiraMetricMeasurement> query = cb.createQuery(JiraMetricMeasurement.class);
		Root<JiraMetricMeasurement> root = query.from(JiraMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(JiraMetricMeasurement_.jiraMetric), metric);
		Predicate condition2 = cb.equal(root.get(JiraMetricMeasurement_.timeStamp), date);
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(JiraMetricMeasurement_.timeStamp)));
		return em.createQuery(query).getResultList();
	}


	/**
	 * 
	 * @param metric
	 * @param date
	 * @return
	 * @throws uQasarException
	 */
	public int countMeasurementsByMetricAndDate(String metric, Date date) 
			throws uQasarException {
		logger.info("Count measurements for metric: " +metric + " and date: " +date);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JiraMetricMeasurement> query = cb.createQuery(JiraMetricMeasurement.class);
		Root<JiraMetricMeasurement> root = query.from(JiraMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(JiraMetricMeasurement_.jiraMetric), metric);
		Predicate condition2 = cb.equal(root.get(JiraMetricMeasurement_.timeStamp), date);
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(JiraMetricMeasurement_.timeStamp)));
		Integer res = em.createQuery(query).getResultList().size();
		logger.info("Results' count: " +res);
		return res;
	}

	/**
	 * 
	 * @param metric
	 * @return
	 * @throws uQasarException
	 */
	public List<JiraMetricMeasurement> getMeasurementsPerProjectByMetricWithinPeriod(Project project, String metric, String period) 
			throws uQasarException {

		List<JiraMetricMeasurement> measurements = new ArrayList<>();
		if (project != null && metric != null && period != null) {
			logger.info("Count measurements for project + " + project.getAbbreviatedName() +" and metric: " +metric);

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<JiraMetricMeasurement> query = cb.createQuery(JiraMetricMeasurement.class);
			Root<JiraMetricMeasurement> root = query.from(JiraMetricMeasurement.class);

			Predicate condition1 = cb.equal(root.get(JiraMetricMeasurement_.jiraMetric), metric);
			Predicate condition2 = cb.equal(root.get(JiraMetricMeasurement_.project), project);

			Date from = getDateForPeriod(period);		
			Date to = DateTime.now().toDate();
			Predicate condition3 = cb.between(root.get(JiraMetricMeasurement_.timeStamp), from, to);
			//		System.out.println("from:"+from);
			//		System.out.println("to:"+to);

			Predicate condition4 = cb.and(condition1, condition2, condition3);
			query.where(condition4);
			query.orderBy(cb.desc(root.get(JiraMetricMeasurement_.timeStamp)));
			measurements = em.createQuery(query).getResultList();
		}

		return measurements;
	}


	/** 
	 * @param metric
	 * @param project
	 * @return
	 * @throws uQasarException
	 */
	public List<JiraMetricMeasurement> getMeasurementsPerProjectByMetricWithLatestDate(Project project, String metric) 
			throws uQasarException {
		logger.info("Count measurements for project + " + project.getAbbreviatedName() +" and metric: " +metric + "with latest date");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JiraMetricMeasurement> query = cb.createQuery(JiraMetricMeasurement.class);
		Root<JiraMetricMeasurement> root = query.from(JiraMetricMeasurement.class);

		Predicate condition1 = cb.equal(root.get(JiraMetricMeasurement_.jiraMetric), metric);
		Predicate condition2 = cb.equal(root.get(JiraMetricMeasurement_.project), project);


		Predicate condition4 = cb.and(condition1, condition2);
		query.where(condition4);
		query.orderBy(cb.desc(root.get(JiraMetricMeasurement_.timeStamp)));
		List<JiraMetricMeasurement> returnResults = em.createQuery(query).getResultList();

		Date newDate = null;
		for (JiraMetricMeasurement jMM : returnResults ){
			if (newDate == null || jMM.getTimeStamp().compareTo(newDate) > 0){
				newDate = jMM.getTimeStamp();
			} 

		}

        return getMeasurementsByMetricAndDate(metric,newDate);
	}


	/**
	 * 
	 * @param timeInterval
	 * @return
	 */
	private Date getDateForPeriod(String timeInterval) {
		DateTime now = DateTime.now();
		Date date;

        switch (timeInterval) {
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
	 * @param metric
	 * @return
	 * @throws uQasarException
	 */
	public int countMeasurementsPerProjectByMetricWithinPeriod(Project project, String metric, String period) 
			throws uQasarException {
		logger.info("Count measurements for metric: " +metric);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JiraMetricMeasurement> query = cb.createQuery(JiraMetricMeasurement.class);
		Root<JiraMetricMeasurement> root = query.from(JiraMetricMeasurement.class);
		Predicate condition1 = cb.equal(root.get(JiraMetricMeasurement_.jiraMetric), metric);
		Predicate condition2 = cb.equal(root.get(JiraMetricMeasurement_.project), project);

		Date from = getDateForPeriod(period);		
		Date to = DateTime.now().toDate();
		Predicate condition3 = cb.between(root.get(JiraMetricMeasurement_.timeStamp), from, to);
		Predicate condition4 = cb.and(condition1, condition2, condition3);
		query.where(condition4);
		query.orderBy(cb.desc(root.get(JiraMetricMeasurement_.timeStamp)));
		Integer res = em.createQuery(query).getResultList().size();
		logger.info("Results' count: " +res);
		return res;
	}

	/**
	 * 
	 * @param metric
	 * @return
	 * @throws uQasarException
	 */
	public int countMeasurementsPerProjectByMetricWithLatestDate(Project project, String metric) 
			throws uQasarException {
		Integer res = getMeasurementsPerProjectByMetricWithLatestDate(project,metric).size();
		logger.info("Results' count: " +res);
		return res;
	}
}
