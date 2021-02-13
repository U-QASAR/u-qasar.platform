package eu.uqasar.web.pages.auth.register;

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


import eu.uqasar.exception.auth.register.UserMailAlreadyExistsException;
import eu.uqasar.exception.auth.register.UserNameAlreadyExistsException;
import eu.uqasar.exception.notification.NotificationException;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.notification.message.auth.register.RegistrationMessageService;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.pages.auth.InfoPage;
import javax.inject.Inject;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 *
 *
 */
public class RegisterPage extends BasePage {
	
	@Inject
	private AuthenticationService authenticationService;

	@Inject
	private RegistrationMessageService registrationMessageService;
	
	private final TextField<String> mail;
	private final TextField<String> userName;
	private final TextField<String> firstName;
	private final TextField<String> lastName;
	private final PasswordTextField password;
	private final PasswordTextField confirmPassword;

	private final User newUser = new User();

	public RegisterPage(PageParameters parameters) {
		super(parameters);

		Form<User> form = new InputValidationForm<User>("form") {

			@Override
			protected void onSubmit() {
				tryToRegister();
			}
		};

		mail = new TextField<>("mail", new PropertyModel<String>(newUser, "mail"));
		InputBorder<String> mailValidationBorder = new OnEventInputBeanValidationBorder<>(
				"mailValidationBorder", mail,
				new StringResourceModel("label.mail", this, null),
				new StringResourceModel("help.mail", this, null),
				HtmlEvent.ONBLUR);
		form.add(mailValidationBorder);

		userName = new TextField<>("userName", new PropertyModel<String>(newUser, "userName"));
		InputBorder<String> userNameValidationBorder = new OnEventInputBeanValidationBorder<>(
				"userNameValidationBorder", userName,
				new StringResourceModel("label.userName", this, null),
				new StringResourceModel("help.userName", this, null),
				HtmlEvent.ONBLUR);
		form.add(userNameValidationBorder);

		firstName = new TextField<>("firstName", new PropertyModel<String>(newUser, "firstName"));
		InputBorder<String> firstNameValidationBorder = new OnEventInputBeanValidationBorder<>(
				"firstNameValidationBorder", firstName,
				new StringResourceModel("label.firstName", this, null), HtmlEvent.ONBLUR);
		form.add(firstNameValidationBorder);

		lastName = new TextField<>("lastName", new PropertyModel<String>(newUser, "lastName"));
		InputBorder<String> lastNameValidationBorder = new OnEventInputBeanValidationBorder<>(
				"lastNameValidationBorder", lastName,
				new StringResourceModel("label.lastName", this, null), HtmlEvent.ONBLUR);
		form.add(lastNameValidationBorder);

		password = new PasswordTextField("password", Model.of(""));
		password.add(new PatternValidator(AuthenticationService.PW_PATTERN));
		InputBorder<String> passwordValidationBorder = new InputBorder(
				"passwordValidationBorder", password,
				new StringResourceModel("label.password", this, null),
				new StringResourceModel("help.password", this, null));
		form.add(passwordValidationBorder);

		confirmPassword = new PasswordTextField("confirmPassword", Model.of(""));
		InputBorder<String> confirmPasswordValidationBorder = new InputBorder(
				"confirmPasswordValidationBorder", confirmPassword,
				new StringResourceModel("label.password.confirm", this, null));
		form.add(confirmPasswordValidationBorder);
		form.add(new EqualPasswordInputValidator(password, confirmPassword));
		
		add(form);
	}

    private void tryToRegister() {
		try {
			authenticationService.checkMailAlreadyRegistered(newUser.getMail());
			authenticationService.checkUserNameAlreadyRegistered(newUser.getUserName());
			newUser.setPreferredLocale(UQasar.getSession().getLocale());
			
			User registeredUser = authenticationService.register(newUser, password.getModelObject());
			
			registrationMessageService.sendRegistrationConfirmationMessage(registeredUser);
			setResponsePage(InfoPage.registeredMessage());
		} catch (UserMailAlreadyExistsException ex) {
			error(new StringResourceModel("error.user.exists.mail", this, Model.of(newUser)).getString());
		} catch (UserNameAlreadyExistsException ex) {
			error(new StringResourceModel("error.user.exists.userName", this, Model.of(newUser)).getString());
		} catch (NotificationException ex) {
			error(new StringResourceModel("error.user.register.notification", this, Model.of(newUser)).getString());
		}
	}
}
