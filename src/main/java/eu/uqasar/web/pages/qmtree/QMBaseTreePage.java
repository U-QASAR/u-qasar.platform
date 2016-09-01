package eu.uqasar.web.pages.qmtree;

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

import eu.uqasar.model.qmtree.IQMTreeNode;
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.qmtree.QModelStatus;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.web.components.qmtree.QMDeletionConfirmationModal;
import eu.uqasar.web.components.qmtree.QMTreePanel;
import eu.uqasar.web.components.qmtree.util.QMTree;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.qmtree.metric.QMMetricEditPage;
import eu.uqasar.web.pages.qmtree.metric.QMMetricViewPage;
import eu.uqasar.web.pages.qmtree.panels.filter.QMFilterPanel;
import eu.uqasar.web.pages.qmtree.panels.filter.QMTreeFilterStructure;
import eu.uqasar.web.pages.qmtree.qmodels.QModelEditPage;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;
import eu.uqasar.web.pages.qmtree.quality.indicator.QMQualityIndicatorEditPage;
import eu.uqasar.web.pages.qmtree.quality.indicator.QMQualityIndicatorViewPage;
import eu.uqasar.web.pages.qmtree.quality.objective.QMQualityObjectiveEditPage;
import eu.uqasar.web.pages.qmtree.quality.objective.QMQualityObjectiveViewPage;

