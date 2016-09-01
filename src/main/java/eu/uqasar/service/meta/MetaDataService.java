package eu.uqasar.service.meta;

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
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SetAttribute;

import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.meta.MetaData;
import eu.uqasar.model.meta.MetaData_;
import eu.uqasar.model.meta.QModelTagData;
import eu.uqasar.model.meta.QModelTagData_;
import eu.uqasar.model.qmtree.QMBaseIndicator;
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMMetric_;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityIndicator_;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QMQualityObjective_;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AbstractService;

/**
 *
 *
 * @param <T>
 */
@Named(value = "MetaDataService")
@Dependent
public class MetaDataService<T extends MetaData> extends AbstractService<T> {

    public static final String NAME = "MetaDataService";

    public MetaDataService() {
        super((Class<T>) MetaData.class);
    }

    public MetaDataService(Class<T> clazz) {
        super(clazz);
    }

    long countUsersWithMetaData(T entity, SetAttribute<User, T> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<User> root = query.from(User.class);
        Join<User, T> join = root.join(attribute);
        query.where(join.in(entity));
        query.select(cb.countDistinct(root));
        return em.createQuery(query).getSingleResult();
    }
    
    long countQOWithMetaData(T entity, SetAttribute<QMBaseIndicator, QModelTagData> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<QMQualityObjective> root = query.from(QMQualityObjective.class);
        Join<QMQualityObjective, QModelTagData> join = root.join(attribute);
        query.where(join.get(QModelTagData_.tagId).in(entity.getId()));
        query.select(cb.countDistinct(root));
        return em.createQuery(query).getSingleResult();
    }
    
    long countQIWithMetaData(T entity, SetAttribute<QMBaseIndicator, QModelTagData> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<QMQualityIndicator> root = query.from(QMQualityIndicator.class);
        Join<QMQualityIndicator, QModelTagData> join = root.join(attribute);
        query.where(join.get(QModelTagData_.tagId).in(entity.getId()));
        query.select(cb.countDistinct(root));
        return em.createQuery(query).getSingleResult();
    }
    
    long countQMetricWithMetaData(T entity, SetAttribute<QMBaseIndicator, QModelTagData> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<QMMetric> root = query.from(QMMetric.class);
        Join<QMMetric, QModelTagData> join = root.join(attribute);
        query.where(join.get(QModelTagData_.tagId).in(entity.getId()));
        query.select(cb.countDistinct(root));
        return em.createQuery(query).getSingleResult();
    }
    
    
    long countprojectsWithMetaData(T entity, SetAttribute<Project, T> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Project> root = query.from(Project.class);
        Join<Project, T> join = root.join(attribute);
        query.where(join.in(entity));
        query.select(cb.countDistinct(root));
        return em.createQuery(query).getSingleResult();
    }
    
    
    

