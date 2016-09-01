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


import eu.uqasar.model.user.RegistrationStatus;
import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.pages.BasePage;
import javax.inject.Inject;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class RegistrationBasePage extends BasePage {

	private final String token;
	private User pendingUser;
	
	@Inject
	UserService userService;
	
	RegistrationBasePage(PageParameters parameters) {
		super(parameters);
		token = parameters.get("token").toOptionalString();
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		pendingUser = getPendingUserByToken();
		if (pendingUser == null) {
			tokenInvalidated();
		} else {
			tokenValidated(pendingUser);
		}
	}
	
	protected abstract void tokenValidated(User user);
	
	protected abstract void tokenInvalidated();
	
	final String getToken() {
		return token;
	}
	
	public final User getPendingUser() {
		return pendingUser;
	}
	
	private User getPendingUserByToken() {
        return userService.getByRegistrationTokenAndRegistrationStatus(token, RegistrationStatus.PENDING);
	}
	
	protected final User getFakeUser() {
		return userService.getByMail("testuser@example.net");
	}

}
