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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import wicket.contrib.tinymce.TinyMceBehavior;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.vaynberg.wicket.select2.Select2Choice;
import com.vaynberg.wicket.select2.Select2MultiChoice;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameRemover;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import eu.uqasar.model.company.Company;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.measure.MetricSource;
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
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.model.user.User;
import eu.uqasar.service.ProcessService;
import eu.uqasar.service.ProductService;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.service.dataadapter.AdapterSettingsService;
import eu.uqasar.service.meta.MetaDataService;
import eu.uqasar.service.meta.MetaDataServiceBroker;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.service.user.TeamMembershipService;
import eu.uqasar.service.user.TeamService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.WebConstants;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.IndicatingAjaxAutoCompleteTextField;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.components.StringResourceModelPlaceholderDelegate;
import eu.uqasar.web.components.behaviour.user.UserProfilePictureBackgroundBehaviour;
import eu.uqasar.web.components.util.DefaultTinyMCESettings;
import eu.uqasar.web.i18n.Language;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.admin.users.UserEditPage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.subset.panel.SubsetProposalPanel;
import eu.uqasar.web.provider.meta.MetaDataCreateMissingEntitiesChoiceProvider;
import eu.uqasar.web.provider.user.UserRoleChoiceProvider;

/**
 *
 *
 */
public class ProjectWizardPanel extends Wizard{

	private static final long serialVersionUID = 1L;
	private final WizardModel wizard;
	private final WizardStep1 step1;
	private final WizardStep2 step2;
	private final WizardStep3 step3;
	private final WizardStep4 step4;
	private final WizardStep5 step5;
	private final WizardStep6 step6;
	private final WizardStep7 step7;

	private Project project;
	private Company company;    
	private User user;
	private List<Role> rolesNeeded = new ArrayList<>();
	private List<Team> teams;
	private List<User> proposedUsers = new ArrayList<>();
	private WebMarkupContainer teamContainer;
	private ListView<User> usersView;
	private boolean toggle = false;
	private Form<Void> step4Form;
	// The AdapterSettings to edit/save
	private final List<AdapterSettings> adapterSettingsList = new ArrayList<>();
    private final AdapterSettings adapterSettings  = new AdapterSettings();
	// New Project Tags to match with the QM
	private WebMarkupContainer subsetContainer;
	private SubsetProposalPanel subsetProposalPanel;

	@Inject 
	private TreeNodeService treeNodeService;
	@Inject 
	private UserService userService;
	@Inject 
	private TeamMembershipService membershipService;
	@Inject 
	private TeamService teamService;
	@Inject 
	private CompanyService companyService;
	@Inject
	private AdapterSettingsService adapterSettingsService;


	private final Multimap<User, String> matchedSkills = ArrayListMultimap.create();

	/**
	 * Step1
	 */
	private final class WizardStep1 extends WizardStep {

		/**
		 * 
		 */
		private static final long serialVersionUID = 504714843653947576L;
		private final Form<Project> form;
		private final TextArea<String> inputDescription;
		private final TextField<Project> name, key;
		private final String description = "";

		public WizardStep1(){            
			super("", "");

			form = new Form<>("form");            
			name = newTextField("projectName", project, "name");       
			key = newTextField("projectKey", project, "nodeKey");
			inputDescription = new TextArea<>("projectDescription", new PropertyModel<String>(project, "description"));
			inputDescription.add(new TinyMceBehavior(DefaultTinyMCESettings.get())); 
			form.add(name.setRequired(true))
			.add(key.setRequired(true))
			.add(inputDescription.setRequired(true));
			add(form);    
		}
		
		
		/**
		 * 
		 * @param markupId
		 * @return
		 */
		private TextField<Project> newTextField(String markupId, Project project, String property){
			return new TextField<>(markupId, new PropertyModel<Project>(project, property));

		}
	}
	
	
	/**
	 * Step2
	 */    
	private final class WizardStep2 extends WizardStep{

		private final Form<Void> form;    
		public WizardStep2(){
			super("","");
			form = new Form<>("form");
			form.add(newDateTextField("startDate", 
					new PropertyModel<Date>(project,"startDate"))
					.setRequired(true));
			form.add(newDateTextField("endDate", 
					new PropertyModel<Date>(project,"endDate"))
					.setRequired(true));
			add(form);
		}
		/**
		 * @param id
		 * @param model
		 * @return 
		 */
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
	}

	
	/**
	 * Step3
	 */
	private final class WizardStep3<T extends MetaData, S extends MetaDataService<T>> extends WizardStep {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5941982365548199496L;

		private final Form<Void> form;      
		private final List<Role> roles = new ArrayList<>();



		@Inject MetaDataServiceBroker metaDataServiceBroker;
		@Inject
		@Named(MetaDataService.NAME)
		private MetaDataService metaDataService;

