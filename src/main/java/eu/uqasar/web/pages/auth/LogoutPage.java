package eu.uqasar.web.pages.auth;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.UQasar;

public class LogoutPage extends BasePage {

	private static final long serialVersionUID = 5843562261447267086L;
	
	public LogoutPage(PageParameters parameters) {
		super(parameters);
		UQasar.getSession().invalidateNow();
		setResponsePage(UQasar.get().getHomePage());
	}


}
