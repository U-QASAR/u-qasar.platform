package eu.uqasar.web.components.navigation.notification.thresholdReached;

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


import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.notification.threshold.ThresholdReachedNotification;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.components.navigation.notification.NotificationBookmarkablePageLink;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveViewPage;

public class ThresholdReachedNotificationLink extends NotificationBookmarkablePageLink<ThresholdReachedNotification, ProjectViewPage> {

	private static final long serialVersionUID = 3310107463188121652L;
	
	public ThresholdReachedNotificationLink(String id,IModel<ThresholdReachedNotification> model) {
		this(id, new PageParameters(), model);
	}

	public ThresholdReachedNotificationLink(String id, PageParameters parameters, final IModel<ThresholdReachedNotification> model) {

		super(id, ProjectViewPage.class, BaseTreePage.forProject(model.getObject().getProject()), model);
		
		add(new Label("projectName", model.getObject().getProject().getAbbreviatedName()));
		
		add(new ListView<TreeNode>("treeNodeNames", model.getObject().getTreeNodes()){
			@Override
			protected void populateItem(ListItem<TreeNode> item) {
				TreeNode node = item.getModelObject();                              
				item.add(new BookmarkablePageLink<QualityObjectiveViewPage>("QOlink", 
						QualityObjectiveViewPage.class, 
						new PageParameters().set("id",  node.getId()))					
					.add(new Label("name", node.getName())));
			}
		});
		
		//set containers 
		setIcon(new IconType("exclamation-sign"));
		get("notification.container").add(new AttributeModifier("style","width:95%;"));
		get("notification.container").add(new AttributeModifier("class","notification project red"));	
		
		//trash
		final WebMarkupContainer deleteContainer = new WebMarkupContainer("delete");
		final String deleteMessage = new StringResourceModel("delete.message", this, null).getString();
		deleteContainer.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = -4295786924073241665L;
			@Override
			protected void onEvent(AjaxRequestTarget target) {				
				UQasarUtil.getNotifications().remove(model.getObject());
				setResponsePage(AboutPage.class, BasePage.appendSuccessMessage(getPageParameters(), deleteMessage));
			}
		});
		deleteContainer.setOutputMarkupId(true);
		add(deleteContainer);
		
		// tooltip
		TooltipConfig confConfig = new TooltipConfig().withPlacement(TooltipConfig.Placement.right);
		deleteContainer.add(new TooltipBehavior(new StringResourceModel("delete.title", this, null), confConfig));
	}	
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		logger.info("########################" + getModelObject().getNotificationType() + "########################");
	}
}
