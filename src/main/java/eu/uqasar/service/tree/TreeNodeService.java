package eu.uqasar.service.tree;

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


import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.Project_;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.model.tree.TreeNode_;
import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AbstractService;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.pages.tree.panels.filter.TreeFilterStructure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.WildcardContext;

/**
 *
 *
 */
@Stateless
public class TreeNodeService extends AbstractService<TreeNode> {

	private static final long serialVersionUID = 3722336656076753579L;

	public TreeNodeService() {
		super(TreeNode.class);
	}

	
    @Override
    protected <P extends TreeNode> TermMatchingContext setFullTextSearchFields(WildcardContext wc, Class<P> clazz) {
        if (TreeNode.class.isAssignableFrom(clazz)) {
            return wc.onField("name").
                    andField("description").
                    andField("nodeKey").
                    andField("children.name").
                    andField("children.description").
                    andField("children.nodeKey");
            
        } else {
            return super.setFullTextSearchFields(wc, clazz);
        }
    }

    /**
     * Get a list of filtered projects
     * @param filter
     * @return
     */
    private List<Project> getAllProjectsFiltered(TreeFilterStructure filter) {
		List<Project> resultList = new ArrayList<>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> query = cb.createQuery(Project.class);
		Root<Project> from = query.from(Project.class);
		List<Predicate> predicates = new ArrayList<>();
		if (filter != null && filter.getQualityStatus() != null) {
			predicates.add(cb.equal(from.get(Project_.qualityStatus), filter.getQualityStatus()));
		}
		if (filter != null && filter.getLCStage() != null) {
			predicates.add(cb.equal(from.get(Project_.lifeCycleStage), filter.getLCStage()));
		}
		if (filter != null && filter.getStartDate() != null) {
			predicates.add(cb.greaterThanOrEqualTo(from.get(Project_.startDate), filter.getStartDate()));
		}
		if (filter != null && filter.getEndDate() != null) {
			predicates.add(cb.lessThanOrEqualTo(from.get(Project_.endDate), filter.getEndDate()));
		}
		Predicate and = null;
		if (!predicates.isEmpty()) {
			and = cb.and(predicates.toArray(new Predicate[predicates.size()]));
		}
		if(and != null) {
			if(filter != null && filter.getCurrentlyEditedProjectId() != null) {
				query.where(cb.or(cb.equal(from.get(Project_.id), filter.getCurrentlyEditedProjectId()), and));
			} else {
				query.where(and);
			}
//		} else {
//			if(filter != null && filter.getCurrentlyEditedProjectId() != null) {
//				query.where(cb.equal(from.get(Project_.id), filter.getCurrentlyEditedProjectId()));
//			}
		}
		query.orderBy(cb.asc(from.get(Project_.endDate)));
		if (!em.createQuery(query).getResultList().isEmpty()) {
			resultList = em.createQuery(query).getResultList();
		}
		return resultList;
	}

	/**
	 * Get all the projects
	 * @return
	 */
	public List<Project> getAllProjects() {
		return getAllProjectsFiltered(new TreeFilterStructure());
	}

