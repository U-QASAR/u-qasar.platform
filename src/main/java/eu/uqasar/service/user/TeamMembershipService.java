/*
 */

package eu.uqasar.service.user;

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
	
	private void removeUserFromTeams(User user) {
		List<TeamMembership> teams = getForUser(user);
		delete(teams);
	}
}
