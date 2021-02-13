package eu.uqasar.message;

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


import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.wicket.Application;
import org.jboss.solder.logging.Logger;

import eu.uqasar.model.user.User;
import eu.uqasar.util.resources.XMLResourceBundleControl;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.provider.UrlProvider;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 *
 *
 */
public class UQasarMessage extends MimeMessage {

	private static final Logger logger = Logger.getLogger(UQasarMessage.class);
	private static final Configuration templateConfig;
	private static final String CHARSET = "UTF-8";
	private Locale locale = new Locale("");

	// TODO get this from the user preferences!
	private MessageType messageType = MessageType.HTML;
	private User recipient;
	private Address from;
	

	static {
		templateConfig = new Configuration();
		templateConfig.setClassForTemplateLoading(UQasarMessage.class, "");
		templateConfig.setDefaultEncoding("UTF-8");
	}

	protected UQasarMessage(Session session, final String recipientAddress, final String fromAddress) throws MessagingException {
		super(session);
		setRecipient(Message.RecipientType.TO, convertStringToAddress(recipientAddress));
		setFrom(fromAddress);
		this.recipient = createDummyRecipient(recipientAddress);
	}

	protected UQasarMessage(Session session, final String recipientAddress, Address fromAddress) throws MessagingException {
		super(session);
		setRecipient(Message.RecipientType.TO, convertStringToAddress(recipientAddress));
		setFrom(fromAddress);
		this.recipient = createDummyRecipient(recipientAddress);
		this.from = fromAddress;
	}

	protected UQasarMessage(Session session, User recipient, Address from) throws MessagingException {
		super(session);
		setRecipient(Message.RecipientType.TO, recipient);
		setFrom(from);
		this.from = from;
	}

	protected UQasarMessage(Session session, User recipient, User from) throws MessagingException {
		super(session);
		setRecipient(Message.RecipientType.TO, recipient);
		setFrom(from);
	}

	protected UQasarMessage(Session session, User recipient, final String fromAddress) throws MessagingException {
		super(session);
		setRecipient(Message.RecipientType.TO, recipient);
		setFrom(fromAddress);
	}
	
	private User createDummyRecipient(final String address) {
		User user = new User();
		user.setFirstName(address);
		user.setMail(address);
		return user;
	}

	protected User getRecipient() {
		return this.recipient;
	}

	private void validate() throws MessagingException {
		if (getFrom() == null) {
			throw new MessagingException("Sender cannot be null!");
		}
		if (getRecipient() == null || getRecipients(Message.RecipientType.TO).length == 0) {
			throw new MessagingException("Recipient(s) cannot be null or empty!");
		}
		if (getSubject() == null) {
			throw new MessagingException("Subject cannot be null!");
		}
		try {
			getContent();
		} catch (IOException ex) {
			throw new MessagingException(ex.getMessage(), ex);
		}
	}

	public void send() throws MessagingException, IOException {
		adjustLocale();
		prepareHeaders();
		generateContentFromTemplate();
		validate();
		Transport.send(this);
	}

	private String getContentTemplateFileName() {
		return this.getClass().getSimpleName() + ".ftl";
	}

	private String getContentTemplatePath() {
		// assume that all message templates reside below UQasarMessage class path
		// base path therefor is UQasarMessage classpath without UQasarMessage name 
		// (i.e. eu.uqasar.message.UQasarMessage -> eu.uqasar.message)
		final String baseMessagePath = UQasarMessage.class
				.getCanonicalName().replace("." + UQasarMessage.class
						.getSimpleName(), "");

		// build the difference from current inherting class to base path
		// (i.e. eu.uqasar.message.auth.register.RegistrationConfirmationMessage -> auth.register)
		// (by stripping eu.uqasar.message and RegistrationConfirmationMessage)
		final String diffPath = this.getClass().getCanonicalName().replace(baseMessagePath, "").
				replace("." + this.getClass().getSimpleName(), "");

		// build final template path by combining diffPath, type of template (txt or html) and name of this class

        return String.format("%s/%s/%s", diffPath.replace(".", "/"),
                getMessageType().getTemplateSourceType(), getContentTemplateFileName());
	}

	private Configuration updateTemplatingConfiguration() {
		// adjust execption handling based on message type
		TemplateExceptionHandler handler = getMessageType() == MessageType.HTML
				? TemplateExceptionHandler.HTML_DEBUG_HANDLER : TemplateExceptionHandler.DEBUG_HANDLER;
		templateConfig.setTemplateExceptionHandler(handler);
		return templateConfig;
	}

	protected Map<String, Object> getDefaultModel() {
		Map<String, Object> defaultModel = new HashMap<>();
		defaultModel.put("homepage", getHomepageLink());
		defaultModel.put("logo", getLogoLink());
		defaultModel.put("from", from.toString());
		return defaultModel;
	}

	protected Object getDataModel() {
		return getDefaultModel();
	}

