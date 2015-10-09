package eu.uqasar.service.notification.message.auth.register;

import eu.uqasar.exception.notification.NotificationException;
import eu.uqasar.message.auth.register.RegistrationCancelledMessage;
import eu.uqasar.model.user.User;
import eu.uqasar.service.notification.message.MessageNotificationService;
import eu.uqasar.message.auth.register.RegistrationConfirmationMessage;
import eu.uqasar.message.auth.register.RegistrationConfirmedMessage;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.mail.MessagingException;

/**
 *
 *
 */
@ApplicationScoped
public class RegistrationMessageService extends MessageNotificationService {

	public void sendRegistrationCancelledMessage(User user) 
			throws NotificationException {
		try {
			RegistrationCancelledMessage message = new RegistrationCancelledMessage(getSession(), user, getDefaultMailSender());
			message.send();
		} catch (MessagingException | IOException ex) {
			throw new NotificationException(ex.getMessage(), ex);
		}
	}
	
	public void sendRegistrationConfirmedMessage(User user) 
			throws NotificationException {
		try {
			RegistrationConfirmedMessage message = new RegistrationConfirmedMessage(getSession(), user, getDefaultMailSender());
			message.send();
		} catch (MessagingException | IOException ex) {
			throw new NotificationException(ex.getMessage(), ex);
		}
	}
	
	public void sendRegistrationConfirmationMessage(User user)
			throws NotificationException {
		try {
			RegistrationConfirmationMessage message = new RegistrationConfirmationMessage(getSession(), user, getDefaultMailSender());
			message.send();
		} catch (MessagingException | IOException ex) {
			throw new NotificationException(ex.getMessage(), ex);
		}
	}
}
