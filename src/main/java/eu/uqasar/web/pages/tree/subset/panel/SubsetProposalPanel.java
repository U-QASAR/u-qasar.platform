package eu.uqasar.web.pages.tree.subset.panel;

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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import eu.uqasar.model.meta.MetaData;
import eu.uqasar.model.meta.QModelTagData;
import eu.uqasar.model.qmtree.QMBaseIndicator;
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;

/**
 * 
 *
 *
 */
public class SubsetProposalPanel extends Panel {

	private static final long serialVersionUID = -6492328025617396184L;

	@Inject
	QMTreeNodeService qmTreeNodeService;
	
	@Inject
	TreeNodeService treeNodeService;
	
	private final WebMarkupContainer dataContainer = new WebMarkupContainer("dataContainer");
//	private final Modal saveConfirmationModal;
	private final CheckGroup<QMTreeNode> dataGroup;
//	private final AjaxSubmitLink saveSelectedButton;
	
	private final Set<MetaData> projectTags;
	
	private TreeNode currentParentProjectNode;
    private TreeNode currentParentQualityObjective;
    private TreeNode currentParentQualityIndicator;

	private QMTreeNode currentParentQMProjectNode;
    private QMTreeNode currentParentQMQualityObjective;
    private QMTreeNode currentParentQMQualityIndicator;


	/**
	 * @param markupId
	 */
	public SubsetProposalPanel(final String markupId){
		this(markupId, new Project());
	}

	/**
	 * @param markupId
     */
	public SubsetProposalPanel(final String markupId, final Project project) {
		super(markupId);

		projectTags = projectTags(project);
		
		final Form<QMTreeNode> saveForm = new Form<>("saveForm");
		add(saveForm);

		// add checkgroup for selecting multiple measurements
		saveForm.add(dataGroup = newDataCheckGroup());

		// add the container holding list of existing measurements
		dataGroup.add(dataContainer.setOutputMarkupId(true));

		dataContainer.add(new CheckGroupSelector(
				"dataGroupSelector", dataGroup));
		List<QModel> qModelList = new LinkedList<>();
		qModelList.add(project.getQmodel());
		dataContainer.add(new ListView<QMTreeNode>("qmlist",qModelList) {
			private static final long serialVersionUID = -8627579715676163392L;

			@Override
			protected void populateItem(ListItem<QMTreeNode> item) {
				final QMTreeNode qmTreeNode = project.getQmodel(); //item.getModelObject();
				
				// Adds every Quality Model persisted on platform
				item.add(drawTreeElement("qmodel", item));
				
				// Adds a tree list of the Quality Objectives of the Model
				item.add(new ListView<QMTreeNode>("qmlist",qmTreeNode.getChildren()) {
					private static final long serialVersionUID = 6089299549826556193L;

					@Override
					  protected void populateItem(ListItem<QMTreeNode> qo) {
					    final QMTreeNode qmTreeNode = qo.getModelObject();
				
					    // Adds every Quality Objective
						qo.add(drawTreeElement("qobjective", qo));
						
						// Adds a tree list of the Quality Indicators of a Objective
						qo.add(new ListView<QMTreeNode>("qmlist",qmTreeNode.getChildren()) {
							private static final long serialVersionUID = 9129630322229875593L;

							@Override
							  protected void populateItem(ListItem<QMTreeNode> qi) {
							    final QMTreeNode qmTreeNode = qi.getModelObject();
							    
							    // Adds every Quality Indicator
								qi.add(drawTreeElement("qindicator", qi));
								
								// Adds a tree list of the Metrics of an Indicator
								qi.add(new ListView<QMTreeNode>("qmlist",qmTreeNode.getChildren()) {
									private static final long serialVersionUID = 9129630322229875593L;

									@Override
									  protected void populateItem(ListItem<QMTreeNode> me) {
										// Adds every Metric from the Indicator
										me.add(drawTreeElement("qmetric",me));
									  }
									});
							  }
							});
					  }
					});
			}
		});
		
	}

