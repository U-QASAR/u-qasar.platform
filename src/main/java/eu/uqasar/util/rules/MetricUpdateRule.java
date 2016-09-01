package eu.uqasar.util.rules;

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


import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.Getter;
import lombok.Setter;
import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Rule;
import org.easyrules.core.BasicRule;
import org.jboss.solder.logging.Logger;

import eu.uqasar.model.notification.INotification;
import eu.uqasar.model.notification.metric.MetricNeedsToBeEdited;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;

/**
 * Rule taking care of viewing notifications about metrics that should be updated
 *
 */
@Setter
@Getter
@Rule(name = "Metric Update Rule", description = "Checking Metric Update")
public class MetricUpdateRule extends BasicRule {
	
	private Logger logger = Logger.getLogger(MetricUpdateRule.class);
	// Target value for the overall project quality
	private Float targetQualityValue = (float) 2;
	private TreeNodeService treeNodeService; 
	private List<Metric> MetricListForNotification = new LinkedList<>();
	private List<Project> projectsForNotifications = new LinkedList<>();
	private List<Integer> dueDaysForNotifications = new LinkedList<>();
	
	public MetricUpdateRule() {

		// Obtain the projects
		try {
			InitialContext ic = new InitialContext();
			treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
			 
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @return true if an action shall be triggered, otherwise false
	 */
	@Condition
	public boolean checkConditions() {
		// Check the target value for the projects; if one of the projects has a lower value than the specified one, return true
		return checkConditionManually();
	}

    private boolean checkConditionManually() {
        List<Metric> AllMetricList= new LinkedList<>();
        MetricListForNotification.clear();
        projectsForNotifications.clear();
        dueDaysForNotifications.clear();
        boolean alert = false;
        for (Project project : treeNodeService.getAllProjects()) {
            for (TreeNode nodeQO : project.getChildren()) {
                if (nodeQO instanceof QualityObjective) {
                    for (TreeNode nodeQI : nodeQO.getChildren()) {
                        if (nodeQI instanceof QualityIndicator) {
                            for (TreeNode nodeMetric : nodeQI.getChildren()) {
                                if (nodeMetric instanceof Metric) {

                                    // for each metric here, Try to find the History latest date
                                    Metric metric = (Metric) nodeMetric;
                                    Date latestDate = null;
                                    for (HistoricValuesBaseIndicator hv : metric.getHistoricValues()) {
                                        // check for latest date historicVelues has..
                                        if (latestDate == null || hv.getDate().compareTo(latestDate) > 0) {
                                            latestDate = hv.getDate();
                                        }
                                    }

                                    AllMetricList.add(metric);
                                    // if latest date is older than 7 days then create Notification
                                    Date cuurentDate = new Date();
                                    if (latestDate != null) {
                                        long duration = cuurentDate.getTime() - latestDate.getTime();
                                        long diffInDays = TimeUnit.MILLISECONDS.toMinutes(duration);

                                        if (diffInDays >= targetQualityValue) {
                                            // TODO: Create Notification...
                                            alert = true;
                                            // save the Metric Name and Project Name
                                            projectsForNotifications.add(project);
                                            MetricListForNotification.add(metric);
                                            dueDaysForNotifications.add((int)diffInDays);
                                        }
                                    } else { // remind user to enter the data for the first time.
                                        projectsForNotifications.add(project);
                                        MetricListForNotification.add(metric);
                                        dueDaysForNotifications.add(0);
                                        alert = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        return alert;
    }
    
	/**
	 * Action to be executed on a triggered rule
	 */
    @Action(order = 1)
    public void performActions() throws Exception {
        logger.info("Performing actions for rule \"" + this.getName() + "\"");

        // Check for the target quality value and add a notification if needed
        int index = 0;
        List<INotification> notifications = UQasarUtil.getNotifications();
        List<INotification> notificationsRemoved = new LinkedList<>();
        for (INotification notif : notifications) {
            if (notif instanceof MetricNeedsToBeEdited) {
                notificationsRemoved.add(notif);
            }
        }
        UQasarUtil.getNotifications().removeAll(notificationsRemoved);
        if (checkConditionManually()) {
            
            logger.info("Note! Metric needs to be updated)");

            for (Metric metric : MetricListForNotification) {
                // Create a new Metric-is-out-of-date notification
                MetricNeedsToBeEdited metricNotification = new MetricNeedsToBeEdited();
                metricNotification.setCreationDate(new Date());
                // metricNotification.setUser(UQasar.getSession().getLoggedInUser());
                 
                metricNotification.setMetric(metric);
                if (index < projectsForNotifications.size()) {
                    metricNotification.setProject(projectsForNotifications.get(index));
                }
                if (index < dueDaysForNotifications.size()){
                    metricNotification.setDueDays(dueDaysForNotifications.get(index));
                }
                index++;

                boolean addItem = true;
               
                for (INotification notif : UQasarUtil.getNotifications()) {
                    if (metricNotification.getNotificationType() == notif.getNotificationType()) {
                        MetricNeedsToBeEdited mn = (MetricNeedsToBeEdited) notif;
                        if (metricNotification.getMetric() == mn.getMetric()) {
                            addItem = false;
                        } 
                    }
                   
                }
                if (addItem) {
                    UQasarUtil.getNotifications().add(metricNotification);
                }
            }

        }

    }


}
