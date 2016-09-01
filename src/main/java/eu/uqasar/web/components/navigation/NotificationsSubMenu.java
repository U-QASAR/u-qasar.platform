package eu.uqasar.web.components.navigation;

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
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonList;
import eu.uqasar.model.notification.INotification;
import eu.uqasar.model.notification.NoNotificationsNotification;
import eu.uqasar.model.notification.complexity.ComplexityNotification;
import eu.uqasar.model.notification.dashboard.DashboardSharedNotification;
import eu.uqasar.model.notification.metric.MetricNeedsToBeEdited;
import eu.uqasar.model.notification.project.ProjectNearEndNotification;
import eu.uqasar.model.notification.softwarequality.BadSoftwareQualityNotification;
import eu.uqasar.model.notification.threshold.ThresholdReachedNotification;
import eu.uqasar.web.components.navigation.notification.NoNotificationsNotificationLink;
import eu.uqasar.web.components.navigation.notification.NotificationBookmarkablePageLink;
import eu.uqasar.web.components.navigation.notification.NotificationDropDownMenu;
import eu.uqasar.web.components.navigation.notification.complexity.ComplexityNotificationLink;
import eu.uqasar.web.components.navigation.notification.dashboard.DashboardSharedNotificationLink;
import eu.uqasar.web.components.navigation.notification.metric.MetricNotificationLink;
import eu.uqasar.web.components.navigation.notification.project.ProjectNearEndNotificationLink;
import eu.uqasar.web.components.navigation.notification.softwarequality.BadSoftwareQualityNotificationLink;
import eu.uqasar.web.components.navigation.notification.thresholdReached.ThresholdReachedNotificationLink;

public class NotificationsSubMenu extends NotificationDropDownMenu {

	private static final long serialVersionUID = -7164720550808278400L;

	public NotificationsSubMenu(INotification[] notifications) {
   		this.notifications = notifications.clone();
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		get("dropdown-menu").add(new CssClassNameAppender("notifications"));
		getParent().add(new AttributeModifier("id", "notifyMenu"));
		((WebMarkupContainer) get("btn")).add(new Label("notification.title", new StringResourceModel("label.notifications" , this, null)));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		String count = "none";
		if(notifications.length == 1) {
			count = "single";
		} else if (notifications.length > 1) {
			count = "multiple";
		}
		final IModel<String> title = new StringResourceModel("label.notifications.unread.count." + count, this, null, notifications.length);
		getNotificationLabel().add(new AttributeModifier("title", title));
	}
	
	@Override
	protected List<AbstractLink> newSubMenuButtons(String buttonMarkupId) {
		List<AbstractLink> links = new ArrayList<>();
		if (notifications.length > 0) {
			for (INotification notification : notifications) {
				Class<? extends NotificationBookmarkablePageLink> clazz = getNotificationLink(notification);
				NotificationBookmarkablePageLink<INotification, Page> link = NotificationBookmarkablePageLink.getLink(clazz, ButtonList.getButtonMarkupId(), Model.of(notification));
				links.add(link);
			}
		} else {
			INotification noNotification = new NoNotificationsNotification();
			Class<? extends NotificationBookmarkablePageLink> clazz = getNotificationLink(noNotification);
			NotificationBookmarkablePageLink<INotification, Page> link = NotificationBookmarkablePageLink.getLink(clazz, ButtonList.getButtonMarkupId(), Model.of(noNotification));
			links.add(link);
		}
		return links;
	}
	
	private static Class<? extends NotificationBookmarkablePageLink> getNotificationLink(INotification notification) {
		if (notification instanceof ProjectNearEndNotification) {
			return ProjectNearEndNotificationLink.class;
		} else if(notification instanceof DashboardSharedNotification){
			return DashboardSharedNotificationLink.class;
		} else if(notification instanceof ComplexityNotification){
			return ComplexityNotificationLink.class;
		} else if(notification instanceof BadSoftwareQualityNotification){
			return BadSoftwareQualityNotificationLink.class;
		} else if(notification instanceof ThresholdReachedNotification){
			return ThresholdReachedNotificationLink.class;
		} else if (notification instanceof NoNotificationsNotification) {
			return NoNotificationsNotificationLink.class;
		} else if (notification instanceof MetricNeedsToBeEdited) {
			return MetricNotificationLink.class;
		}
		return null;
	}
}
