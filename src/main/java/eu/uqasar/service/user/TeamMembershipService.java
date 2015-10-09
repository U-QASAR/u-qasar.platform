/*
 */

package eu.uqasar.service.user;

import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.model.user.TeamMembership_;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AbstractService;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 *
 */
@Stateless
public class TeamMembershipService extends AbstractService<TeamMembership> {
	
	public TeamMembershipService() {
		super(TeamMembership.class);
	}
	
	public List<TeamMembership> getForUser(final User user) {
		if (user == null) {
			return Collections.emptyList();
		}
		logger.infof("loading all teams memberships for user %s ...", user);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TeamMembership> query = cb.createQuery(TeamMembership.class);
		Root<TeamMembership> root = query.from(TeamMembership.class);
		query.select(root).where(cb.equal(root.get(TeamMembership_.member), user)).distinct(true);
		return em.createQuery(query).getResultList();
	}
	
	public void removeUsersFromTeams(Collection<User> users) {
		for(User user : users) {
			removeUserFromTeams(user);
		}
	}
	
	public void removeUserFromTeams(User user) {
		List<TeamMembership> teams = getForUser(user);
		delete(teams);
	}
}
