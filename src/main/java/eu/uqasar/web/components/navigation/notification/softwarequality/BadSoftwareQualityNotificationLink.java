package eu.uqasar.web.components.navigation.notification.softwarequality;

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


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.notification.softwarequality.BadSoftwareQualityNotification;
import eu.uqasar.model.tree.Project;
import eu.uqasar.web.components.navigation.notification.NotificationBookmarkablePageLink;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;

public class BadSoftwareQualityNotificationLink extends
		NotificationBookmarkablePageLink<BadSoftwareQualityNotification, ProjectViewPage> {

	private static final long serialVersionUID = 3310107463188121652L;
	
	public BadSoftwareQualityNotificationLink(String id,
			IModel<BadSoftwareQualityNotification> model) {
		this(id, new PageParameters(), model);
	}

	public BadSoftwareQualityNotificationLink(String id, PageParameters parameters,
			final IModel<BadSoftwareQualityNotification> model) {
		
		super(id, ProjectViewPage.class, BaseTreePage.forProject(model.getObject().getProject()), model);
		
		add(new Label("project.name", getProject().getAbbreviatedName()));
		add(new Label("project.decrease", new StringResourceModel("project.about.to.decrease", null, getProject().getQualityStatus())));
		
		setIcon(new IconType("exclamation-sign"));
		get("notification.container").add(new CssClassNameAppender("project", getProject().getQualityStatus().getCssClassName()));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}
	
	private Project getProject() {
		return getModelObject() != null ? getModelObject().getProject() : null;
	}
}
