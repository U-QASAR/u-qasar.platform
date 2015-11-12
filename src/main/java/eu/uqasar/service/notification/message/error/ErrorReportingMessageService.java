/*
 */
package eu.uqasar.service.notification.message.error;

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
import eu.uqasar.message.error.ErrorReportMessage;
import eu.uqasar.service.notification.message.MessageNotificationService;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.mail.MessagingException;

/**
 *
 *
 */
@ApplicationScoped
public class ErrorReportingMessageService extends MessageNotificationService {

	public void sendErrorReport(final String report, final String errorMessage, final String stackTrace,
			final String markup, final String reportedBy) throws NotificationException {
		try {
			ErrorReportMessage message = new ErrorReportMessage(getSession(), "testuser@example.net", getDefaultMailSender());
			message.setReport(report);
			message.setErrorMessage(errorMessage);
			message.setStacktrace(stackTrace);
			message.setMarkup(markup);
			message.setReporter(reportedBy);
			message.send();
		} catch (MessagingException | IOException ex) {
			throw new NotificationException(ex.getMessage(), ex);
		}
	}
}
