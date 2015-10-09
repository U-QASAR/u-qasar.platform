package eu.uqasar.message.auth.register;

import eu.uqasar.message.UQasarMessage;
import eu.uqasar.model.user.User;
import eu.uqasar.web.pages.auth.register.CancelRegistrationPage;
import eu.uqasar.web.pages.auth.register.ConfirmRegistrationPage;
import java.util.Map;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 */
public class RegistrationConfirmationMessage extends UQasarMessage {

	public RegistrationConfirmationMessage(Session session, User recipient, User from) throws MessagingException {
		super(session, recipient, from);
	}

	public RegistrationConfirmationMessage(Session session, User recipient, final String from) throws MessagingException {
		super(session, recipient, from);
	}

	public RegistrationConfirmationMessage(Session session, User recipient, Address from) throws MessagingException {
		super(session, recipient, from);
	}
	
	@Override
	public Object getDataModel() {
		Map<String, Object> defaultModel = super.getDefaultModel();
		defaultModel.put("user", getRecipient());
		defaultModel.put("confirmation_link", getUrlProvider().urlFor(ConfirmRegistrationPage.class, new PageParameters().add("token", getRecipient().getRegistrationToken())));
		defaultModel.put("cancellation_link", getUrlProvider().urlFor(CancelRegistrationPage.class, new PageParameters().add("token", getRecipient().getRegistrationToken())));
		return defaultModel;
	}

}
