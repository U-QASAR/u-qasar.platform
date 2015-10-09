/*
 */
package eu.uqasar.web.provider;

import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.LoggedInUser;
import eu.uqasar.web.UQSession;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import org.apache.wicket.Session;

@SessionScoped
public class LoggedInUserProvider implements Serializable {

	@Inject
	UserService userService;

	@Inject
	HttpServletRequest httpRequest;

	private User loggedInUser;

	@Produces
	@LoggedInUser
	public User getLoggedInUser() {
		if (loggedInUser == null) {
			retrieveLoggedInUser();
		}
		return loggedInUser;
	}

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