public abstract class QMBaseTreePage<Type extends QMTreeNode>
		extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -636602588297568690L;

	@Inject
	private QMTreeNodeService qmtreeNodeService;

	@SuppressWarnings("unused")
	private WebMarkupContainer content;
	private QMTreePanel treePanel;
	private final QMFilterPanel filterPanel;
	private Long currentlySelectedNodeId;
    private Long initiallyRequestedNodeId;
	private QMTreeNode currentlySelectedNode;
    private QMTreeNode initiallyRequestedNode;
	private QMDeletionConfirmationModal deleteConfirmationModal;
	private static final String Container_Id = "content";
	private static final Logger logger = Logger.getLogger(QMBaseTreePage.class);

	protected QMBaseTreePage(final PageParameters parameters) {
		super(parameters);
		
		add(filterPanel = new QMFilterPanel("filter"){
			@Override
			public void applyClicked(AjaxRequestTarget target, Form<?> form) {
				
				QMTreeFilterStructure filter = this.getFilter();
				filter.setCurrentlyEditedQModelId(getCurrentlyEditedQModelId());
				unselectNodeCurrentlySelectedNode();
				treePanel.updateFilter(filter);
				target.add(treePanel);
			}

			@Override
			public void resetClicked(AjaxRequestTarget target, Form<?> form) {
				treePanel.updateFilter(new QMTreeFilterStructure());
				target.add(treePanel);
			}
		});
		add(treePanel = getTreePanel(filterPanel.getFilter()));
		
		add(content = new WebMarkupContainer(Container_Id));
		add(deleteConfirmationModal = createDeleteConfirmationModal());
	}
	
	
	private Long getCurrentlyEditedQModelId() {
		if(initiallyRequestedNode instanceof QModel){
			return initiallyRequestedNode.getId();
		} else {
			if(initiallyRequestedNode != null) {
				return initiallyRequestedNode.getQModel().getId();
			} 
		}
		return null;
	}
	
	private void moveNodeQM(final QMTreeNode node, AjaxRequestTarget target, boolean down) {
		boolean up = !down;
		if (!(node instanceof QModel)) {
			boolean moved = false;
			QMTreeNode oldParent = node.getParent();
			if (down) {
				moved = node.changePositionWithNextSibling();
			} else if (up) {
				moved = node.changePositionWithPreviousSibling();
			}
			QMTreeNode newParent = node.getParent();
			if (moved) {
				if (!oldParent.equals(newParent)) {
					qmtreeNodeService.update(oldParent);
					if (oldParent.getChildren().isEmpty() || !oldParent.allChildrenCompleted()){
						oldParent.setCompleted(false);
						updateCompleted(oldParent);
					}
					if (newParent.allChildrenCompleted()){
						newParent.setCompleted(true);
					}
				}
				updateCompleted(node);
				
				qmtreeNodeService.update(node.getParent());
				updateCompleted(node.getParent());
				
				expandTreeToSelectedNode(node.getId());
				target.add(QMBaseTreePage.this.treePanel);
			}
		}
	}

	private void deleteNode(final IModel<QMTreeNode> node,
                            AjaxRequestTarget target) {
		if ((node.getObject() instanceof QModel) &&
			((((QModel)node.getObject()).getIsActive()).equals(QModelStatus.Active) || (((QModel)node.getObject()).getIsActive()).equals(QModelStatus.OldActive))){
					
			target.add(getDenyDeleteConfirmationModal());
			deleteConfirmationModal.appendShowDialogJavaScript(target);
			
		} else {
			target.add(getUpdatedDeleteConfirmationModal());
			deleteConfirmationModal.appendShowDialogJavaScript(target);
		}
	}
	
	private void createNewQModel() {
		long randomId = UUID.randomUUID().toString().hashCode();
		QModel qm = (QModel) qmtreeNodeService.create(new QModel("Quality Model", String.format("qm-%s", randomId)));
		
		setResponsePage(QModelEditPage.class, forQModel(qm, true));

	}

	private QMTreeNode createNewQualityObjective(QModel parent) {
		if (parent != null) {
			QMQualityObjective obj = new QMQualityObjective("Quality Objective B", parent);
			return qmtreeNodeService.addNewChild(parent, obj);
		}
		return null;
	}

	private QMTreeNode createNewQualityIndicator(QMQualityObjective parent) {
		if (parent != null) {
			QMQualityIndicator indi = new QMQualityIndicator("Quality Indicator B", parent);
			return qmtreeNodeService.addNewChild(parent, indi);
		} 
		return null;
	}

	private QMTreeNode createNewMetric(QMQualityIndicator parent) {
		if (parent != null) {
			QMMetric metric = new QMMetric("Metric B", parent);
			return qmtreeNodeService.addNewChild(parent, metric);
		}
		return null;
	}

	private void unselectNodeCurrentlySelectedNode() {
		this.currentlySelectedNode = null;
		this.currentlySelectedNodeId = null;
		treePanel.unselectNode();
	}
	
	public QMTree getTree() {
		return getTreePanel().getTree();
	}

	private QMTreePanel getTreePanel() {
		return this.treePanel;
	}

	private QMTreePanel getTreePanel(QMTreeFilterStructure filter) {
        return new QMTreePanel("tree", filter) {
            private static final long serialVersionUID1 = -3862120828153062154L;

            @Override
            public void onNodeClicked(AjaxRequestTarget target,
                    IModel<QMTreeNode> nodeModel) {
                QMBaseTreePage.this.handleNodeClick(target, nodeModel);
            }

            @Override
            public void onMoveUpClicked(AjaxRequestTarget target,
                    IModel<QMTreeNode> node) {
                //TODO REVIEW
                moveNodeQM(node.getObject(), target, false);
            }

            @Override
            public void onMoveDownClicked(AjaxRequestTarget target,
                    IModel<QMTreeNode> node) {
                //TODO REVIEW
                moveNodeQM(node.getObject(), target, true);
            }

            @Override
            public void onEditClicked(AjaxRequestTarget target,
                    IModel<QMTreeNode> node) {
                if (node.getObject() instanceof QModel) {
                    setResponsePage(QModelEditPage.class,
                            QMBaseTreePage.forQModel((QModel) node.getObject()));
                } else if (node.getObject() instanceof QMQualityObjective) {
                    setResponsePage(
                            QMQualityObjectiveEditPage.class,
                            QMBaseTreePage
                            .forQualityObjective((QMQualityObjective) node
                                    .getObject()));
                } else if (node.getObject() instanceof QMQualityIndicator) {
                    setResponsePage(
                            QMQualityIndicatorEditPage.class,
                            QMBaseTreePage
                            .forQualityIndicator((QMQualityIndicator) node
                                    .getObject()));
                } else if (node.getObject() instanceof QMMetric) {
                    setResponsePage(QMMetricEditPage.class,
                            QMBaseTreePage.forMetric((QMMetric) node.getObject()));
                }
            }

            @Override
            public void onDeleteClicked(AjaxRequestTarget target,
                    IModel<QMTreeNode> node) {
                deleteNode(node, target);
            }

            @Override
            public void onNewClicked(AjaxRequestTarget target,
                    IModel<QMTreeNode> node, Class<? extends QMTreeNode> newNodeType) {
                QMTreeNode newNode = null;
                if (newNodeType == QModel.class) {
                    createNewQModel();
                } else if (newNodeType == QMQualityObjective.class) {
                    newNode = createNewQualityObjective(node.getObject().getQModel());
                } else if (newNodeType == QMQualityIndicator.class) {
                    newNode = createNewQualityIndicator(node.getObject()
                            .getQualityObjective());
                } else if (newNodeType == QMMetric.class) {
                    newNode = createNewMetric(node.getObject().getQualityIndicator());
                }

                if (newNode instanceof QMQualityObjective) {
                    setResponsePage(QMQualityObjectiveEditPage.class, forQualityObjective((QMQualityObjective) newNode, true));
                } else if (newNode instanceof QMQualityIndicator) {
                    setResponsePage(QMQualityIndicatorEditPage.class, forQualityIndicator((QMQualityIndicator) newNode, true));
                } else if (newNode instanceof QMMetric) {
                    setResponsePage(QMMetricEditPage.class, forMetric((QMMetric) newNode, true));
                }
            }

        };
	}

	
	
	public IQMTreeNode<String> getCurrentlySelectedNode() {
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
		response.render(CssHeaderItem.forUrl("assets/css/qmtree/panels.css"));
	}

	protected abstract WebMarkupContainer getContent(final String markupId,
                                                     final IModel<Type> model);

	@SuppressWarnings("unchecked")
	@Override
	protected void onConfigure() {
		super.onConfigure();
		currentlySelectedNodeId = initiallyRequestedNodeId = getRequestedNodeId();
		
		if (currentlySelectedNodeId == null){
			//click on create quality model in navigation menu
			long randomId = UUID.randomUUID().toString().hashCode();
			QModel qm = (QModel) qmtreeNodeService.create(new QModel("New Quality Model", String.format("qm-%s", randomId)));
			currentlySelectedNodeId = initiallyRequestedNodeId = qm.getId();
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
                         IModel<QMTreeNode> nodeModel) {
		QMTreeNode node = nodeModel.getObject();
		setRedirectToNodePage(node);
		currentlySelectedNode = node;
		currentlySelectedNodeId = node.getId();
	}

	private void setRedirectToNodePage(QMTreeNode node) {
		if (node instanceof QModel) {
			setResponsePage(QModelViewPage.class, forQModel((QModel) node));
		} else if (node instanceof QMQualityObjective) {
			setResponsePage(QMQualityObjectiveViewPage.class,
					forQualityObjective((QMQualityObjective) node));
		} else if (node instanceof QMQualityIndicator) {
			setResponsePage(QMQualityIndicatorViewPage.class,
					forQualityIndicator((QMQualityIndicator) node));
		} else if (node instanceof QMMetric) {
			setResponsePage(QMMetricViewPage.class, forMetric((QMMetric) node));
		}
	}

	private QMDeletionConfirmationModal createDeleteConfirmationModal() {
		deleteConfirmationModal = new QMDeletionConfirmationModal(
				"deleteConfirmationModal", currentlySelectedNode) {

			
			/**
					 * 
					 */
					private static final long serialVersionUID = 4096454354420614489L;

			@Override
			public void onConfirmed(AjaxRequestTarget target) {
				QMTreeNode removedQMTreeNode = qmtreeNodeService
						.removeTreeNode(currentlySelectedNode.getId());
				
				if (removedQMTreeNode != null) {
					updateCompleted(removedQMTreeNode);
					if (!(removedQMTreeNode instanceof QModel)) {
						setRedirectToNodePage(removedQMTreeNode.getParent());
					} else {
						// TODO where to go, when there is no qmodel anymore?
						setResponsePage(AboutPage.class);
					}
				}
			}
		};
		return deleteConfirmationModal;
	}

	private QMDeletionConfirmationModal getUpdatedDeleteConfirmationModal() {
		deleteConfirmationModal.update(currentlySelectedNode);
		return deleteConfirmationModal;
	}

	private QMDeletionConfirmationModal getDenyDeleteConfirmationModal() {
		deleteConfirmationModal.deny(currentlySelectedNode);
		return deleteConfirmationModal;
	}
	private Long getRequestedNodeId() {
		StringValue id = getPageParameters().get("id");
		if (id.isEmpty()) {
			String key = getPageParameters().get("qmodel-key")
					.toOptionalString();
			if (key != null) {
				IQMTreeNode<String> node = qmtreeNodeService
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
		if (getRequestedNodeId()==null) {
			//create qmodel from navigation menu
			return (Type) (new QModel("new QModel", "new QModel"));
		} else {
			return (Type) qmtreeNodeService.getTreeNode(getRequestedNodeId());
		}
	}

	@SuppressWarnings("unchecked")
    private Type getRequestedNode(final Long requestedNodeId) {
		return (Type) qmtreeNodeService.getTreeNode(requestedNodeId);
	}

	private void selectCurrentNode(final Long requestedNodeId) {
		QMTreeNode node = qmtreeNodeService
				.getTreeNode(requestedNodeId);
		// select the requested node
		treePanel.getTree().selectNode(node);
	}

	private void expandTreeToSelectedNode(final Long requestedNodeId) {
		QMTreeNode node = qmtreeNodeService
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

	public static PageParameters forNode(IQMTreeNode<String> node) {
		if (node instanceof QModel) {
			return forQModel((QModel) node);
		} else if (node instanceof QMQualityObjective) {
			return forQualityObjective((QMQualityObjective) node);
		} else if (node instanceof QMQualityIndicator) {
			return forQualityIndicator((QMQualityIndicator) node);
		} else if (node instanceof QMMetric) {
			return forMetric((QMMetric) node);
		}
		return new PageParameters();
	}

	
	private static PageParameters forQModel(final QModel qmodel, final boolean newQModel) {
		PageParameters params = new PageParameters();
		params.add("isNew", newQModel);
		if(qmodel != null) {
			return params.add("qmodel-key", qmodel.getNodeKey());
		} else return params;
	}

	public static PageParameters forQModel(QModel qmodel) {
		if(qmodel != null) {
			return forQModel(qmodel.getNodeKey());
		} else return new PageParameters();
	}
	
	private static PageParameters forQModel(final String qmodelKey) {
		return new PageParameters().add("qmodel-key", qmodelKey);
	}

	
	private static PageParameters forQualityObjective(final QMQualityObjective objective, final boolean newQO) {
		PageParameters params = forQModel(objective.getQModel()).add("id", objective.getId());
		params.add("isNew", newQO);
		return params;
	}
	
	public static PageParameters forQualityObjective(QMQualityObjective objective) {
		return forQModel(objective.getQModel()).add("id", objective.getId());
	}

	private static PageParameters forQualityIndicator(final QMQualityIndicator indicator, final boolean newQI) {
		PageParameters params = forQModel(indicator.getQModel()).add("id", indicator.getId());
		params.add("isNew", newQI);
		return params;
	}

	public static PageParameters forQualityIndicator(QMQualityIndicator indicator) {
		return forQModel(indicator.getQModel()).add("id", indicator.getId());
	}

	private static PageParameters forMetric(final QMMetric metric, final boolean newMet) {
		PageParameters params = forQModel(metric.getQModel()).add("id", metric.getId());
		params.add("isNew", newMet);
		return params;
	}

	public static PageParameters forMetric(QMMetric metric) {
		return forQModel(metric.getQModel()).add("id", metric.getId());
	}

	
	private void updateCompleted(QMTreeNode node) {
		if (node instanceof QMMetric){
			if (node.getParent().getChildren().isEmpty()){
				//indicator
				node.getParent().setCompleted(false);
				qmtreeNodeService.update (node.getParent());
				node.getParent().getParent().setCompleted(false);
				qmtreeNodeService.update(node.getParent().getParent());
				node.getParent().getParent().getParent().setCompleted(false);
				qmtreeNodeService.update(node.getParent().getParent().getParent());
			}
		} else if (node instanceof QMQualityIndicator){
			if (node.getParent().getChildren().isEmpty() || !node.getParent().allChildrenCompleted()){
				node.getParent().setCompleted(false);
				qmtreeNodeService.update(node.getParent());
				node.getParent().getParent().setCompleted(false);
				qmtreeNodeService.update(node.getParent().getParent());
			} else if (node.getParent().allChildrenCompleted()) {
				node.getParent().setCompleted(true);
				qmtreeNodeService.update(node.getParent());
				if (node.getParent().getParent().allChildrenCompleted()){
					node.getParent().getParent().setCompleted(true);
					qmtreeNodeService.update(node.getParent().getParent());
				}
			}
		} else if (node instanceof QMQualityObjective){
			if (node.getParent().getChildren().isEmpty() || !node.getParent().allChildrenCompleted()){
				node.getParent().setCompleted(false);
				qmtreeNodeService.update(node.getParent());
			} else if (node.getParent().allChildrenCompleted()) {
				node.getParent().setCompleted(true);
				qmtreeNodeService.update(node.getParent());
			}
		}
	}
	
}
