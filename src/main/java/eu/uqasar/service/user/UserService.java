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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.WildcardContext;

import eu.uqasar.model.company.Company;
import eu.uqasar.model.user.RegistrationStatus;
import eu.uqasar.model.user.User;
import eu.uqasar.model.user.UserSource;
import eu.uqasar.model.user.User_;
import eu.uqasar.service.AbstractService;
import eu.uqasar.util.ldap.LdapUser;
import eu.uqasar.web.pages.admin.users.panels.UserFilterStructure;
import eu.uqasar.web.upload.UserProfilePictureUploadHelper;

/**
 *
 *
 */
@Stateless
public class UserService extends AbstractService<User> {

	@Inject
	private UserProfilePictureUploadHelper userPictureHelper;

	public UserService() {
		super(User.class);
	}

	public List<User> getAllExceptOne(User user) {
		if (user == null) {
			return this.getAll();
		}
		List<User> users;
		logger.infof("loading all Users except %s ...", user);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.where(cb.notEqual(root.get(User_.id), user.getId()));
		return em.createQuery(query).getResultList();
	}

	public List<User> getAllExceptAndFilter(Collection<User> usersToExclude, final String filter) {
		if (usersToExclude == null || usersToExclude.isEmpty()) {
			return this.getAll();
		}
		logger.infof("loading all Users except %s ...", usersToExclude);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		if (filter != null && !filter.isEmpty()) {

			Expression<String> literal = cb.upper(cb.literal(LIKE_WILDCARD + filter + LIKE_WILDCARD));
			Predicate likeFirstName = cb.like(cb.upper(root.get(User_.firstName)), literal);
			Predicate likeLastName = cb.like(cb.upper(root.get(User_.lastName)), literal);
			Predicate likeUserName = cb.like(cb.upper(root.get(User_.userName)), literal);
			Predicate orLikeName = cb.or(likeFirstName, likeLastName, likeUserName);
			query.where(cb.and(cb.not(root.in(usersToExclude)), orLikeName));
		} else {
			query.where(cb.not(root.in(usersToExclude)));
		}
		return em.createQuery(query).getResultList();
	}

