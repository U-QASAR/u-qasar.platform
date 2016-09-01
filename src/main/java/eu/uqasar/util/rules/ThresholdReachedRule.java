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
import eu.uqasar.model.notification.NotificationType;
import eu.uqasar.model.notification.threshold.ThresholdReachedNotification;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;

@Setter
@Getter
@Rule(name = "Threashold Reached Rule", description = "Check, if a threshold of a children in the project tree is reached")
public class ThresholdReachedRule extends BasicRule {
	
	private Logger logger = Logger.getLogger(ThresholdReachedRule.class);
	// Projects to be checked for conditions
	private List<Project> projects = new ArrayList<>();
	private List<TreeNode> treeNodes = new ArrayList<>();
	
	
	private TreeNodeService treeNodeService; 

	public ThresholdReachedRule() {

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

		// overwrite existing projects with current ones
		projects = treeNodeService.getAllProjects();
		
		if(projects!=null){
			for (Project project : projects) {
				logger.debug("Checking the conditions of the rule \"" +this.getName() +"\" for the project " +project);
				
				for(TreeNode child : project.getChildren()){
					if(child instanceof QualityObjective){
						QualityObjective QO = child.getQualityObjective();
						
						if(QO.getValue() <= QO.getThreshold().getLowerAcceptanceLimit() || QO.getValue() == QO.getThreshold().getUpperAcceptanceLimit()){
							alert = true;
							break;
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
		logger.info("Performing actions for rule \"" +this.getName() +"\"");
	
		treeNodes.removeAll(treeNodes);
	//	projects = treeNodeService.getAllProjects(); 
		
		if(projects!=null){
			for (Project project : projects) {
				for(TreeNode child : project.getChildren()){
					if(child instanceof QualityObjective){
						QualityObjective QO = child.getQualityObjective();
						if(QO.getValue() <= QO.getThreshold().getLowerAcceptanceLimit() || QO.getValue() == QO.getThreshold().getUpperAcceptanceLimit()){
							// if new
							treeNodes.add(QO);
							
						}
					}
					// TODO do also for Indicators and metrics?
				}
				//remove old, if exists
				boolean deleteItem = false;
				ThresholdReachedNotification trn = null;
				for (INotification notif : UQasarUtil.getNotifications()) {
					if (notif.getNotificationType() == NotificationType.THRESHOLD) {
						trn = (ThresholdReachedNotification) notif;
						deleteItem = true;
					}
				}
				if(deleteItem){
					UQasarUtil.getNotifications().remove(trn);
				}
				
				ThresholdReachedNotification thresholdReach = new ThresholdReachedNotification();
				thresholdReach.setCreationDate(DateTime.now().toDate());
				thresholdReach.setTreeNodes(treeNodes);
				thresholdReach.setProject(project);
				//add notify
				UQasarUtil.getNotifications().add(thresholdReach);
				
				
			}
		}
	}


}
