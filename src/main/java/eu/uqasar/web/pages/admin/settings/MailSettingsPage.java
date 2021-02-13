/*
 */
package eu.uqasar.web.pages.admin.settings;

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
import eu.uqasar.model.settings.mail.MailSettings;
import eu.uqasar.service.settings.MailSettingsService;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.components.CSSAppender;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.pages.admin.AdminBasePage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 */
public class MailSettingsPage extends AdminBasePage {

	@Inject
	Logger logger;
	
	@Inject
	MailSettingsService msService;

	private final Form<Void> form;
	private final TextField<String> serverField, userField, fromUserField, fromAddressField, receiverAddressField, subjectField;
	private final PasswordTextField passwordField;
	private final TextField<Integer> portField;
	private final CheckBox checkTls, checkAuth;
	private final Label validationResults;

	private String receiverAddress;
    private final String subject = "U-QASAR Test Mail";
	private final MailSettings settings;

	public MailSettingsPage(PageParameters pageParameters) {
		super(pageParameters);
		settings = msService.get(new MailSettings());
		form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				String message = new StringResourceModel("save.confirmed", this, null).getString();
				msService.update(settings);
			    getPage().success(message);
			    setResponsePage(getPage());
			}
		};
		
		if(UQSession.get().getLoggedInUser() != null) {
			receiverAddress = UQSession.get().getLoggedInUser().getMail();
		}
		
		serverField = new TextField<>("server", new PropertyModel<String>(settings, "host"));
		InputBorder<String> serverValidationBorder = new InputBorder<>(
				"serverValidationBorder", serverField,
				new StringResourceModel("label.server", this, null),
				new StringResourceModel("help.server", this, null));
		form.add(serverValidationBorder);

		portField = new NumberTextField<>("port", new PropertyModel<Integer>(settings, "port"));
		InputBorder<Integer> portValidationBorder = new InputBorder<>(
				"portValidationBorder", portField,
				new StringResourceModel("label.port", this, null));
		form.add(portValidationBorder);

		userField = new TextField<>("user", new PropertyModel<String>(settings, "authUser"));
		userField.setRequired(false);
		InputBorder<String> userNameValidationBorder = new InputBorder<>(
				"userValidationBorder", userField,
				new StringResourceModel("label.user", this, null),
				new StringResourceModel("help.user", this, null));
		form.add(userNameValidationBorder);

		passwordField = new PasswordTextField("password", new PropertyModel<String>(settings, "authPassword"));
		passwordField.setRequired(false);
		passwordField.setResetPassword(false);
		InputBorder<String> passwordValidationBorder = new InputBorder(
				"passwordValidationBorder", passwordField,
				new StringResourceModel("label.password", this, null));
		form.add(passwordValidationBorder);

		checkAuth = new CheckBox("auth.required", new PropertyModel<Boolean>(settings, "authenticationRequired"));
		form.add(checkAuth);

		checkTls = new CheckBox("auth.tls", new PropertyModel<Boolean>(settings, "tlsRequired"));
		form.add(checkTls);

		fromUserField = new TextField<>("fromUser", new PropertyModel<String>(settings, "fromUser"));
		InputBorder<String> fromUserValidationBorder = new InputBorder<>(
				"fromUserValidationBorder", fromUserField,
				new StringResourceModel("label.from.user", this, null),
				new StringResourceModel("help.from.user", this, null));
		form.add(fromUserValidationBorder);

		fromAddressField = new TextField<>("fromAddress", new PropertyModel<String>(settings, "fromAddress"));
		InputBorder<String> fromAddressValidationBorder = new InputBorder<>(
				"fromAddressValidationBorder", fromAddressField,
				new StringResourceModel("label.from.address", this, null),
				new StringResourceModel("help.from.address", this, null));
		form.add(fromAddressValidationBorder);

		receiverAddressField = new TextField<>("receiverAddress", new PropertyModel<String>(this, "receiverAddress"));
		InputBorder<String> receiverAddressValidationBorder = new InputBorder<>(
				"receiverAddressValidationBorder", receiverAddressField,
				new StringResourceModel("label.to.address", this, null),
				new StringResourceModel("help.to.address", this, null));
		form.add(receiverAddressValidationBorder);
		
		subjectField = new TextField<>("subject", new PropertyModel<String>(this, "subject"));
		InputBorder<String> subjectValidationBorder = new InputBorder<>(
				"subjectValidationBorder", subjectField,
				new StringResourceModel("label.to.subject", this, null),
				new StringResourceModel("help.to.subject", this, null));
		form.add(subjectValidationBorder);

		Button validateConnection = new IndicatingAjaxButton("validateConnection") {

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.add(form);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					tryToConnect();
					validationResults.setDefaultModel(new StringResourceModel("label.validation.success", this, null));
					validationResults.add(new CSSAppender("success"));
				} catch (Throwable ex) {
					logger.warn(ex.getMessage(), ex);
					StringWriter errors = new StringWriter();
					ex.printStackTrace(new PrintWriter(errors));
					validationResults.setDefaultModel(Model.of(errors.toString()));
					validationResults.add(new CSSAppender("error"));
				}
				target.add(validationResults);
			}
		};
		form.add(validateConnection);

		validationResults = new Label("validationResults");
		form.add(validationResults.setOutputMarkupId(true));
		add(form);
	}

	private Properties getSMTPConnectionProperties() {
		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.host", settings.getHost());
		mailProperties.put("mail.smtp.port", settings.getPort());
		mailProperties.put("mail.smtp.auth", settings.getAuthenticationRequired());
		mailProperties.put("mail.smtp.starttls.enable", settings.getTlsRequired());

		return mailProperties;
	}

	private void tryToConnect() {
		Properties mailProperties = getSMTPConnectionProperties();
		Session session;
		if (checkAuth.getModelObject()) {
			session = Session.getInstance(mailProperties,
					new javax.mail.Authenticator() {
						@Override
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(settings.getAuthUser(), settings.getAuthPassword());
						}
					});
		} else {
			session = Session.getInstance(mailProperties);
		}
		session.setDebug(true);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream debugStream  = new PrintStream(bos);
		session.setDebugOut(debugStream);
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(UQasarMessage.convertStringToAddress(settings.getFromAddress(), settings.getFromUser()));
			message.setSubject(subject);
			message.setContent(new StringResourceModel("content.mail.text", this, null).getObject(), "text/plain");
			message.setRecipient(Message.RecipientType.TO, UQasarMessage.convertStringToAddress(receiverAddress));
			Transport.send(message);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage() + "\r\n" + bos.toString(), e);
//			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
