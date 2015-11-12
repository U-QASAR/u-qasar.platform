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
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import eu.uqasar.model.user.User;
import eu.uqasar.web.components.behaviour.user.UserProfilePictureBackgroundBehaviour;
import eu.uqasar.web.pages.auth.LogoutPage;
import eu.uqasar.web.pages.reporting.ReportingPage;
import eu.uqasar.web.pages.user.ProfilePage;

public class UserSubMenu extends NavbarDropDownButton {

	private static final long serialVersionUID = 3888693104220442599L;

	private final User user;
	private WebMarkupContainer userContainer;

	public UserSubMenu(User user) {
		super(Model.of(""));	
		this.user = user;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		getParent().add(new AttributeModifier("id", "usersMenu"));
		
		if (user != null) {
			userContainer.add(new UserProfilePictureBackgroundBehaviour(user, User.PictureDimensions.NavbarMenu));
			get("btn").add(new CssClassNameAppender("loggedin user"));
			get("btn").add(new AttributeModifier("title", user.getFullName()));
			((WebMarkupContainer) get("btn")).add(new Label("user.name", Model.of(user.getFullName())));
		} else {
			((WebMarkupContainer) get("btn")).add(new Label("user.name", Model.of("")));
		}
	}

	@Override
	public boolean isActive(Component item) {
		return false;
	}

	@Override
	protected Component newButtonLabel(final String markupId,
			final IModel<?> labelModel) {
		userContainer = new WebMarkupContainer("user.container");
		userContainer.add(new Label(markupId, labelModel));
		return userContainer;
	}

	@Override
	protected List<AbstractLink> newSubMenuButtons(String buttonMarkupId) {
		final List<AbstractLink> links = new ArrayList<>();
		links.add(newProfileLink());
		links.add(newDashboardsLink());
		links.add(newAnalyticsLink());
		links.add(newReportingLink());                
		links.add(newTakeTourLink());
		links.add(HeaderNavigationBar.createMenuDevider());
		links.add(newLogoutLink());
		return links;
	}

	private MenuBookmarkablePageLink<ProfilePage> newTakeTourLink(){
		 MenuBookmarkablePageLink<ProfilePage> tourLink = new MenuBookmarkablePageLink<>(
				 ProfilePage.class, new StringResourceModel("menu.tour.take.text", this, null));
		 tourLink.add(new AttributeModifier("id", "takeTour"));
		 // disable, since the link is not used, only javascript uses this 'label' in order to start the tour
		 tourLink.setEnabled(false);
		 tourLink.setIconType(IconType.infosign);
		 return tourLink;
	}
	
	private AbstractLink newDashboardsLink() {
		return new DashboardsSubMenu(new StringResourceModel("menu.dashboards.link.text", this, null)).setIconType(new IconType("dashboard"));
	}

	private AbstractLink newAnalyticsLink() {
		return new AnalyticsSubMenu(new StringResourceModel("menu.analytic.link.text", this, null)).setIconType(new IconType("tasks"));
	}

	private MenuBookmarkablePageLink<ProfilePage> newProfileLink() {
		MenuBookmarkablePageLink<ProfilePage> logoutLink = new MenuBookmarkablePageLink<>(
				ProfilePage.class, new StringResourceModel(
						"menu.profile.link.text", this, null));
		logoutLink.setIconType(IconType.user);
		return logoutLink;
	}

	private MenuBookmarkablePageLink<LogoutPage> newLogoutLink() {
		MenuBookmarkablePageLink<LogoutPage> logoutLink = new MenuBookmarkablePageLink<>(
				LogoutPage.class, new StringResourceModel(
						"menu.logout.link.text", this, null));
		logoutLink.setIconType(IconType.off);
		return logoutLink;
	}
        
	private MenuBookmarkablePageLink<ReportingPage> newReportingLink() {
		MenuBookmarkablePageLink<ReportingPage> reportingLink = new MenuBookmarkablePageLink<>(
				ReportingPage.class, new StringResourceModel(
						"menu.reporting.link.text", this, null));
		reportingLink.setIconType(IconType.eyeopen);
		return reportingLink;
	}        
        
}

