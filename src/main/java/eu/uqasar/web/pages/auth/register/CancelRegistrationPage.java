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


import eu.uqasar.exception.notification.NotificationException;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.notification.message.auth.register.RegistrationMessageService;
import eu.uqasar.web.UQasar;
import javax.inject.Inject;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.PageHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

public class CancelRegistrationPage extends RegistrationBasePage {

	@Inject
	AuthenticationService authService;
	
	@Inject
	RegistrationMessageService registrationMessageService;

	@Inject
	Logger logger;
	
	private Boolean confirmed = null;
	private final static int secondsToRedirect = 15;
	
	public CancelRegistrationPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void tokenValidated(User user) {
		addOrReplace(new CancelRegistrationPage.SuccessFragment("fragment", "success", this, user));
		confirmed = true;
		try {
			registrationMessageService.sendRegistrationCancelledMessage(user);
			authService.cancelRegistration(user);
		} catch (NotificationException ex) {
			error(ex.getMessage());
			logger.error(ex.getMessage(), ex);
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		if(confirmed) {
			final String url = UQasar.get().getHomePageUrl();
			response.render(new PageHeaderItem("<meta http-equiv=\"refresh\" content=\"" + secondsToRedirect + ";" + url + "\" />"));
		}
	}

	@Override
	public void tokenInvalidated() {
		Fragment error = new Fragment("fragment", "error", this);
		error.add(
			new Label("message", new StringResourceModel(
					"label.error", this, null, new Object[] {getToken()})
				).setEscapeModelStrings(false)
			);
		addOrReplace(error);
		confirmed = false;
	}

	private class SuccessFragment extends Fragment {

		private final User user;
		private final MarkupContainer provider;
		public SuccessFragment(String id, String markupId, MarkupContainer markupProvider, User user) {
			super(id, markupId, markupProvider);
			this.user = user;
			this.provider = markupProvider;
			
			BookmarkablePageLink<Class<? extends Page>> link = new BookmarkablePageLink<>("link", UQasar.get().getHomePage());
			link.add(new Label("linklabel", new StringResourceModel("label.success.link", provider, null, new Object[] {secondsToRedirect})));
			addOrReplace(link);

			addOrReplace(new Label("message", new StringResourceModel("label.success", provider, null, new Object[] {this.user.getFullName(), this.user.getMail()})).setEscapeModelStrings(false));
		}
	}
	
}