	private Set<MetaData> projectTags(Project project) {
		Set<MetaData> projectTagsSet= new HashSet<>();
		
		    projectTagsSet = new HashSet<>();
        	projectTagsSet.addAll(project.getCustomerTypes());
        	projectTagsSet.addAll(project.getProjectTypes());
        	projectTagsSet.addAll(project.getSoftwareTypes());
        	projectTagsSet.addAll(project.getSoftwareLicenses());
        	projectTagsSet.addAll(project.getProgrammingLanguages());
        	projectTagsSet.addAll(project.getContinuousIntegrationTools());
        	projectTagsSet.addAll(project.getIssueTrackingTools());
        	projectTagsSet.addAll(project.getSourceCodeManagementTools());
        	projectTagsSet.addAll(project.getStaticAnalysisTools());
        	projectTagsSet.addAll(project.getTestManagementTools());
        	projectTagsSet.addAll(project.getSoftwareDevelopmentMethodologies());
		
		return projectTagsSet;
	}

	/**
	 * @param markupId
	 * @param item
	 * @return Returns a WebMarkContainer with the item details of an element of the tree
	 */
	private WebMarkupContainer drawTreeElement(String markupId, ListItem<QMTreeNode> item){
		QMTreeNode node = item.getModelObject();
		WebMarkupContainer container = new WebMarkupContainer(markupId);

		container.add(new Label("name", new PropertyModel<String>(node, "name")));
		container.add(new Icon("icon", node.getIconType()));

		Check<QMTreeNode> check = new Check<>("nodeCheck", item.getModel(),
				dataGroup);
		container.add(check);

		// Adds tags and Checks for elements of the QM
		if (node instanceof QMBaseIndicator) {

			// Marks as Checked if the item has tags coincidences
			for (MetaData tag : projectTags) {
				for (QModelTagData tagModel : ((QMBaseIndicator) node)
						.getQModelTagData()) {
					if (tagModel.getName().equals(tag.getName())) {
						check.add(new AttributeModifier("checked", "checked"));
					}
				}
			}

			// Adds the tags of every element
			WebMarkupContainer tags = new WebMarkupContainer("tags");
			tags.add(newLabelInWMC("tag", new PropertyModel<QModelTagData>(
					node, "qModelTagData"), tags));
			tags.add(new AttributeModifier("style", "margin-left:50px"));
			container.add(tags);
		}
        
        return container;
	}


