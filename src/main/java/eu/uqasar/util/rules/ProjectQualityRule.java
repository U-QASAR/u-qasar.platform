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


import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.Getter;
import lombok.Setter;
import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Rule;
import org.easyrules.core.BasicRule;
import org.jboss.solder.logging.Logger;
import org.joda.time.DateTime;

import eu.uqasar.model.notification.INotification;
import eu.uqasar.model.notification.softwarequality.BadSoftwareQualityNotification;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;

@Setter
@Getter
@Rule(name = "Project Quality Rule", description = "Checking the project overall quality")
public class ProjectQualityRule extends BasicRule {
	
	private Logger logger = Logger.getLogger(ProjectQualityRule.class);
	// Projects to be checked for conditions
	private List<Project> projects = new ArrayList<>();
	// Target value for the overall project quality
	private Float targetQualityValue = (float) 50;
	private TreeNodeService treeNodeService; 

	public ProjectQualityRule() {

		// Obtain the projects
		try {
			InitialContext ic = new InitialContext();
			treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
			projects = treeNodeService.getAllProjects();
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
		boolean alert = false;
		// Check the target value for the projects; if one of the projects has a lower value than the specified one, return true
		for (Project project : projects) {
			logger.debug("Checking the conditions of the rule \"" +this.getName() +"\" for the project " +project);
			// If one of the projects has a quality value below the targetvalue, the rules should be applied
			if (project.getValue() < targetQualityValue) {
				alert = true;
				break;
			}
		}

		return alert;
	}

	/**
	 * Action to be executed on a triggered rule
	 */
	@Action(order = 1)
	public void performActions() throws Exception {
		logger.info("Performing actions for rule \"" +this.getName() +"\"");
		for (Project project : projects) {

			// Check for the target quality value and add a notification if needed 
			if (project.getValue() < targetQualityValue) {
				logger.info("Note! Project quality level below " 
						+targetQualityValue +"% (" +project.getValue() +")");

				// Create a new notification
				BadSoftwareQualityNotification badSoftwareNotify = new BadSoftwareQualityNotification();	
				badSoftwareNotify.setCreationDate(DateTime.now().toDate());
//				badSoftwareNotify.setUser(UQasar.getSession().getLoggedInUser());	
				badSoftwareNotify.setProject(project);
				boolean addItem = true;
				for (INotification notif : UQasarUtil.getNotifications()) {
					if (badSoftwareNotify.getNotificationType() == notif.getNotificationType()) {
						BadSoftwareQualityNotification bsqn = (BadSoftwareQualityNotification) notif;
						if (badSoftwareNotify.getProject() == bsqn.getProject()) {
							addItem = false;
						}
					}
				}
				if (addItem) {
					UQasarUtil.getNotifications().add(badSoftwareNotify);
				}


		        
			} else {
				logger.info("Project quality: " +project.getValue());
			}
		}
	}


}
