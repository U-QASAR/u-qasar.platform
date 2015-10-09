package eu.uqasar.web;

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
	
	public boolean hasPersistedEnvironment() {
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
