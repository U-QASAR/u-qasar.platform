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


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.naming.InitialContext;

import lombok.Setter;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;

import ro.fortsoft.wicket.dashboard.Dashboard;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.notification.INotification;
import eu.uqasar.model.notification.complexity.ComplexityNotification;
import eu.uqasar.model.notification.dashboard.DashboardSharedNotification;
import eu.uqasar.model.notification.project.ProjectNearEndNotification;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;
import eu.uqasar.service.HistoricalDataService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.UQasar;

@Setter
public abstract class NotificationDropDownMenu extends NavbarDropDownButton {

	private static final long serialVersionUID = 969519549174632887L;

	protected INotification[] notifications = new INotification[0];


	protected NotificationDropDownMenu() {
		super(Model.of(""));
		setOutputMarkupId(true);
	}

	@Override
	public boolean isActive(Component item) {
		return false;
	}

	private void updateLabelAndIcon() {
		if (notifications.length == 0) {
			getNotificationCountLabelContainer().add(
					new CssClassNameAppender("badge-inverse"));
		} else {
			getNotificationCountLabelContainer().add(
					new CssClassNameAppender("badge-important"));
		}
		getNotificationCountLabel().setDefaultModelObject(
				String.valueOf(notifications.length));
	}

	protected Component getNotificationLabel() {
		return this.get("btn");
	}

	private Component getNotificationCountLabelContainer() {
		return this.get("btn").get("label.container");
	}

	private Component getNotificationCountLabel() {
		return this.get("btn").get("label.container").get("label");
	}

	@Override
	protected Component newButtonLabel(final String markupId,
			final IModel<?> labelModel) {
		final WebMarkupContainer labelContainer = new WebMarkupContainer(
				"label.container");
		labelContainer.add(new Label(markupId, labelModel));
		return labelContainer;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		updateLabelAndIcon();
	}


	/** 
	 * createNotifications
	 * 
	 */
	public static INotification[] createNotifications(TreeNodeService service){

		List<Project> projects = service.getAllProjects();
		List<INotification> notifications = new ArrayList<>();

		// Attempt to fetch notifications only if there are projects
		if (projects.size() > 0) {

			// Obtain additional notifications from a shared list (these are created by rules triggered by timer)
			if (UQasarUtil.getNotifications() != null && UQasarUtil.getNotifications().size() > 0) {
				notifications.addAll(UQasarUtil.getNotifications());
			}

			// ProjectNearEndNotification
			/////////////////////////////
			for(Project project : projects){	
				if (project.getRemainingDays() < 365) {
					ProjectNearEndNotification nearEndNotify = new ProjectNearEndNotification();
					nearEndNotify.setCreationDate(DateTime.now().minusHours(3)
							.minusMinutes(12).toDate());
					nearEndNotify.setUser(UQasar.getSession().getLoggedInUser());
					nearEndNotify.setProject(project);
					notifications.add(nearEndNotify);
				}
			}			
			// DashboardSharedNotification 
			//////////////////////////////
			if(UQasar.getSession().getLoggedInUser().getDashboards().size() > 0){
				List<Dashboard> userDbs = UQasar.getSession().getLoggedInUser().getDashboards();
				for(Dashboard db : userDbs){
					DbDashboard dash = (DbDashboard) db;
					if(dash.getSharedBy() != null){	
						DashboardSharedNotification dbSharedNotify = new DashboardSharedNotification();
						dbSharedNotify.setCreationDate(DateTime.now().minusHours(13).minusMinutes(2).toDate());
						dbSharedNotify.setUser(UQasar.getSession().getLoggedInUser());	
						dbSharedNotify.setDashboard(dash);
						notifications.add(dbSharedNotify);
					}
				}				
			}
			// ComplexityNotification
			/////////////////////////////
			for(Project project : projects){
				InitialContext ic = null;
				HistoricalDataService histService = null;
				// JNDI call for Hist-DataService
				try{
					ic = new InitialContext();
					histService = (HistoricalDataService) ic.lookup("java:module/HistoricalDataService");		
				}  catch (Exception e){
					e.printStackTrace();
				}
				// go through tree
				List<TreeNode> objectives = project.getChildren();
				for(TreeNode obj : objectives){
					List<TreeNode> indicators = obj.getChildren();
					for(TreeNode ind : indicators){
						List<TreeNode> metrics = ind.getChildren();
						for(TreeNode metric : metrics){											
							// cast to a real Metric
							Metric m = (Metric)  metric;

							if(m.getMetricSource() == MetricSource.StaticAnalysis 
									&& m.getMetricType()!=null 
									&& m.getMetricType().toLowerCase().contains("complexity")){

								// 1. get last max. 10 historic values			
								// set size (avoid divide by 0 zero)
								List<HistoricValuesBaseIndicator> allHistoricalMetricValues = histService.getAllHistValuesForBaseIndAsc(metric.getId());
								int size = allHistoricalMetricValues.size() == 0 ? 1 : allHistoricalMetricValues.size();
								List<HistoricValuesBaseIndicator> lastMaxTenValues = allHistoricalMetricValues.subList(Math.max(size - 10, 0), size);

								// 2. calculate mean, if the increase of the mean  of the last max. 10 values
								float sum = 0;
								for(HistoricValuesBaseIndicator bi : lastMaxTenValues){
									sum += bi.getValue();
								}
								float mean = (sum / size) / 100; // mean in percent
								// System.out.println("Mean: "+mean);

								// 3. create notify, if mean increase is > 25% 
								if(mean > 0.25){
									ComplexityNotification complexity = new ComplexityNotification();
									complexity.setProject(project);
									complexity.setName(metric.getName());
									complexity.setUser(UQasar.getSession().getLoggedInUser());
									complexity.setCreationDate(DateTime.now().minusHours(13).minusMinutes(2).toDate());
									notifications.add(complexity);
								}
							} 

						}
					}
				}
			}	
		}
		return notifications.toArray(new INotification[notifications.size()]);
	}


	public static INotification[] createRandomNotifications(TreeNodeService service) {
		List<INotification> notifications = new ArrayList<>();
		List<Project> projects = service.getAllProjects();
		if (projects.size() > 0) {
			Random random = new Random();
			int notificationCount = random.nextInt(4);
			for (int i = 0; i < notificationCount; i++) {
				ProjectNearEndNotification notification = new ProjectNearEndNotification();
				notification.setCreationDate(DateTime.now().minusHours(3)
						.minusMinutes(12).toDate());
				notification.setUser(UQasar.getSession().getLoggedInUser());
				notification.setProject(projects.get(0));
				notifications.add(notification);
			}
		}
		return notifications.toArray(new INotification[notifications.size()]);
	}
}