		public WizardStep3(){          
			super("","");               
			form = new Form<>("form");

			form.add(newSelect2Multi("customerType", CustomerType.class, new PropertyModel<Collection<CustomerType>>(project, "customerTypes")))
			.add(newSelect2Multi("projectType", ProjectType.class, new PropertyModel<Collection<ProjectType>>(project, "projectTypes")))
			.add(newSelect2Multi("softwareType", SoftwareType.class, new PropertyModel<Collection<SoftwareType>>(project, "softwareTypes")))
			.add(newSelect2Multi("softwareLicense", SoftwareLicense.class, new PropertyModel<Collection<SoftwareLicense>>(project, "softwareLicenses")))
			.add(newSelect2Multi("topic", Topic.class, new PropertyModel<Collection<Topic>>(project, "topics")));

			form.add(newSelect2MultiRoles("roles", Role.class, new PropertyModel<Collection<Role>>(this, "roles")))
			.add(newSelect2Multi("progLanguage", ProgrammingLanguage.class, new PropertyModel<Collection<ProgrammingLanguage>>(project, "programmingLanguages")))
			.add(newSelect2Multi("contIntegration", ContinuousIntegrationTool.class, new PropertyModel<Collection<ContinuousIntegrationTool>>(project, "continuousIntegrationTools")))
			.add(newSelect2Multi("issueTracking", IssueTrackingTool.class, new PropertyModel<Collection<IssueTrackingTool>>(project, "issueTrackingTools")))
			.add(newSelect2Multi("srcCodeManagement", SourceCodeManagementTool.class, new PropertyModel<Collection<SourceCodeManagementTool>>(project, "sourceCodeManagementTools")))
			.add(newSelect2Multi("staticAnalysis", StaticAnalysisTool.class, new PropertyModel<Collection<StaticAnalysisTool>>(project, "staticAnalysisTools")))
			.add(newSelect2Multi("testManagement", TestManagementTool.class, new PropertyModel<Collection<TestManagementTool>>(project, "testManagementTools")))
			.add(newSelect2Multi("softwareDevelopmentMethodology", SoftwareDevelopmentMethodology.class, new PropertyModel<Collection<SoftwareDevelopmentMethodology>>(project, "softwareDevelopmentMethodologies")));

			rolesNeeded = roles;
			add(form);



		}

		// NEXT BUTTON on STEP3
		@Override
		public void applyState(){   	
			proposedUsers = teamBuilding();
		}

		/**
		 * 
		 * @param id
		 * @param clazz
		 * @param model
		 * @return
		 */
		private <T extends MetaData> Select2Choice<T> newSelect2Single(final String id, Class<T> clazz, PropertyModel<T> model){
			return new Select2Choice<>(
                    id, model, new MetaDataCreateMissingEntitiesChoiceProvider<>(
                    metaDataService.getAll(clazz), clazz));
		}
		/**
		 * 
		 * @param id
		 * @param clazz
		 * @param model
		 * @return
		 */
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
		
		/**
		 * 
		 * @param id
		 * @param clazz
		 * @param model
		 * @return
		 */
		private <T extends MetaData> Select2MultiChoice<T> newSelect2Multi(final String id,
				Class<T> clazz, PropertyModel<Collection<T>> model) {
			Select2MultiChoice<T> select2MultiChoice = new Select2MultiChoice<>(
					id, model, new MetaDataCreateMissingEntitiesChoiceProvider<>(
							metaDataService.getAll(clazz), clazz));
			final IModel<String> placeHolder = new StringResourceModelPlaceholderDelegate("placeholder.meta.input", this, null, MetaData.getLabelModel(clazz));
			select2MultiChoice.getSettings().setCloseOnSelect(false);
			select2MultiChoice.getSettings().setPlaceholder(placeHolder);
			select2MultiChoice.getSettings().setTokenSeparators(new String[]{","});
			select2MultiChoice.getSettings().setCreateSearchChoice(
					"function(term) { if (term.length > 1) { return { id: term, text: term }; } }");
			return select2MultiChoice;
		}

		/**
		 * @param markupId
		 * @param metaDataObject
		 * @return 
		 */
		private DropDownChoice<T> newDropDownChoice(String markupId, MetaData metaDataObject){
			Class<T> clazz = (Class<T>) metaDataObject.getClass();
			S service = metaDataServiceBroker.getService(clazz);
			long count = service.countAll();
			return new DropDownChoice<>(
                    markupId,
                    new PropertyModel<T>(metaDataObject, "name"),
                    service.getAllAscendingByName(clazz, 0, count));
		}   
	}

	/**
	 * Step4
	 */
	private final class WizardStep4 extends WizardStep {


		@Inject
		private TeamMembershipService teamMemberService;
		private final AutoCompleteTextField<User> userComplete;

