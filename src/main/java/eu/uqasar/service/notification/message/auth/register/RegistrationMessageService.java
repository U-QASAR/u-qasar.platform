package eu.uqasar.service.notification.message.auth.register;

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
