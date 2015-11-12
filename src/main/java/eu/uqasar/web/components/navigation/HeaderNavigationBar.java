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


import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuDivider;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.AbstractNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.ImmutableNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import eu.uqasar.model.role.Role;
import eu.uqasar.service.notification.message.auth.register.RegistrationMessageService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.navigation.notification.NotificationDropDownMenu;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.auth.login.LoginPage;

public class HeaderNavigationBar extends Navbar {

	private static final long serialVersionUID = -4140568215278705336L;

	@Inject
	TreeNodeService treeNodeService;

	@Inject
	RegistrationMessageService mailing;

	private final Page containingPage;
    private HeaderSearchForm searchForm;
	private boolean menusAdded = false;
    private String searchQuery;

	public HeaderNavigationBar(String componentId, Page page) {
		super(componentId);
		this.containingPage = page;

		// for ajax replacements
		setOutputMarkupId(true);
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		if (!menusAdded) {
			menusAdded = true;

			// add logo
			addUQasarLogo();

			if (UQasar.getSession().getLoggedInUser() != null) {
				// add projects submenu
				addComponents(new ImmutableNavbarComponent(new ProjectsSubMenu(
						new StringResourceModel("menu.projects.link.text", this,
								null)), Navbar.ComponentPosition.LEFT));
				

				// add quality models submenu
				addComponents(new ImmutableNavbarComponent(new QModelsSubMenu(
						new StringResourceModel("menu.qmodels.link.text", this,
								null)), Navbar.ComponentPosition.LEFT));

				// add search form
				addComponents(newNavbarSearchForm());

				// add notifications
				addComponents(new ImmutableNavbarComponent(
						new NotificationsSubMenu(
								NotificationDropDownMenu.createNotifications(treeNodeService)),
								Navbar.ComponentPosition.RIGHT));

				// add languages
				addComponents(newLanguageMenu());

				// add admin menu, if user has admin rights
				if (UQasar.getSession().getLoggedInUser().getRole() == Role.Administrator) {
					addComponents(new ImmutableNavbarComponent(new AdminSubMenu(
							Model.of("")), Navbar.ComponentPosition.RIGHT));
				}

				// add user menu
				addComponents(new ImmutableNavbarComponent(new UserSubMenu(
						UQasar.getSession().getLoggedInUser()),
						Navbar.ComponentPosition.RIGHT));
			} else {
				addComponents(newLanguageMenu());
				addComponents(newLoginButton());
			}
		}
	}

	private void addUQasarLogo() {
		setPosition(Position.TOP);
		final StringResourceModel alt = new StringResourceModel(
				"menu.home.link.text", this, null);
		setBrandImage(new PackageResourceReference(BasePage.class,
				"../img/uqasar-logo.png"), alt);
		get("brandName").get("brandImage").add(
				new AttributeModifier("title", alt));
		get("brandName").add(
				new AttributeModifier("id", "brand"));
	}

	private AbstractNavbarComponent newLanguageMenu() {
		return new ImmutableNavbarComponent(
				new LanguageSubMenu(containingPage, new StringResourceModel("menu.languages.submenu.text", this, null)),
				Navbar.ComponentPosition.RIGHT);
	}

	private AbstractNavbarComponent newLoginButton() {
		NavbarButton<LoginPage> loginButton = new NavbarButton<>(LoginPage.class, new StringResourceModel(
				"menu.login.link.text", this, null));
		return new ImmutableNavbarComponent(loginButton, Navbar.ComponentPosition.RIGHT);
	}

	private AbstractNavbarComponent newNavbarSearchForm() {
		return new AbstractNavbarComponent(ComponentPosition.RIGHT) {
			private static final long serialVersionUID = 4462437740704835525L;

			@Override
			public Component create(String markupId) {
                searchForm = new HeaderSearchForm(markupId, HeaderNavigationBar.this.searchQuery);
				return searchForm;
			}
		};
	}
    
    public void setSearchTerm(final String query) {
        this.searchQuery = query;
    }

	public static MenuDivider createMenuDevider() {
		return new MenuDivider() {

			private static final long serialVersionUID = -8053812486693771370L;

			@Override
			protected void onBeforeRender() {
				super.onBeforeRender();
				getParent().add(new AttributeModifier("class", "divider"));
			}
		};
	}

}
