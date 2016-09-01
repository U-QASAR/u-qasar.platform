package eu.uqasar.web.pages.tree.visual.panel;

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


import javax.inject.Inject;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.metric.MetricViewPage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorViewPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveViewPage;

/**
 * 
 *
 *
 */
public class VisualProjectPanel extends Panel {

	private static final long serialVersionUID = -6492328025617396184L;

	@Inject
	TreeNodeService treeNodeService;
	
	/**
	 * Constructor building the page
	 *
     */
	public VisualProjectPanel(final String markupId) {
		super(markupId);
		
		add(new ListView<TreeNode>("qmlist",treeNodeService.getAllProjects()) {
			private static final long serialVersionUID = -8627579715676163392L;

			@Override
			protected void populateItem(ListItem<TreeNode> item) {
				final TreeNode qmTreeNode = item.getModelObject();
				
				// Adds to the list every Project persisted on platform
				item.add(drawTreeElement("qmodel", item));
				
				// Adds a tree list of the Quality Objectives of the Project
				item.add(new ListView<TreeNode>("qmlist",qmTreeNode.getChildren()) {
					private static final long serialVersionUID = 6089299549826556193L;

					@Override
					  protected void populateItem(ListItem<TreeNode> qo) {
					    final TreeNode qmTreeNode = qo.getModelObject();
				
					    // Adds every Quality Objective
						qo.add(drawTreeElement("qobjective", qo));
						
						// Adds a tree list of the Quality Indicators of a Objective
						qo.add(new ListView<TreeNode>("qmlist",qmTreeNode.getChildren()) {
							private static final long serialVersionUID = 9129630322229875593L;

							@Override
							  protected void populateItem(ListItem<TreeNode> qi) {
							    final TreeNode qmTreeNode = qi.getModelObject();
							    
							    // Adds every Quality Indicator
								qi.add(drawTreeElement("qindicator", qi));
								
								// Adds a tree list of the Metrics of an Indicator
								qi.add(new ListView<TreeNode>("qmlist",qmTreeNode.getChildren()) {
									private static final long serialVersionUID = 9129630322229875593L;

									@Override
									  protected void populateItem(ListItem<TreeNode> me) {
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

	/**
	 * @param markupId
	 * @param item
	 * @return Returns a WebMarkContainer with the item details of an element of the tree
	 */
	private WebMarkupContainer drawTreeElement(String markupId, ListItem<TreeNode> item){
		TreeNode node = item.getModelObject();
		WebMarkupContainer container = new WebMarkupContainer(markupId);
		QualityStatus qs = node.getQualityStatus();
		
		// Adds an icon with color status 
		Label iconLabel = new Label("icon");
		iconLabel.add(new CssClassNameAppender(node.getIconType().cssClassName()));
		iconLabel.add(new AttributeAppender("class", new Model(node.getQualityStatus().getCssClassName()), " "));
		container.add(iconLabel);
		
		// Adds the link to the Page of the element according its type
		container.add(setLinkToNodePage(node));
		
		WebMarkupContainer valueContainer = new WebMarkupContainer("valueContainer");
		valueContainer.add(new AttributeAppender("class", " text-" + qs.getCssClassName()));
		container.add(valueContainer);
		
		// Adds the value with color according to the status of the element
		Label qualityValue = new Label("value", new PropertyModel<String>(node, "value"));
		valueContainer.add(qualityValue);
		
		// Adds a tooltip to show Value source and type and its Unit
		if(node instanceof Metric){
			valueContainer.add(metricTooltip(node));
			valueContainer.add(new Label("unit", new PropertyModel<String>(node, "unit") ));
		} else{
			valueContainer.add( new Label("unit", Model.of("%"))); 
		}
		
        return container;
	}


	/**
	 * @param node
	 * @return Returns a Link to the page element according to the type of the node
	 */
    private BookmarkablePageLink<?> setLinkToNodePage(final TreeNode node) {
		BookmarkablePageLink<?> link = null;
		
		if (node instanceof Project) {
			link = new BookmarkablePageLink<ProjectViewPage>("link", ProjectViewPage.class, 
					BaseTreePage.forProject((Project) node));
		} else if (node instanceof QualityObjective) {
			link = new BookmarkablePageLink<ProjectViewPage>("link", QualityObjectiveViewPage.class,
					BaseTreePage.forQualityObjective((QualityObjective) node));
		} else if (node instanceof QualityIndicator) {
			link = new BookmarkablePageLink<ProjectViewPage>("link",QualityIndicatorViewPage.class,
					BaseTreePage.forQualityIndicator((QualityIndicator) node));
		} else if (node instanceof Metric) {
			link = new BookmarkablePageLink<ProjectViewPage>("link",MetricViewPage.class, BaseTreePage.forMetric((Metric) node));
		}
		
		link.add(new Label("name", new PropertyModel<String>(node, "name")));
		
		return link;
	}
	
	/**
	 * @param node
	 * @return The Tooltip with Metric source and type information.  
	 */
	private TooltipBehavior metricTooltip(TreeNode node){
		// tooltip config
		TooltipConfig confConfig = new TooltipConfig().withPlacement(TooltipConfig.Placement.top); 
		Metric metric = (Metric) node;
		
		String info = metric.getMetricSource().toString();
		
		if(metric.getMetricType() != null ){
			info += " - " + metric.getMetricType();
			}
		
		 return new TooltipBehavior(new Model(info) , confConfig);
	}

}
