/**
 * 
 */
package eu.uqasar.service;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.historic.Snapshot;
import eu.uqasar.model.tree.historic.Snapshot_;

/**
 *
 */
public class SnapshotService extends AbstractService<Snapshot> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5129370786783378083L;

	public SnapshotService() {
		super(Snapshot.class);
	}
	
	/**
	 * Return a list of Snapshots of the provided project
	 * @return 
	 */
	public List<Snapshot> getProjectSnapshot(Project project){
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Snapshot> query = cb.createQuery(Snapshot.class);
		Root<Snapshot>root = query.from(Snapshot.class);

		query.where(cb.equal(root.get(Snapshot_.project), project));
		query.orderBy(cb.desc(root.get(Snapshot_.date)));
		
		return em.createQuery(query).getResultList();
	}
	
	/**
	 * @param id Provided Snapshot ID 
	 * @return Snapshot with the desired ID
	 */
	public Snapshot getSnapshotById(String id){
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Snapshot> query = cb.createQuery(Snapshot.class);
		Root<Snapshot> root = query.from(Snapshot.class);
		
		query.where(cb.equal(root.get(Snapshot_.id),id));
		
		return em.createQuery(query).getSingleResult();
	}
	
}
