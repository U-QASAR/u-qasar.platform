package eu.uqasar.service;

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


import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import eu.uqasar.model.tree.BaseIndicator;
import eu.uqasar.model.tree.BaseIndicator_;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.Project_;
import eu.uqasar.model.tree.historic.AbstractHistoricValues;
import eu.uqasar.model.tree.historic.AbstractHistoricValues_;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator_;
import eu.uqasar.model.tree.historic.HistoricValuesProject;
import eu.uqasar.model.tree.historic.HistoricValuesProject_;
import eu.uqasar.service.dataadapter.AdapterSettingsService;

/**
 * 
 *
 *
 */

@Stateless
public class HistoricalDataService extends AbstractService<AbstractHistoricValues> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4613153136416606134L;
	
	@Inject
	AdapterSettingsService settingsService;
		
	public HistoricalDataService() {
		super(AbstractHistoricValues.class);
	}

	/**
	 * 
	 * @return
	 */
	public List<HistoricValuesBaseIndicator> getAllHistoricalDataObjects() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HistoricValuesBaseIndicator> query = 
				cb.createQuery(HistoricValuesBaseIndicator.class);
		query.from(HistoricValuesBaseIndicator.class);
        return em.createQuery(query).getResultList();
	}	

	/**
	 * 
	 * @param first
	 * @param count
	 * @return All the gathered historic values persisted on platform
	 */
	public List<AbstractHistoricValues> getAllByAscendingDate(int first, int count) {
		logger.infof("loading all Historical Data  ordered by ascending date ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AbstractHistoricValues> criteria = 
				cb.createQuery(AbstractHistoricValues.class);
		Root<AbstractHistoricValues> root = criteria.from(AbstractHistoricValues.class);
		criteria.orderBy(cb.asc(root.get(AbstractHistoricValues_.date)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}	
	
	//TODO: Implement methods to retrieve data:
	//	- from concrete a month
	//	- number of times going over/under Acceptance Limits
	
	/**
	 *
	 * @param baseIndicatorId
	 * @param first
	 * @param count
	 *
	 * @return
	 */
	public List<HistoricValuesBaseIndicator> getHistValuesForBaseInd(
			Long baseIndicatorId, int first, int count) {
		logger.infof(
				"loading %d Historic Values ordered by descending date for BaseIndicator with ID %d ...",
				count, baseIndicatorId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HistoricValuesBaseIndicator> criteria = cb.createQuery(HistoricValuesBaseIndicator.class);
		Root<BaseIndicator> root = criteria.from(BaseIndicator.class);
		Join<BaseIndicator, HistoricValuesBaseIndicator> join = root.join(BaseIndicator_.historicValues);
		criteria.select(join)
				.where(cb.and(
						cb.equal(root.get(BaseIndicator_.id), baseIndicatorId),
						cb.not(join.get(HistoricValuesBaseIndicator_.deleted)))
						)
				.orderBy(cb.desc(join.get(HistoricValuesBaseIndicator_.date)));
		
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}
	

	/**
	 * @param baseIndicatorId
	 * @return the number of historic values 
	 */
	public int countHistValuesForBaseIndicator(
			Long baseIndicatorId) {
		logger.infof("Counting meassures of the base indicator with %d id", baseIndicatorId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AbstractHistoricValues> criteria = cb.createQuery(AbstractHistoricValues.class);
		Root<BaseIndicator> root = criteria.from(BaseIndicator.class);
		Join<BaseIndicator, HistoricValuesBaseIndicator> join = root.join(BaseIndicator_.historicValues);
		criteria.select(join)
				.where(cb.and(
						cb.equal(root.get(BaseIndicator_.id), baseIndicatorId),
						cb.not(join.get(AbstractHistoricValues_.deleted)))
						);
		return em.createQuery(criteria).getResultList().size();
	}
	
	public List<HistoricValuesBaseIndicator> getAllHistValuesForBaseIndAsc(
			Long baseIndicatorId) {
		logger.infof(
				"loading All Historic Values ordered by descending date for BaseIndicator with ID %d ...",
				baseIndicatorId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HistoricValuesBaseIndicator> criteria = cb.createQuery(HistoricValuesBaseIndicator.class);
		Root<BaseIndicator> root = criteria.from(BaseIndicator.class);
		Join<BaseIndicator, HistoricValuesBaseIndicator> join = root.join(BaseIndicator_.historicValues);
		criteria.select(join)
				.where(cb.and(
						cb.equal(root.get(BaseIndicator_.id), baseIndicatorId),
						cb.not(join.get(HistoricValuesBaseIndicator_.deleted)))
						)
				.orderBy(cb.asc(join.get(HistoricValuesBaseIndicator_.date)));
		
		return em.createQuery(criteria).getResultList();
	}	

	public List<HistoricValuesProject> getAllHistValuesForProjectAsc(
			Long projectId) {
		logger.infof(
				"loading All Historic Values ordered by descending date for BaseIndicator with ID %d ...",
				projectId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HistoricValuesProject> criteria = cb.createQuery(HistoricValuesProject.class);
		Root<Project> root = criteria.from(Project.class);
		Join<Project, HistoricValuesProject> join = root.join(Project_.historicValues);
		criteria.select(join)
		.where(cb.and(
				cb.equal(root.get(Project_.id), projectId),
				cb.not(join.get(HistoricValuesProject_.deleted)))
				)
				.orderBy(cb.asc(join.get(HistoricValuesProject_.date)));
		
		return em.createQuery(criteria).getResultList();
	}


	//	public int countHValsBelowLowLimitForBI(
//			Long baseIndicatorId) {
//		logger.infof("Counting meassures of the base indicator with %d id", baseIndicatorId);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<HistoricValues> criteria = cb.createQuery(HistoricValues.class);
//		Root<BaseIndicator> root = criteria.from(BaseIndicator.class);
//		Join<BaseIndicator, HistoricValuesBaseIndicator> join = root.join(BaseIndicator_.historicValues);
//		criteria.select(join)
//		.where(cb.and(
//				cb.equal(root.get(BaseIndicator_.id), baseIndicatorId),
//				cb.not(join.get(HistoricValues_.deleted))),
//				cb.greaterThan(root.get(BaseIndicator_.lowerAcceptanceLimit),
//						root.get(BaseIndicator_.value))
//				);
//		return em.createQuery(criteria).getResultList().size();
//	}	
	
	/**
	 * @param baseIndicatorId
	 * @return BaseIndicator with provided Id
	 */
	public BaseIndicator getBaseIndicatorById(Long baseIndicatorId){
		logger.infof( "getting name of BaseIndicator with ID %d ...", baseIndicatorId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BaseIndicator> criteria = cb.createQuery(BaseIndicator.class);
		Root<BaseIndicator> root = criteria.from(BaseIndicator.class);
		criteria.where(
					cb.and(
						cb.equal(root.get(BaseIndicator_.id), baseIndicatorId)
//						,cb.not((root.get(BaseIndicator_.deleted))
						));
		
		return em.createQuery(criteria).getSingleResult();
	}
	
	/**
	 * @param projectId
	 * @return Returns the Project with provided Id
	 */
	public Project getProjectById(Long projectId){
		logger.infof( "getting name of Project with ID %d ...", projectId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> criteria = cb.createQuery(Project.class);
		Root<Project> root = criteria.from(Project.class);
		criteria.where(
				cb.and(
						cb.equal(root.get(Project_.id), projectId)
//						,cb.not((root.get(Project_.deleted))
						));
		
		return em.createQuery(criteria).getSingleResult();
	}
	
	
	/**
	 *
	 * @param projectId
	 * @param count
	 *
	 * @return
	 */
	public List<HistoricValuesProject> getAllHistValuesForProject(
			Long projectId, int first, int count) {
		logger.infof(
				"loading %d Historic Values ordered by descending date for Project with ID %d ...",
				count, projectId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HistoricValuesProject> criteria = cb.createQuery(HistoricValuesProject.class);
		Root<Project> root = criteria.from(Project.class);
		Join<Project, HistoricValuesProject> join = root.join(Project_.historicValues);
		criteria.select(join)
				.where(cb.and(
						cb.equal(root.get(Project_.id), projectId),
						cb.not(join.get(HistoricValuesProject_.deleted)))
						)
				.orderBy(cb.desc(join.get(HistoricValuesProject_.date)));
		
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}	
	
	/**
	 * @param projectId
	 * @return the number of historic values 
	 */
	public int countHistValuesForProject(
			Long projectId) {
		logger.infof("Counting meassures of the Project with %d id", projectId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HistoricValuesProject> criteria = cb.createQuery(HistoricValuesProject.class);
		Root<Project> root = criteria.from(Project.class);
		Join<Project, HistoricValuesProject> join = root.join(Project_.historicValues);
		criteria.select(join)
				.where(cb.and(
						cb.equal(root.get(Project_.id), projectId),
						cb.not(join.get(HistoricValuesProject_.deleted)))
						);
		return em.createQuery(criteria).getResultList().size();
	}	
	
	// Backup - Original
	
//	/**  
//	 *
//	 * @param baseIndicatorId
//	 * @param postedAt
//	 * @param count
//	 *
//	 * @return
//	 */
//	public List<HistoricValues> getHistValuesForBaseIndicatorBeforeDateByDescendingPostDate(
//			Long baseIndicatorId, Date postedAt, int count) {
//		logger.infof(
//				"loading %d Historic Values before %s ordered by descending gather date for BaseIndicator with ID %d ...",
//				count, postedAt, baseIndicatorId);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<HistoricValues> criteria = cb.createQuery(HistoricValues.class);
//		Root<BaseIndicator> root = criteria.from(BaseIndicator.class);
//		Join<BaseIndicator, HistoricValues> join = root.join(BaseIndicator_.historicValues);
//		criteria.select(join)
//				.where(cb.and
//						(
//						 cb.equal(root.get(BaseIndicator_.id), baseIndicatorId)
//						,cb.lessThan(join.get(HistoricValues_.date), postedAt))
//						,cb.not(join.get(HistoricValues_.deleted))
//					     )
//				.orderBy(cb.desc(join.get(HistoricValues_.date)));
//		return em.createQuery(criteria).setMaxResults(count).getResultList();
//	}	
	
	/**
	 * 	Collection of Historic data register to be deleted
	 * 
	 * @param dataMetrics
	 */
	public void delete(Collection<AbstractHistoricValues> dataMetrics) {
		for (AbstractHistoricValues m : dataMetrics) {
			delete(m);
		}
	}

	/**
	 * 	Collection of Historic data register to be soft deleted
	 * 
	 * @param dataMetrics
	 */
	public void softDelete(Collection<AbstractHistoricValues> dataMetrics) {
		for (AbstractHistoricValues m : dataMetrics) {
			m.setDeleted(true);
		}
	}


	//	public List<HistoricValues> getMeasurementsByMetricAndDate(String metric, Date date)
//			throws uQasarException {
//		logger.info("Get measurements for metric: " +metric);
//
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<HistoricValues> query = cb.createQuery(HistoricValues.class);
//		Root<HistoricValues> root = query.from(HistoricValues.class);
//		Predicate condition = cb.equal(root.get(HistoricValues_.cubesMetric), metric);
//		Predicate condition2 = cb.equal(root.get(HistoricValues_.date), date);
//		Predicate condition3 = cb.and(condition, condition2);
//		query.where(condition3);
//		query.orderBy(cb.desc(root.get(HistoricValues_.date)));
//		return em.createQuery(query).getResultList();
//	}	
	
//	/**
//	 * 
//	 * @param first
//	 * @param count
//	 * @return
//	 */
//	public List<CubesMetricMeasurement> getAllByAscendingName(int first, int count) {
//		logger.infof("loading all CubesMetricMeasurement ordered by ascending name ...");
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<CubesMetricMeasurement> criteria = 
//				cb.createQuery(CubesMetricMeasurement.class);
//		Root<CubesMetricMeasurement> root = criteria.from(CubesMetricMeasurement.class);
//		criteria.orderBy(cb.asc(root.get(CubesMetricMeasurement_.self)));
//		return em.createQuery(criteria).setFirstResult(first)
//				.setMaxResults(count).getResultList();
//	}	
//	
//
//	/**
//	 * Get an updated set of adapter data
//	 * @param settings
//	 * @throws uQasarException
//	 */
//	public void updateAdapterData(AdapterSettings settings) throws uQasarException {
//
//		String credentials = settings.getAdapterUsername() +":" +settings.getAdapterPassword();
//		String boundSystemURL = settings.getUrl();
//		String project = settings.getAdapterProject();
//		
//		if(testConnectionToServer(boundSystemURL,10000) == false) return;
//		
//		//TODO: Manu, create Cubes adapter
////		CubesAdapter adapter = new CubesAdapter();
//		
//		List<String> metricNames = UQasarUtil.getCubesMetricNames();
//		Date snapshotTimeStamp = new Date();
//		// Iterate the metric names and fetch results for each of these
//		// create a persistent entity for each measurement 
//		for (String metric : metricNames) {
//			logger.info("Obtaining CUBES measurements for metric: " + metric);
//			List<Measurement> metricMeasurements =  cubesAdapterQuery(boundSystemURL, project ,metric);
//			for (Measurement measurement : metricMeasurements) {
//				String json = measurement.getMeasurement();
//				Gson gson = new Gson();
//				CubesMetricMeasurement[] CubesMetricMeasurement = gson.fromJson(json, 
//						CubesMetricMeasurement[].class);
//				for (int i = 0; i < CubesMetricMeasurement.length; i++) {
//					// Add a timestamp and metric name to the object
//					CubesMetricMeasurement[i].setTimeStamp(snapshotTimeStamp);
//					CubesMetricMeasurement[i].setCubesMetric(metric);
//					CubesMetricMeasurement[i].setProject(settings.getProject());
//					create(CubesMetricMeasurement[i]);
//				}
//			}
//		}
//
//		// Fetch the actual content in JSON format
//		storeJSONPayload(settings);
//		
//		settings.setLatestUpdate(snapshotTimeStamp);
//		settingsService.update(settings);
//		
//		// Update values of all the Cubes based metrics of the adaptor setting Project
//
//	}
//
//	/**
//	 * Update the content of a CubesMetricMeasurement that is behind a URL (payload as JSON)
//	 * @param settings
//	 */
//	public void storeJSONPayload(AdapterSettings settings) {
//		logger.info("Get JSON Payload for all CUBES metrics.");
//		String creds = settings.getAdapterUsername() +":" +settings.getAdapterPassword();
//		Resty resty = new Resty();
//		
//		//encoding  byte array into base 64
//		byte[] encoded = Base64.encodeBase64(creds.getBytes());     
//		String encodedString = new String(encoded);
//		resty.alwaysSend("Authorization", "Basic " + encodedString);
//
//		Date currentDatasetDate = getLatestDate();
//		for (String metric : UQasarUtil.getCubesMetricNames()) {
//			List<CubesMetricMeasurement> measurements;
//			try {
//				measurements = getMeasurementsByMetricAndDate(metric, currentDatasetDate);
//				
//				// Makes a Ping to the server, cos sometimes does not respond as it should
//				resty.json("http://uqasar.pythonanywhere.com/cubes");
//				
//				// Iterate 
//				for (CubesMetricMeasurement CubesMetricMeasurement : measurements) {
//					String url = CubesMetricMeasurement.getSelf();
//					String jsonContent = "";
//					
//					try {
//						JSONResource res = resty.json(url);
//						us.monoid.json.JSONObject jobj = res.toObject();
//									
//						logger.info(jobj.toString());
//						jsonContent = jobj.toString();
//						logger.info("JSON Content: " +jsonContent);
//						
//						
//					// Value that can be select as metric - ONLY for cut
//						CubesMetricMeasurement.setValue(jobj.getJSONObject("summary").getString("count"));
//						
//					// TBR, JSON with collection of Values will be used within Analytic Workbench
//						// This stores all the JSON info, maybe is too much and contains unneeded info
//						//CubesMetricMeasurement.setJsonContent(jsonContent);
//						
//						// This ONLY stores the cells data,
//						CubesMetricMeasurement.setJsonContent(jobj.getJSONArray("cells").toString());
//
//						// Persist in database
//						update(CubesMetricMeasurement);
//						
//					} catch (IOException e) {
//						e.printStackTrace();
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			} catch (uQasarException e) {
//				e.printStackTrace();
//			} catch (IOException e1) {
//				// Thrower by http://uqasar.pythonanywhere.com/cubes
//				// TODO: eliminate after RM
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
//	}
//	
//	
//	public List<Measurement> cubesAdapterQuery(String bindedSystem, String cubeName, String queryExpression) throws uQasarException{
//        URI uri = null;
//        LinkedList<Measurement> measurements = new LinkedList<Measurement>();
//        Resty resty = new Resty();
//        
//        try {
//			uri = new URI(bindedSystem + cubeName + "/aggregate?" + queryExpression);
//			String url = uri.toASCIIString();
//			
//			JSONResource res = resty.json(url);
//			us.monoid.json.JSONObject json = res.toObject();
//			logger.info(json.toString());
//			
//			JSONArray measurementResultJSONArray = new JSONArray();
//            JSONObject bp = new JSONObject();
//            bp.put("self", url);
//            bp.put("key", cubeName);
//            bp.put("name", queryExpression);
//            measurementResultJSONArray.put(bp);
//            logger.info(measurementResultJSONArray.toString());
//			
//			measurements.add(new Measurement(uQasarMetric.PROJECTS_PER_SYSTEM_INSTANCE, measurementResultJSONArray.toString()));
//			
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (org.apache.wicket.ajax.json.JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        
//		return measurements;
//	}
//	
//	/**
//	 *  Check if the connection to the provided URL is possible within the provided timeout
//	 * 
//	 * @param url
//	 * @param timeout
//	 * @return 	True or False according to the connection success
//	 * 			
//	 */
//	public static boolean testConnectionToServer(String url, int timeout) {
//		try {
//			URL myUrl = new URL(url);
//			URLConnection connection = myUrl.openConnection();
//			connection.setConnectTimeout(timeout);
//			connection.connect();
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//	
//	/**
//	 * 
//	 * @param metric
//	 * @return
//	 * @throws uQasarException
//	 */
//	public List<CubesMetricMeasurement> getMeasurementsForMetric(String metric) 
//			throws uQasarException {
//		logger.info("Get measurements for metric: " +metric);
//
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
//		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
//		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.cubesMetric), metric);
//		query.where(condition);
//		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
//		return em.createQuery(query).getResultList();
//	}
//	
//	/**
//	 * Get the latest date of measurement snapshots
//	 * @return
//	 */
//	public Date getLatestDate() {
//		logger.info("Get the latest date from CUBES measurements...");
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
//		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
//		query.select(root);
//		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
//		return em.createQuery(query).setMaxResults(1).getSingleResult().getTimeStamp();		
//	}
//	
//	/**
//	 * 
//	 * @param metric
//	 * @return
//	 * @throws uQasarException
//	 */
//	public List<CubesMetricMeasurement> getMeasurementsByMetricAndDate(String metric, Date date) 
//			throws uQasarException {
//		logger.info("Get measurements for metric: " +metric);
//
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
//		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
//		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.cubesMetric), metric);
//		Predicate condition2 = cb.equal(root.get(CubesMetricMeasurement_.timeStamp), date);
//		Predicate condition3 = cb.and(condition, condition2);
//		query.where(condition3);
//		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
//		return em.createQuery(query).getResultList();
//	}
//	
//
//	/**
//	 * @param project
//	 * @param metric
//	 * @return
//	 */
//	public CubesMetricMeasurement getLatestMeasurementByProjectAndMetric(
//			String project, String metric) {
//		logger.info("Obtaining measurements for the project: " +project 
//				+" and metric: " +metric);
//
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<CubesMetricMeasurement> query = 
//				cb.createQuery(CubesMetricMeasurement.class);
//		Root<CubesMetricMeasurement> root = 
//				query.from(CubesMetricMeasurement.class);
//		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.cubeName), 
//				project);
//		Predicate condition2 = cb.equal(root.get(
//				CubesMetricMeasurement_.cubesMetric), metric);
//		Predicate condition3 = cb.and(condition, condition2);
//		query.where(condition3);
//		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
//		List<CubesMetricMeasurement> measurements = 
//				em.createQuery(query).getResultList();
//		CubesMetricMeasurement measurement = null;
//		if (measurements != null && measurements.size() > 0 
//				&& measurements.get(0) != null) {
//			measurement = measurements.get(0); // Get the most "fresh" result 
//		}
//		return measurement;
//	}	
//	
//
//	/**
//	 * @param project
//	 * @return List with all the measurements of a project
//	 */
//	public List<CubesMetricMeasurement> getMeasurementByProject(Project project) {
//		logger.info("Obtaining measurements for the project: " +project);
//
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<CubesMetricMeasurement> query = 
//				cb.createQuery(CubesMetricMeasurement.class);
//		Root<CubesMetricMeasurement> root = 
//				query.from(CubesMetricMeasurement.class);
//		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.project), 
//				project);
//		query.where(condition);
//		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
//		return em.createQuery(query).getResultList();
//		
//	}	
//	
//	
//	/**
//	 * 
//	 * @param metric
//	 * @param date
//	 * @return
//	 * @throws uQasarException
//	 */
//	public int countMeasurementsByMetricAndDate(String metric, Date date) 
//			throws uQasarException {
//		logger.info("Count measurements for metric: " +metric + "and date: " +date);
//
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<CubesMetricMeasurement> query = cb.createQuery(CubesMetricMeasurement.class);
//		Root<CubesMetricMeasurement> root = query.from(CubesMetricMeasurement.class);
//		Predicate condition = cb.equal(root.get(CubesMetricMeasurement_.cubesMetric), metric);
//		Predicate condition2 = cb.equal(root.get(CubesMetricMeasurement_.timeStamp), date);
//		Predicate condition3 = cb.and(condition, condition2);
//		query.where(condition3);
//		query.orderBy(cb.desc(root.get(CubesMetricMeasurement_.timeStamp)));
//		return em.createQuery(query).getResultList().size();
//	}	
}
