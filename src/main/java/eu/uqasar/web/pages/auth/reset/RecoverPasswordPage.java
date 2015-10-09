/*
 */
package eu.uqasar.web.pages.auth.reset;

import eu.uqasar.exception.notification.NotificationException;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.notification.message.auth.reset.ResetPasswordMessageService;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.pages.BasePage;
import static eu.uqasar.web.pages.auth.InfoPage.noSuchUserMessage;
import static eu.uqasar.web.pages.auth.InfoPage.recoverPWEmailSentMessage;
import eu.uqasar.web.provider.UrlProvider;
import javax.inject.Inject;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 */
public class RecoverPasswordPage extends BasePage {

	private final TextField<String> mail;
	private String mailAddress;

	@Inject
	AuthenticationService authenticationService;

	@Inject
	ResetPasswordMessageService resetPWMessageService;
	
	@Inject
	Logger logger;

	@Inject
	UrlProvider urlProvider;

	public RecoverPasswordPage(PageParameters parameters) {
		super(parameters);

		Form<Void> form = new InputValidationForm<Void>("form") {

			@Override
			protected void onSubmit() {
				sendResetPasswordMail();
			}
		};

		mail = new TextField<>("mail", new PropertyModel<String>(this, "mailAddress"));
		InputBorder<String> mailValidationBorder = new OnEventInputBeanValidationBorder<>(
				"mailValidationBorder", mail,
				new StringResourceModel("label.mail", this, null),
				new StringResourceModel("help.mail", this, null),
				HtmlEvent.ONBLUR);
		form.add(mailValidationBorder);

		add(form);
	}

	private void sendResetPasswordMail() {
		User user = authenticationService.requestNewPassword(this.mailAddress);
		if(user == null) {
			setResponsePage(noSuchUserMessage());
		} else {
			try {
				resetPWMessageService.sendPWResetRequestedMessage(user);
				setResponsePage(recoverPWEmailSentMessage());
			} catch (NotificationException ex) {
				logger.error(ex.getMessage(), ex);
				error(ex.getMessage());
			}
		}
	}
}
