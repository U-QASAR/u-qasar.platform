/*
 */
package eu.uqasar.service.notification.message.error;

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
