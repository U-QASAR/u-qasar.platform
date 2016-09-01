/*
 */
package eu.uqasar.web.pages.errors;

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
import eu.uqasar.model.role.Role;
import eu.uqasar.model.user.User;
import eu.uqasar.service.notification.message.error.ErrorReportingMessageService;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.components.util.DefaultTinyMCESettings;
import eu.uqasar.web.pages.BasePage;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import org.apache.wicket.Page;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.MarkupException;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Generics;
import org.jboss.solder.logging.Logger;
import wicket.contrib.tinymce.TinyMceBehavior;

/**
 *
 *
 */
public class ErrorPage extends BasePage {

	private static final Logger logger = Logger.getLogger(ErrorPage.class);

	@Inject
	ErrorReportingMessageService erms;

	private final Throwable throwable;
	private String report;
	private String userName, userMail;

	public ErrorPage() {
		this(null);
	}

	public ErrorPage(Throwable t) {
		super(new PageParameters());
		this.throwable = t;

		// add home page link
		add(new BookmarkablePageLink<Page>("homeLink", getApplication().getHomePage()));

		if (throwable == null) {
			// we are submitting a bug
			add(newLabel("header", "bug.report.header"));
			add(newLabel("message", "bug.report.message"));
		} else {
			// actual error page
			add(newLabel("header", "error.header"));
			add(newLabel("message", "error.message"));
		}

		// add bug report form
		add(newBugReportForm(throwable));
	}

	private Label newLabel(final String id, final String property) {
		return new Label(id, new StringResourceModel(property, this, null));
	}

