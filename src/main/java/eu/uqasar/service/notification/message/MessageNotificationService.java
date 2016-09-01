package eu.uqasar.service.notification.message;

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


import eu.uqasar.message.UQasarMessage;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 */
public abstract class MessageNotificationService {

	// TODO put into properties file and load when required
	private static final String SMTP_HOST = "smtp.1and1.es";
	private static final String SMTP_PORT = "25";
	private static final boolean SMTP_AUTH = true;
	private static final boolean SMTP_TLS_ENABLED = false;
	private static final String USERNAME = "support@extremefactories.eu";
	private static final String PASSWORD = "3xtr3m3F4ct0r13s";
	private static final String FROM_ADDRESS = "noreply@uqasar.eu";
	private static final String FROM_NAME = "U-QASAR";

	private static final Properties mailProperties = new Properties();
	private static final org.jboss.solder.logging.Logger logger = Logger.getLogger(MessageNotificationService.class);

	static {
		mailProperties.put("mail.smtp.host", SMTP_HOST);
		mailProperties.put("mail.smtp.port", SMTP_PORT);
		mailProperties.put("mail.smtp.auth", SMTP_AUTH);
		mailProperties.put("mail.smtp.starttls.enable", SMTP_TLS_ENABLED);
	}
	
	public static Address getDefaultMailSender() {
		return UQasarMessage.convertStringToAddress(FROM_ADDRESS, FROM_NAME);
	}

	public static Session getSession() {
		if (SMTP_AUTH) {
			Session session = Session.getInstance(mailProperties,
					new javax.mail.Authenticator() {
						@Override
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(USERNAME, PASSWORD);
						}
					});
			session.setDebug(true);
			return session;
		} else {
			Session session = Session.getInstance(mailProperties);
			session.setDebug(true);
			return session;
		}
	}
}