		public WizardStep4(){            
			super("", "");   

			final Modal deleteConfirmationModal;
			add(deleteConfirmationModal = newDeleteConfirmationModal());

			step4Form = new Form<>("form");
			teamContainer = new WebMarkupContainer("teamContainer", new Model<User>());
			teamContainer.setOutputMarkupId(true);
			step4Form.add(teamContainer);

			// DATAVIEW
			teamContainer.add(usersView = new ListView<User>("users", Model.ofList(proposedUsers)) {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onConfigure(){
					super.onConfigure();
					System.out.println("proposedUsers: "+proposedUsers);
					//update model
					usersView.setModelObject(proposedUsers);
				}

				@Override
				protected void populateItem(ListItem<User> item) {

					final User proposedUser = item.getModelObject();

					item.add(new Label("company", new PropertyModel<String>(proposedUser, "company.shortName")));

					Link userEditPictureLink = new BookmarkablePageLink("link.picture.edit.user", UserEditPage.class, new PageParameters().add("id", proposedUser.getId()));
					WebMarkupContainer picture = new WebMarkupContainer("picture");
					picture.add(new UserProfilePictureBackgroundBehaviour(proposedUser, User.PictureDimensions.Badge));

					item.add(userEditPictureLink.add(picture));

					Link userEditNameLink = new BookmarkablePageLink("link.name.edit.user", UserEditPage.class, new PageParameters().add("id", proposedUser.getId()));
					item.add(userEditNameLink.add(new Label("td.username", new PropertyModel<>(proposedUser, "fullNameWithUserName"))));

					item.add(new DropDownChoice<>("role", new PropertyModel<Role>(proposedUser, "role"), Role.getAllRolesWithLoggedInUser(proposedUser)).setOutputMarkupId(true).setEnabled(false));
					item.add(new MultiLineLabel("skills", getMatchedSkillsFromUser(proposedUser)));

					final WebMarkupContainer btnContainer = new WebMarkupContainer("btnContainer");
					btnContainer.setOutputMarkupId(true);


					item.add(btnContainer.add(new AjaxLink("confirm"){
						@Override
						public void onClick(AjaxRequestTarget target) {
							if(!toggle){
								add(new CssClassNameAppender(Model.of("btn-success")));
								target.add(btnContainer);

							} else {
								add(new CssClassNameRemover(Model.of("btn-success")));
								target.add(btnContainer);
							}
							toggle = !toggle;

						}
					}));

					item.add(btnContainer.add(new AjaxLink("reject"){
						@Override
						public void onClick(AjaxRequestTarget target) {
							user = proposedUser;
							deleteConfirmationModal.appendShowDialogJavaScript(target);
						}
					}));
				}

			});


			userComplete = new IndicatingAjaxAutoCompleteTextField<User>("add.userName", new PropertyModel(membership1, "user"),
					User.class, getAutocompleteRenderer(), WebConstants.getDefaultAutoCompleteSettings()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 3334121038493199841L;

				@Override
				protected Iterator<User> getChoices(String input) {
					return getTeamableUsers(input);
				}

				@Override
				public <User> IConverter<User> getConverter(Class<User> type) {
					return (IConverter<User>) getAutocompleteConverter();
				}
			};

			AjaxSubmitLink addMember = new AjaxSubmitLink("add.member", step4Form) {

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					//target.add(feedbackPanel);
					target.add(form);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

					addUserToTeam(target, form);
				}
			};
			addMember.add(new Label("button.add.save", new StringResourceModel("button.add.save", this, Model.of(team))));
			step4Form.add(addMember);
			step4Form.add(userComplete);	  	

			usersView.setOutputMarkupId(true); 
			add(step4Form);
		}


		/**
		 * 
		 * @param target
		 * @param form
		 */
		private void addUserToTeam(AjaxRequestTarget target, Form<?> form) {
			User user = userComplete.getConvertedInput();
			if(user == null)  {
				error(getString("error.user.notvalid"));
				userComplete.error(getString("error.user.notvalid"));
				//target.add(feedbackPanel);
				target.add(form);
				return;
			}
			step4Form.clearInput();
			userComplete.setModelObject(null);
			proposedUsers.add(user);				            
			target.add(teamContainer);
			target.add(step4Form);
		}


		/**
		 * 
		 * @param user
		 * @return
		 */
		private String getMatchedSkillsFromUser(User user) {

			List<String> skills = (List<String>) matchedSkills.get(user);
			Set<String> skillsNoDoubles = new HashSet<>();
			skillsNoDoubles.addAll(skills);

			return skillsNoDoubles.toString().replace("[", "").replace("]", "");
		}

