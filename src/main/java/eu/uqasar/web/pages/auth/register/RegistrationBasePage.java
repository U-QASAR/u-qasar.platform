package eu.uqasar.web.pages.auth.register;

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
	
	public RegistrationBasePage(PageParameters parameters) {
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
	
	public abstract void tokenValidated(User user);
	
	public abstract void tokenInvalidated();
	
	public final String getToken() {
		return token;
	}
	
	public final User getPendingUser() {
		return pendingUser;
	}
	
	public final User getPendingUserByToken() {
		User user = userService.getByRegistrationTokenAndRegistrationStatus(token, RegistrationStatus.PENDING);
		return user;
	}
	
	protected final User getFakeUser() {
		return userService.getByMail("testuser@example.net");
	}

}
