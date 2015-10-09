package eu.uqasar.service.notification.message.auth.reset;

import eu.uqasar.exception.notification.NotificationException;
import eu.uqasar.message.auth.reset.PWResetConfirmedMessage;
import eu.uqasar.message.auth.reset.PWResetRequestedMessage;
import eu.uqasar.model.user.User;
import eu.uqasar.service.notification.message.MessageNotificationService;
import static eu.uqasar.service.notification.message.MessageNotificationService.getDefaultMailSender;
import static eu.uqasar.service.notification.message.MessageNotificationService.getSession;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.mail.MessagingException;

/**
 *
 *
 */
@ApplicationScoped
public class ResetPasswordMessageService  extends MessageNotificationService {
	
	public void sendPWResetRequestedMessage(User user) 
			throws NotificationException {
		try {
			PWResetRequestedMessage message = new PWResetRequestedMessage(getSession(), user, getDefaultMailSender());
			message.send();
		} catch (MessagingException | IOException ex) {
			throw new NotificationException(ex.getMessage(), ex);
		}
	}
	
	public void sendPWResetConfirmedMessage(User user) throws NotificationException { 
		try {
			PWResetConfirmedMessage message = new PWResetConfirmedMessage(getSession(), user, getDefaultMailSender());
			message.send();
		} catch (MessagingException | IOException ex) {
			throw new NotificationException(ex.getMessage(), ex);
		}
	}
}