		/**
		 * 
		 * @return
		 */
		private NotificationModal newDeleteConfirmationModal() {
			final NotificationModal notificationModal = new NotificationModal(
					"deleteConfirmationModal", new StringResourceModel(
							"delete.confirmation.modal.header", this, null),
							new StringResourceModel("delete.confirmation.modal.message",
									this, null), false);
			notificationModal.addButton(new ModalActionButton(notificationModal,
					Buttons.Type.Primary, new StringResourceModel(
							"delete.confirmation.modal.submit.text", this, null),
							true) {

				@Override
				protected void onAfterClick(AjaxRequestTarget target) {
					// confirmed --> delete user from team list
					proposedUsers.remove(user);				            
					target.add(teamContainer);
					target.add(step4Form);


					// close modal
					closeDeleteConfirmationModal(notificationModal, target);
				}
			});
			notificationModal.addButton(new ModalActionButton(notificationModal,
					Buttons.Type.Default, new StringResourceModel(
							"delete.confirmation.modal.cancel.text", this, null),
							true) {
				@Override
				protected void onAfterClick(AjaxRequestTarget target) {
					// Cancel clicked --> do nothing, close modal
					closeDeleteConfirmationModal(notificationModal, target);
				}
			});
			return notificationModal;
		}


		private void closeDeleteConfirmationModal(final Modal modal,
				AjaxRequestTarget target) {
			modal.appendCloseDialogJavaScript(target);
		}



		private final Team team = new Team();

		final TeamMembership membership1 = new TeamMembership();



