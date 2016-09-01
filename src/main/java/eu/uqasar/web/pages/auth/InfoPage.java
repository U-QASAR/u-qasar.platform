package eu.uqasar.web.pages.auth;

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


import eu.uqasar.exception.auth.NoPendingRegistrationException;
import eu.uqasar.exception.notification.NotificationException;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.notification.message.auth.register.RegistrationMessageService;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.auth.login.LoginPage;
import eu.uqasar.web.pages.auth.register.RegisterPage;
import eu.uqasar.web.provider.UrlProvider;
import javax.inject.Inject;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 */
public class InfoPage extends BasePage {

	public static InfoPage registeredMessage() {
		return new InfoPage("headerRegistered", "messageRegistered",
				InfoFragmentType.EMPTY, null, new PageParameters());
	}

	public static InfoPage registrationConfirmedMessage() {
		return new InfoPage("headerConfirmSuccess", "messageConfirmSuccess",
				InfoFragmentType.LOGIN, null, new PageParameters());
	}

	public static InfoPage registrationCancelledMessage() {
		return new InfoPage("headerCancelSuccess", "messageCancelSuccess",
				InfoFragmentType.EMPTY, null, new PageParameters());
	}

	public static InfoPage registrationNotPendingMessage() {
		return new InfoPage("headerRegistrationFailure",
				"messageRegistrationFailureNotPending",
				InfoFragmentType.LOGIN_REGISTER, null, new PageParameters());
	}

	/**
	 *
	 * @param model
	 * @return
	 */
	public static InfoPage registrationTimeoutMessage(final IModel<String> model) {
		return new InfoPage("headerRegistrationFailure",
				"messageRegistrationFailureTimeout",
				InfoFragmentType.RESEND_REGISTRATION, model,
				new PageParameters());
	}

	/**
	 *
	 * @return
	 */
	public static InfoPage recoverPWEmailSentMessage() {
		return new InfoPage("headerRecoverPassword", "messageRecoverPassword",
				InfoFragmentType.EMPTY, null, new PageParameters());
	}

	/**
	 *
	 * @return
	 */
	public static InfoPage noSuchUserMessage() {
		return new InfoPage("headerNoSuchUser", "messageNoSuchUser",
				InfoFragmentType.REGISTER, null, new PageParameters());
	}

	/**
	 *
	 * @return
	 */
	public static InfoPage resetPWRequestNotPendingMessage() {
		return new InfoPage("headerResetPWFailure", "messageResetPWNotPending",
				InfoFragmentType.EMPTY, null, new PageParameters());
	}

	/**
	 *
	 * @return
	 */
	public static InfoPage resetPWRequestTimeoutMessage() {
		return new InfoPage("headerResetPWFailure", "messageResetPWTimeout",
				InfoFragmentType.FORGOT_PASSWORD, null, new PageParameters());
	}

	/**
	 *
	 * @return
	 */
	public static InfoPage resetPWSuccessMessage() {
		return new InfoPage("headerResetPWSuccess", "messageResetPWSuccess",
				InfoFragmentType.LOGIN, null, new PageParameters());
	}

	/**
	 *
	 * @param model
	 * @return
	 */
	public static InfoPage resendRegistrationConfirmationMessage(
			final IModel<String> model) {
		return new InfoPage("headerResendRegistrationConfirmation",
				"messageResendRegistrationConfirmation",
				InfoFragmentType.RESEND_REGISTRATION, model,
				new PageParameters());
	}

	/**
	 *
	 * @return
	 */
	private static InfoPage registrationConfirmationSentAgainMessage() {
		return new InfoPage("headerRegistrationConfirmationSentAgain",
				"messageRegistrationConfirmationSentAgain",
				InfoFragmentType.EMPTY, null, new PageParameters());
	}

	/**
	 *
	 * @return
	 */
	private static InfoPage resendRegistrationFailureMessage() {
		return new InfoPage("headerResendRegistrationFailure",
				"messageResendRegistrationFailure",
				InfoFragmentType.LOGIN_REGISTER, null, new PageParameters());
	}

