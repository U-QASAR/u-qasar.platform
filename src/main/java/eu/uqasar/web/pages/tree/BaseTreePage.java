package eu.uqasar.web.pages.tree;

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
import java.util.UUID;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.jboss.solder.logging.Logger;

import eu.uqasar.model.analytic.Analysis;
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.tree.ITreeNode;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.model.tree.historic.Snapshot;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.components.tree.DeletionConfirmationModal;
import eu.uqasar.web.components.tree.TreePanel;
import eu.uqasar.web.components.tree.util.Tree;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.metric.MetricEditPage;
import eu.uqasar.web.pages.tree.metric.MetricViewPage;
import eu.uqasar.web.pages.tree.panels.filter.FilterPanel;
import eu.uqasar.web.pages.tree.panels.filter.TreeFilterStructure;
import eu.uqasar.web.pages.tree.projects.ProjectEditPage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.projects.ProjectWizardPage;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorEditPage;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorViewPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveEditPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveViewPage;

public abstract class BaseTreePage<Type extends TreeNode>
		extends BasePage {

	/**
	 *
	 */
	private static final long serialVersionUID = -7161895697889951287L;
	private final Logger logger = Logger.getLogger(getClass());

	@Inject
	TreeNodeService treeNodeService;
	@Inject
	QMTreeNodeService qmTreeNodeService;

	@SuppressWarnings("unused")
	private WebMarkupContainer content;
	private TreePanel treePanel;
	private final FilterPanel filterPanel;
	private Long currentlySelectedNodeId;
    private Long initiallyRequestedNodeId;
	private TreeNode currentlySelectedNode;
    private TreeNode initiallyRequestedNode;
	private TreeNode currentParentProjectNode;
    private TreeNode currentParentQualityObjective;
    private TreeNode currentParentQualityIndicator;
	private DeletionConfirmationModal deleteConfirmationModal;
	private static final String Container_Id = "content";

	protected BaseTreePage(final PageParameters parameters) {
		super(parameters);

		add(filterPanel = new FilterPanel("filter"){
			@Override
			public void applyClicked(AjaxRequestTarget target, Form<?> form) {
				TreeFilterStructure filter = this.getFilter();
				filter.setCurrentlyEditedProjectId(getCurrentlyEditedProjectId());
				unselectNodeCurrentlySelectedNode();
				treePanel.updateFilter(filter);
				target.add(treePanel);
			}

			@Override
			public void resetClicked(AjaxRequestTarget target, Form<?> form) {
				treePanel.updateFilter(new TreeFilterStructure());
				target.add(treePanel);
			}
		});
		add(treePanel = getTreePanel(filterPanel.getFilter()));
		add(content = new WebMarkupContainer(Container_Id));
		add(deleteConfirmationModal = createDeleteConfirmationModal());
	}
	
	private Long getCurrentlyEditedProjectId() {
		if(initiallyRequestedNode instanceof Project){
			return initiallyRequestedNode.getId();
		} else {
			if(initiallyRequestedNode != null) {
				return initiallyRequestedNode.getProject().getId();
			} 
		}
		return null;
	}
	
	private TreePanel getTreePanel(TreeFilterStructure filter) {
        return new TreePanel("tree", filter) {
            private static final long serialVersionUID1 = -3862120828153062154L;

            @Override
            public void onNodeClicked(AjaxRequestTarget target,
                    IModel<TreeNode> nodeModel) {
                BaseTreePage.this.handleNodeClick(target, nodeModel);
            }

            @Override
            public void onMoveUpClicked(AjaxRequestTarget target,
                    IModel<TreeNode> node) {
                moveNode(node.getObject(), target, false);
            }

            @Override
            public void onMoveDownClicked(AjaxRequestTarget target,
                    IModel<TreeNode> node) {
                moveNode(node.getObject(), target, true);
            }

            @Override
            public void onEditClicked(AjaxRequestTarget target,
                    IModel<TreeNode> node) {
                if (node.getObject() instanceof Project) {
                    setResponsePage(ProjectEditPage.class,
                            BaseTreePage.forProject((Project) node.getObject()));
                } else if (node.getObject() instanceof QualityObjective) {
                    setResponsePage(
                            QualityObjectiveEditPage.class,
                            BaseTreePage
                            .forQualityObjective((QualityObjective) node
                                    .getObject()));
                } else if (node.getObject() instanceof QualityIndicator) {
                    setResponsePage(
                            QualityIndicatorEditPage.class,
                            BaseTreePage
                            .forQualityIndicator((QualityIndicator) node
                                    .getObject()));
                } else if (node.getObject() instanceof Metric) {
                    setResponsePage(MetricEditPage.class,
                            BaseTreePage.forMetric((Metric) node.getObject()));
                }
            }

            @Override
            public void onDeleteClicked(AjaxRequestTarget target,
                    IModel<TreeNode> node) {
                deleteNode(node.getObject(), target);
            }

            @Override
            public void onNewClicked(AjaxRequestTarget target,
                    IModel<TreeNode> node, Class<? extends TreeNode> newNodeType) {
                TreeNode newNode = null;
                if (newNodeType == Project.class) {
                    createNewProject();
                } else if (newNodeType == QualityObjective.class) {
                    newNode = createNewQualityObjective(node.getObject().getProject());
                } else if (newNodeType == QualityIndicator.class) {
                    newNode = createNewQualityIndicator(node.getObject()
                            .getQualityObjective());
                } else if (newNodeType == Metric.class) {
                    newNode = createNewMetric(node.getObject().getQualityIndicator());
                }

                if (newNode instanceof QualityObjective) {
                    setResponsePage(QualityObjectiveEditPage.class, forQualityObjective((QualityObjective) newNode, true));
                } else if (newNode instanceof QualityIndicator) {
                    setResponsePage(QualityIndicatorEditPage.class, forQualityIndicator((QualityIndicator) newNode, true));
                } else if (newNode instanceof Metric) {
                    setResponsePage(MetricEditPage.class, forMetric((Metric) newNode, true));
                }
            }

//			@Override
//			public void onNewProjectFromQMClicked(AjaxRequestTarget target,
//					IModel<TreeNode> node) {
//				createProjectTreeLeaves(node.getObject(), target);
//			}
        };
	}

//	/**
//	 * Create quality project tree nodes from a quality model TODO: take only
//	 * parts of the quality tree into account
//	 *
//	 * @param node
//	 * @param target
//	 */
//	protected void createProjectTreeLeaves(final TreeNode node, AjaxRequestTarget target) {
//		logger.info("Associate a quality model to the quality project");
//
//		// Check whether there is a quality model active
//		QModel activeQModel = qmTreeNodeService.getActiveQModel();
//		if (activeQModel != null) {
//			// Mark the current tree node as the parent where start from
//			// to add nodes to the quality project tree
//			currentParentProjectNode = node;
//			// Start traversal from the root of the tree
//			preorder(activeQModel);
//			target.add(BaseTreePage.this.treePanel);
//		}
//	}

	/**
	 * Traverse the quality model tree in preorder and associate nodes to the
	 * quality project.
	 *
	 * @param node
	 */
    private void preorder(QMTreeNode node) {
		if (node == null) {
			return;
		}
		logger.info("Traversing quality tree in preorder..." + node.toString());

		addNodeToProjectTree(node);
		// If the node has children, iterate those
		for (Object o : node.getChildren()) {
			node = (QMTreeNode) o;
			preorder(node);
		}
	}

	/**
	 * Associate a quality model tree node to the quality project
	 *
	 * @param node
	 */
	private void addNodeToProjectTree(QMTreeNode node) {
		if (node instanceof QModel) {
			QModel qm = (QModel) node;
			long randomId = UUID.randomUUID().toString().hashCode();
			Project project = (Project) treeNodeService.create(new Project("Project from " + qm.getName(), String.format("prj-%s", randomId)));
			logger.info("[Quality Model] " + qm);
			project.setQmodel(qm);
			currentParentProjectNode = project;
		} else if (node instanceof QMQualityObjective) {
			QMQualityObjective qmqo = (QMQualityObjective) node;
			logger.info("[Quality Objective] " + qmqo);
			QualityObjective qo = new QualityObjective(qmqo, (Project) currentParentProjectNode);
			logger.info("qo: " + qo);
			currentParentQualityObjective = qo;
		} else if (node instanceof QMQualityIndicator) {
			QMQualityIndicator qmqi = (QMQualityIndicator) node;
			logger.info("[Quality Indicator] " + qmqi);
            currentParentQualityIndicator = new QualityIndicator(qmqi, (QualityObjective) currentParentQualityObjective);
		} else if (node instanceof QMMetric) {
			QMMetric qmm = (QMMetric) node;
			logger.info("[Quality Metric] " + qmm);
			//Metric m = new Metric(qmm.getName(), (QualityIndicator) currentParentQualityIndicator);
			new Metric(qmm, (QualityIndicator) currentParentQualityIndicator);
		}
	}

	private void moveNode(final TreeNode node,
                          AjaxRequestTarget target, boolean down) {
		boolean up = !down;
		if (!(node instanceof Project)) {
			boolean moved = false;
			TreeNode oldParent = node.getParent();
			if (down) {
				moved = node.changePositionWithNextSibling();
			} else if (up) {
				moved = node.changePositionWithPreviousSibling();
			}
			TreeNode newParent = node.getParent();
			if (moved) {
				if (oldParent.equals(newParent)) {
					treeNodeService.update(oldParent);
				}
				treeNodeService.update(node.getParent());
				expandTreeToSelectedNode(node.getId());
				target.add(BaseTreePage.this.treePanel);
			}
		}
	}

	private void deleteNode(final TreeNode node,
                            AjaxRequestTarget target) {
		target.add(getUpdatedDeleteConfirmationModal());
		deleteConfirmationModal.appendShowDialogJavaScript(target);
	}

	private void createNewProject() {
		//long randomId = UUID.randomUUID().toString().hashCode();
		//Project project = (Project) treeNodeService.create(new Project("Project", String.format("prj-%s", randomId)));
		setResponsePage(ProjectWizardPage.class);
	}

	private TreeNode createNewQualityObjective(Project parent) {
		if (parent != null) {
			QualityObjective obj = new QualityObjective("Quality Objective", parent);
			return treeNodeService.addNewChild(parent, obj);
		}
		return null;
	}

	private TreeNode createNewQualityIndicator(QualityObjective parent) {
		if (parent != null) {
			QualityIndicator indi = new QualityIndicator("Quality Indicator", parent);
			return treeNodeService.addNewChild(parent, indi);
		}
		return null;
	}

	private TreeNode createNewMetric(QualityIndicator parent) {
		if (parent != null) {
			Metric metric = new Metric("Metric", parent);
			return treeNodeService.addNewChild(parent, metric);
		}
		return null;
	}
	
	private void unselectNodeCurrentlySelectedNode() {
		this.currentlySelectedNode = null;
		this.currentlySelectedNodeId = null;
		treePanel.unselectNode();
	}

	public Tree getTree() {
		return getTreePanel().getTree();
	}

	private TreePanel getTreePanel() {
		return this.treePanel;
	}

	public ITreeNode<String> getCurrentlySelectedNode() {
		return this.currentlySelectedNode;
	}

	public Long getCurrentlySelectedNodeId() {
		return this.currentlySelectedNodeId;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forUrl("assets/css/tree/tree.css"));
		response.render(CssHeaderItem.forUrl("assets/css/tree/panels.css"));
	}

	protected abstract WebMarkupContainer getContent(final String markupId,
                                                     final IModel<Type> model);

	@SuppressWarnings("unchecked")
	@Override
	protected void onConfigure() {
		super.onConfigure();
		currentlySelectedNodeId = initiallyRequestedNodeId = getRequestedNodeId();

		if (currentlySelectedNodeId == null) {
			// create project 
			long randomId = UUID.randomUUID().toString().hashCode();
			Project project = (Project) treeNodeService.create(new Project("Project", String.format("prj-%s", randomId)));
			currentlySelectedNodeId = initiallyRequestedNodeId = project.getId();
		}

		currentlySelectedNode = initiallyRequestedNode = getRequestedNode(currentlySelectedNodeId);
		if (currentlySelectedNodeId != null) {
			selectCurrentNode(currentlySelectedNodeId);
			expandTreeToSelectedNode(currentlySelectedNodeId);
			replace(content = getContent(Container_Id,
					Model.of((Type) currentlySelectedNode)));
		}
		addOrReplace(getUpdatedDeleteConfirmationModal());
	}

	void handleNodeClick(AjaxRequestTarget target,
                         IModel<TreeNode> nodeModel) {
		TreeNode node = nodeModel.getObject();
		setRedirectToNodePage(node);
		currentlySelectedNode = node;
		currentlySelectedNodeId = node.getId();
	}

	private void setRedirectToNodePage(TreeNode node) {
		if (node instanceof Project) {
			setResponsePage(ProjectViewPage.class, forProject((Project) node));
		} else if (node instanceof QualityObjective) {
			setResponsePage(QualityObjectiveViewPage.class,
					forQualityObjective((QualityObjective) node));
		} else if (node instanceof QualityIndicator) {
			setResponsePage(QualityIndicatorViewPage.class,
					forQualityIndicator((QualityIndicator) node));
		} else if (node instanceof Metric) {
			setResponsePage(MetricViewPage.class, forMetric((Metric) node));
		}
	}

	private DeletionConfirmationModal createDeleteConfirmationModal() {
		deleteConfirmationModal = new DeletionConfirmationModal(
				"deleteConfirmationModal", currentlySelectedNode) {

					private static final long serialVersionUID = -2647776051399096877L;

					@Override
					public void onConfirmed(AjaxRequestTarget target) {
						/* either project or node of project */
						if (currentlySelectedNode instanceof Project) {
							treeNodeService.delete(currentlySelectedNodeId);

							List<Project> projects = treeNodeService.getAllProjects();
							if (projects.isEmpty()) {
								// TODO what todo if no project left?
								setResponsePage(AboutPage.class);
							} else {
								setRedirectToNodePage(projects.get(0));
							}
						} else {
							if (currentlySelectedNode instanceof QualityObjective) {
								Project p = currentlySelectedNode.getProject();
				                // Update the project tree content (metric values and computed values of QOs and QIs based on metrics)
				                UQasarUtil.updateTree(p);
								p.removeChild(currentlySelectedNode);
								setRedirectToNodePage(currentlySelectedNode.getParent());
							} else {
								TreeNode n = currentlySelectedNode.getParent();
				                // Update the project tree content (metric values and computed values of QOs and QIs based on metrics)
				                UQasarUtil.updateTree(n.getProject());
								n.removeChild(currentlySelectedNode);
								setRedirectToNodePage(n);
							}
							/*for(Project p : treeNodeService.getAllProjects()) {
							 if(p.getChildren().contains(currentlySelectedNode)) {
							 p.removeChild(currentlySelectedNode);
							 setRedirectToNodePage(currentlySelectedNode.getParent());
							 }
							 }*/
						}
						/* previous code
						
						 TreeNode removedTreeNode = treeNodeService
						 .removeTreeNode(currentlySelectedNode.getId());
						
						 if (removedTreeNode != null) {
						 if (!(removedTreeNode instanceof Project)) {
						 setRedirectToNodePage(removedTreeNode.getParent());
						 } else {
						 // TODO where to go, when there is no project anymore?
						 setResponsePage(AboutPage.class);
						 }
						 }*/
					}
				};
		return deleteConfirmationModal;
	}

	private DeletionConfirmationModal getUpdatedDeleteConfirmationModal() {
		deleteConfirmationModal.update(currentlySelectedNode);
		return deleteConfirmationModal;
	}

	private Long getRequestedNodeId() {
		StringValue id = getPageParameters().get("id");
		if (id.isEmpty()) {
			String key = getPageParameters().get("project-key")
					.toOptionalString();
			if (key != null) {
				ITreeNode<String> node = treeNodeService
						.getTreeNodeByKey(key);
				if (node != null) {
					id = StringValue.valueOf(node.getId());
				} else {
					// TODO give translated message for the 404 reason
					throw new AbortWithHttpErrorCodeException(404);
				}
			}
		}
		return id.toOptionalLong();
	}

	@SuppressWarnings("unchecked")
    private Type getRequestedNode() {
		if (getRequestedNodeId() == null) {
			// create project 
			return (Type) (new Project());
		} else {
			return (Type) treeNodeService.getTreeNode(getRequestedNodeId());
		}
	}

	@SuppressWarnings("unchecked")
    private Type getRequestedNode(final Long requestedNodeId) {
		return (Type) treeNodeService.getTreeNode(requestedNodeId);
	}

	private void selectCurrentNode(final Long requestedNodeId) {
		TreeNode node = treeNodeService
				.getTreeNode(requestedNodeId);
		if (node == null) {
			throw new eu.uqasar.exception.model.EntityNotFoundException(TreeNode.class, String.valueOf(requestedNodeId));
		}
		// select the requested node
		treePanel.getTree().selectNode(node);
	}

	private void expandTreeToSelectedNode(final Long requestedNodeId) {
		TreeNode node = treeNodeService
				.getTreeNode(requestedNodeId);
		// add nodes and all parents to initial tree expansion
		while (node != null) {
			treePanel.getTree().getModel().getObject().add(node);
			node = node.getParent();
		}
	}

	@Override
	protected IModel<String> getPageTitleModel() {
		Type node = getRequestedNode();
		if (node != null) {
			return new StringResourceModel("page.title", this, Model.of(node));
		}
		return new StringResourceModel("page.title", this, null);
	}

	public static PageParameters forNode(ITreeNode<String> node) {
		if (node instanceof Project) {
			return forProject((Project) node);
		} else if (node instanceof QualityObjective) {
			return forQualityObjective((QualityObjective) node);
		} else if (node instanceof QualityIndicator) {
			return forQualityIndicator((QualityIndicator) node);
		} else if (node instanceof Metric) {
			return forMetric((Metric) node);
		}
		return new PageParameters();
	}

	public static PageParameters forProject(final Project project,
			final boolean newProject) {
		PageParameters params = new PageParameters();
		params.add("isNew", newProject);
		if (project != null) {
			return params.add("project-key", project.getNodeKey());
		} else {
			return params;
		}
	}

	public static PageParameters forProject(Project project) {
		if (project != null) {
			return forProject(project.getNodeKey());
		} else {
			return new PageParameters();
		}
	}

	public static PageParameters forProject(final String projectKey) {
		return new PageParameters().add("project-key", projectKey);
	}

	// New QO
	private static PageParameters forQualityObjective(QualityObjective objective,
                                                      final boolean newQO) {
		PageParameters params = forProject(objective.getProject()).add("id", objective.getId());
		params.add("isNew", newQO);
		return params;
	}

	public static PageParameters forQualityObjective(QualityObjective objective) {
		return forProject(objective.getProject()).add("id", objective.getId());
	}

	// New QI
	private static PageParameters forQualityIndicator(QualityIndicator indicator,
                                                      final boolean newQI) {
		PageParameters params
				= forProject(indicator.getProject()).add("id", indicator.getId());
		params.add("isNew", newQI);
		return params;
	}

	public static PageParameters forQualityIndicator(QualityIndicator indicator) {
		return forProject(indicator.getProject()).add("id", indicator.getId());
	}

	// New QM
	private static PageParameters forMetric(Metric metric, final boolean newM) {
		PageParameters params = forProject(metric.getProject()).add("id", metric.getId());
		params.add("isNew", newM);
		return params;
	}

	public static PageParameters forMetric(Metric metric) {
		return forProject(metric.getProject()).add("id", metric.getId());
	}
	
	public static PageParameters forAnalysis(Analysis analysis) {
		return new PageParameters().add("analysis-id", analysis.getId());
	}

	public static PageParameters forSnapshot(Snapshot snapshot){
		return new PageParameters().add("snap-id", snapshot.getId());
	}
}
