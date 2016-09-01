package eu.uqasar.web.pages.tree.projects.panels;

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


import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

import com.vaynberg.wicket.select2.Select2MultiChoice;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.meta.ContinuousIntegrationTool;
import eu.uqasar.model.meta.CustomerType;
import eu.uqasar.model.meta.IssueTrackingTool;
import eu.uqasar.model.meta.MetaData;
import eu.uqasar.model.meta.ProgrammingLanguage;
import eu.uqasar.model.meta.ProjectType;
import eu.uqasar.model.meta.SoftwareDevelopmentMethodology;
import eu.uqasar.model.meta.SoftwareLicense;
import eu.uqasar.model.meta.SoftwareType;
import eu.uqasar.model.meta.SourceCodeManagementTool;
import eu.uqasar.model.meta.StaticAnalysisTool;
import eu.uqasar.model.meta.TestManagementTool;
import eu.uqasar.model.meta.Topic;
import eu.uqasar.model.process.Process;
import eu.uqasar.model.product.Product;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.user.User;
import eu.uqasar.service.ProcessService;
import eu.uqasar.service.ProductService;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.service.meta.MetaDataService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.StringResourceModelPlaceholderDelegate;
import eu.uqasar.web.components.util.DefaultTinyMCESettings;
import eu.uqasar.web.i18n.Language;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.admin.teams.TeamListPage;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;
import eu.uqasar.web.pages.tree.panels.thresholds.ThresholdEditor;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.provider.meta.MetaDataCreateMissingEntitiesChoiceProvider;
import eu.uqasar.web.provider.user.UserRoleChoiceProvider;

public class ProjectEditPanel extends BaseTreePanel<Project> {

	private static final long serialVersionUID = -1742487426459945529L;

	private TinyMceBehavior tinyMceBehavior;
	private final TextArea<String> description;
	private final IModel<Boolean> richEnabledModel = Model.of(Boolean.TRUE);
	private final Form<Project> form;
	private final Logger logger = Logger.getLogger(ProjectEditPanel.class);
	private final User loggedInUser = UQasar.getSession().getLoggedInUser();
	
	@Inject 
	private TreeNodeService treeNodeService;
	@Inject 
	private ProductService productService;
	@Inject
	private ProcessService processService;
	@Inject
	private QMTreeNodeService qmTreeNodeService;
	
    @Inject
    @Named(MetaDataService.NAME)
    private MetaDataService metaDataService;
	
	public ProjectEditPanel(String id, final IModel<Project> model, boolean isNew) {
		super(id, model);

		form = new Form<Project>("form", model) {
			private static final long serialVersionUID = 6993544095735400633L;

			@Override
			protected void onSubmit() {
				// Update the tree on save
				UQasarUtil.updateTree(getModelObject().getProject());
				
				PageParameters parameters = BasePage.appendSuccessMessage(
						ProjectViewPage.forProject(getModelObject()),
						new StringResourceModel("treenode.saved.message", this,
								getModel()));
				setResponsePage(ProjectViewPage.class, parameters);
			}
		};
		
		TextField<Object> name = new TextField<>("name", new PropertyModel<>(model, "name"));
		name.setRequired(true);
		form.add(name).add(new AttributeAppender("maxlength", 255));
		form.add(new TextField<>("nodeKey", new PropertyModel<>(model, "nodeKey"))
				.add(new AttributeAppender("maxlength", 255)));
		form.add(description = new TextArea<>("description",
                new PropertyModel<String>(model, "description")));
		form.add(newDateTextField("startDate", new PropertyModel<Date>(model,
				"startDate")));
		form.add(newDateTextField("endDate", new PropertyModel<Date>(model,
				"endDate")));
		form.add(new DropDownChoice<>("qmodel",
                new PropertyModel<QModel>(model, "qmodel"), qmTreeNodeService.getAllQModels()));
		form.add(new DropDownChoice<Product>("product", 
				new PropertyModel<Product>(model, "product"), productService.getAllProducts()){
			// TODO: If it is uncommented there is no way to select any product
//			public void onConfigure(){
//				super.onConfigure();
//				if(this.getDefaultModelObject() == null){
//					setVisible(false);
//				}
//			}
		});
		form.add(new DropDownChoice<Process>("process", 
				new PropertyModel<Process>(model, "process"), processService.getAllProcesses()){
			// TODO: If it is uncommented there is no way to select any product
//			public void onConfigure(){
//				super.onConfigure();
//				if(this.getDefaultModelObject() == null){
//					setVisible(false);
//				}
//			}
		});
		
		// Threshold indicator
		form.add(new ThresholdEditor<>("thresholdEditor", model));
		
//		form.add(new NumberTextField<Double>("lowerAcceptanceLimit", new PropertyModel<Double>(model, "lowerAcceptanceLimit")));
//		form.add(new NumberTextField<Double>("upperAcceptanceLimit", new PropertyModel<Double>(model, "upperAcceptanceLimit")));

		form.add(new DropDownChoice<>("formulaAverage", new PropertyModel<>(model, "formulaAverage"), Arrays.asList(Boolean.TRUE, Boolean.FALSE) ));
		
		form.add(new DropDownChoice<>("lcStage",
                new PropertyModel<LifeCycleStage>(model, "lifeCycleStage"),
                Arrays.asList(LifeCycleStage.values())));
		description.add(tinyMceBehavior = new TinyMceBehavior(
				DefaultTinyMCESettings.get()));
		form.add(new AjaxCheckBox("toggle.richtext", richEnabledModel) {
			private static final long serialVersionUID = 6814340728713542784L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (getModelObject()) {
					description.add(tinyMceBehavior);
				} else {
					description.remove(tinyMceBehavior);
					tinyMceBehavior = new TinyMceBehavior(
							DefaultTinyMCESettings.get());
				}
				target.add(ProjectEditPanel.this);
			}
		});
		