	private UQasarMessage generateContentFromTemplate() throws MessagingException {
		try {
			Configuration cfg = updateTemplatingConfiguration();
			final String templatePath = getContentTemplatePath();
			logger.infof("Trying to load template at %s", templatePath);
			Template template = cfg.getTemplate(templatePath, getLocale());
			StringWriter writer = new StringWriter();
			template.process(getDataModel(), writer);
			final String processedTemplate = writer.toString();
			setContent(processedTemplate, getMessageType().getMimeType());
		} catch (IOException | TemplateException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return this;
	}

	protected UrlProvider getUrlProvider() {
		if (Application.exists()) {
			return UQasar.get().getUrlProvider();
		} else {
			return null;
		}
	}

	private static String getBaseURL() {
		return "http://"+BasePage.SERVERNAMEANDPORT+"/uqasar";
	}

	private static String getAssetURL(final String assetPath) {
		return String.format("http://"+BasePage.SERVERNAMEANDPORT+"/uqasar/%s", assetPath);
	}

	private String getHomepageLink() {
		if (Application.exists()) {
			return UQasar.get().getHomePageUrl();
		} else {
			return getBaseURL();
		}
	}

	private String getLogoLink() {
		if (Application.exists()) {
			return UQasar.get().getHomePageUrl() + "/assets/img/uqasar-logo.png";
		} else {
			return getAssetURL("assets/img/uqasar-logo.png");
		}
	}

	private String getLocalizedSubject() {
		final String KEY_SUBJECT = "subject";
		ResourceBundle bundle = getMyOrMyParentsResourceBundle();
		if (bundle != null && bundle.containsKey(KEY_SUBJECT)) {
			return bundle.getString(KEY_SUBJECT);
		}
		return null;
	}

	private ResourceBundle getMyOrMyParentsResourceBundle() {
		Class<?> clazzes[] = new Class<?>[]{this.getClass(), this.getClass().getSuperclass()};
		Locale[] locales = new Locale[]{locale, new Locale("")};
		ResourceBundle bundle = null;
		ResourceBundle.Control xmlLocator = new XMLResourceBundleControl();
		for (Class<?> clazz : clazzes) {
			for (Locale localeT : locales) {
				try {
					bundle = ResourceBundle.getBundle(clazz.getCanonicalName(), localeT, clazz.getClassLoader(), xmlLocator);
					return bundle;
				} catch (MissingResourceException ex) {
					try {
						bundle = ResourceBundle.getBundle(clazz.getCanonicalName(), localeT, clazz.getClassLoader());
						return bundle;
					} catch (MissingResourceException ignored) {
					}
				}
			}
		}
		if (bundle == null) {
			logger.warn("Could not find any resource bundle!");
		}
		return bundle;
	}

	private void adjustLocale() {
		if (recipient != null && recipient.getPreferredLocale() != null) {
			this.locale = recipient.getPreferredLocale();
		} else if (UQasar.exists()) {
			this.locale = UQasar.getSession().getLocale();
		}
		templateConfig.setLocale(this.locale);
	}

	private UQasarMessage prepareHeaders() {
		try {
			if (this.getSubject() == null) {
				// TODO add "U-QASAR" in front of every subject?
				setSubject(getLocalizedSubject(), CHARSET.toLowerCase());
			}
		} catch (MessagingException ex) {
			logger.warn(ex.getMessage(), ex);
		}
		return this;
	}

	private MessageType getMessageType() {
		return this.messageType;
	}

	private void setMessageType(MessageType type) {
		this.messageType = type;
	}

	private Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	private void setFrom(User user) throws MessagingException {
		Address address = convertStringToAddress(user.getMail(), user.getFullName());
		setFrom(address);
		this.from = address;
	}

	private void setFrom(final String mailAddress) throws MessagingException {
		Address address = convertStringToAddress(mailAddress, mailAddress);
		setFrom(address);
		this.from = address;
	}

	private void setRecipient(Message.RecipientType type, User user) throws MessagingException {
		Address address = convertStringToAddress(user.getMail(), user.getFullName());
		this.recipient = user;
		setRecipient(type, address);
		setMessageType(user.getPreferredMessageType());
	}

	public final void setRecipient(Message.RecipientType type, final String mailAddress) throws MessagingException {
		Address address = convertStringToAddress(mailAddress);
		setRecipient(type, address);
	}

	public static Address convertStringToAddress(final String address) {
		try {
            return new InternetAddress(address, CHARSET);
		} catch (UnsupportedEncodingException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	public static Address convertStringToAddress(final String address, final String name) {
		try {
            return new InternetAddress(address, name, CHARSET);
		} catch (UnsupportedEncodingException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	public enum MessageType {

		TEXT("text/plain; charset=UTF-8", "txt"),
		HTML("text/html; charset=UTF-8", "html"),;

		private final String templateMimeType;
		private final String templateType;

		MessageType(final String templateMimeType, final String templateType) {
			this.templateMimeType = templateMimeType;
			this.templateType = templateType;
		}

		public String getTemplateSourceType() {
			return this.templateType;
		}

		public String getMimeType() {
			return this.templateMimeType;
		}
	}
}