	private Form<Void> newBugReportForm(Throwable t) {
		final Form<Void> form = new Form<Void>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				try {
					reportIssue(report);

					// set thank you message and return to home page
					setResponsePage(
							getApplication().getHomePage(),
							new PageParameters().set(MESSAGE_PARAM, getString("success.message")).set(LEVEL_PARAM,
									FeedbackMessage.SUCCESS));
				} catch (NotificationException e) {
					String message = getString("fail.message");
					logger.error(message, e);
					getPage().error(message);
				}
			}
		};

		TextField<String> userNameField = new TextField<>("userName", new PropertyModel<String>(ErrorPage.this, "userName"));
		TextField<String> userMailField = new TextField<>("userMail", new PropertyModel<String>(ErrorPage.this, "userMail"));
		UQSession session = UQSession.get();
		if (session != null && session.isAuthenticated()) {
			this.userName = session.getLoggedInUser().getFullName();
			this.userMail = session.getLoggedInUser().getMail();
			if (!session.getLoggedInUser().hasAnyRoles(Role.Administrator)) {
				userNameField.setEnabled(false);
				userMailField.setEnabled(false);
			}
		}
		form.add(userNameField);
		form.add(userMailField);
		TextArea<String> description = new TextArea<>("report", new PropertyModel<String>(ErrorPage.this, "report"));
		description.add(new TinyMceBehavior(DefaultTinyMCESettings.get()));
		form.add(description);
		Button btnStacktraceShow = new Button("stacktrace.show");
		btnStacktraceShow.setVisible(canSeeStackTrace(t));
		Label stackTrace = new Label("stacktrace.text", t != null ? Model.of(getStackTraceForOutput(t)) : Model.of(""));
		WebMarkupContainer stackTraceContainer = new WebMarkupContainer("stacktrace.container");
		stackTraceContainer.add(stackTrace);
		stackTraceContainer.setVisible(canSeeStackTrace(t));
		form.add(stackTraceContainer);
		form.add(btnStacktraceShow);
		return form;
	}

	private boolean canSeeStackTrace(Throwable t) {
		if (t == null) {
			return false;
		}
		if (UQSession.exists()) {
			User user = UQSession.get().getLoggedInUser();
			if (user != null) {
				return user.hasAnyRoles(Role.Administrator);
			}
		}
		return false;
	}

	private String getStackTraceForOutput(Throwable t) {
		String errorMessage = getErrorMessage(throwable);
		String stackTrace = getStackTrace(throwable);
		String markup = getMarkUp(throwable);
		return errorMessage + stackTrace + markup;
	}

	private void reportIssue(final String report) throws NotificationException {
		String errorMessage = getErrorMessage(throwable);
		String stackTrace = getStackTrace(throwable);
		String markup = getMarkUp(throwable);
		String reportedBy = userName + " (" + userMail + ")";
		erms.sendErrorReport(report, errorMessage, stackTrace, markup, reportedBy);
	}

	private String getMarkUp(final Throwable throwable) {
		String resource = "";
		String markup = "";
		MarkupStream markupStream;
		if (throwable instanceof MarkupException) {
			markupStream = ((MarkupException) throwable).getMarkupStream();

			if (markupStream != null) {
				markup = markupStream.toHtmlDebugString();
				resource = markupStream.getResource().toString();
			}
		}
		String markupStr = "Resource: " + (resource.isEmpty() ? "[No Resource]" : resource);
		markupStr += "\nMarkup: " + (markup.isEmpty() ? "[No Markup]" : markup);
		return markupStr;
	}

	/**
	 *
	 * @param throwable
	 * <p>
	 * @return
	 */
	private String getErrorMessage(final Throwable throwable) {
		if (throwable != null) {
			StringBuilder sb = new StringBuilder(256);

			// first print the last cause
			List<Throwable> al = convertToList(throwable);
			int length = al.size() - 1;
			Throwable cause = al.get(length);
			sb.append("Last cause: ").append(cause.getMessage()).append('\n');
			if (throwable instanceof WicketRuntimeException) {
				String msg = throwable.getMessage();
				if ((msg != null) && (!msg.equals(cause.getMessage()))) {
					if (throwable instanceof MarkupException) {
						MarkupStream stream = ((MarkupException) throwable).getMarkupStream();
						if (stream != null) {
							String text = "\n" + stream.toString();
							if (msg.endsWith(text)) {
								msg = msg.substring(0, msg.length() - text.length());
							}
						}
					}

					sb.append("WicketMessage: ");
					sb.append(msg);
					sb.append("\n\n");
				}
			}
			return sb.toString();
		} else {
			return "[Unknown]";
		}
	}

	/**
	 * Converts a Throwable to a string.
	 * <p>
	 * @param throwable The throwable
	 * <p>
	 * @return The string
	 */
	private String getStackTrace(final Throwable throwable) {
		if (throwable != null) {
			List<Throwable> al = convertToList(throwable);

			StringBuilder sb = new StringBuilder(256);

			// first print the last cause
			int length = al.size() - 1;
			Throwable cause = al.get(length);

			sb.append("Root cause:\n\n");
			outputThrowable(cause, sb, false);

			if (length > 0) {
				sb.append("\n\nComplete stack:\n\n");
				for (int i = 0; i < length; i++) {
					outputThrowable(al.get(i), sb, true);
					sb.append("\n");
				}
			}
			return sb.toString();
		} else {
			return "[No Throwable]";
		}
	}

	/**
	 * Outputs the throwable and its stacktrace to the stringbuffer. If
	 * stopAtWicketSerlvet is true then the output will stop when the
	 * org.apache.wicket servlet is reached. sun.reflect. packages are filtered
	 * out.
	 * <p>
	 * @param cause
	 * @param sb
	 * @param stopAtWicketServlet
	 */
	private void outputThrowable(Throwable cause, StringBuilder sb, boolean stopAtWicketServlet) {
		sb.append(cause);
		sb.append("\n");
		StackTraceElement[] trace = cause.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			String traceString = trace[i].toString();
			if (!(traceString.startsWith("sun.reflect.") && i > 1)) {
				sb.append("     at ");
				sb.append(traceString);
				sb.append("\n");
				if (stopAtWicketServlet
						&& (traceString.startsWith("org.apache.wicket.protocol.http.WicketServlet") || traceString
						.startsWith("org.apache.wicket.protocol.http.WicketFilter"))) {
					return;
				}
			}
		}
	}

	/**
	 *
	 * @param throwable
	 * <p>
	 * @return
	 */
	private List<Throwable> convertToList(final Throwable throwable) {
		List<Throwable> al = Generics.newArrayList();
		Throwable cause = throwable;
		al.add(cause);
		while ((cause.getCause() != null) && (cause != cause.getCause())) {
			cause = cause.getCause();
			al.add(cause);
		}
		return al;
	}

	@Override
	protected void setHeaders(final WebResponse response) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	@Override
	public boolean isVersioned() {
		return false;
	}

	@Override
	public boolean isErrorPage() {
		return true;
	}
}