		form.add(new BootstrapBookmarkablePageLink<TeamListPage>("buttonTeam", 
				TeamListPage.class, new PageParameters().add("id", model.getObject().getId()),Type.Success){		
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(){
				setVisible(loggedInUser.getRole() == Role.Administrator);
			}
		}
		
				.setLabel(new StringResourceModel("button.team", this,  null)));
		
		form.add(newSelect2("programmingLanguages", ProgrammingLanguage.class,
                	new PropertyModel<Collection<ProgrammingLanguage>>(model.getObject(), "programmingLanguages"), form));
        form.add(newSelect2("issueTrackingTools", IssueTrackingTool.class,
                	new PropertyModel<Collection<IssueTrackingTool>>(model.getObject(), "issueTrackingTools"),form));
        form.add(newSelect2("sourceCodeManagementTools", SourceCodeManagementTool.class,
        		new PropertyModel<Collection<SourceCodeManagementTool>>(model.getObject(), "sourceCodeManagementTools"),form));
        form.add(newSelect2("staticAnalysisTools", StaticAnalysisTool.class,
        		new PropertyModel<Collection<StaticAnalysisTool>>(model.getObject(), "staticAnalysisTools"),form));
        form.add(newSelect2("testManagementTools", TestManagementTool.class,
        		new PropertyModel<Collection<TestManagementTool>>(model.getObject(), "testManagementTools"),form));
        form.add(newSelect2("continuousIntegrationTools", ContinuousIntegrationTool.class,
        		new PropertyModel<Collection<ContinuousIntegrationTool>>(model.getObject(), "continuousIntegrationTools"),form));
        form.add(newSelect2("customerType", CustomerType.class,
        		new PropertyModel<Collection<CustomerType>>(model.getObject(), "customerTypes"),form));
        form.add(newSelect2("projectType", ProjectType.class,
        		new PropertyModel<Collection<ProjectType>>(model.getObject(), "projectTypes"),form));
        form.add(newSelect2("softwareType", SoftwareType.class,
        		new PropertyModel<Collection<SoftwareType>>(model.getObject(), "softwareTypes"),form));
        form.add(newSelect2("softwareLicense", SoftwareLicense.class,
        		new PropertyModel<Collection<SoftwareLicense>>(model.getObject(), "softwareLicenses"),form));
        form.add(newSelect2("topic", Topic.class,
        		new PropertyModel<Collection<Topic>>(model.getObject(), "topics"),form)); 
        form.add(newSelect2("softwareDevelopmentMethodology", SoftwareDevelopmentMethodology.class,
        		new PropertyModel<Collection<SoftwareDevelopmentMethodology>>(model.getObject(), "softwareDevelopmentMethodologies"),form)); 
        
        
		// If cancelling creation of a new project, delete the entry
		if (isNew){
			Button cancel = new Button("cancel"){
				private static final long serialVersionUID = -6733104400111745683L;

				public void onSubmit() {
					logger.info("Deleting " +model.getObject());
	                treeNodeService.delete(model.getObject());
					List<Project> projects = treeNodeService.getAllProjects();
					// If there are no projects left after the deletion, 
					// redirect to the widget page, otherwise use the first 
					// project found.
					if (projects.size() == 0) { 
						setResponsePage(AboutPage.class);
					}
					else { 
						setResponsePage(ProjectViewPage.class, 
								ProjectViewPage.forProject(projects.get(0)));
					}
	            }
	        };
	        cancel.setDefaultFormProcessing(false);
	        form.add(cancel);			
		} else {
			form.add(
                    new BootstrapBookmarkablePageLink<>("cancel",
                            ProjectViewPage.class, ProjectViewPage.forProject(model
                            .getObject()), Type.Default)
					.setLabel(new StringResourceModel("button.cancel", this, 
							null)));
		}

