/*
 */
package eu.uqasar.web.provider;

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


import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.UQSession;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import org.apache.wicket.Session;

@Getter
@SessionScoped
public class LoggedInUserProvider implements Serializable {

	@Inject
	UserService userService;

	@Inject
	HttpServletRequest httpRequest;

	private User loggedInUser;

	/**
	 * Updates the logged in user entity when it receives the respective event.
	 * Whenever the logged in user changes his profile settings, the respective
	 * event should be fired.
	 *
	 * @param user
	 */
	public void onUserChanged(
			@Observes(notifyObserver = Reception.IF_EXISTS) final User user) {
		retrieveLoggedInUser();
	}

	/**
	 * Gets the logged in user's email from the session and loads user from DB.
	 */
	@PostConstruct
	public void retrieveLoggedInUser() {
		if (((UQSession) Session.get()).isAuthenticated()) {
			loggedInUser = userService.getById(((UQSession) Session.get()).getLoggedInUser().getId());
		}
	}
}
