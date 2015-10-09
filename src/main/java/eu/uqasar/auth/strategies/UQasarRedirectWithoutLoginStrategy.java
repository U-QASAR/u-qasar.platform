/*
 */
package eu.uqasar.auth.strategies;

import eu.uqasar.web.UQSession;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.ContactPage;
import eu.uqasar.web.pages.auth.InfoPage;
import eu.uqasar.web.pages.auth.login.LoginPage;
import eu.uqasar.web.pages.auth.register.CancelRegistrationPage;
import eu.uqasar.web.pages.auth.register.ConfirmRegistrationPage;
import eu.uqasar.web.pages.auth.register.RegisterPage;
import eu.uqasar.web.pages.auth.reset.RecoverPasswordPage;
import eu.uqasar.web.pages.auth.reset.ResetPasswordPage;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.pages.BrowserInfoPage;
import org.apache.wicket.request.component.IRequestableComponent;

/**
 *
 *
 */
public class UQasarRedirectWithoutLoginStrategy implements IAuthorizationStrategy {

	// List of pages that don't require authentication, all other pages will redirect to login if user is not authenticated!
	private static final List<Class<? extends WebPage>> PAGES_WO_AUTH_REQ = Arrays.asList(
			LoginPage.class, RecoverPasswordPage.class, ResetPasswordPage.class,
			RegisterPage.class, InfoPage.class, AboutPage.class, ContactPage.class, ConfirmRegistrationPage.class,
			CancelRegistrationPage.class, BrowserInfoPage.class
	);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.authorization.IAuthorizationStrategy#
	 * isInstantiationAuthorized(java.lang.Class)
	 */
	@Override
	public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass) {
		// if it's not a wicket page --> allow
		if (!Page.class.isAssignableFrom(componentClass)) {
			return true;
		}

		// if it's a page that does not require authentication --> allow
		for (Class<? extends WebPage> clazz : PAGES_WO_AUTH_REQ) {
			if (clazz.isAssignableFrom(componentClass)) {
				return true;
			}
		}

		// if it's any other wicket page and user is not logged in -->
		// redirect to login page
		if (!((UQSession) Session.get()).isAuthenticated()) {
			throw new RestartResponseAtInterceptPageException(LoginPage.class);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.authorization.IAuthorizationStrategy#isActionAuthorized
	 * (org.apache.wicket.Component, org.apache.wicket.authorization.Action)
	 */
	@Override
	public boolean isActionAuthorized(Component component, Action action) {
		return true;
	}
}
