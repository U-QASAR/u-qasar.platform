package eu.uqasar.web.components.navigation.notification;

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


import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.notification.NoNotificationsNotification;
import eu.uqasar.web.pages.AboutPage;

public class NoNotificationsNotificationLink extends NotificationBookmarkablePageLink<NoNotificationsNotification, AboutPage>{

	private static final long serialVersionUID = 4904334928788603128L;
	
	public NoNotificationsNotificationLink(String id, IModel<NoNotificationsNotification> model) {
		this(id, new PageParameters(), model);
	}

	public NoNotificationsNotificationLink(String id, PageParameters parameters,
			IModel<NoNotificationsNotification> model) {
		super(id, AboutPage.class, parameters, model);
		this.gotoContainer.setVisible(false);
		setEnabled(false);
		setIcon(IconType.check);
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		
	}

}