	/**
	 *
	 * @param headerMarkupId
	 * @param messageMarkupId
     */
	private InfoPage(final String headerMarkupId, final String messageMarkupId,
			final InfoFragmentType infoFragmentType,
			final IModel<String> model, final PageParameters parameters) {
		super(parameters);

		add(new Fragment("header", headerMarkupId, this));
		add(new Fragment("message", messageMarkupId, this, model));

		switch (infoFragmentType.ordinal()) {
			case 0:
				add(new EmptyInfoFragment(this));
				break;
			case 1:
				add(new LoginLinkFragment(this));
				break;
			case 2:
				add(new RegisterLinkFragment(this));
				break;
			case 3:
				add(new LoginRegisterLinkFragment(this));
				break;
			case 4:
				add(new ResendRegistrationLinkFragment(this, model.getObject()));
				break;
			case 5:
				add(new Fragment("info", "forgotPasswordInfo", this));
				break;
		}
	}

	/**
	 *
	 *
	 *
	 */
	private enum InfoFragmentType {

		EMPTY, LOGIN, REGISTER, LOGIN_REGISTER, RESEND_REGISTRATION, FORGOT_PASSWORD
	}

	private static class EmptyInfoFragment extends Fragment {

		/**
		 *
		 */
		private static final long serialVersionUID = 1160829122649467153L;

		public EmptyInfoFragment(final MarkupContainer markupProvider) {
			super("info", "empty", markupProvider);
		}

	}

	/**
	 *
	 *
	 *
	 */
	private static class LoginLinkFragment extends Fragment {

		/**
		 *
		 */
		private static final long serialVersionUID = -2158476919741722089L;

		public LoginLinkFragment(final MarkupContainer markupProvider) {
			super("info", "loginInfo", markupProvider);

			// add login link
			add(new BookmarkablePageLink<LoginPage>("loginLink",
					LoginPage.class));
		}

	}

	/**
	 *
	 *
	 *
	 */
	private static class RegisterLinkFragment extends Fragment {

		/**
		 *
		 */
		private static final long serialVersionUID = 5530137478910282540L;

		public RegisterLinkFragment(final MarkupContainer markupProvider) {
			super("info", "registerInfo", markupProvider);

			// add register link
			add(new BookmarkablePageLink<RegisterPage>("registerLink",
					RegisterPage.class));
		}

	}

	/**
	 *
	 *
	 *
	 */
	private static class LoginRegisterLinkFragment extends Fragment {

		/**
		 *
		 */
		private static final long serialVersionUID = 431378188094728560L;

		public LoginRegisterLinkFragment(final MarkupContainer markupProvider) {
			super("info", "loginRegisterInfo", markupProvider);

			// add login link
			add(new BookmarkablePageLink<LoginPage>("loginLink",
					LoginPage.class));

			// add register link
			add(new BookmarkablePageLink<RegisterPage>("registerLink",
					RegisterPage.class));
		}

	}

	/**
	 *
	 *
	 *
	 */
	private static class ResendRegistrationLinkFragment extends Fragment {

		/**
		 *
		 */
		private static final long serialVersionUID = 4210241478105940579L;

		@Inject
		AuthenticationService authenticationService;

		@Inject
		RegistrationMessageService registrationMailService;

		@Inject
		UrlProvider urlProvider;

		public ResendRegistrationLinkFragment(
				final MarkupContainer markupProvider, final String email) {
			super("info", "resendRegistrationInfo", markupProvider);

			// add register link
			PageParameters parameters = new PageParameters();
			parameters.add("email", email);
			add(new Link<Void>("resendRegistrationLink") {

				private static final long serialVersionUID = -5958845052833917370L;

				@Override
				public void onClick() {
					resendRegistrationConfirmationEmail(email);
				}

			});
		}

		/**
		 *
		 * @param email
		 */
		private void resendRegistrationConfirmationEmail(final String email) {
			try {
				// reset the registration information
				User user = authenticationService
						.resetRegistrationInformation(email);
				// send the confirm registration email again
				registrationMailService.sendRegistrationConfirmationMessage(user);
				// continue to info page telling the user to confirm
				// registration
				setResponsePage(registrationConfirmationSentAgainMessage());
			} catch (NotificationException e) {
				error(e.getMessage());
			} catch (NoPendingRegistrationException e) {
				// user doesn't exist or has already confirmed/cancelled
				// registration
				setResponsePage(resendRegistrationFailureMessage());
			}
		}

	}

}
