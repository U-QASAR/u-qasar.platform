/*
 * To change this license header, choose License Headers in QModel Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.WildcardContext;

import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QMTreeNode_;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.qmtree.QModelStatus;
import eu.uqasar.model.qmtree.QModel_;
import eu.uqasar.web.pages.qmtree.panels.filter.QMTreeFilterStructure;

/**
 *
 *
 */
@Stateless
public class QMTreeNodeService extends AbstractService<QMTreeNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4206888309357893418L;

	public QMTreeNodeService() {
		super(QMTreeNode.class);
	}

    @Override
    protected <P extends QMTreeNode> TermMatchingContext setFullTextSearchFields(WildcardContext wc, Class<P> clazz) {
        if (QMTreeNode.class.isAssignableFrom(clazz)) {
            return wc.onField("name").
                    andField("description").
                    andField("nodeKey").
                    andField("children.name").
                    andField("children.description").
                    andField("children.mKey");
        } else {
            return super.setFullTextSearchFields(wc, clazz);
        }
    }

	public List<QModel> getAllQModelsFiltered(QMTreeFilterStructure filter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QModel> query = cb.createQuery(QModel.class);
		Root<QModel> from = query.from(QModel.class);
		List<Predicate> predicates = new ArrayList<>();
		if (filter != null && filter.getIsActive() != null) {
			predicates.add(cb.equal(from.get(QModel_.isActive), filter.getIsActive()));
		}

		Predicate and = null;
		if (!predicates.isEmpty()) {
			and = cb.and(predicates.toArray(new Predicate[predicates.size()]));
		}
		
		if(and != null) {
			query.where(and);
		}

        return em.createQuery(query).getResultList();
	}

	
	public List<QModel> getAllQModels() {
		return getAllQModelsFiltered(new QMTreeFilterStructure());
	}
	
	public QModel getQModelByName(String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QModel> query = cb.createQuery(QModel.class);
		Root<QModel> root = query.from(QModel.class);
		Predicate condition = cb.equal(root.get(QModel_.name), name);
		query.where(condition);
		return em.createQuery(query).getSingleResult();
	}
	
	public <T extends QMTreeNode> T addNewChild(T parent, T child) {
		if(!parent.getChildren().contains(child)) {
			parent.getChildren().add(child);
			child.setParent(parent);
		}
		update(parent);
		return (T) ((LinkedList<QMTreeNode>)parent.getChildren()).getLast();
	}
	
	public QModel getQModel(Long id) {
		return findTreeNode(id);
	}

	public QMQualityObjective getQualityObjective(Long id) {
		return findTreeNode(id);
	}

	public QMQualityIndicator getQualityIndicator(Long id) {
		return findTreeNode(id);
	}

	public QMMetric getMetric(Long id) {
		return findTreeNode(id);
	}

	public <T extends QMTreeNode> T removeTreeNode(
			Long id) {
		T node = getTreeNode(id);
		// TODO what todo with persisting??
		if (node != null) {
			if (node instanceof QModel) {
				delete(node);
				//detach(node);
			} else {
				QMTreeNode oldParent = node.getParent();
				List<QMTreeNode> oldParentChildren = oldParent.getChildren();
				if (oldParentChildren != null ) {
					oldParentChildren.remove(node);
					oldParent.setChildren(oldParentChildren);
					//checkCompleted(oldParent);
				}
//				node.getParent().getChildren().remove(node);
			}
		}
		return node;
	}

	@SuppressWarnings("unchecked")
	public <T extends QMTreeNode> T getTreeNode(
			Long id) {
		return (T) findTreeNode(id);
	}

	@SuppressWarnings("unchecked")
	public <T extends QMTreeNode> T getTreeNodeByKey(
			final String key) {
		return (T) findTreeNodeByKey(key);
	}

	private <T extends QMTreeNode> T findTreeNode(Long id) {
		return (T) getById(id);
	}

	private <T extends QMTreeNode> T findTreeNodeByKey(final String key) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QMTreeNode> query = cb.createQuery(QMTreeNode.class);
		Root<QMTreeNode> root = query.from(QMTreeNode.class);
		query.where(cb.equal(root.get(QMTreeNode_.nodeKey), key));
		List<QMTreeNode> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		return (T) resultList.get(0);
	}

	
	/**
	 * Check if there is other quality model active.
	 * @param current QModel
	 * @return true if there is other QModel active
	 */
	public boolean checkOthersQMActive(QModel current) {
		boolean found=false;
		
		List<QModel> resultList = getAllQModels();
		Iterator<QModel> it = resultList.iterator();
		QModel next;
		while (it.hasNext() && !found){
			next = it.next();
			if (next.getIsActive().equals(QModelStatus.Active) && !next.getId().equals(current.getId())){
				//add companyId if its required
				found=true;
			}
		}
		return found;
	}

	/**
	 * Check if there is a quality model active.
	 * @return true if there is a QModel active
	 */
	public boolean checkQMActive() {
		boolean found=false;
		
		List<QModel> resultList = getAllQModels();
		Iterator<QModel> it = resultList.iterator();
		QModel next;
		while (it.hasNext() && !found){
			next = it.next();
			if (next.getIsActive().equals(QModelStatus.Active)){
				found=true;
			}
		}
		return found;
	}	
	

	/**
	 * Get the active QModel or null if no active QModels available
	 * @return
	 */
	public QModel getActiveQModel() {
		for (QModel qModel : getAllQModels()) {
			if (qModel.getIsActive().equals(QModelStatus.Active)) {
				logger.info("Found active QModel " +qModel);
				return qModel;
			}
		}
		return null;
	}
	

	/**
	 * Get the nodeKeys of all QModels
	 * @return list of nodeKeys
	 */
	public List<String> getAllNodeKeys() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QModel> query = cb.createQuery(QModel.class);
		query.from(QModel.class);
		List<QModel> resultList = em.createQuery(query).getResultList();
		List<String> qmodelNodeKeysList = new ArrayList<>();
		for (QModel node : resultList) {
			qmodelNodeKeysList.add(node.getNodeKey());
		}
		return qmodelNodeKeysList;
	}

}

