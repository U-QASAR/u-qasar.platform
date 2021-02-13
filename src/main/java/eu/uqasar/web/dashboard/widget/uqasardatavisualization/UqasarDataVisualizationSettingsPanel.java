package eu.uqasar.web.dashboard.widget.uqasardatavisualization;

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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.collections.ListUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.AppendingStringBuffer;

import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import ro.fortsoft.wicket.dashboard.web.DashboardContextAware;
import ro.fortsoft.wicket.dashboard.web.DashboardPanel;
import ro.fortsoft.wicket.dashboard.web.WidgetPanel;
import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.dashboard.DashboardViewPage;

/**
 * 
 *
 *
 */
public class UqasarDataVisualizationSettingsPanel extends GenericPanel<UqasarDataVisualizationWidget> 
	implements DashboardContextAware {

	private static final long serialVersionUID = 1L;
	
	private transient DashboardContext dashboardContext;
	private Project project;
	private String projectName, qualityParams;
	private final DropDownChoice<Project> projectChoice;
	private final DropDownChoice<String> qualityParameterChoice;
	private final WebMarkupContainer wmcGeneral;
	private List<String> OBJS = new ArrayList<>(),
						 INDIS= new ArrayList<>(),
						 MTRX = new ArrayList<>();
	private List<Project> projects;
	
	public UqasarDataVisualizationSettingsPanel(String id, IModel<UqasarDataVisualizationWidget> model) {
		super(id, model);
				
		setOutputMarkupPlaceholderTag(true);
				
		//Form and WMCs
		final Form<Widget> form = new Form<>("form");
		wmcGeneral = newWebMarkupContainer("wmcGeneral");
		form.add(wmcGeneral);

		// Get the project from the settings
		projectName = getModelObject().getSettings().get("project");

		// DropDown select for Projects
		TreeNodeService treeNodeService = null;
		try {
			InitialContext ic = new InitialContext();
			treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");   
			projects = treeNodeService.getAllProjectsOfLoggedInUser();
			if (projects != null && projects.size() != 0) {
				if (projectName == null || projectName.isEmpty()) {
					projectName = projects.get(0).getName();
				}   
				project = treeNodeService.getProjectByName(projectName);
				recreateAllProjectTreeChildrenForProject(project);
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}			
		//project
		List<String> joinedQualityParameters = ListUtils.union(ListUtils.union(OBJS, INDIS),MTRX);
		qualityParameterChoice = new DropDownChoice<String>("qualityParams", new PropertyModel<String>(this, "qualityParams"), joinedQualityParameters){
	        @Override
	        protected void appendOptionHtml(AppendingStringBuffer buffer, String choice, int index, String selected) {
	            super.appendOptionHtml(buffer, choice, index, selected);
	            
	            if(UqasarDataVisualizationWidget.ALL.get(0).equals(choice)){
	            	buffer.append("<optgroup label='Quality Objectives'></optgroup>");
	            }
	            
	            int noOfObjs = OBJS.size();
	            int noOfIndis = INDIS.size();

	            if(index+1==noOfObjs && noOfObjs > 0){
	            	buffer.append("<optgroup label='Quality Indicators'></optgroup>");
	            }
	            if(index+1==(noOfObjs+noOfIndis) && noOfIndis > 0){
	            	buffer.append("<optgroup label='Metrics'></optgroup>");
	            }
	        
	        }
	    };
		qualityParameterChoice.setOutputMarkupId(true);
		wmcGeneral.add(qualityParameterChoice);

		// project
        projectChoice = new DropDownChoice<>("projects", new PropertyModel<Project>(this, "project"), projects);
		if (project != null) {
			projectChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					System.out.println(project);
					recreateAllProjectTreeChildrenForProject(project);				
					qualityParameterChoice.setChoices(ListUtils.union(ListUtils.union(OBJS, INDIS), MTRX));				
					target.add(qualityParameterChoice);			
					target.add(wmcGeneral);
					
					target.add(form);
				}
			});
		
        }

		wmcGeneral.setOutputMarkupId(true);
		wmcGeneral.add(projectChoice);
			
		form.add(new AjaxSubmitLink("submit") {			
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                if (project != null) {
					getModelObject().getSettings().put("qualityParams", qualityParams);
					getModelObject().getSettings().put("project", project.getName());
									
					Dashboard dashboard = findParent(DashboardPanel.class).getDashboard();
            		DbDashboard dbdb = (DbDashboard) dashboard;

					WidgetPanel widgetPanel = findParent(WidgetPanel.class);
					UqasarDataVisualizationWidgetView widgetView = (UqasarDataVisualizationWidgetView) widgetPanel.getWidgetView();
					target.add(widgetPanel);
	            	hideSettingPanel(target);
	            	
	            	// Do not save the default dashboard
					if (dbdb.getId() != null && dbdb.getId() != 0) {
						dashboardContext.getDashboardPersiter().save(dashboard);
		            	PageParameters params = new PageParameters();
						params.add("id", dbdb.getId());
						setResponsePage(DashboardViewPage.class, params);
	            	}
				}
			}
		});
		form.add(new AjaxLink<Void>("cancel") {
            private static final long serialVersionUID = 1L;
			@Override
            public void onClick(AjaxRequestTarget target) {
            	hideSettingPanel(target);
            }         
        });
		
		add(form);	 
	}
	
	/**
	 * 
	 * @param wicketId
	 * @param selections
	 * @return
	 */
	private SelectOptions<String> newSelectOptions(String wicketId, final List<String> selections){
		
	//	IModel<List<String>> model = new Model<List<String>>();
		
		
		SelectOptions<String> selectOptions = new SelectOptions<>(
                wicketId,
                Model.ofList(selections),
                new IOptionRenderer<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getDisplayValue(String object) {
                        return object;
                    }

                    @Override
                    public IModel<String> getModel(String value) {
                        return new Model<>(value);
                    }
                }
        );
		selectOptions.setRecreateChoices(true);
		selectOptions.setOutputMarkupId(true);
		
		return selectOptions;
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	private WebMarkupContainer newWebMarkupContainer(String string) {
		WebMarkupContainer wmc = new WebMarkupContainer(string);
		wmc.setOutputMarkupId(true);
		return wmc;
	}

	/**
	 * 
	 */
	@Override
	public void setDashboardContext(DashboardContext dashboardContext) {
		this.dashboardContext = dashboardContext;
	}

	/**
	 * 
	 * @param target
	 */
	private void hideSettingPanel(AjaxRequestTarget target) {
    	setVisible(false);
    	target.add(this);
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}


	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}


	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	
	private void recreateAllProjectTreeChildrenForProject(Project proj) {	
			
		if (proj != null) {
			List<TreeNode> objs = proj.getChildren();
			List<String> objNames = new LinkedList<>(),
					indiNames = new LinkedList<>(),		
					metricNames = new LinkedList<>();		

			// Add "All Quality Parameters"
			objNames.add(UqasarDataVisualizationWidget.ALL.get(0));

			// Objectives of Project	 	 
			for(TreeNode obj : objs){
				objNames.add(obj.getName());				
				List<TreeNode> indicatorsOfObj = obj.getChildren();
				// Indicators of Objective
				for(TreeNode ind: indicatorsOfObj){
					indiNames.add(ind.getName());				
					List<TreeNode> metricsOfIndis = ind.getChildren();
					// Metrics Of Indicator
					for(TreeNode metric : metricsOfIndis){
						metricNames.add(metric.getName());
					}
				}
			}

			OBJS = removeDuplicates(objNames);
			INDIS = removeDuplicates(indiNames);
			MTRX = removeDuplicates(metricNames);

			System.out.println(proj.getName());
			System.out.println(OBJS);
			System.out.println(INDIS);
			System.out.println(MTRX);
		}
	}
		
	/**
	 * 
	 * @param list
	 * @return
	 */
	private List<String> removeDuplicates(List<String> list){
		Set<String> set = new LinkedHashSet<>(list);
		list.clear();
		list.addAll(set);
		return list;
	}	

}


