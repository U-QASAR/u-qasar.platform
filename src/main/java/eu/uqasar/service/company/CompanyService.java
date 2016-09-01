package eu.uqasar.service.company;

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


import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import eu.uqasar.model.company.Company;
import eu.uqasar.model.company.Company_;
import eu.uqasar.service.AbstractService;
import eu.uqasar.web.pages.admin.companies.panels.CompanyFilterStructure;

public class CompanyService extends AbstractService<Company> {

	protected CompanyService() {
		super(Company.class);
	}
	
	
	public List<Company> getAllCompanys() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Company> query = cb.createQuery(Company.class);
		query.from(Company.class);
		return em.createQuery(query).getResultList();
	}	
	
	/**
	 * 
	 * @return
	 */
	public List<Company> getAllByAscendingName() {
		logger.infof("loading all Company ordered by ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Company> criteria = cb.createQuery(Company.class);
		Root<Company> root = criteria.from(Company.class);
		criteria.orderBy(cb.asc(root.get(Company_.name)));
		return em.createQuery(criteria).getResultList();
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Company> getAllByAscendingNameFiltered(CompanyFilterStructure filter, int first, int count) {
		logger.infof("loading all Companies ordered by ascending name matching the given filter %s...", filter);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Company> criteria = cb.createQuery(Company.class);
		Root<Company> root = criteria.from(Company.class);
		
		criteria.where(
				cb.or(
					cb.equal(root.get(Company_.name), filter.getName()),
					cb.equal(root.get(Company_.shortName), filter.getShortName()),
					cb.equal(root.get(Company_.country), filter.getCountry()))
					
		);
		return em.createQuery(criteria).setFirstResult(first).setMaxResults(count).getResultList();
	}
	
	public long countAllFiltered(final CompanyFilterStructure filter) {
		logger.infof("counting all Companies matching the filter %s ...", filter);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<Company> from = criteria.from(Company.class);
		criteria.where(
				cb.or(
					cb.equal(from.get(Company_.name), filter.getName()),
					cb.equal(from.get(Company_.shortName), filter.getShortName()),
					cb.equal(from.get(Company_.country), filter.getCountry())
					)
		);
		criteria.select(cb.countDistinct(from));
		return em.createQuery(criteria).getSingleResult();
	}
	

	/**
	 * 
	 * @param companyId
	 * @return
	 */
	public boolean companyExists(Long companyId) {
		logger.info(String.format(
				"checking if Company with ID %d exists ...", companyId));
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<Company> from = criteria.from(Company.class);
		criteria.where(cb.equal(from.get(Company_.id), companyId));
		criteria.select(cb.countDistinct(from));
		return (em.createQuery(criteria).getSingleResult() == 1);
	}
}
