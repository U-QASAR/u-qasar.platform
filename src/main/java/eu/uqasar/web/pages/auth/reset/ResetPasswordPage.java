/*
 */
package eu.uqasar.web.pages.auth.reset;

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


import eu.uqasar.exception.auth.reset.NoPendingResetPWRequestException;
import eu.uqasar.exception.auth.reset.ResetPWRequestTimeoutException;
import eu.uqasar.exception.notification.NotificationException;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.notification.message.auth.reset.ResetPasswordMessageService;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.auth.InfoPage;
import static eu.uqasar.web.pages.auth.InfoPage.resetPWRequestNotPendingMessage;
import static eu.uqasar.web.pages.auth.InfoPage.resetPWRequestTimeoutMessage;
import javax.inject.Inject;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 *
 *
 */
public class ResetPasswordPage extends BasePage {

	@Inject
	AuthenticationService authenticationService;

	@Inject
	ResetPasswordMessageService resetPasswordMessageService;

	private final PasswordTextField password;
	private final PasswordTextField confirmPassword;

	private User user = new User();

	public ResetPasswordPage(PageParameters parameters) {
		super(parameters);

		String token = parameters.get("token").toString();
		try {
			user = authenticationService.getUserByPWResetToken(token);
		} catch (NoPendingResetPWRequestException e) {
			// there's no user with this token and a pending reset password
			// request
			throw new RestartResponseException(
					resetPWRequestNotPendingMessage());
		} catch (ResetPWRequestTimeoutException e) {
			// request for resetting password has been too long ago
			throw new RestartResponseException(resetPWRequestTimeoutMessage());
		}

		Form<User> form = new InputValidationForm<User>("form") {

			@Override
			protected void onSubmit() {
				resetPassword();
			}
		};

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

	private void resetPassword() {
		try {
			// encrypt password and store user entity in db
			authenticationService.resetPassword(user, password.getModelObject());
			// send the confirmation email
			resetPasswordMessageService.sendPWResetConfirmedMessage(user);
			// continue to info page telling the user that his password has been
			// changed
			setResponsePage(InfoPage.resetPWSuccessMessage());
		} catch (NotificationException e) {
			error(e.getMessage());
		}
	}
}
