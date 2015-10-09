package eu.uqasar.service;

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
		List<DbDashboard> resultList = em.createQuery(query).getResultList();
		return resultList;
	}	
	
	/**
	 * 
	 * @param processes
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
