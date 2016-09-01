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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;

import us.monoid.json.JSONException;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import com.google.gson.Gson;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.adapter.model.uQasarMetric;
import eu.uqasar.cubes.adapter.CubesAdapter;
import eu.uqasar.model.measure.CubesMetricMeasurement;
import eu.uqasar.model.measure.CubesMetricMeasurement_;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;
import eu.uqasar.qualifier.Conversational;
import eu.uqasar.service.AbstractService;
import eu.uqasar.util.UQasarUtil;

/**
 * 
 */
@Stateless
@Conversational
public class CubesDataService extends AbstractService<CubesMetricMeasurement> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3413935749717004973L;
	
	@Inject
	AdapterSettingsService settingsService;

	// Counts the number of trials to connect to Rest service, and limits them to 10
	// TO BE removed after Cubes adapter integration
	private Integer counter = 0;
	private final Integer counterLimit = 10;

	public CubesDataService() {
		super(CubesMetricMeasurement.class);
	}

	/**
	 * 
	 * @return
	 */
	public List<CubesMetricMeasurement> getAllCubesMetricObjects() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> query = 
				cb.createQuery(CubesMetricMeasurement.class);
		query.from(CubesMetricMeasurement.class);
        return em.createQuery(query).getResultList();
	}	


	/**
	 * 
	 * @param adapter
	 * @return
	 */
	public List<CubesMetricMeasurement> getAllByAdapter(AdapterSettings adapter) {
		logger.info("Get measurements for adapter: " +adapter);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.adapter), adapter);
		query.where(condition);
		return em.createQuery(query).getResultList();
	}

	/**
	 *
     */
	public void delete(Collection<CubesMetricMeasurement> metrics) {
		for (CubesMetricMeasurement m : metrics) {
			delete(m);
		}
	}

	/**
	 * 
	 * @param first
	 * @param count
	 * @return
	 */
	public List<CubesMetricMeasurement> getAllByAscendingName(int first, int count) {
		logger.infof("loading all CubesMetricMeasurement ordered by ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> criteria = 
				cb.createQuery(CubesMetricMeasurement.class);
		Root<CubesMetricMeasurement> root = criteria.from(CubesMetricMeasurement.class);
		criteria.orderBy(cb.asc(root.get(CubesMetricMeasurement_.self)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}	


	/**
	 * 
	 * @param first
	 * @param count
	 * @param adapter
	 * @return
	 */
	public List<CubesMetricMeasurement> getAllByAdapter(int first, int count, AdapterSettings adapter) {
		logger.info("Get measurements for adapter: " +adapter);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.adapter), adapter);
		query.orderBy(cb.asc(root.get(CubesMetricMeasurement_.self)));
		query.where(condition);
		return em.createQuery(query).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}

	/**
	 * Count the measurement results belonging to a specific adapter
	 * @param adapter
	 * @return
	 */
	public int countAllByAdapter(AdapterSettings adapter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.adapter), adapter);
		query.where(condition);		
		return em.createQuery(query).getResultList().size();
	}	


	/**
	 * Get an updated set of adapter data
	 * @param settings
	 * @throws uQasarException
	 */
	public void updateAdapterData(AdapterSettings settings) throws uQasarException {

		String credentials = settings.getAdapterUsername() +":" +settings.getAdapterPassword();
		String boundSystemURL = settings.getUrl();
		String project = settings.getAdapterProject();

		if(!testConnectionToServer(boundSystemURL, 10000)) return;

		//TODO: Manu, create Cubes adapter
		CubesAdapter adapter = new CubesAdapter();

		List<String> metricNames = UQasarUtil.getCubesMetricNames();
		Date snapshotTimeStamp = new Date();
		// Iterate the metric names and fetch results for each of these
		// create a persistent entity for each measurement 
		for (String metric : metricNames) {
			logger.info("Obtaining CUBES measurements for metric: " + metric);
			List<Measurement> metricMeasurements =  adapter.query(boundSystemURL, credentials ,metric);
			for (Measurement measurement : metricMeasurements) {
				String json = measurement.getMeasurement();
				Gson gson = new Gson();
				CubesMetricMeasurement[] CubesMetricMeasurement = gson.fromJson(json, 
						CubesMetricMeasurement[].class);
                for (eu.uqasar.model.measure.CubesMetricMeasurement aCubesMetricMeasurement : CubesMetricMeasurement) {
                    // Add a timestamp and metric name to the object
                    aCubesMetricMeasurement.setTimeStamp(snapshotTimeStamp);
                    aCubesMetricMeasurement.setCubesMetric(metric);
                    aCubesMetricMeasurement.setProject(settings.getProject());
                    aCubesMetricMeasurement.setAdapter(settings);
                    create(aCubesMetricMeasurement);
                }
			}
		}

		// Fetch the actual content in JSON format
		storeJSONPayload(settings);

		settings.setLatestUpdate(snapshotTimeStamp);
		settingsService.update(settings);

		// Update values of all the Cubes based metrics of the adaptor setting Project

	}

	/**
	 * Update the content of a CubesMetricMeasurement that is behind a URL (payload as JSON)
	 * @param settings
	 */
    private void storeJSONPayload(AdapterSettings settings) {
		logger.info("Get JSON Payload for all CUBES metrics.");
		String creds = settings.getAdapterUsername() +":" +settings.getAdapterPassword();

		//encoding  byte array into base 64
		byte[] encoded = Base64.encodeBase64(creds.getBytes());     
		String encodedString = new String(encoded);
		//		resty.alwaysSend("Authorization", "Basic " + encodedString);

		Date currentDatasetDate = getLatestDate();
		for (String metric : UQasarUtil.getCubesMetricNames()) {
			List<CubesMetricMeasurement> measurements;
			try {
				measurements = getMeasurementsByMetricAndDate(metric, currentDatasetDate);

				// Iterate 
				for (CubesMetricMeasurement CubesMetricMeasurement : measurements) {
					String url = CubesMetricMeasurement.getSelf();
					String jsonContent = "";

					try {
						JSONResource res = getJSON(url);
						us.monoid.json.JSONObject jobj = res.toObject();

						logger.info(jobj.toString());
						jsonContent = jobj.toString();
						logger.info("JSON Content: " +jsonContent);


						// Value that can be select as metric - ONLY for cut
						CubesMetricMeasurement.setValue(jobj.getJSONObject("summary").getString("count"));

						// TBR, JSON with collection of Values will be used within Analytic Workbench
						// This stores all the JSON info, maybe is too much and contains unneeded info
						//CubesMetricMeasurement.setJsonContent(jsonContent);

						// This ONLY stores the cells data,
						CubesMetricMeasurement.setJsonContent(jobj.getJSONArray("cells").toString());

						// Persist in database
						update(CubesMetricMeasurement);

					} catch (IOException | JSONException e) {
						e.printStackTrace();
					}
                }
			} catch (uQasarException e) {
				e.printStackTrace();
			}
		}
	}


	public List<Measurement> cubesAdapterQuery(String bindedSystem, String cubeName, String queryExpression) throws uQasarException{
		URI uri = null;
		LinkedList<Measurement> measurements = new LinkedList<>();

		try {
			uri = new URI(bindedSystem + "/" + queryExpression);
			String url = uri.toASCIIString();

			JSONResource res = getJSON(url);
			us.monoid.json.JSONObject json = res.toObject();
			logger.info(json.toString());

			JSONArray measurementResultJSONArray = new JSONArray();
			JSONObject bp = new JSONObject();
			bp.put("self", url);
			bp.put("key", cubeName);
			bp.put("name", queryExpression);
			measurementResultJSONArray.put(bp);
			logger.info(measurementResultJSONArray.toString());

			measurements.add(new Measurement(uQasarMetric.PROJECTS_PER_SYSTEM_INSTANCE, measurementResultJSONArray.toString()));

		} catch (URISyntaxException | org.apache.wicket.ajax.json.JSONException | JSONException | IOException e) {
			e.printStackTrace();
		}

        return measurements;
	}


	// To be deleted after CubesAdapter integration
	/**
	 * @param url
	 * @return Returns the JSON as JSON Resource
	 */
	private JSONResource getJSON(String url) throws uQasarException{
		JSONResource res = null;
		Resty resty = new Resty();

		// Connection counter +1
		counter +=1;

		// Replaces spaces in URL with char %20
		url = url.replaceAll(" ", "%20");

		try {
			res = resty.json(url);

		} catch (IOException e) {

			// Check if the limit of trials has been reached
			if(counter<counterLimit){
				return getJSON(url);} else
					throw new uQasarException("Cubes Server is not availabe at this moument, error to connect with " +url);
		}

		// Reset the connection counter to 0
		counter = 0;

		return res;
	}    

	public void printMeasurements(List<Measurement> measurements){
		String newLine = System.getProperty("line.separator");
		for (Measurement measurement : measurements) {
			System.out.println(measurement.getMeasurement()+newLine);

		}
	}

	/**
	 *  Check if the connection to the provided URL is possible within the provided timeout
	 * 
	 * @param url
	 * @param timeout
	 * @return 	True or False according to the connection success
	 * 			
	 */
	private static boolean testConnectionToServer(String url, int timeout) {
		try {
			URL myUrl = new URL(url);
			URLConnection connection = myUrl.openConnection();
			connection.setConnectTimeout(timeout);
			connection.connect();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param metric
	 * @return
	 * @throws uQasarException
	 */
	public List<CubesMetricMeasurement> getMeasurementsForMetric(String metric) 
			throws uQasarException {
		logger.info("Get measurements for metric: " +metric);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.cubesMetric), metric);
		query.where(condition);
		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
		return em.createQuery(query).getResultList();
	}

	/**
	 * Get the latest date of measurement snapshots
	 * @return
	 */
    private Date getLatestDate() {
		logger.info("Get the latest date from CUBES measurements...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
		query.select(root);
		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
		return em.createQuery(query).setMaxResults(1).getSingleResult().getTimeStamp();		
	}

	/**
	 * 
	 * @param metric
	 * @return
	 * @throws uQasarException
	 */
    private List<CubesMetricMeasurement> getMeasurementsByMetricAndDate(String metric, Date date)
			throws uQasarException {
		logger.info("Get measurements for metric: " +metric);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.cubesMetric), metric);
		Predicate condition2 = cb.equal(root.get(CubesMetricMeasurement_.timeStamp), date);
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
		return em.createQuery(query).getResultList();
	}


	/**
	 * @param project
	 * @param metric
	 * @return
	 */
	public CubesMetricMeasurement getLatestMeasurementByProjectAndMetric(
			String project, String metric) {
		logger.info("Obtaining measurements for the project: " +project 
				+" and metric: " +metric);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> query = 
				cb.createQuery(CubesMetricMeasurement.class);
		Root<CubesMetricMeasurement> root = 
				query.from(CubesMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.cubeName), 
				project);
		Predicate condition2 = cb.equal(root.get(
				CubesMetricMeasurement_.cubesMetric), metric);
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
		List<CubesMetricMeasurement> measurements = 
				em.createQuery(query).getResultList();
		CubesMetricMeasurement measurement = null;
		if (measurements != null && measurements.size() > 0 
				&& measurements.get(0) != null) {
			measurement = measurements.get(0); // Get the most "fresh" result 
		}
		return measurement;
	}	


	/**
	 * @param project
	 * @return List with all the measurements of a project
	 */
	public List<CubesMetricMeasurement> getMeasurementByProject(Project project) {
		logger.info("Obtaining measurements for the project: " +project);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> query = 
				cb.createQuery(CubesMetricMeasurement.class);
		Root<CubesMetricMeasurement> root = 
				query.from(CubesMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.project), 
				project);
		query.where(condition);
		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
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
		logger.info("Count measurements for metric: " +metric + "and date: " +date);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.cubesMetric), metric);
		Predicate condition2 = cb.equal(root.get(CubesMetricMeasurement_.timeStamp), date);
		Predicate condition3 = cb.and(condition, condition2);
		query.where(condition3);
		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
		return em.createQuery(query).getResultList().size();
	}	
}