	/**
	 * 
	 * @return
	 */
	private CheckGroup<QMTreeNode> newDataCheckGroup() {
		CheckGroup<QMTreeNode> checkGroup = new CheckGroup<>(
                "dataGroup", new ArrayList<QMTreeNode>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

			private static final long serialVersionUID = 7348039334236716476L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
//				updateSaveSelectedButton(target);
			}
		});
		
		return checkGroup;
	}

	/**
	 * @param target
	 */
	public void saveSelectedItems(AjaxRequestTarget target) {
		String message = new StringResourceModel("subset.selected.created", this, null).getString();
		String labelProject = new StringResourceModel("auto.created.project", this, null).getString();
		
		long randomId = UUID.randomUUID().toString().hashCode();
        currentParentProjectNode = new Project(labelProject, String.format("prj-%s", randomId));
		
		// Add selected elements to the current created project
		for (QMTreeNode qmTreeNode : dataGroup.getModelObject()) {
			addNodeToProjectTree(qmTreeNode);
		}

		// Project is created
		treeNodeService.create(currentParentProjectNode);
		
		// Prints success message
		getPage().success(message);

		// update the save button
		dataGroup.updateModel();
		
		// Goes to Project view page of the new create project
		setResponsePage(ProjectViewPage.class, BaseTreePage.forProject(currentParentProjectNode.getNodeKey()));		
	}
	
	public void saveSubset(Project project){
		
		// Add selected elements to the current created project
		for (QMTreeNode qmTreeNode : dataGroup.getModelObject()) {
			addNodeToProjectTree(qmTreeNode, project);
		}
	}

	/**
	 * Create elements from the subset into the provided Project
	 * 
	 * @param node
	 */
	private void addNodeToProjectTree(QMTreeNode node, Project project) {
		
		String labelObjective = new StringResourceModel("auto.created.objective", this, null).getString();
		String labelIndicator = new StringResourceModel("auto.created.indicator", this, null).getString();
		

		if (node instanceof QModel) {
				QModel qm = (QModel) node;
				project.setQmodel(qm);
				currentParentQMProjectNode = qm;
				
			} else if (node instanceof QMQualityObjective) {
				QMQualityObjective qmqo = (QMQualityObjective) node;
            currentParentQualityObjective = new QualityObjective(qmqo, project);
				currentParentQMQualityObjective = qmqo;
				
			} else if (node instanceof QMQualityIndicator) {
				QMQualityIndicator qmqi = (QMQualityIndicator) node;

				if(qmqi.getParent()!=currentParentQMQualityObjective){
                    currentParentQualityObjective = new QualityObjective(labelObjective, project);
						currentParentQMQualityObjective = qmqi.getParent();
				}

            currentParentQualityIndicator = new QualityIndicator(qmqi, (QualityObjective) currentParentQualityObjective);
				currentParentQMQualityIndicator = qmqi;
				
			} else if (node instanceof QMMetric) {
				QMMetric qmm = (QMMetric) node;

				if(qmm.getParent().getParent()!=currentParentQMQualityObjective){
                    currentParentQualityObjective = new QualityObjective(labelObjective, project);
						currentParentQMQualityObjective = qmm.getParent().getParent();
				}
				
				if(qmm.getParent() != currentParentQMQualityIndicator){
                    currentParentQualityIndicator = new QualityIndicator(labelIndicator, (QualityObjective) currentParentQualityObjective);
						currentParentQMQualityIndicator = qmm.getParent();
				}
				
				new Metric(qmm, (QualityIndicator) currentParentQualityIndicator);
			}
	  }	
	
	/**
	 * TO be replaced with the method  addNodeToProjectTree(QMTreeNode node, Project project) 
	 * 
	 * @param node
	 */
	private void addNodeToProjectTree(QMTreeNode node) {
		
		String labelProjectFrom = new StringResourceModel("from.created.project", this, null).getString();
		String labelObjective = new StringResourceModel("auto.created.objective", this, null).getString();
		String labelIndicator = new StringResourceModel("auto.created.indicator", this, null).getString();
		
		
		if (node instanceof QModel) {
			QModel qm = (QModel) node;
			long randomId = UUID.randomUUID().toString().hashCode();
			Project project = new Project(labelProjectFrom + qm.getName(), String.format("prj-%s", randomId));
			project.setQmodel(qm);
			currentParentProjectNode = project;
			currentParentQMProjectNode = qm;
			
		} else if (node instanceof QMQualityObjective) {
			QMQualityObjective qmqo = (QMQualityObjective) node;
            currentParentQualityObjective = new QualityObjective(qmqo, (Project) currentParentProjectNode);
			currentParentQMQualityObjective = qmqo;
			
		} else if (node instanceof QMQualityIndicator) {
			QMQualityIndicator qmqi = (QMQualityIndicator) node;
			
			if(qmqi.getParent()!=currentParentQMQualityObjective){
                currentParentQualityObjective = new QualityObjective(labelObjective, (Project) currentParentProjectNode);
				currentParentQMQualityObjective = qmqi.getParent();
			}

            currentParentQualityIndicator = new QualityIndicator(qmqi, (QualityObjective) currentParentQualityObjective);
			currentParentQMQualityIndicator = qmqi;
			
		} else if (node instanceof QMMetric) {
			QMMetric qmm = (QMMetric) node;
			
			if(qmm.getParent().getParent()!=currentParentQMQualityObjective){
                currentParentQualityObjective = new QualityObjective(labelObjective, (Project) currentParentProjectNode);
				currentParentQMQualityObjective = qmm.getParent().getParent();
			}
			
			if(qmm.getParent() != currentParentQMQualityIndicator){
                currentParentQualityIndicator = new QualityIndicator(labelIndicator, (QualityObjective) currentParentQualityObjective);
				currentParentQMQualityIndicator = qmm.getParent();
			}
			
			new Metric(qmm, (QualityIndicator) currentParentQualityIndicator);
		}
	}	

	/**
	 * @param markUpId
	 * @param propertyModel
	 * @param wmc
	 * @return Returns a label with tags, hidden in the case of object does not have tags
	 */
	private <T> Label newLabelInWMC(String markUpId, PropertyModel<QModelTagData> propertyModel, final WebMarkupContainer wmc){
		return new Label(markUpId, propertyModel){
			private static final long serialVersionUID = 1817873106739742280L;

			@Override
			public void onConfigure(){
				super.onConfigure();
				setDefaultModel(Model.of(this.getDefaultModelObjectAsString().replace("[", "").replace("]", "")));
				if(this.getDefaultModelObjectAsString().isEmpty()){
					wmc.setVisible(false);
				}
			}
		};
	}

	/**
	 * @return the dataGroup
	 */
	public CheckGroup<QMTreeNode> getDataGroup() {
		return dataGroup;
	}


	
}