		form.add(new Button("save", new StringResourceModel("button.save",
				this, null)));

		add(form);
		setOutputMarkupId(true);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptReferenceHeaderItem
				.forReference(TinyMCESettings.javaScriptReference()));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		form.addOrReplace(newDateTextField("startDate",
				new PropertyModel<Date>(form.getModel(), "startDate")));
		form.addOrReplace(newDateTextField("endDate", new PropertyModel<Date>(
				form.getModel(), "endDate")));
	}

	private DateTextField newDateTextField(final String id, IModel<Date> model) {
		Language language = Language.fromSession();
		DateTextFieldConfig config = new DateTextFieldConfig()
				.withFormat(language.getDatePattern())
				.withLanguage(language.getLocale().getLanguage())
				.allowKeyboardNavigation(true).autoClose(false)
				.highlightToday(true).showTodayButton(true);
		DateTextField dateTextField = new DateTextField(id, model, config);
		dateTextField.add(new AttributeAppender("placeHolder", language
				.getLocalizedDatePattern()));
		return dateTextField;
	}
	
	private <T extends MetaData> Select2MultiChoice<T> newSelect2(final String id,
            Class<T> clazz, PropertyModel<Collection<T>> model, Form<?> form) {
        Select2MultiChoice<T> select2MultiChoice = new Select2MultiChoice<>(
                id, model, new MetaDataCreateMissingEntitiesChoiceProvider<>(
                        metaDataService.getAll(clazz), clazz));
        final IModel<String> placeHolder = new StringResourceModelPlaceholderDelegate("placeholder.meta.input", this, null, MetaData.getLabelModel(clazz));
        select2MultiChoice.getSettings().setCloseOnSelect(false);
        select2MultiChoice.getSettings().setPlaceholder(placeHolder);
        select2MultiChoice.getSettings().setTokenSeparators(new String[]{","});
        select2MultiChoice.getSettings().setCreateSearchChoice(
                "function(term) { if (term.length > 1) { return { id: term, text: term }; } }");
        form.add(new Label("label." + id, new StringResourceModel("label.meta.known", this, null, MetaData.getLabelModel(clazz))));
        form.add(new Label("help." + id, new StringResourceModel("help.meta.input", this, null, MetaData.getLabelModel(clazz))));
        return select2MultiChoice;
    }
	
	 private Select2MultiChoice<Role> newSelect2MultiRoles(final String id, Class clazz, PropertyModel<Collection<Role>> model) {
     	
     	Select2MultiChoice<Role> select2MultiChoice = new Select2MultiChoice<>(id, model, new UserRoleChoiceProvider());

     			
     	final IModel<String> placeHolder = new StringResourceModelPlaceholderDelegate("placeholder.meta.role", this, null);
     	select2MultiChoice.getSettings().setCloseOnSelect(false);
     	select2MultiChoice.getSettings().setPlaceholder(placeHolder);
     	select2MultiChoice.getSettings().setTokenSeparators(new String[]{","});
     	select2MultiChoice.getSettings().setCreateSearchChoice(
     			"function(term) { if (term.length > 1) { return { id: term, text: term }; } }");
     	return select2MultiChoice;
     }

}