    List<User> getUsersWithMetaData(T entity, SetAttribute<User, T> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        Join<User, T> join = root.join(attribute);
        query.where(join.in(entity));
        return em.createQuery(query).getResultList();
    }

   
    List<Project> getProjectsWithMetaData(T entity, SetAttribute<Project, T> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);
        Root<Project> root = query.from(Project.class);
        Join<Project, T> join = root.join(attribute);
        query.where(join.in(entity));
        return em.createQuery(query).getResultList();
    }

    public <T extends MetaData> List<T> getAllAscendingByName(Class<T> clazz, long first, long count) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> from = query.from(clazz);
        query.orderBy(cb.asc(from.get(MetaData_.name)));
        return em.createQuery(query).
                setFirstResult((int) first).
                setMaxResults((int) count).
                getResultList();
    }

 
    public Collection<T> getExistingMetaDataAndCreateMissingFromChoicesString(
            Class<T> clazz, final Collection<String> choiceStrings) {
        Collection<T> metaData = new ArrayList<>(choiceStrings.size());
        for (String potentialId : choiceStrings) {
            try {
                Long id = Long.parseLong(potentialId);
                // try to find existing tag
                T existingMetaData = getById(clazz, id);
                if (existingMetaData == null) {
                    // not existent yet, create a new tag
                    existingMetaData = getByMetaDataOrCreate(clazz, potentialId);
                }
                metaData.add(existingMetaData);
            } catch (NumberFormatException e) {
                // potential id is not a number, create a new metadata out of it!
                metaData.add(getByMetaDataOrCreate(clazz, potentialId));
            }
        }
        return metaData;
    }

    public Collection<T> getExistingMetaDataFromChoicesString(
            Class<T> clazz, final Collection<String> choiceStrings) {
    	
    	Collection<T> metaData = new ArrayList<>(choiceStrings.size());
        for (String potentialId : choiceStrings) {
            try {
                Long id = Long.parseLong(potentialId);
                // try to find existing tag
                T existingMetaData = getById(clazz, id);
                if (existingMetaData == null) {
                    // not existent yet
                    throw new EntityNotFoundException(potentialId);
                }
                metaData.add(existingMetaData);
            } catch (NumberFormatException e) {
            	
            	 T existingMetaData = getByName(clazz, potentialId);
                 if (existingMetaData == null) {
                     // not existent yet
                     throw new EntityNotFoundException(potentialId);
                 } 
            	metaData.add(existingMetaData);
            }
        }
        return metaData;
    }

    public T getByMetaDataOrCreate(Class<T> clazz, final String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(clazz);
        Root<T> root = criteria.from(clazz);
        criteria.where(cb.equal(root.get(MetaData_.name), name));
        List<T> list = em.createQuery(criteria).getResultList();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return this.create(MetaData.newInstance(clazz, name));
        }
    }

    public boolean isInUse(T entity) {
        return false;
    }
    
    @Override
    public T create (T ent){
    	logger.info("MetaDataService: @Override create");
    	T newTag = super.update(ent);
    	
    	if (!ent.getClass().equals(QModelTagData.class)){
        	logger.info("Creating QModelTagData");
    		QModelTagData newQmTag = this.mergeQMTagData(new QModelTagData(newTag));
    		logger.info("Created QModelTagData " + newQmTag.getName());
    	}
    	return newTag;
    }
    
    @Override
    public T update (T ent){
    	logger.info("MetaDataService: @Override update");
    	T newTag;
    	if (!ent.getClass().equals(QModelTagData.class)){
    	  QModelTagData oldTag = this.getByQMTagData(ent.getId());
    	  logger.info("Updating qmtag from :"+oldTag.getName());
    	  oldTag.setName(ent.getName());
    	  QModelTagData newQMTag = this.mergeQMTagData(oldTag);
    	  logger.info("Updated qmtag to :"+newQMTag.getName());

    	}
    	newTag = super.update(ent);
    	return newTag;
    }
 
    @Override
    public void delete (T ent){
    	logger.info("MetaDataService: @Override delete");
    	super.delete(ent);
    	
    	if (!ent.getClass().equals(QModelTagData.class)){
        	logger.info("Deleting QModelTagData");
    		this.deleteQMTagData(ent.getId());
    	}
    }
    
    
    private QModelTagData mergeQMTagData (QModelTagData ent){
    	logger.info("createQMTagData");
    	ent = em.merge(ent);
    	logger.infof("persisted %s %s ...","QModelTagData", ent);
		return ent;
    }

    private void deleteQMTagData (Long idTag){
    	logger.info("deleteQMTagData");
    	QModelTagData qmtag = this.getByQMTagData(idTag);
    	Serializable id = qmtag.getId();
		qmtag = em.merge(qmtag);
		em.remove(qmtag);
		logger.infof("deleted %s with id %s and idTag %s...", "QModelTagData", id, idTag);
    }

    private List<QMQualityObjective> getQOWithMetaData(T entity, SetAttribute<QMBaseIndicator, QModelTagData> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<QMQualityObjective> query = cb.createQuery(QMQualityObjective.class);
        Root<QMQualityObjective> root = query.from(QMQualityObjective.class);
        Join<QMQualityObjective, QModelTagData> join = root.join(attribute);
        query.where(join.get(QModelTagData_.tagId).in(entity.getId()));
        return em.createQuery(query).getResultList();
    }

    private List<QMQualityIndicator> getQIWithMetaData(T entity, SetAttribute<QMBaseIndicator, QModelTagData> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<QMQualityIndicator> query = cb.createQuery(QMQualityIndicator.class);
        Root<QMQualityIndicator> root = query.from(QMQualityIndicator.class);
        Join<QMQualityIndicator, QModelTagData> join = root.join(attribute);
        query.where(join.get(QModelTagData_.tagId).in(entity.getId()));
        return em.createQuery(query).getResultList();
    }

    private List<QMMetric> getQMetricWithMetaData(T entity, SetAttribute<QMBaseIndicator, QModelTagData> attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<QMMetric> query = cb.createQuery(QMMetric.class);
        Root<QMMetric> root = query.from(QMMetric.class);
        Join<QMMetric, QModelTagData> join = root.join(attribute);
        query.where(join.get(QModelTagData_.tagId).in(entity.getId()));
        return em.createQuery(query).getResultList();
    }
    
    public QModelTagData getByQMTagData(final Long idTag) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<QModelTagData> criteria = cb.createQuery(QModelTagData.class);
        Root<QModelTagData> root = criteria.from(QModelTagData.class);
        
        Predicate predicate = cb.equal(root.<Long> get("tagId"), idTag);
        criteria.where(predicate);
        
        List<QModelTagData> list = em.createQuery(criteria).getResultList();
       
        if (list.size() > 0) {
            return list.get(0);
        } else {
        	return null;
        }
    }
    
    void removefromQM(T entity) {
    	
        List<QMQualityObjective> qmqo = getQOWithMetaData(entity, QMQualityObjective_.qModelTagData);
        for (QMQualityObjective qo : qmqo) {
        	QModelTagData ent = this.getByQMTagData(entity.getId());
        	qo.getQModelTagData().remove(ent);
            qo.setQModelTagData(qo.getQModelTagData());
            em.merge(qo);
            logger.info("Deleted QModelTagData"+ent.getName() + " from Quality Objective : " + qo.getName());
        }
        
        List<QMQualityIndicator> qmqi = getQIWithMetaData(entity, QMQualityIndicator_.qModelTagData);
        for (QMQualityIndicator qi : qmqi) {
        	QModelTagData ent = this.getByQMTagData(entity.getId());
        	qi.getQModelTagData().remove(ent);
            qi.setQModelTagData(qi.getQModelTagData());
            em.merge(qi);
            logger.info("Deleted QModelTagData"+ent.getName() + " from Quality Indicator : " + qi.getName());
        }
        
        List<QMMetric> qmqm = getQMetricWithMetaData(entity, QMMetric_.qModelTagData);
        for (QMMetric qm : qmqm) {
        	QModelTagData ent = this.getByQMTagData(entity.getId());
        	qm.getQModelTagData().remove(ent);
            qm.setQModelTagData(qm.getQModelTagData());
            em.merge(qm);
            logger.info("Deleted QModelTagData"+ent.getName() + " from Quality Metric : " + qm.getName());
        }
        
    }
}