		public <T> IConverter<T> getAutocompleteConverter() {
			return new IConverter<T>() {

				@Override
				public T convertToObject(String value, Locale locale) throws ConversionException {
					return (T) userService.getByFullNameWithUsername(value);
				}

				@Override
				public String convertToString(T value, Locale locale) {
					return String.valueOf(((User)value).getFullNameWithUserName());
				}
			};
		}
		public IAutoCompleteRenderer<User> getAutocompleteRenderer() {
			return new AbstractAutoCompleteTextRenderer<User>() {

				@Override
				protected String getTextValue(User object) {
					return object.getFullNameWithUserName();
				}
			};
		}
		Iterator<User> getTeamableUsers(final String input) {
			List<User> potentialUsers = userService.getAllExceptAndFilter(team.getAllUsers(), input);
			potentialUsers.removeAll(proposedUsers);
			return potentialUsers.iterator();
		}
	}

	/**
	 * Step5
	 */
	private final class WizardStep5 extends WizardStep{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2019337959910060462L;
		private final Form<Void> form;
		private final DropDownChoice<Product> productDropDown;
		private final DropDownChoice<Process> processDropDown;

		private final WebMarkupContainer wmcProduct;
		private final WebMarkupContainer wmcProcess;

		@Inject 
		private QMTreeNodeService qmTreeNodeService;
		@Inject 
		private ProductService productService;
		@Inject 
		private ProcessService processService;

		public WizardStep5(){
			super("","");
			form = new Form<>("form");

			wmcProduct = new WebMarkupContainer("wmcProduct"){
				private static final long serialVersionUID = -5789117935259774051L;
				@Override
				protected void onConfigure(){
					super.onConfigure();
					setVisible(productService.countAll()>0);
				}
			};
			wmcProduct.setOutputMarkupId(true);

			wmcProcess = new WebMarkupContainer("wmcProcess"){
				private static final long serialVersionUID = -5789117935259774051L;
				@Override
				protected void onConfigure(){
					super.onConfigure();
					setVisible(processService.countAll()>0);
				}
			};
			wmcProcess.setOutputMarkupId(true);

			productDropDown = new DropDownChoice<Product>("product", new PropertyModel<Product>(project, "product"),  productService.getAllProducts()){
				private static final long serialVersionUID = -5789117935259774051L;
				@Override
				protected void onConfigure(){
					super.onConfigure();
					setVisible(productService.countAll()>0);
				}
			};
			productDropDown.setNullValid(true);
			processDropDown = new DropDownChoice<Process>("process", new PropertyModel<Process>(project, "process"),  processService.getAllProcesses()){
				private static final long serialVersionUID = -5789117935259774051L;
				@Override
				protected void onConfigure(){
					super.onConfigure();
					setVisible(processService.countAll()>0);
				}
			};
			processDropDown.setNullValid(true);

			wmcProduct.add(productDropDown);
			wmcProcess.add(processDropDown);

			productDropDown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if(wmcProcess.isVisible()){
						if(productDropDown.getModelObject() != null){
							wmcProcess.add(new AttributeModifier("style", "display: none"));
							target.add(wmcProcess);	
						} else{
								wmcProcess.add(new AttributeModifier("style", "display: block"));
								target.add(wmcProcess);
							
						}
					}
				}
			});
			processDropDown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if(wmcProduct.isVisible()){
						if(processDropDown.getModelObject()!= null){						
							wmcProduct.add(new AttributeModifier("style", "display: none"));
							target.add(wmcProduct);
	
						} else{
							wmcProduct.add(new AttributeModifier("style", "display: block"));
							target.add(wmcProduct);
						}
					}
				}
			});


			form.add(wmcProduct);
			form.add(wmcProcess);
			form.add(new DropDownChoice<>("qmodel", 
					new PropertyModel<QModel>(project, "qmodel"), qmTreeNodeService.getAllQModels())
					.setRequired(true));



			form.add(new DropDownChoice<>("lcStage",
					new PropertyModel<LifeCycleStage>(project, "lifeCycleStage"), LifeCycleStage.getAllLifeCycleStages())
					.setRequired(true));
			add(form);
		}

		// NEXT BUTTON on STEP5
		@Override
		public void applyState(){   	

			// Removes the container before add the updated one
			subsetContainer.remove(subsetProposalPanel);

			// TODO: pass the project Model to use the selected model in the previous step.
			subsetContainer.add(subsetProposalPanel = new SubsetProposalPanel("subsetPanel",project));
		}
	}

	/*
	 * Step 6 Subset proposal
	 */
	private final class WizardStep6 extends WizardStep {
		private static final long serialVersionUID = -623329684264166738L;

		public WizardStep6() {
			super("", "");

			subsetContainer = new WebMarkupContainer("subsetContainer", new Model<QMTreeNode>());
			subsetContainer.setOutputMarkupId(true);
			add(subsetContainer);

			// Added the panel with the proposed elements from the QM Tree
			subsetProposalPanel = new SubsetProposalPanel("subsetPanel", project);
			subsetContainer.add(subsetProposalPanel);

		}
	}


	/**
	 * Step6 (Configuration of data adapters)
	 * // TODO: Enable addition of further adapters 
	 */
	private final class WizardStep7 extends WizardStep {

		private static final long serialVersionUID = 1L;

		private final Form<AdapterSettings> adapterSettingsForm;
		@SuppressWarnings("unused")
		private final InputBorder<String> nameValidationBorder;
		@SuppressWarnings("unused")
		private final DropDownChoice<MetricSource> metricSourceChoice;
		@SuppressWarnings("unused")
		private final InputBorder<String> urlValidationBorder;
		private final TextField<String> usernameTextField;
		@SuppressWarnings("unused")
		private final PasswordTextField passwordTextField;
		@SuppressWarnings("unused")
		private final InputBorder<String> adapterProjectTextField;
		@SuppressWarnings("unused")
		private final InputBorder<String> adapterTestPlanTextField;
        @SuppressWarnings("unused")
        private final AjaxSubmitLink addButton;
	    private TextField<String> urlField;
	    private TextField<String> nameField;
	    private TextField<String> adapterProjectField;
	    private TextField<String> adapterTestPlanField;

		public WizardStep7(){            
			super("", "");               

			// add form to create new adapter
			add(adapterSettingsForm = newAdapterSettingsForm());

			// add text field for name inside a border component that performs bean
			// validation
			adapterSettingsForm.add(nameValidationBorder = newNameTextField());

			// add drop down choice component for choosing the metric source
			adapterSettingsForm.add(metricSourceChoice = newMetricSourceDropDownChoice());

			// add text field for url
			adapterSettingsForm.add(urlValidationBorder = newUrlTextField());

			// add text field for user name
			adapterSettingsForm.add(usernameTextField = newUsernameTextField());

			// add text field for password
			adapterSettingsForm.add(passwordTextField = newPwdTextField());

			// add text field for adapter project (used for Sonar and TestLink)
			adapterSettingsForm.add(adapterProjectTextField = newAdapterProjectTextField());

			// add text field for adapter test plan project (used for TestLink)
			adapterSettingsForm.add(adapterTestPlanTextField = newAdapterTestPlanTextField());

	        adapterSettingsForm.add(addButton = newAddButton());
	        nameValidationBorder.setOutputMarkupId(true);

			add(adapterSettingsForm);
		}


		/**
		 * 
		 * @return
		 */
		private Form<AdapterSettings> newAdapterSettingsForm() {
			Form<AdapterSettings> form = new InputValidationForm<>("form");
			form.setOutputMarkupId(true);
			return form;
		}

		/**
		 * 
		 * @return
		 */
		private InputBorder<String> newNameTextField() {
			nameField = new TextField<>("name", new PropertyModel<String>(adapterSettings, "name"));
			nameField.add(new PopoverBehavior(Model.of(""), new StringResourceModel("step7.name.popover.text", this, null), getPopoverConfig(false)));
			nameField.setRequired(true);
			return new OnEventInputBeanValidationBorder<>(
					"nameValidationBorder", nameField,
					new StringResourceModel("step7.name.input.label", this, null),
					HtmlEvent.ONCHANGE);
		}


		/**
		 * 
		 * @return
		 */
		private DropDownChoice<MetricSource> newMetricSourceDropDownChoice() {	
			DropDownChoice<MetricSource> choice = new DropDownChoice<>("metricSource", new PropertyModel<MetricSource>(
					adapterSettings, "metricSource"), Arrays.asList(MetricSource.values()));
			choice.add(new PopoverBehavior(Model.of(""), new StringResourceModel("step7.metricSource.popover.text", this, null), getPopoverConfig(false)));
			return choice;
		}

		/**
		 * 
		 * @return
		 */
		private InputBorder<String> newUrlTextField() {
			urlField = new TextField<>("url", new PropertyModel<String>(adapterSettings, "url"));
			urlField.add(new PopoverBehavior(Model.of(""), new StringResourceModel("step7.url.popover.text", this, null), getPopoverConfig(false)));
			urlField.setRequired(true);
			return new OnEventInputBeanValidationBorder<>(
					"urlValidationBorder", urlField,
					new StringResourceModel("step7.url.input.label", this, null),
					HtmlEvent.ONCHANGE);
		}


		/**
		 * 
		 * @return
		 */
		private TextField<String> newUsernameTextField() {
			TextField<String> usernameField = new TextField<>("adapterUsername", new PropertyModel<String>(adapterSettings, "adapterUsername"));
			usernameField.add(new PopoverBehavior(Model.of(""), new StringResourceModel("step7.username.popover.text", this, null), getPopoverConfig(false)));
			usernameField.setRequired(false);
			return usernameField;
		}


		/**
		 * 
		 * @return
		 */
		private PasswordTextField newPwdTextField() {
			PasswordTextField pwdField = new PasswordTextField("adapterPassword",
					new PropertyModel<String>(adapterSettings, "adapterPassword"));
			pwdField.add(new PopoverBehavior(Model.of(""), new StringResourceModel("step7.password.popover.text", this, null), getPopoverConfig(false)));
			pwdField.setRequired(false);
			return pwdField;
		}


		/**
		 * 
		 * @return 
		 */
		private InputBorder<String> newAdapterProjectTextField() {
			adapterProjectField = new TextField<>("adapterProject",
					new PropertyModel<String>(adapterSettings, "adapterProject"));
			adapterProjectField.add(new PopoverBehavior(Model.of(""), new StringResourceModel("step7.adapterProject.popover.text", this, null), getPopoverConfig(false)));
			adapterProjectField.setRequired(false);

			return new OnEventInputBeanValidationBorder<>(
					"adapterProjectValidationBorder", adapterProjectField,
					new StringResourceModel("step7.adapterProject.input.label", this, null),
					HtmlEvent.ONCHANGE);
		}


		
		 private AjaxSubmitLink newAddButton() {
				AjaxSubmitLink submitLink = new AjaxSubmitLink("add", adapterSettingsForm) {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = 1L;

					@Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> adapterSettingsForm) {
						
						String message = new StringResourceModel("step7.add.confirmed", this, null).getString();
				        // If the adapter has at least the service URL defined, attempt to save 
				        // the adapter settings
                        if (adapterSettings.getName() != null) {
                            AdapterSettings as = adapterSettingsService.create(adapterSettings);
                            adapterSettingsList.add(as);
				        }
				     
				        
				        getPage().success(message);
				        adapterSettingsForm.clearInput();
				        usernameTextField.setModelObject(null);
				        metricSourceChoice.setModelObject(null);
				        urlField.setModelObject(null);
				        nameField.setModelObject(null);
				        adapterProjectField.setModelObject(null);
				        adapterTestPlanField.setModelObject(null);
				        target.add(adapterSettingsForm);
				        setResponsePage(getPage());
				       
					}
				};
				
				submitLink.setOutputMarkupId(true);
				return submitLink;
			} 

		/**
		 * 
		 * @return 
		 */
		private InputBorder<String> newAdapterTestPlanTextField() {

			adapterTestPlanField = new TextField<>("adapterTestPlan", 
					new PropertyModel<String>(adapterSettings, "adapterTestPlan"));
			adapterTestPlanField.add(new PopoverBehavior(Model.of(""), new StringResourceModel("step7.adapterTestPlan.popover.text", this, null), getPopoverConfig(false)));
			adapterTestPlanField.setRequired(false);

			return new OnEventInputBeanValidationBorder<>(
					"adapterTestPlanValidationBorder", adapterTestPlanField,
					new StringResourceModel("step7.adapterTestPlan.input.label", this, null),
					HtmlEvent.ONCHANGE);
		}


		/**
		 * @param onHover The parameter indicates whether the popover item be activated on hover (true) or on focus (false). 
		 * @return
		 */
		private PopoverConfig getPopoverConfig(boolean onHover) {
			PopoverConfig config = new PopoverConfig();
			config.withAnimation(true);
			config.withPlacement(TooltipConfig.Placement.right);
			if (onHover) {
				config.withHoverTrigger();
			} else {
				config.withTrigger(TooltipConfig.OpenTrigger.focus);				
			}
			return config;
		}
	}



	/**
	 *
	 * @param id
	 */
	public ProjectWizardPanel(String id){
		super(id, false);   // disable default wizard stylesheets 

		long randomId = UUID.randomUUID().toString().hashCode();
		project = new Project("Project", String.format("prj-%s", randomId));

		user = UQasar.getSession().getLoggedInUser();   
		if (user.getCompany() != null) {
			company = companyService.getById(user.getCompany().getId());
			project.setCompany(company);
		}

		wizard = new WizardModel();
		step1 = new WizardStep1();
		step2 = new WizardStep2();
		step3 = new WizardStep3();
		step4 = new WizardStep4();
		step5 = new WizardStep5();
		step6 = new WizardStep6();
		step7 = new WizardStep7();

		wizard.add(step1);
		wizard.add(step2);
		wizard.add(step3);
		wizard.add(step4);
		wizard.add(step5);
		wizard.add(step6);
		wizard.add(step7);
		init(wizard);
	}


	@Override
	public void onCancel(){
		setResponsePage(AboutPage.class);
	}


	@Override
	public void onFinish(){

		// step4: build teams
		teams = buildTeams(proposedUsers);

		// step5: add team to project
		project.setTeams(teams);

		// step6: create checked elements from the subset
		subsetProposalPanel.saveSubset(project);

		// create project
		project = (Project) treeNodeService.create(project); 

		// step7: add adapter settings
		// set the QA Project for the Adapter(s)
        if(adapterSettingsList.size() != 0){
            for (AdapterSettings adapterSettingsInside : adapterSettingsList) {
                
                if (adapterSettingsInside.getName() != null) {    
                    adapterSettingsInside.setProject(project);
                    adapterSettingsService.update(adapterSettingsInside);
                }
            }
        } 
        
        
        // set the latest adapterSetting
        if(adapterSettings.getName() != null){
			adapterSettings.setProject(project);
			adapterSettingsService.update(adapterSettings);
		}
		
		// call new projectspage
		setResponsePage(ProjectViewPage.class, 
				ProjectViewPage.forProject(project)); 
	}


	/**
	 * 
	 */
	private List<User> teamBuilding() {

		// step1: matchUsersWithNewProject
		matchUsersWithNewProject(project);

		// step2: matchUsersFromOldProjectsWithNewProject
		matchUsersFromOldProjectsWithNewProject(project);

		// step3: get proposed users by skillcount --> TOP5
		List<User> proposedUsers = userService.getAllUsersByDescendingSkillCount(0, 5);

		// step4: check if proposedUsers has logged-in user or not, if not then add logged-in user.
        User userLocal = UQasar.getSession().getLoggedInUser();

        if (!proposedUsers.contains(userLocal)) {
            proposedUsers.add(userLocal);

        }
        
		System.out.println("U S E R S: " + proposedUsers);  	
		for (int u = 0; u < proposedUsers.size(); u++) {
			System.out.println("TOP" + (u + 1) + ": "
					+ proposedUsers.get(u).getFullName() + " @ "
					+ proposedUsers.get(u).getSkillCount()
					+ " points SkillCount");
		}

		return proposedUsers;
	}


	/**
	 * 
	 * @param proposedUsers
	 * @return
	 */
	private List<Team> buildTeams(List<User> proposedUsers) {

		List<Team> teams = new ArrayList<>();

		Team proposedTeam = new Team();

		proposedTeam.setName("Team Proposed");
		proposedTeam.setDescription("Proposed Team based on skills and roles of U-QASAR Users");
		//first save team to db
		proposedTeam = teamService.create(proposedTeam);  
		// then set TM
		proposedTeam.setMembers(
				setMemberships(proposedTeam, proposedUsers));  	
		// then update db
		proposedTeam = teamService.update(proposedTeam);
		teams.add(proposedTeam);    

		return teams;
	}

	/**
	 * 
	 * @param proposedTeam
	 * @param proposedUsers
	 * @return
	 */
	private Set<TeamMembership> setMemberships(Team proposedTeam, List<User> proposedUsers) {
		Set<TeamMembership> memberships = new HashSet<>();
		TeamMembership membership;

		for(User user : proposedUsers){	
			membership = new TeamMembership();
			membership.setUser(user);
			membership.setRole(user.getRole());
			membership.setTeam(proposedTeam);	        
			membership = membershipService.create(membership);	        
			memberships.add(membership);
		}
		return memberships;
	}

	/**
	 * 
	 * @param newProject
	 */
	private void matchUsersFromOldProjectsWithNewProject(Project newProject) {

		List<Project> oldProjects = treeNodeService.getAllProjects();

		for(Project project : oldProjects){

			List<Team> projectTeams = project.getTeams();

			if(projectTeams!= null){
				for(Team team : projectTeams){
					for(User user : team.getAllUsers()){
						// step1: match skills
						matchToolSkills(user, user.getKnownContinuousIntegrationTools(), 	newProject.getContinuousIntegrationTools());
						matchToolSkills(user, user.getKnownIssueTrackingTools(), 			newProject.getIssueTrackingTools());
						matchToolSkills(user, user.getKnownProgrammingLanguages(), 			newProject.getProgrammingLanguages());
						matchToolSkills(user, user.getKnownSourceCodeManagementTools(), 	newProject.getSourceCodeManagementTools());
						matchToolSkills(user, user.getKnownStaticAnalysisTools(), 			newProject.getStaticAnalysisTools());
						matchToolSkills(user, user.getKnownTestManagementTools(), 			newProject.getTestManagementTools());

						//matchRole
						if(rolesNeeded.contains(user.getRole())){
							System.out.println("match in old Project by user: " + user.getFullName());
							user.incrementSkillCount();
						}

						// match customertype, projecttype, etc.
						matchSoftwareEngineeringData(user, user.getCustomerTypes(), newProject.getCustomerTypes());
						matchSoftwareEngineeringData(user, user.getProjectTypes(), newProject.getProjectTypes());
						matchSoftwareEngineeringData(user, user.getSoftwareTypes(), newProject.getSoftwareTypes());
						matchSoftwareEngineeringData(user, user.getSoftwareLicenses(), newProject.getSoftwareLicenses());
						matchSoftwareEngineeringData(user, user.getTopics(), newProject.getTopics());
						matchSoftwareEngineeringData(user, user.getSoftwareDevelopmentMethodologies(), newProject.getSoftwareDevelopmentMethodologies());
					}
				}
			}
			else {
				System.out.println("NO TEAMS of Old projects");
			}

		}

	}



	/**
	 * 
	 * @param newProject
	 */
    private void matchUsersWithNewProject(Project newProject){

		// get ALL platform Users
		List<User> users = userService.getAllByAscendingName(0, (int) userService.countAll());    	
		for(User user : users){

			//matchToolSkills
			matchToolSkills(user, user.getKnownContinuousIntegrationTools(), 	newProject.getContinuousIntegrationTools());
			matchToolSkills(user, user.getKnownIssueTrackingTools(), 			newProject.getIssueTrackingTools());
			matchToolSkills(user, user.getKnownProgrammingLanguages(), 			newProject.getProgrammingLanguages());
			matchToolSkills(user, user.getKnownSourceCodeManagementTools(), 	newProject.getSourceCodeManagementTools());
			matchToolSkills(user, user.getKnownStaticAnalysisTools(), 			newProject.getStaticAnalysisTools());
			matchToolSkills(user, user.getKnownTestManagementTools(), 			newProject.getTestManagementTools());

			//matchRole
			for(Role role : rolesNeeded){
				matchRole(user, role);
			}

			// match customertype, projecttype, etc.
			matchSoftwareEngineeringData(user, user.getCustomerTypes(), newProject.getCustomerTypes());
			matchSoftwareEngineeringData(user, user.getProjectTypes(), newProject.getProjectTypes());
			matchSoftwareEngineeringData(user, user.getSoftwareTypes(), newProject.getSoftwareTypes());
			matchSoftwareEngineeringData(user, user.getSoftwareLicenses(), newProject.getSoftwareLicenses());
			matchSoftwareEngineeringData(user, user.getTopics(), newProject.getTopics());
			matchSoftwareEngineeringData(user, user.getSoftwareDevelopmentMethodologies(), newProject.getSoftwareDevelopmentMethodologies());
		}
	}


	/**
	 * 
	 * @param user
     */
	private <T> void matchSoftwareEngineeringData(User user, Set<T> userTypes, Set<T> projectTypes) {	
		for(T userType : userTypes){
			for(T projectType : projectTypes){
				if(userType.equals(projectType)){
					matchedSkills.put(user, projectType.toString());
					System.out.println("match SWE type: " + projectType + " by user: " + user.getFullName());
					user.incrementSkillCount();
				}
			}
		}
	}



	private void matchRole(User user, Role newRole) {
		Role userRole = user.getRole();
		if(userRole.equals(newRole)){
			matchedSkills.put(user, newRole.getLabelModel().toString());
			System.out.println("match in old Project: " + newRole + " by user: " + user.getFullName());
			user.incrementSkillCount();
		}
	}


	/**
	 * 
	 * @param user
	 * @param skillsGiven
	 * @param skillsNeeded
	 */
    private <T> void matchToolSkills(User user, Set<T> skillsGiven, Set<T> skillsNeeded){

		for(T skillGiven : skillsGiven){
			for(T skillNeeded : skillsNeeded){
				if(skillGiven.equals(skillNeeded)){
					System.out.println("match in old Project: " + skillNeeded + " by user: " + user.getFullName());
					matchedSkills.put(user, skillNeeded.toString());
					user.incrementSkillCount();
				}
			}
		}

	}
}
