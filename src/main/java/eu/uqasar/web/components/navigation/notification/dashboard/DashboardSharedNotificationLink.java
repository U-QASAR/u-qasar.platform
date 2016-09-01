package eu.uqasar.web.components.navigation.notification.dashboard;

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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.notification.dashboard.DashboardSharedNotification;
import eu.uqasar.web.components.navigation.notification.NotificationBookmarkablePageLink;
import eu.uqasar.web.dashboard.DashboardViewPage;

public class DashboardSharedNotificationLink extends NotificationBookmarkablePageLink<DashboardSharedNotification, DashboardViewPage> {

	private static final long serialVersionUID = 3310107463188121652L;
	
	public DashboardSharedNotificationLink(String id,IModel<DashboardSharedNotification> model) {
		this(id, new PageParameters(), model);
	}

	public DashboardSharedNotificationLink(String id, PageParameters parameters, IModel<DashboardSharedNotification> model) {

		super(id, DashboardViewPage.class, parameters.add("id", model.getObject().getDashboard().getId()), model);
		

		//get user who shared dashboard		
		add(new Label("dashboard.user", model.getObject().getDashboard().getSharedBy()));	

		//set containers 
		setIcon(new IconType("dashboard"));
		get("notification.container").add(new AttributeModifier("style","width:95%;"));
		get("notification.container").add(new AttributeModifier("class","notification project green"));	
		
		add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = -4295786924073241665L;
			@Override
			protected void onEvent(AjaxRequestTarget target) {				
				getModelObject().getDashboard().setSharedBy(null);
				setResponsePage(DashboardViewPage.class, getPageParameters().add("id", getModelObject().getDashboard().getId()));
			}
		});
	}	
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		logger.info("########################" + getModelObject().getNotificationType() + "########################");
	}
}
