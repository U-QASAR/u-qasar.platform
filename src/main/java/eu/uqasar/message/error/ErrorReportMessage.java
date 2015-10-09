/*
 */
package eu.uqasar.message.error;

import eu.uqasar.message.UQasarMessage;
import java.util.Map;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import org.apache.commons.lang.StringUtils;

/**
 *
 *
 */
public class ErrorReportMessage extends UQasarMessage {

	private String report, reporter, errorMessage, markup, stacktrace;

	public ErrorReportMessage(Session session, String recipientAddress, String fromAddress) throws MessagingException {
		super(session, recipientAddress, fromAddress);
	}

	public ErrorReportMessage(Session session, String recipientAddress, Address fromAddress) throws MessagingException {
		super(session, recipientAddress, fromAddress);
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getReporter() {
		return reporter;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getMarkup() {
		return markup;
	}

	public void setMarkup(String markup) {
		this.markup = markup;
	}

	public String getStacktrace() {
		return stacktrace;
	}

	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}

	@Override
	protected Object getDataModel() {
		Map<String, Object> defaultModel = super.getDefaultModel();
		defaultModel.put("report", StringUtils.trim(getReport()));
		defaultModel.put("reporter", StringUtils.trim(getReporter()));
		defaultModel.put("errormessage", StringUtils.trim(getErrorMessage()));
		defaultModel.put("stacktrace", StringUtils.trim(getStacktrace()));
		defaultModel.put("markup", StringUtils.trim(getMarkup()));
		return defaultModel;
	}

}