	public List<User> getAllByAscendingNameFiltered(UserFilterStructure filter, int first, int count) {
		logger.infof("loading all Users ordered by ascending name matching the given filter %s...", filter);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = cb.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		List<Predicate> predicates = getFilterPredicates(filter, cb, root);
		if (!predicates.isEmpty()) {
			criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		}
		criteria.orderBy(cb.asc(root.get(User_.lastName)), cb.asc(root.get(User_.firstName)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();

	}
	
	public List<User> getAllByAscendingName(int first, int count) {
		logger.infof("loading all Users ordered by ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = cb.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		criteria.orderBy(cb.asc(root.get(User_.lastName)), cb.asc(root.get(User_.firstName)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}

	public boolean ldapBasedUserExists(final LdapUser user) {
		logger.infof("looking for users with username %s or mail %s", user.getUserName(), user.getMail());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		Predicate userName = cb.equal(root.get(User_.userName), user.getUserName());
		Predicate mail = cb.equal(root.get(User_.mail), user.getMail());
		query.where(cb.or(userName, mail));
		List<User> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		return !resultList.isEmpty();
	}
    
    public User getLdapBasedUser(final LdapUser user) {
		logger.infof("looking for LDAP users with username %s or mail %s", user.getUserName(), user.getMail());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		Predicate userName = cb.equal(root.get(User_.userName), user.getUserName());
		Predicate mail = cb.equal(root.get(User_.mail), user.getMail());
		query.where(cb.and(
                cb.or(userName, mail)
            )
        );
		List<User> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}

	public boolean userNameExists(final String userName) {
		return countByUserName(userName) > 0;
	}

	private Long countByUserName(final String userName) {
		logger.infof("counting Users with userName %s ...", userName);
		return performDistinctCountWithEqualPredicate(User_.userName, userName);
	}

	public User getByUserName(final String userName) {
		logger.infof("loading User with username %s ...", userName);
		User entity = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.where(cb.equal(root.get(User_.userName), userName));
		List<User> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (!resultList.isEmpty()) {
			return resultList.get(0);
		}
		return entity;
	}
    
    public User getByLdapLogin(final String userName) {
		logger.infof("loading LDAP-based User with username %s ...", userName);
		User entity = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.where(
                cb.and(
                        cb.equal(root.get(User_.userName), userName), 
                        cb.equal(root.get(User_.source), UserSource.LDAP)
                )
        );
		List<User> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (!resultList.isEmpty()) {
			return resultList.get(0);
		}
		return entity;
	}

	public User getByFullNameWithUsername(final String fullNameWithUsername) {
		logger.infof("loading User with fullName and username %s ...", fullNameWithUsername);
		User entity = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		Expression<String> fullName = cb.concat(cb.concat(root.get(User_.firstName), " "), root.get(User_.lastName));
		Expression<String> userName = cb.concat(" (", cb.concat(root.get(User_.userName), ")"));
		query.where(cb.equal(
				cb.lower(cb.concat(fullName, userName)),
				fullNameWithUsername.toLowerCase()));
		List<User> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (!resultList.isEmpty()) {
			return resultList.get(0);
		}
		return entity;
	}

	public User getByFullName(final String fullName) {
		logger.infof("loading User with fullName %s ...", fullName);
		User entity = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.where(cb.equal(
				cb.lower(
						cb.concat(
								root.get(User_.firstName),
								root.get(User_.lastName)
						)
				),
				fullName.toLowerCase()));
		List<User> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (!resultList.isEmpty()) {
			return resultList.get(0);
		}
		return entity;

	}

	public boolean mailExists(final String mail) {
		return countByMail(mail) > 0;
	}

	private Long countByMail(final String mail) {
		logger.infof("counting Users with mail %s ...", mail);
		return performDistinctCountWithEqualPredicate(User_.mail, mail);
	}

	public User getByMail(final String mail) {
		logger.infof("loading User with mail %s ...", mail);
		User entity = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.where(cb.equal(root.get(User_.mail), mail));
		List<User> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (!resultList.isEmpty()) {
			return resultList.get(0);
		}
		return entity;
	}
	
	
	// my company
	public List<User> getByCompany(final Company company) {
		logger.infof("loading User with company %s ...", company);
		User entity = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.where(cb.equal(root.get(User_.company), company));
        return em.createQuery(query).getResultList();
	}

	public User getByPWResetToken(final String token) {
		if (token == null || token.isEmpty()) {
			return null;
		}
		logger.infof("retrievung User with password reset token %s ...", token);
		User entity = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.where(cb.equal(root.get(User_.resetPWRequestToken), token));
		List<User> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (!resultList.isEmpty()) {
			return resultList.get(0);
		}
		return entity;
	}

	public User getByRegistrationTokenAndRegistrationStatus(final String token, final RegistrationStatus status) {
		logger.infof("retrievung User with registration token %s ...", token);
		User entity = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.where(
				cb.and(
						cb.equal(root.get(User_.registrationToken), token),
						cb.equal(root.get(User_.registrationStatus), status)
				)
		);
		List<User> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (!resultList.isEmpty()) {
			return resultList.get(0);
		}
		return entity;
	}

	@Override
	public void delete(User entity) {
		userPictureHelper.removeUserPictures(entity);
		super.delete(entity);
	}

	public long countAllFiltered(final UserFilterStructure filter) {
		logger.infof("counting all Users matching the filter %s ...", filter);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<User> from = criteria.from(User.class);
		List<Predicate> predicates = getFilterPredicates(filter, cb, from);
		if (!predicates.isEmpty()) {
			criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		}
		criteria.select(cb.countDistinct(from));
		return em.createQuery(criteria).getSingleResult();
	}

	private List<Predicate> getFilterPredicates(final UserFilterStructure filter, CriteriaBuilder cb, Root<User> from) {
		List<Predicate> predicates = new ArrayList<>();
		if (filter == null) {
			return predicates;
		}

		if (filter.getRole() != null) {
			predicates.add(cb.equal(from.get(User_.userRole), filter.getRole()));
		}
		if (filter.getSource() != null) {
			predicates.add(cb.equal(from.get(User_.source), filter.getSource()));
		}
		if (filter.getStatus() != null) {
			predicates.add(cb.equal(from.get(User_.registrationStatus), filter.getStatus()));
		}
		if (!StringUtils.isEmpty(filter.getName())) {
			Predicate firstName = cb.like(cb.lower(from.get(User_.lastName)), LIKE_WILDCARD + filter.getName().toLowerCase() + LIKE_WILDCARD);
			Predicate lastName = cb.like(from.get(User_.firstName), LIKE_WILDCARD + filter.getName() + LIKE_WILDCARD);
			Predicate userName = cb.like(from.get(User_.userName), LIKE_WILDCARD + filter.getName() + LIKE_WILDCARD);
			Expression<String> fullName = cb.concat(cb.concat(from.get(User_.firstName), " "), from.get(User_.lastName));
			Expression<String> un = cb.concat(" (", cb.concat(from.get(User_.userName), ")"));
			Predicate fn = cb.like(fullName, LIKE_WILDCARD + filter.getName() + LIKE_WILDCARD);
			Expression<String>  fnu = cb.concat(fullName, un);
			Predicate ff1 = cb.like(fnu, LIKE_WILDCARD + filter.getName() + LIKE_WILDCARD);
			
			predicates.add(cb.or(firstName, lastName, userName, fn, ff1));
			
			
			
		}
		return predicates;
	}
	
	
	
	
	@Override
    protected <P extends User> TermMatchingContext setFullTextSearchFields(WildcardContext wc, Class<P> clazz) {
        return wc.onField("firstName").andField("lastName");
    }
	

	public List<User> getAllUsersByDescendingSkillCount(int first, int count) {
		
		logger.infof("loading all Users ordered by ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = cb.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		
		criteria.orderBy(
				cb.desc(root.get(User_.skillCount)));
		
		return em.createQuery(criteria).setFirstResult(first).setMaxResults(count).getResultList();
	}
	
}
