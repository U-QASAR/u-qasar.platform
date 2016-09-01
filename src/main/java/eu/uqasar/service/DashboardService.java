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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.dashboard.DbDashboard_;

@Stateless
public class DashboardService extends AbstractService<DbDashboard> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7955819915072501841L;

	public DashboardService() {
		super(DbDashboard.class);
	}

	/**
	 * 
	 * @return
	 */
	public List<DbDashboard> getAllDashboards() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DbDashboard> query = cb.createQuery(DbDashboard.class);
		query.from(DbDashboard.class);
        return em.createQuery(query).getResultList();
	}	
	
	/**
	 * 
	 * @param dashboards
	 */
	public void delete(Collection<DbDashboard> dashboards) {
		for (DbDashboard db : dashboards) {
			delete(db);
		}
	}
	
	/**
	 * 
	 * @param first
	 * @param count
	 * @return
	 */
	public List<DbDashboard> getAllByAscendingTitle(int first, int count) {
		logger.infof("loading all DbDashboards ordered by ascending title ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DbDashboard> criteria = cb.createQuery(DbDashboard.class);
		Root<DbDashboard> root = criteria.from(DbDashboard.class);
		criteria.orderBy(cb.asc(root.get(DbDashboard_.title)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}
	
	
	/**
	 * 
	 * @param title
	 * @return
	 */
	public List<DbDashboard> getDashboardByTitle(String title) {
		logger.infof("getting DbDashboard by title ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DbDashboard> criteria = cb.createQuery(DbDashboard.class);
		Root<DbDashboard> root = criteria.from(DbDashboard.class);
		criteria.where(cb.equal(root.get(DbDashboard_.title), title));
		return em.createQuery(criteria).getResultList();
	}
	
	/**
	 * 
	 * @param dashboardId
	 * @return
	 */
	public DbDashboard getDashboardByDashboardId(String dashboardId) {
		logger.infof("getting DbDashboard by dashboardId ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DbDashboard> criteria = cb.createQuery(DbDashboard.class);
		Root<DbDashboard> root = criteria.from(DbDashboard.class);
		criteria.where(cb.equal(root.get(DbDashboard_.dashboardId), dashboardId));
		return em.createQuery(criteria).getSingleResult();
	}
}