	/**
	 * 
	 * @param filter
	 * @return
	 */
    public List<Project> getAllProjectsOfLoggedInUser(TreeFilterStructure filter) {

    	// If the user is admin, return all the projects (filtered)
    	if (UQasar.getSession().isUserAdmin()) {
    		return getAllProjectsFiltered(filter);    		
    	}
    	
    	User loggedInUser = UQasar.getSession().getLoggedInUser();
       
        List<Project> projects = getAllProjectsFiltered(filter);
        List<Project> returnProjects = new LinkedList<>();
        for (Project project : projects) {
            List<Team> teams = project.getTeams();
            for (Team team : teams) {
                Collection<User> users = team.getAllUsers();

                if (users.contains(loggedInUser)) {
                    returnProjects.add(project);
                }
            }

        }

        return returnProjects;
    }
    
    
    /**
     * Get all the projects for the logged in user.
     * @return
     */
    public List<Project> getAllProjectsOfLoggedInUser() {
    	
    	// If the user is admin, return all the projects
    	if (UQasar.getSession().isUserAdmin()) {
    		return getAllProjects();    		
    	}
   	
        User loggedInUser = UQasar.getSession().getLoggedInUser();

        List<Project> projects = getAllProjectsFiltered(new TreeFilterStructure());
        List<Project> returnProjects = new LinkedList<>();
        for (Project project : projects) {
            List<Team> teams = project.getTeams();
            for (Team team : teams) {
                Collection<User> users = team.getAllUsers();

                if (users.contains(loggedInUser)) {
                    returnProjects.add(project);
                }
            }

        }

        return returnProjects;
    }
	
	
	/**
	 * Get the names of Treenodes
	 * @return 
	 */
	public List<String> getAllTreeNodeNames() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TreeNode> query = cb.createQuery(TreeNode.class);
		query.from(TreeNode.class);
		List<TreeNode> resultList = em.createQuery(query).getResultList();
		List<String> treeNodeNamesList = new ArrayList<>();
		for (TreeNode node : resultList) {
			treeNodeNamesList.add(node.getName());
		}
		return treeNodeNamesList;
	}
	
	/**
	 * Get a project by its name
	 * @param name
	 * @return
	 */
	public Project getProjectByName(String name) {
		Project project = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> query = cb.createQuery(Project.class);
		Root<Project> root = query.from(Project.class);
		Predicate condition = cb.equal(root.get(Project_.name), name);
		query.where(condition);
		if (!em.createQuery(query).getResultList().isEmpty()) {
			project = em.createQuery(query).getSingleResult();
		}
		return project;
	}

	/**
	 * Get a project by using its key
	 * @param key
	 * @return
	 */
	public Project getProjectByKey(String key) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> query = cb.createQuery(Project.class);
		Root<Project> root = query.from(Project.class);
		Predicate condition = cb.equal(root.get(Project_.nodeKey), key);
		query.where(condition);
		return em.createQuery(query).getSingleResult();
	}

	/**
	 * Add a child node to a parent
	 * @param parent
	 * @param child
	 * @return
	 */
	public <T extends TreeNode> T addNewChild(T parent, T child) {
		if (!parent.getChildren().contains(child)) {
			parent.getChildren().add(child);
			child.setParent(parent);
		}
		update(parent);
		return (T) parent.getChildren().getLast();
	}

	/**
	 * Get a project by ID
	 * @param id
	 * @return
	 */
	public Project getProject(Long id) {
		return findTreeNode(id);
	}

	/**
	 * Get QO by ID
	 * @param id
	 * @return
	 */
	public QualityObjective getQualityObjective(Long id) {
		return findTreeNode(id);
	}

	/**
	 * Get QI by ID
	 * @param id
	 * @return
	 */
	public QualityIndicator getQualityIndicator(Long id) {
		return findTreeNode(id);
	}

	/**
	 * Get metric by ID
	 * @param id
	 * @return
	 */
	public Metric getMetric(Long id) {
		return findTreeNode(id);
	}

	/**
	 * Remove a node from the tree and from its parent (if exists)
	 * @param id
	 * @return
	 */
	public <T extends TreeNode> T removeTreeNode(
			Long id) {
		T node = getTreeNode(id);
		if (node != null) {
			if (node instanceof Project) {
				delete(node);
			} else {
				TreeNode oldParent = node.getParent();
				List<TreeNode> oldParentChildren = oldParent.getChildren();
				if (oldParentChildren != null) {
					oldParentChildren.remove(node);
					oldParent.setChildren(oldParentChildren);
				}
			}
		}
		return node;
	}

	/**
	 * Get TreeNode by ID
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends TreeNode> T getTreeNode(
			Long id) {
		return (T) findTreeNode(id);
	}

	/**
	 * Get TreeNode by Key
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends TreeNode> T getTreeNodeByKey(
			final String key) {
		return (T) findTreeNodeByKey(key);
	}
	
	/**
	 * Get a TreeNode by name
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends TreeNode> T getTreeNodeByName(final String name) {
		return (T) findTreeNodeByName(name);
	}
	

	/**
	 * Get a TreeNode by ID
	 * @param id
	 * @return
	 */
	private <T extends TreeNode> T findTreeNode(Long id) {
		return (T) getById(id);
	}

	/**
	 * Get a TreeNode by its key; in case of multiple results, return the first one.
	 * @param key
	 * @return
	 */
	private <T extends TreeNode> T findTreeNodeByKey(final String key) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TreeNode> query = cb.createQuery(TreeNode.class);
		Root<TreeNode> root = query.from(TreeNode.class);
		query.where(cb.equal(root.get(TreeNode_.nodeKey), key));
		List<TreeNode> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		return (T) resultList.get(0);
	}
	
	/**
	 * Get a TreeNode by its name; in case of multiple results, return the first one.
	 * @param name
	 * @return
	 */
	private <T extends TreeNode> T findTreeNodeByName(final String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TreeNode> query = cb.createQuery(TreeNode.class);
		Root<TreeNode> root = query.from(TreeNode.class);
		query.where(cb.equal(root.get(TreeNode_.name), name));
		List<TreeNode> resultList = em.createQuery(query).setMaxResults(1).getResultList();
		if (resultList.isEmpty()) {
			return null;
		}
		return (T) resultList.get(0);
	}
	
	/**
	 * Get the nodeKeys of all quality projects.
	 * @return list of nodeKeys
	 */
	public List<String> getAllNodeKeys() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> query = cb.createQuery(Project.class);
		query.from(Project.class);
		List<Project> resultList = em.createQuery(query).getResultList();
		List<String> nodeKeysList = new ArrayList<>();
		for (Project node : resultList) {
			nodeKeysList.add(node.getNodeKey());
		}
		return nodeKeysList;
	}
}
