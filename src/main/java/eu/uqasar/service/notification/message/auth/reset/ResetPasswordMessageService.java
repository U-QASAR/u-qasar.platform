package eu.uqasar.service.notification.message.auth.reset;

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
