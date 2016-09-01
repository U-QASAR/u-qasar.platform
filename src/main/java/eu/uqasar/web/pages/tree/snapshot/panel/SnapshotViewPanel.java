package eu.uqasar.web.pages.tree.snapshot.panel;

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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import eu.uqasar.model.tree.BaseIndicator;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.model.tree.historic.Snapshot;
import eu.uqasar.service.SnapshotService;
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
public class SnapshotViewPanel extends Panel {

	private static final long serialVersionUID = -6492328025617396184L;

	@Inject
	TreeNodeService treeNodeService;
	
	@Inject
	SnapshotService snapshotService;
	
	private final Snapshot snapshot;
	
	/**
	 * Constructor building the page
	 * @param snap
     */
	public SnapshotViewPanel(final String markupId, IModel<Snapshot> snap) {
		super(markupId);
		
		snapshot = snap.getObject();
		
		final TreeNode qmTreeNode = snapshot.getProject();
		
		// Adds to the list every Project persisted on platform
		add(drawTreeElement("qmodel", qmTreeNode));
		
		// Adds a tree list of the Quality Objectives of the Project
		add(new ListView<TreeNode>("qmlist",qmTreeNode.getChildren()) {
			private static final long serialVersionUID = 6089299549826556193L;

			@Override
			  protected void populateItem(ListItem<TreeNode> qo) {
			    final TreeNode qmTreeNode = qo.getModelObject();
		
			    // Adds every Quality Objective
				qo.add(drawTreeElement("qobjective", qmTreeNode));
				
				// Adds a tree list of the Quality Indicators of a Objective
				qo.add(new ListView<TreeNode>("qmlist",qmTreeNode.getChildren()) {
					private static final long serialVersionUID = 9129630322229875593L;

					@Override
					  protected void populateItem(ListItem<TreeNode> qi) {
					    final TreeNode qmTreeNode = qi.getModelObject();
					    
					    // Adds every Quality Indicator
						qi.add(drawTreeElement("qindicator", qmTreeNode));
						
						// Adds a tree list of the Metrics of an Indicator
						qi.add(new ListView<TreeNode>("qmlist",qmTreeNode.getChildren()) {
							private static final long serialVersionUID = 9129630322229875593L;

							@Override
							  protected void populateItem(ListItem<TreeNode> me) {
								final TreeNode qmTreeNode = me.getModelObject();
								// Adds every Metric from the Indicator
								me.add(drawTreeElement("qmetric",qmTreeNode));
							  }
							});
					  } 
					});
			  }
			});		
	}

	/**
	 * @param markupId
	 * @return Returns a WebMarkContainer with the item details of an element of the tree
	 */
	private WebMarkupContainer drawTreeElement(String markupId, TreeNode node){
		WebMarkupContainer container = new WebMarkupContainer(markupId);
		
		QualityStatus qs = QualityStatus.Gray;
		
		// Retrieve snapshot stored value
		if(node instanceof BaseIndicator){
			qs = ((BaseIndicator)node).getHistoricObject(snapshot.getLastUpdate()).getQualityStatus();
		} else if(node instanceof Project){
			qs = ((Project)node).getHistoricObject(snapshot.getLastUpdate()).getQualityStatus();
		}
		
		// Adds an icon with color status 
		Label iconLabel = new Label("icon");
		iconLabel.add(new CssClassNameAppender(node.getIconType().cssClassName()));
		iconLabel.add(new AttributeAppender("class", Model.of(qs.getCssClassName()), " "));
		container.add(iconLabel);
		
		// Adds the link to the Page of the element according its type
		container.add(setLinkToNodePage(node));
		
		WebMarkupContainer valueContainer = new WebMarkupContainer("valueContainer");
		valueContainer.add(new AttributeAppender("class", " text-" + qs.getCssClassName()));
		container.add(valueContainer);
		
		// Adds a tooltip to show Value source and type and its Unit
		if(node instanceof Metric){
			valueContainer.add(metricTooltip(node));
			valueContainer.add(new Label("unit", new PropertyModel<String>(node, "unit") ));
		} else{
			valueContainer.add( new Label("unit", Model.of("%"))); 
		}
		
		// Retrieve snapshot stored value
		if(node instanceof BaseIndicator){
			valueContainer.add(new Label("hValue", ((BaseIndicator)node).getHistoricValue(snapshot.getLastUpdate())));
		} else if(node instanceof Project){
			valueContainer.add(new Label("hValue", ((Project)node).getHistoricValue(snapshot.getLastUpdate())));
		}
		
		//TODO: add the current value/state beside snapshot value/state
		// Adds the value with color according to the status of the element
//		Label qualityValue = new Label("value", new PropertyModel<String>(node, "value"));
//		valueContainer.add(qualityValue);
		
		
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
