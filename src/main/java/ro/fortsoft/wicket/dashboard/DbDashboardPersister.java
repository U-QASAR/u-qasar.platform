/*
 * Copyright 2012 Decebal Suiu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with
 * the License. You may obtain a copy of the License in the LICENSE file, or at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ro.fortsoft.wicket.dashboard;

import java.util.Collections;
import java.util.List;

import javax.naming.InitialContext;

import org.jboss.solder.logging.Logger;

import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.user.User;
import eu.uqasar.service.DashboardService;
import eu.uqasar.service.user.UserService;

/**
 * Persisting a dashboard to a database
 *
 */
public class DbDashboardPersister implements DashboardPersister {
	
	private static final Logger logger = Logger.getLogger(DbDashboardPersister.class);
	private final Long userId; // Id of the user whose dashboard is used
	private UserService userService;
	private DashboardService dashboardService;
	
	public DbDashboardPersister(Long userId) {
		this.userId = userId;
	}
	
	@Override
	public Dashboard load() {
		try {
			InitialContext ic = new InitialContext();
			userService = (UserService) ic.lookup("java:module/UserService");
			User user = userService.getById(userId);
			logger.info("Loading dashboard for user " +user);
			if (user != null && user.getDashboards() != null && user.getDashboards().size() > 0) {
				return user.getDashboards().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void save(Dashboard dashboard) {
		
		logger.info("Persisting dashboard " +dashboard);
		
		// sort widgets
		Collections.sort(dashboard.getWidgets(), new WidgetComparator());

		try {
			InitialContext ic = new InitialContext();
			dashboardService = (DashboardService) ic.lookup("java:module/DashboardService");
			DbDashboard dbDashboard = (DbDashboard) dashboard;
			dashboardService.update(dbDashboard);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load all the dashboards belonging to the user
	 */
	@Override
	public List<Dashboard> loadAll() {
		logger.info("Loading all the dashboards for the user." );
		try {
			InitialContext ic = new InitialContext();
			userService = (UserService) ic.lookup("java:module/UserService");
			User user = userService.getById(userId);
			logger.info("Loading dashboard for user " +user);
			if (user != null && user.getDashboards() != null && user.getDashboards().size() > 0) {
				return user.getDashboards();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		return null;
	}

	/**
	 * Save all the dashboards of the user.
	 */
	@Override
	public void saveAll(List<Dashboard> dashboards) {
		logger.info("Persisting dashboard " +dashboards);
		
		for (Dashboard db : dashboards) {
			// sort widgets
			Collections.sort(db.getWidgets(), new WidgetComparator());

			try {
				InitialContext ic = new InitialContext();
				dashboardService = (DashboardService) ic.lookup("java:module/DashboardService");
				DbDashboard dbDashboard = (DbDashboard) db;
				dashboardService.update(dbDashboard);		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
