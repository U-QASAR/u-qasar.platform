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

import eu.uqasar.model.analytic.Analysis;
import eu.uqasar.model.analytic.Analysis_;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.Project_;
import eu.uqasar.service.dataadapter.AdapterSettingsService;


/**
 * 
 *
 *
 */

@Stateless
public class AnalyticService extends AbstractService<Analysis> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4613153136416606134L;
	
	@Inject
	AdapterSettingsService settingsService;
		
	public AnalyticService() {
		super(Analysis.class);
	}

	/**
	 * 
	 * @return
	 */
	public List<Analysis> getAllAnalysis() {
		logger.info("Obtaining all the Analysis");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Analysis> query = cb.createQuery(Analysis.class);
		query.from(Analysis.class);
		
		return em.createQuery(query).getResultList();
	}	
	
	/**
	 * 
	 * @param first
	 * @param count
	 * @return All the Analysis persisted on platform
	 */
	public List<Analysis> getAllAnalysisByAscendingDate(int first, int count) {
		logger.infof("loading all Analysis ordered by ascending date ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Analysis> criteria = 
				cb.createQuery(Analysis.class);
		Root<Analysis> root = criteria.from(Analysis.class);
		criteria.orderBy(cb.asc(root.get(Analysis_.date)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}	
	
	/**
	 *
	 * @param projectId
	 * @param count
	 *
	 * @return
	 */
	public List<Analysis> getAllAnalysisForProject(
			Long projectId, int first, int count) {
		logger.infof(
				"loading %d Analysis ordered by descending date for Project with ID %d ...",
				count, projectId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Analysis> criteria = cb.createQuery(Analysis.class);
		Root<Project> root = criteria.from(Project.class);
		Join<Project, Analysis> join = root.join(Project_.analysis);
		criteria.select(join).where(
				 cb.and(
						cb.equal(root.get(Project_.id), projectId),
						cb.not(join.get(Analysis_.deleted)))
						).orderBy(cb.desc(join.get(Analysis_.date)));
		
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}
	

	/**
	 * @param projectId
	 * @return the number of Analysis for provided ID
	 */
	public int countAnalysisForProject(
			Long projectId) {
		logger.infof("Counting meassures of the base indicator with %d id", projectId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Analysis> criteria = cb.createQuery(Analysis.class);
		Root<Project> root = criteria.from(Project.class);
		Join<Project, Analysis> join = root.join(Project_.analysis);
		criteria.select(join)
				.where(cb.and(
						cb.equal(root.get(Project_.id), projectId),
						cb.not(join.get(Analysis_.deleted)))
						);
		return em.createQuery(criteria).getResultList().size();
	}	
	
	/**
	 * @param projectId
	 * @return Project with provided Id
	 */
	public Project getProjectById(Long projectId){
		logger.infof( "getting name of Project with ID %d ...", projectId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> criteria = cb.createQuery(Project.class);
		Root<Project> root = criteria.from(Project.class);
		criteria.where(
					cb.and(
						cb.equal(root.get(Project_.id), projectId)
						));
		
		return em.createQuery(criteria).getSingleResult();
	}
	
	/**
	 * 	Collection of Historic data register to be deleted
	 * 
	 * @param dataMetrics
	 */
	public void delete(Collection<Analysis> dataMetrics) {
		for (Analysis m : dataMetrics) {
			delete(m);
		}
	}

	/**
	 * 	Soft-Delete a collection of Analysis
	 * 
	 * @param analysis
	 */
    private void softDelete(Analysis analysis) {
			analysis.setDeleted(true);
	}

	/**
	 * 	Soft-Delete a collection of Analysis
	 * 
	 * @param analysis
	 */
	public void softDelete(Collection<Analysis> analysis) {
		for (Analysis m : analysis) {
			softDelete(m);
		}
	}
	
}
