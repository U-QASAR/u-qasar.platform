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


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.WildcardContext;

import eu.uqasar.model.product.Product;
import eu.uqasar.model.product.Product_;
import eu.uqasar.web.pages.products.panels.ProductFilterStructure;

@Stateless
public class ProductService extends AbstractService<Product> {

	private static final long serialVersionUID = 8712537848736122775L;

	public ProductService() {
		super(Product.class);
	}

	public List<Product> getAllProducts() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Product> query = cb.createQuery(Product.class);
		query.from(Product.class);
        return em.createQuery(query).getResultList();
	}	
	
	/**
	 * 
	 * @return
	 */
	public List<Product> getAllByAscendingName(int first, int count) {
		logger.infof("loading all Products ordered by ascending name ...");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Product> criteria = cb.createQuery(Product.class);
		Root<Product> root = criteria.from(Product.class);
		criteria.orderBy(cb.asc(root.get(Product_.name)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();
	}

	/**
	 * 
	 * @param productId
	 * @return
	 */
	public boolean productExists(Long productId) {
		logger.info(String.format(
				"checking if product with ID %d exists ...", productId));
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<Product> from = criteria.from(Product.class);
		criteria.where(cb.equal(from.get(Product_.id), productId));
		criteria.select(cb.countDistinct(from));
		return (em.createQuery(criteria).getSingleResult() == 1);
	}

	public List<Product> sortAscendingDates() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Product> criteria = cb.createQuery(Product.class);
		Root<Product> from = criteria.from(Product.class);
		criteria.select(from);
		return em
				.createQuery(criteria.orderBy(cb.asc(from.get("releaseDate"))))
				.getResultList();
	}
	
	public List fillVersionDropDownChoice(){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Product> criteria = cb.createQuery(Product.class);
		Root<Product> from = criteria.from(Product.class);
		criteria.multiselect(from.get(Product_.version));
        return (List) em.createQuery(criteria).getResultList();
	}

	public List<Product> sortDescendingDates() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Product> criteria = cb.createQuery(Product.class);
		Root<Product> from = criteria.from(Product.class);
		criteria.select(from);
		return em.createQuery(
				criteria.orderBy(cb.desc(from.get("releaseDate"))))
				.getResultList();
	}

	public List<Product> getAllByAscendingNameFiltered(
			eu.uqasar.web.pages.products.panels.ProductFilterStructure filter, int first, int count) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Product> criteria = cb.createQuery(Product.class);
		Root<Product> from = criteria.from(Product.class);
		List<Predicate> predicates = getFilterPredicates(filter, cb, from);
		if (!predicates.isEmpty()) {
			criteria.where(cb.and(predicates.toArray(new Predicate[predicates
					.size()])));
		}

		criteria.orderBy(cb.asc(from.get(Product_.releaseDate)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();

	}

	public List<Product> getAllByDescendingNameFiltered(
			ProductFilterStructure filter, int first, int count) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Product> criteria = cb.createQuery(Product.class);
		Root<Product> root = criteria.from(Product.class);
		List<Predicate> predicates = getFilterPredicates(filter, cb, root);
		if (!predicates.isEmpty()) {
			criteria.where(cb.and(predicates.toArray(new Predicate[predicates
					.size()])));
		}

		criteria.orderBy(cb.desc(root.get(Product_.releaseDate)));
		return em.createQuery(criteria).setFirstResult(first)
				.setMaxResults(count).getResultList();

	}
	
	
	 @Override
	    protected <P extends Product> TermMatchingContext setFullTextSearchFields(WildcardContext wc, Class<P> clazz) {
	        return wc.onField("name").andField("description");
	    }
	    
	

	private List<Predicate> getFilterPredicates(
            final ProductFilterStructure filter, CriteriaBuilder cb,
            Root<Product> from) {
		List<Predicate> predicates = new ArrayList<>();
		if (filter == null) {
			return predicates;
		}

		if (filter.getReleaseDate() != null) {
			predicates.add(cb.equal(from.get(Product_.releaseDate),
					filter.getReleaseDate()));
		}
		if (filter.getVersion() != null) {
			predicates.add(cb.equal(from.get(Product_.version),
					filter.getVersion()));
		}

		if (!StringUtils.isEmpty(filter.getName())) {
			Predicate firstName = cb.like(
					cb.lower(from.get(Product_.name)), LIKE_WILDCARD
							+ filter.getName().toLowerCase() + LIKE_WILDCARD);
			predicates.add(firstName);
		}
		return predicates;
	}

}
