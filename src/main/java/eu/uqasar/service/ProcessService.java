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


import eu.uqasar.model.process.Process;
import eu.uqasar.model.process.Process_;
import eu.uqasar.web.pages.processes.panels.ProcessesFilterStructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.WildcardContext;

@Stateless
public class ProcessService extends AbstractService<Process> {

	private static final long serialVersionUID = -5357568841490119519L;

	public ProcessService() {
		super(Process.class);
	}

	/**
	 * 
	 * @return
	 */
	public List<Process> getAllProcesses() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> query = cb.createQuery(Process.class);
		query.from(Process.class);
        return em.createQuery(query).getResultList();
	}	
	
	/**
	 * 
	 * @param processes
	 */
	public void delete(Collection<Process> processes) {
		for (Process process : processes) {
			delete(process);
		}
	}
	
	/**
	 * 
	 * @param first
	 * @param count
	 * @return
	 */
	public List<Process> getAllByAscendingName(int first, int count) {
		logger.infof("loading all Processes ordered by ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> criteria = cb.createQuery(Process.class);
		Root<Process> root = criteria.from(Process.class);
		criteria.orderBy(cb.asc(root.get(Process_.name)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}

	/**
	 * 
	 * @param processId
	 * @return
	 */
	public boolean processExists(Long processId) {
		logger.info(String.format(
				"checking if process with ID %d exists ...", processId));
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<Process> from = criteria.from(Process.class);
		criteria.where(cb.equal(from.get(Process_.id), processId));
		criteria.select(cb.countDistinct(from));
		return (em.createQuery(criteria).getSingleResult() == 1);
	}

    @Override
    protected <P extends Process> TermMatchingContext setFullTextSearchFields(WildcardContext wc, Class<P> clazz) {
        return wc.onField("name").andField("description").andField("stages");
    }
    
    public List<Process> getAllByAscendingEndDateNameFiltered(
			ProcessesFilterStructure filter, int first, int count) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> criteria = cb.createQuery(Process.class);
		Root<Process> root = criteria.from(Process.class);
		List<Predicate> predicates = getFilterPredicates(filter, cb, root);
		if (!predicates.isEmpty()) {
			criteria.where(cb.and(predicates.toArray(new Predicate[predicates
					.size()])));
		}

		criteria.orderBy(cb.asc(root.get(Process_.endDate)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();

	}

	public List<Process> getAllByDescendingEndDateNameFiltered(
			ProcessesFilterStructure filter, int first, int count) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> criteria = cb.createQuery(Process.class);
		Root<Process> root = criteria.from(Process.class);
		List<Predicate> predicates = getFilterPredicates(filter, cb, root);
		if (!predicates.isEmpty()) {
			criteria.where(cb.and(predicates.toArray(new Predicate[predicates
					.size()])));
		}

		criteria.orderBy(cb.desc(root.get(Process_.endDate)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();

	}

	public List<Process> getAllByDescendingStartDateNameFiltered(
			ProcessesFilterStructure filter, int first, int count) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> criteria = cb.createQuery(Process.class);
		Root<Process> root = criteria.from(Process.class);
		List<Predicate> predicates = getFilterPredicates(filter, cb, root);
		if (!predicates.isEmpty()) {
			criteria.where(cb.and(predicates.toArray(new Predicate[predicates
					.size()])));
		}

		criteria.orderBy(cb.desc(root.get(Process_.startDate)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();

	}

	public List<Process> getAllAscendingStartDateNameFiltered(
			ProcessesFilterStructure filter, int first, int count) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> criteria = cb.createQuery(Process.class);
		Root<Process> root = criteria.from(Process.class);
		List<Predicate> predicates = getFilterPredicates(filter, cb, root);
		if (!predicates.isEmpty()) {
			criteria.where(cb.and(predicates.toArray(new Predicate[predicates
					.size()])));
		}

		criteria.orderBy(cb.asc(root.get(Process_.startDate)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();

	}

	private List<Predicate> getFilterPredicates(
            final ProcessesFilterStructure filter, CriteriaBuilder cb,
            Root<Process> from) {
		List<Predicate> predicates = new ArrayList<>();
		if (filter == null) {
			return predicates;
		}

		if (filter.getStartDate() != null) {
			predicates.add(cb.equal(from.get(Process_.startDate),
					filter.getStartDate()));
		}
		if (filter.getEndDate() != null) {
			predicates.add(cb.equal(from.get(Process_.endDate),
					filter.getEndDate()));
		}

		if (!StringUtils.isEmpty(filter.getName())) {
			Predicate firstName = cb.like(
					cb.lower(from.get(Process_.name)), LIKE_WILDCARD
							+ filter.getName().toLowerCase() + LIKE_WILDCARD);
			predicates.add((firstName));
		}
		return predicates;
	}

	public long countAllFiltered(final ProcessesFilterStructure filter) {
		logger.infof("counting all Processes matching the filter %s ...",
				filter);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<Process> from = criteria.from(Process.class);
		List<Predicate> predicates = getFilterPredicates(filter, cb, from);
		if (!predicates.isEmpty()) {
			criteria.where(cb.and(predicates.toArray(new Predicate[predicates
					.size()])));
		}
		criteria.select(cb.countDistinct(from));
		return em.createQuery(criteria).getSingleResult();
	}

}
