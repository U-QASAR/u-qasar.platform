package eu.uqasar.web;

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


import eu.uqasar.model.monitoring.WebEnvironment;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.user.User;
import eu.uqasar.service.monitoring.WebEnvironmentService;
import eu.uqasar.service.user.UserService;
import java.util.Locale;
import javax.inject.Inject;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

public class UQSession extends WebSession {

	@Inject
	UserService userService;
	
	@Inject
	WebEnvironmentService envService;

	private static final long serialVersionUID = -1193535621584070624L;

	private User user = null;
	private WebEnvironment userEnv = null;

	public UQSession(Request request) {
		super(request);
	}

	public static UQSession get() {
		return UQasar.getSession();
	}

	public User getLoggedInUser() {
		return user;
	}

	public boolean setLoggedInUser(final long userId, Locale locale) {
		User dbUser = userService.getById(userId);
		if (dbUser != null) {
			setLoggedInUser(user, locale);
		}
		return dbUser != null;
	}

	public boolean setLoggedInUser(User user, Locale locale) {
		this.user = user;
		this.user = updateUserLocale(locale);
		return user != null;
	}

	public User updateUserLocale(Locale locale) {
		setLocale(locale);
		if (user != null) {
			user.setPreferredLocale(locale);
			this.user = userService.update(user);
		}
		return user;
	}
    
    public boolean isUserAdmin() {
        return isAuthenticated() && getLoggedInUser().hasAnyRoles(Role.Administrator);
    }

	public boolean isAuthenticated() {
		return user != null && user.getId() != null;
	}

	public void setEnvironment(WebEnvironment environment) {
		this.userEnv = environment;
	}

	public WebEnvironment getEnvironment() {
		return this.userEnv;
	}
	
	private boolean hasPersistedEnvironment() {
		return userEnv != null && userEnv.getId() != null;
	}
	
	public WebEnvironment initializeEnvironment() {
		if(!hasPersistedEnvironment()) {
			userEnv = WebEnvironment.fromClientProperties(getClientInfo().getProperties());
			userEnv = envService.create(userEnv);
		}
		return userEnv;
	}
}
