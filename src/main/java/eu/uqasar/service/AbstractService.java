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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.WildcardContext;
import org.jboss.solder.logging.Logger;

import eu.uqasar.model.AbstractEntity_;
import eu.uqasar.model.Persistable;
@NoArgsConstructor
public abstract class AbstractService<T extends Persistable> implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4171879462026403888L;

	private Class<T> clazz;
	private String readableClassName;

	protected static final String LIKE_WILDCARD = "%";

	/**
	 *
	 * @param clazz
	 */
	protected AbstractService(Class<T> clazz) {
		this.clazz = clazz;
		this.readableClassName = getReadableClassName(clazz);
	}

	/**
	 *
	 * @param clazz
	 * @param readableClassName
	 */
	protected AbstractService(Class<T> clazz, String readableClassName) {
		this.clazz = clazz;
		this.readableClassName = readableClassName;
	}

	// inject a logger
	@Inject
	protected Logger logger;

	@Inject
	protected EntityManager em;

	/**
	 * Detaches entity from persistence context.
	 *
	 * @param t
	 * @return
	 */
	public T detach(T t) {
		em.detach(t);
		return t;
	}

	/**
	 *
	 * @return
	 */
	@SuppressWarnings("unused")
	public List<T> getAll() {
		logger.infof("loading all %ss ...", readableClassName);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = cb.createQuery(clazz);
		Root<T> root = criteria.from(clazz);
		return em.createQuery(criteria).getResultList();
	}

	public List<T> getAllExcept(Collection<T> toExclude) {
		if (toExclude == null || toExclude.isEmpty()) {
			return this.getAll();
		}
		logger.infof("loading all %s except %s ...", readableClassName, toExclude);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(this.clazz);
		Root<T> root = query.from(this.clazz);
		query.where(cb.not(root.in(toExclude)));
		return em.createQuery(query).getResultList();
	}
    
    public List<T> getRange(long first, long count) {
        return getRange((int) first, (int) count);
    }
    
    private List<T> getRange(int first, int count) {
		logger.infof("loading %d %s starting from %d  ...", count, readableClassName, first);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(this.clazz);
		query.from(this.clazz);
		return em.createQuery(query).setFirstResult(first).setMaxResults(count).getResultList();
    }

	/**
	 *
	 * @return
	 */
	public long countAll() {
		logger.infof("counting all %ss ...", readableClassName);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<T> root = criteria.from(clazz);
		criteria.select(cb.countDistinct(root));
		return em.createQuery(criteria).getSingleResult();
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public T getById(Long id) {
		logger.infof("loading %s with ID %d ...", readableClassName, id);
		return em.find(clazz, id);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	public T create(T entity) {
		entity = update(entity);
		logger.infof("persisted %s %s ...", readableClassName, entity);
		return entity;
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	public T update(T entity) {
		entity = em.merge(entity);
		logger.infof("updated %s %s ...", readableClassName, entity);
		return entity;
	}

	/**
	 *
	 * @param entityId
	 */
	public void delete(Long entityId) {
		T item = em.find(clazz, entityId);
		delete(item);
	}

	public void delete(Collection<T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	/**
	 *
	 * @param entity
	 */
	public void delete(T entity) {
		Serializable id = entity.getId();
		entity = em.merge(entity);
		em.remove(entity);
		logger.infof("deleted %s %s with id %s ...", readableClassName, entity, id);
	}

    private <T extends Persistable> String getReadableClassName(Class<T> clazz) {
        return clazz.getSimpleName();
    }
    
    public <T extends Persistable> long countAll(Class<T> clazz) {
        logger.infof("counting all %ss ...", getReadableClassName(clazz));
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<T> root = criteria.from(clazz);
		criteria.select(cb.countDistinct(root));
		return em.createQuery(criteria).getSingleResult();
    }
    
	protected <T extends Persistable> T getById(Class<T> clazz, Long id) {
		logger.infof("loading %s with ID %d ...",  getReadableClassName(clazz), id);
		return em.find(clazz, id);
	}
    
	/**
	 * Method to get the entity of class by a given name.
	 * @param clazz Class
	 * @param name attribute name of the searched entity
	 * @return T entity
	 */
	public <T extends Persistable> T getByName(Class<T> clazz, String name) {
		logger.infof("loading %s with name %d ...",  getReadableClassName(clazz), name);
		T entity = null;
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> ent = criteriaQuery.from(clazz);
        criteriaQuery.select(ent).where(criteriaBuilder.equal(ent.get("name"), name));
		
		List<T> entities = em.createQuery(criteriaQuery).getResultList();
		
		if (entities != null && entities.size()>0) {
			entity = entities.get(0);
		}
		return entity;
	}
    
    
	private <T extends Persistable> List<T> getByIds(Class<T> clazz, Collection<Long> ids) {
		logger.infof("loading %s with IDs %d ...",  getReadableClassName(clazz), ids);
        List<T> results = new ArrayList<>();
        for(Long id : ids) {
            results.add(em.find(clazz, id));
        }
		return results;
	}
    
    public static String[] prepareSearchTerm(String term) {
        return StringUtils.split(term, ' ');
    }
    
	public <T extends Persistable> List<T> getAll(Class<T> clazz) {
		logger.infof("loading all %ss ...", getReadableClassName(clazz));
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = cb.createQuery(clazz);
		criteria.from(clazz);
		return em.createQuery(criteria).getResultList();
	}

	public <T extends Persistable> List<T> getAllExcept(Class<T> clazz, Collection<T> toExclude) {
		if (toExclude == null || toExclude.isEmpty()) {
			return getAll(clazz);
		}
		logger.infof("loading all %s except %s ...", getReadableClassName(clazz), toExclude);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(clazz);
		Root<T> root = query.from(clazz);
		query.where(cb.not(root.in(toExclude)));
		return em.createQuery(query).getResultList();
	}
    
    public <T extends Persistable> List<T> getRange(Class<T> clazz, long first, long count) {
        return getRange(clazz, (int) first, (int) count);
    }
    
     private <T extends Persistable> List<T> getRange(Class<T> clazz, int first, int count) {
		logger.infof("loading %d %s starting from %d  ...", count, getReadableClassName(clazz), first);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(clazz);
		query.from(clazz);
		return em.createQuery(query).setFirstResult(first).setMaxResults(count).getResultList();
    }

    public <P extends T> List<P> fullTextSearch(Class<P> clazz, int first, int count, String... terms) {
        Collection<Long> ids = fullTextSearchForIds(clazz, terms);
        List<P> results = new ArrayList<>();
        if(!ids.isEmpty()) {
            results = getByIds(clazz, ids);
            results = filterFullTextSearchResults(results, clazz);
        }
        return results;
    }
     
    public <P extends T> List<T> fullTextSearch(int first, int count, String terms) {
        return fullTextSearch(this.clazz, first, count, terms);
    }
    
    protected Collection<Long> fullTextSearchForIds(String terms) {
        return fullTextSearchForIds(this.clazz, terms);
    }
    
    private <P extends T> Collection<Long> fullTextSearchForIds(Class<P> clazz, String... terms) {
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
        QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder().
                forEntity(clazz).get();
        Query luceneQuery;
        WildcardContext wc = qb.keyword().wildcard();
        TermMatchingContext tmc = setFullTextSearchFields(wc, clazz);
        Set<Long> results = new HashSet<>();
        try {
            for (String term : terms) {
                luceneQuery = tmc.ignoreAnalyzer().ignoreFieldBridge().
                        matching(term.toLowerCase()).createQuery();
                FullTextQuery query = ftem.
                        createFullTextQuery(luceneQuery, clazz);
                query.setProjection("id");
                List<Object[]> idList = query.getResultList();
                for (Object[] id : idList) {
                    if (id[0] instanceof Long) {
                        results.add((Long) id[0]);
                    }
                }
            }
        } catch (Exception e) {
            // because of lucene stop words
            logger.warn(e.getMessage(), e);
        }
        return results;
    }

    protected <P extends T> TermMatchingContext setFullTextSearchFields(WildcardContext wc, Class<P> clazz) {
        return wc.onField(AbstractEntity_.id.getName());
    }

    private <P extends T> List<P> filterFullTextSearchResults(List<P> results, Class<P> clazz) {
        return results;
    }

    protected List<T> filterFullTextSearchResults(List<T> results) {
        return results;
    }

    public Long countFullTextResults(String... terms) {
        return countFullTextResults(this.clazz, terms);
    }
    
    public <P extends T> Long countFullTextResults(Class<P> clazz, String... terms) {
        return (long) fullTextSearchForIds(clazz, terms).size();
    }
     
     protected List<T> getAllOrdered(Class<T> clazz, Order... orders) {
		logger.infof("loading all %ss ...", getReadableClassName(clazz));
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = cb.createQuery(clazz);
		criteria.from(clazz);
        if(orders != null && orders.length > 0) {
            criteria.orderBy(orders);
        }
		return em.createQuery(criteria).getResultList();
	}
     
    protected <T extends Persistable> List<T> getAllOrderedExcept(Class<T> clazz, Collection<T> toExclude, Order... orders) {
		if (toExclude == null || toExclude.isEmpty()) {
			return getAll(clazz);
		}
		logger.infof("loading all %s except %s ...", getReadableClassName(clazz), toExclude);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(clazz);
		Root<T> root = query.from(clazz);
        if(orders != null && orders.length > 0) {
            query.orderBy(orders);
        }
		query.where(cb.not(root.in(toExclude)));
		return em.createQuery(query).getResultList();
	}
    
    protected <T extends Persistable> List<T> getRangeOrdered(Class<T> clazz, long first, long count, Order... orders) {
        return getRangeOrdered(clazz, (int) first, (int) count, orders);
    }
    
    
    private <T extends Persistable> List<T> getRangeOrdered(Class<T> clazz, int first, int count, Order... orders) {
		logger.infof("loading %d %s starting from %d  ...", count, getReadableClassName(clazz), first);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(clazz);
		query.from(clazz);
        if(orders != null && orders.length > 0) {
            query.orderBy(orders);
        }
		return em.createQuery(query).setFirstResult(first).setMaxResults(count).getResultList();
    }

	protected Long performDistinctCountWithEqualPredicate(final Attribute<T, ?> attr, Object value) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<T> root = query.from(clazz);
		query.where(cb.equal(root.get(attr.getName()), value));
		query.select(cb.countDistinct(root));
		return em.createQuery(query).getSingleResult();
	}

}
