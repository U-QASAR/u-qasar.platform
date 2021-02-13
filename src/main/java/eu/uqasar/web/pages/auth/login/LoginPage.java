package eu.uqasar.web.pages.auth.login;

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

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.DbDashboardPersister;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import eu.uqasar.exception.auth.RegistrationCancelledException;
import eu.uqasar.exception.auth.RegistrationNotYetConfirmedException;
import eu.uqasar.exception.auth.UnknownUserException;
import eu.uqasar.exception.auth.WrongUserCredentialsException;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.DashboardService;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.behaviour.DefaultFocusBehavior;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.auth.register.RegisterPage;
import eu.uqasar.web.pages.auth.reset.RecoverPasswordPage;

/**
 *
 *
 */
public class LoginPage extends BasePage {

	@Inject
	private AuthenticationService authService;
	@Inject
	private DashboardService dashboardService;
	
	private String login;
	private String password;

	public LoginPage(PageParameters parameters) {
		super(parameters);

		final Form<User> form = new InputValidationForm<User>("form") {

			private static final long serialVersionUID = 962359934825069181L;

			@Override
			protected void onSubmit() {
				doSubmit(login, password);
			}
		};
		add(form);

		// add text input field for user's email
		final TextField<String> emailTextField = new TextField<>("mail",
				new PropertyModel<String>(this, "login"));
		// set focus to email text field as soon as page gets rendered
		emailTextField.add(new DefaultFocusBehavior());
		form.add(new InputBorder<>("mailValidationBorder",
				emailTextField.setRequired(true), new StringResourceModel(
						"label.mail", this, null)));

		// add password input field for user's password
		final PasswordTextField passwordTextField = new PasswordTextField(
				"password", new PropertyModel<String>(this, "password"));
		form.add(new InputBorder<>("passwordValidationBorder",
				passwordTextField, new StringResourceModel(
						"label.password", this, null)));

		// add forgot password link
		form.add(new BookmarkablePageLink<RecoverPasswordPage>("forgotPasswordLink", RecoverPasswordPage.class));
		
		add(new BookmarkablePageLink<RegisterPage>("registerLink", RegisterPage.class));
	}

	private void doSubmit(final String login, final String password) {
		final String cred = StringUtils.trimToEmpty(login);
		try {
			User user = authService.authenticate(cred, password);
			if (user != null) {
				UQSession.get().setLoggedInUser(user, UQSession.get().getLocale());
				// Initialize the dashboard for the user
				initDashboard();
				continueToOriginalDestination();
				// if we get here there was no previous request and we can continue
				// to home page
				setResponsePage(getApplication().getHomePage());
			}
		} catch (UnknownUserException ex) {
			error(new StringResourceModel("error.user.unknown", this, null, new Object[]{cred}).getString());
		} catch (WrongUserCredentialsException ex) {
			error(new StringResourceModel("error.user.wrong.credentials", this, null, new Object[]{cred}).getString());
		} catch (RegistrationCancelledException ex) {
			error(new StringResourceModel("error.user.register.cancelled", this, null, new Object[]{cred}).getString());
		} catch (RegistrationNotYetConfirmedException ex) {
			error(new StringResourceModel("error.user.register.notconfirmed", this, null, new Object[]{cred}).getString());
		}
	}


	/**
	 * Takes care of initializing a dashboard for the user.
	 */
	private void initDashboard() {

		// Get the dashboard context
		DashboardContext dashboardContext = UQasar.get().getDashboardContext();
		Long userId = UQasar.getSession().getLoggedInUser().getId();
		dashboardContext.setDashboardPersiter(new DbDashboardPersister(userId));
		// Load all the dashboards belonging to the user
		List<Dashboard> dashboards = dashboardContext.getDashboardPersiter().loadAll();
		if (dashboards == null || dashboards.size() == 0) {
			dashboards = new ArrayList<>();
		}
		UQasar.getSession().getLoggedInUser().setDashboards(dashboards);
	}	
}
