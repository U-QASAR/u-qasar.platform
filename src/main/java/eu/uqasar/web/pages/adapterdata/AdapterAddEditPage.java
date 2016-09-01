package eu.uqasar.web.pages.adapterdata;

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

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.dataadapter.AdapterSettingsService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.pages.BasePage;

public class AdapterAddEditPage extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6712189902433775235L;

	@Inject
	private AdapterSettingsService adapterSettingsService;
	@Inject
	private TreeNodeService treeNodeService;
	private final Form<AdapterSettings> adapterSettingsForm;
	@SuppressWarnings("unused")
	private final InputBorder<String> nameValidationBorder;
	@SuppressWarnings("unused")
	private final DropDownChoice<MetricSource> metricSourceChoice;
	@SuppressWarnings("unused")
	private final InputBorder<String> urlValidationBorder;
	@SuppressWarnings("unused")
	private final TextField<String> usernameTextField;
	private final PasswordTextField passwordTextField;
	@SuppressWarnings("unused")
	private final InputBorder<String> adapterProjectTextField;
	@SuppressWarnings("unused")
	private final InputBorder<String> adapterTestPlanTextField;
	private final DropDownChoice<Project> projectChoice;

	// The AdapterSettings to edit/save
	private AdapterSettings adapterSettings;

	/**
	 * @param parameters
	 */
	public AdapterAddEditPage(final PageParameters parameters) {
		super(parameters);

		// extract id parameter and set page title, header and adapter 
		// depending on whether we are editing an existing adapter or
		// creating	a new one
		loadAdapter(parameters.get("id"));

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

		// add drop down choice component for choosing the project
		adapterSettingsForm.add(projectChoice = newProjectDropDownChoice());

		// add a button to create new adapter
		adapterSettingsForm.add(newSubmitLink());

		// add cancel button to return to adapter list page
		adapterSettingsForm.add(newCancelLink());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.markup.html.WebPage#onAfterRender()
	 */
	@Override
	protected void onAfterRender() {
		super.onAfterRender();
		// detach entity to avoid automatic update of changes in form.
		adapterSettingsService.detach(adapterSettings);
	}

	/**
	 * 
	 * @param idParam
	 */
	private void loadAdapter(final StringValue idParam) {
		if (idParam.isEmpty()) {
			setPageTitle(new StringResourceModel("page.create.title", this,
					null));
			add(new Label("header", new StringResourceModel(
					"form.create.header", this, null)));
			adapterSettings = new AdapterSettings();
		} else {

			adapterSettings = adapterSettingsService.getById(idParam.toOptionalLong());
			if (adapterSettings == null) {
				throw new EntityNotFoundException(AdapterSettings.class, idParam.toOptionalString());
			}

			setPageTitle(new StringResourceModel("page.edit.title", this, null));
			add(new Label("header", new StringResourceModel("form.edit.header",
					this, null)));
		}
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
		return new OnEventInputBeanValidationBorder<>(
				"nameValidationBorder", new TextField<>("name",
						new PropertyModel<String>(adapterSettings, "name"))
				.setRequired(true),
				new StringResourceModel("name.input.label", this, null),
				HtmlEvent.ONCHANGE);
	}


	/**
	 * 
	 * @return
	 */
	private DropDownChoice<MetricSource> newMetricSourceDropDownChoice() {

		return new DropDownChoice<>("metricSource", 
				new PropertyModel<MetricSource>(
						adapterSettings, "metricSource"), Arrays.asList(MetricSource.values()));
	}

	/**
	 * 
	 * @return
	 */
	private InputBorder<String> newUrlTextField() {
		return new OnEventInputBeanValidationBorder<>(
				"urlValidationBorder", new TextField<>("url",
						new PropertyModel<String>(adapterSettings, "url"))
				.setRequired(true),
				new StringResourceModel("url.input.label", this, null),
				HtmlEvent.ONCHANGE);
	}


	/**
	 * 
	 * @return
	 */
	private TextField<String> newUsernameTextField() {
		TextField<String> usernameField = new TextField<>("adapterUsername", new PropertyModel<String>(adapterSettings, "adapterUsername"));
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
		pwdField.setRequired(false);
		return pwdField;
	}


	/**
	 * 
	 * @return 
	 */
	private InputBorder<String> newAdapterProjectTextField() {
		return new OnEventInputBeanValidationBorder<>(
				"adapterProjectValidationBorder", new TextField<>("adapterProject",
						new PropertyModel<String>(adapterSettings, "adapterProject"))
				.setRequired(false),
				new StringResourceModel("adapterProject.input.label", this, null),
				HtmlEvent.ONCHANGE);
	}

	/**
	 * 
	 * @return 
	 */
	private InputBorder<String> newAdapterTestPlanTextField() {
		return new OnEventInputBeanValidationBorder<>(
				"adapterTestPlanValidationBorder", new TextField<>("adapterTestPlan",
						new PropertyModel<String>(adapterSettings, "adapterTestPlan"))
				.setRequired(false),
				new StringResourceModel("adapterTestPlan.input.label", this, null),
				HtmlEvent.ONCHANGE);
	}


	/**
	 * 
	 * @return
	 */
	private DropDownChoice<Project> newProjectDropDownChoice() {

		DropDownChoice<Project> projectChoice = new DropDownChoice<>("project", 
				new PropertyModel<Project>(
						adapterSettings, "project"), treeNodeService.getAllProjects());
		projectChoice.setNullValid(true); // enable null
		return projectChoice;
	}


	/**
	 * 
	 * @return
	 */
	private AjaxSubmitLink newSubmitLink() {
		return new AjaxSubmitLink("submit", adapterSettingsForm) {

			private static final long serialVersionUID = 6099483467114314555L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				save(target);
				AdapterAddEditPage.this.onSubmit(adapterSettings, passwordTextField.getModelObject());

			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				showErrors(target);
			}
		};
	}

	/**
	 * 
	 * @return
	 */
	private Link<AdapterManagementPage> newCancelLink() {
		return new Link<AdapterManagementPage>("cancel") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 6433075921789830757L;

			@Override
			public void onClick() {
				// redirect to adapter list page
				setResponsePage(
						AdapterManagementPage.class,
						new PageParameters().set(
								MESSAGE_PARAM,
								new StringResourceModel("canceled.message", this, Model
										.of(adapterSettings)).getString()).set(LEVEL_PARAM,
												FeedbackMessage.WARNING));
			}
		};
	}

	/** 
	 * Save adapter settings, show message to the user and redirect to the 
	 * adapter list
	 * @param target
	 */
	private void save(AjaxRequestTarget target) {

		// Set the QA Project for the Adapter
		adapterSettings.setProject(projectChoice.getModelObject());

		// Persist the adapter settings
		saveAdapterSettings();

		// success message has to be associated to session so that it is shown
		// in the global feedback panel
		if (adapterSettings.getId() == null){
			setResponsePage(AdapterManagementPage.class,
					new PageParameters().set(MESSAGE_PARAM,
							new StringResourceModel("add.confirmed", this, Model
									.of(adapterSettings)).getString()).set(LEVEL_PARAM,
											FeedbackMessage.SUCCESS));	
		} else {
			setResponsePage(AdapterManagementPage.class,
					new PageParameters().set(MESSAGE_PARAM,
							new StringResourceModel("saved.message", this, Model
									.of(adapterSettings)).getString()).set(LEVEL_PARAM,
											FeedbackMessage.SUCCESS));	
		}
	}

	/**
	 * Save entity
	 */
	private boolean saveAdapterSettings() {
		adapterSettingsService.update(adapterSettings);
		return true;
	}

	/**
	 * 
	 * @param target
	 */
	private void showErrors(AjaxRequestTarget target) {
		// in case of errors (e.g. validation errors) show error
		// messages in form
		target.add(adapterSettingsForm);
	}

	private void onSubmit(AdapterSettings adapterSettings, final String password) {
	}	
}
