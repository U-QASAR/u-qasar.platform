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


import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.model.user.TeamMembership_;
import eu.uqasar.model.user.Team_;
import eu.uqasar.model.user.User;
import eu.uqasar.model.user.User_;
import eu.uqasar.service.AbstractService;
import eu.uqasar.util.ldap.LdapGroup;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.WildcardContext;

/**
 *
 *
 */
@Stateless
public class TeamService extends AbstractService<Team> {

	public TeamService() {
		super(Team.class);
	}

	public List<Team> getForUser(final User user) {
		if (user == null) {
			return Collections.emptyList();
		}
		logger.infof("loading all teams for user %s ...", user);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Team> query = cb.createQuery(Team.class);
		Root<Team> root = query.from(Team.class);
		Join<Team, TeamMembership> members = root.join(Team_.members);
		Join<TeamMembership, User> users = members.join(TeamMembership_.member);
		query.select(root).where(cb.equal(users.get(User_.id), user.getId()));
		return em.createQuery(query).getResultList();
	}

	public List<Team> getAllExceptAndFilter(Collection<Team> teamsToExclude, final String filterValue) {
		logger.infof("loading all Teams except %s and filtering by name %s...", teamsToExclude, filterValue);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Team> query = cb.createQuery(Team.class);
		Root<Team> root = query.from(Team.class);
		Expression filter = null;
		Expression exclude = null;
		if (teamsToExclude != null && !teamsToExclude.isEmpty()) {
			exclude = cb.not(root.in(teamsToExclude));
		}
		if (filterValue != null && !filterValue.isEmpty()) {
			Expression<String> literal = cb.upper(cb.literal(LIKE_WILDCARD + filterValue + LIKE_WILDCARD));
			filter = cb.like(cb.upper(root.get(Team_.name)), literal);
		}
		if (exclude != null || filter != null) {
			if (exclude != null && filter != null) {
				query.where(cb.and(filter, exclude));
			} else if (exclude == null) {
				query.where(filter);
			} else {
				query.where(exclude);
			}
		}
		return em.createQuery(query).getResultList();
	}

	public List<Team> getAllByAscendingName(int first, int count) {
		logger.infof("loading all Teams ordered by ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Team> criteria = cb.createQuery(Team.class);
		Root<Team> root = criteria.from(Team.class);
		criteria.orderBy(cb.asc(root.get(Team_.name)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}
	
	public boolean ldapBasedGroupExists(final LdapGroup group) {
		logger.infof("looking for teams with name %s", group.getName());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Team> query = cb.createQuery(Team.class);
		Root<Team> root = query.from(Team.class);
		Predicate teamName = cb.equal(root.get(Team_.name), group.getName());
		query.where(teamName);
		List<Team> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		return !resultList.isEmpty();
	}

	public Team getByName(final String name) {
		logger.infof("loading Team with name %s ...", name);
		Team entity = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Team> query = cb.createQuery(Team.class);
		Root<Team> root = query.from(Team.class);
		query.where(cb.equal(root.get(Team_.name), name));
		List<Team> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (!resultList.isEmpty()) {
			return resultList.get(0);
		}
		return entity;
	}
	
	
	@Override
    protected <P extends Team> TermMatchingContext setFullTextSearchFields(WildcardContext wc, Class<P> clazz) {
        return wc.onField("name");
    }
}
