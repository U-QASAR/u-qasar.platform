/*
 */

package eu.uqasar.message.auth.reset;

import eu.uqasar.message.UQasarMessage;
import eu.uqasar.model.user.User;
import eu.uqasar.web.pages.auth.reset.ResetPasswordPage;
import java.util.Map;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 */
public class PWResetRequestedMessage extends UQasarMessage {

	public PWResetRequestedMessage(Session session, User recipient, User from) throws MessagingException {
		super(session, recipient, from);
	}

	public PWResetRequestedMessage(Session session, User recipient, final String from) throws MessagingException {
		super(session, recipient, from);
	}

	public PWResetRequestedMessage(Session session, User recipient, Address from) throws MessagingException {
		super(session, recipient, from);
	}
	
	@Override
	public Object getDataModel() {
		Map<String, Object> defaultModel = super.getDefaultModel();
		defaultModel.put("user", getRecipient());
		defaultModel.put("link", getUrlProvider().urlFor(ResetPasswordPage.class, new PageParameters().add("token", getRecipient().getResetPWRequestToken())));
		return defaultModel;
	}
}
