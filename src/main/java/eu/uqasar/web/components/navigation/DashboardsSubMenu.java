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

import javax.naming.InitialContext;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.DropDownSubMenu;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.dashboard.DashboardEditPage;
import eu.uqasar.web.dashboard.DashboardViewPage;
import ro.fortsoft.wicket.dashboard.Dashboard;

public class DashboardsSubMenu extends DropDownSubMenu {

	private static final long serialVersionUID = 2708741180794473231L;	
	private final Logger logger = Logger.getLogger(DashboardsSubMenu.class);
	
	public DashboardsSubMenu(IModel<String> labelModel) {
		super(labelModel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		get("dropdown-menu").add(new CssClassNameAppender("dashboards"));
	}

	@Override
	public boolean isActive(Component item) {
		return false;
	}

	@Override
	protected List<AbstractLink> newSubMenuButtons(final String buttonMarkupId) {
		final List<AbstractLink> subMenu = new ArrayList<>();
		List<Dashboard> dashboards = null;
		
		try {
			InitialContext ic = new InitialContext();
			UserService userService = (UserService) ic.lookup("java:module/UserService");
			User user = userService.getById(UQasar.getSession().getLoggedInUser().getId());
			logger.info("Loading dashboards for user " +user);
			
			if (user != null && user.getDashboards() != null && user.getDashboards().size() > 0) {
				dashboards = user.getDashboards();

				if (dashboards != null && dashboards.size() > 0) {
					// Show a list of existing dashboards on top of the menu
					for (Dashboard dash : dashboards) {
						DbDashboard dbdb = (DbDashboard) dash;
						PageParameters params = new PageParameters();
						if (dbdb != null) {
							params.add("id", dbdb.getId());
						}
						logger.info("Adding dashboard " +dbdb.toString() +" to menu...");
						MenuBookmarkablePageLink<DashboardViewPage> dashboardLink = 
						new MenuBookmarkablePageLink<>(
						DashboardViewPage.class, 
						params,
						Model.of(dash.getTitle()));
						dashboardLink.setIconType(IconType.list);
						subMenu.add(dashboardLink);		
					}
					// Create a menu divider
					subMenu.add(HeaderNavigationBar.createMenuDevider());
				} 
			}
					
			// -- NEW Dashboard
			MenuBookmarkablePageLink<DashboardViewPage> newDashboardLink = new MenuBookmarkablePageLink<>(
					DashboardEditPage.class,
					new PageParameters(),
					new StringResourceModel("menu.dashboards.create.text", this, null));
			newDashboardLink.setIconType(IconType.plus);
			subMenu.add(newDashboardLink);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

				
		return subMenu;
	}
}
