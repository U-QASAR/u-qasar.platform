package eu.uqasar.web.pages.processes;

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

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.process.Process;
import eu.uqasar.service.ProcessService;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.pages.BasePage;

public class ProcessAddEditPage extends BasePage {

	private static final long serialVersionUID = 6315866033652820606L;

	@Inject
	private ProcessService processService;

	private final Form<Process> processForm;

	private final InputBorder<String> nameValidationBorder;
	private final InputBorder<String> descriptionValidationBorder;
	private final DateTextField startDateTextField;
	private final DateTextField endDateTextField;
	// TODO: Stages

	// The process to edit/save
    private Process process;

	/**
	 * @param parameters
	 */
	public ProcessAddEditPage(final PageParameters parameters) {
		super(parameters);

		// extract id parameter and set page title, header and product
		// depending on whether we are editing an existing process or
		// creating a new one
		loadProcess(parameters.get("id"));

		// add form to create new product
		add(processForm = newProcessForm());

		// add text field for name inside a border component that performs bean
		// validation
		processForm.add(nameValidationBorder = newNameTextField());

		// add text field for description
		processForm.add(descriptionValidationBorder = newDescriptionTextField());

		// add field for start date
		processForm.add(newStartDateTextField(startDateTextField = newDateTextField()));

		// add field for end date
		processForm.add(newEndDateTextField(endDateTextField = newDateTextField2()));

		// add a button to create new process
		processForm.add(newSubmitLink());

		// add cancel button to return to process list page
		processForm.add(newCancelLink());
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
		processService.detach(process);
	}

	/**
	 * 
	 * @param idParam
	 */
    private void loadProcess(final StringValue idParam) {
		// If no id is provided
		if (idParam.isEmpty()) {
			setPageTitle(new StringResourceModel("page.create.title", this,
					null));
			add(new Label("header", new StringResourceModel(
					"form.create.header", this, null)));
			process = new Process();
		} 
		// Attempt to load the process by the id
		else {			
			process = processService.getById(idParam.toLong());
			if (process == null) {
				throw new EntityNotFoundException(Process.class, idParam.toOptionalString());
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
	private Form<Process> newProcessForm() {
		Form<Process> form = new InputValidationForm<>("form");
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
                new PropertyModel<String>(process, "name"))
                .setRequired(true),
                new StringResourceModel("name.input.label", this, null),
                HtmlEvent.ONCHANGE);
	}

	/**
	 * 
	 * @return
	 */
	private InputBorder<String> newDescriptionTextField() {
		return new OnEventInputBeanValidationBorder<>(
                "descriptionValidationBorder", new TextField<>("description",
                new PropertyModel<String>(process, "description")),
                new StringResourceModel("description.input.label", this, null),
                HtmlEvent.ONCHANGE);
	}

	/**
	 * 
	 * @param startDateTextField
	 * @return
	 */
	private InputBorder<Date> newStartDateTextField(
			final DateTextField startDateTextField) {
		return new OnEventInputBeanValidationBorder<>(
                "startDateValidationBorder", startDateTextField,
                new StringResourceModel("startdate.input.label", this, null),
                HtmlEvent.ONCHANGE);
	}

	/**
	 * 
	 * @param endDateTextField
	 * @return
	 */
	private InputBorder<Date> newEndDateTextField(
			final DateTextField endDateTextField) {
		return new OnEventInputBeanValidationBorder<>(
                "endDateValidationBorder", endDateTextField,
                new StringResourceModel("enddate.input.label", this, null),
                HtmlEvent.ONCHANGE);
	}

	/**
	 * 
	 * @return
	 */
	private DateTextField newDateTextField() {
		DateTextFieldConfig config = new DateTextFieldConfig()
		.withFormat("dd.MM.yyyy")
		.allowKeyboardNavigation(true).autoClose(true)
		.highlightToday(true).showTodayButton(false);
		DateTextField dateTextField = new DateTextField("startDate",
				new PropertyModel<Date>(process, "startDate"), config);
		dateTextField.setRequired(true);
		return dateTextField;
	}

	/**
	 * 
	 * @return
	 */
	private DateTextField newDateTextField2() {
		DateTextFieldConfig config = new DateTextFieldConfig()
		.withFormat("dd.MM.yyyy")
		.allowKeyboardNavigation(true).autoClose(true)
		.highlightToday(true).showTodayButton(false);
		DateTextField dateTextField = new DateTextField("endDate",
				new PropertyModel<Date>(process, "endDate"), config);
		dateTextField.setRequired(true);
		return dateTextField;
	}

	/**
	 * 
	 * @return
	 */
	private AjaxSubmitLink newSubmitLink() {
		return new AjaxSubmitLink("submit", processForm) {

			private static final long serialVersionUID = 4491426877738892819L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				save(target);
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
	private Link<ProcessManagementPage> newCancelLink() {
		return new Link<ProcessManagementPage>("cancel") {

			private static final long serialVersionUID = -3695465420798817598L;

			@Override
			public void onClick() {
				setResponsePage(ProcessManagementPage.class,
						new PageParameters().set(
								MESSAGE_PARAM,
								new StringResourceModel("canceled.message", this, Model
										.of(process)).getString()).set(LEVEL_PARAM,
												FeedbackMessage.WARNING));
			}
		};
	}

	/**
	 * 
	 */
    private void save(AjaxRequestTarget target) {
		// save process
		saveProcess();

		//date
		Date fromDate = startDateTextField.getConvertedInput();
		Date toDate = endDateTextField.getConvertedInput();
		int compDate = fromDate.compareTo(toDate);
		if ( (compDate == 0) || compDate > 0) {
			String message = new StringResourceModel("date.message", this, null).getString();
			getPage().warn(message);
			setResponsePage(getPage());
		}
		// success message has to be associated to session so that it is shown
		// in the global feedback panel
		else if (process.getId() == null){
			// redirect to process list page
			setResponsePage(
					ProcessManagementPage.class,
					new PageParameters().set(
							MESSAGE_PARAM,
							new StringResourceModel("add.confirmed", this, Model
									.of(process)).getString()).set(LEVEL_PARAM,
											FeedbackMessage.SUCCESS));
		} else {
			// redirect to process list page
			setResponsePage(
					ProcessManagementPage.class,
					new PageParameters().set(
							MESSAGE_PARAM,
							new StringResourceModel("saved.message", this, Model
									.of(process)).getString()).set(LEVEL_PARAM,
											FeedbackMessage.SUCCESS));
		}
	}


	/**
	 * Save the process
	 */
	private boolean saveProcess() {
		processService.create(process);
		return true;
	}

	/**
	 * 
	 * @param target
	 */
    private void showErrors(AjaxRequestTarget target) {
		// in case of errors (e.g. validation errors) show error
		// messages in form
		target.add(processForm);
	}

	public static PageParameters linkToEdit(Process entity) {
		return linkToEdit(entity.getId());
	}

	private static PageParameters linkToEdit(Long entityId) {
		return new PageParameters().set("id", entityId);
	}
}
