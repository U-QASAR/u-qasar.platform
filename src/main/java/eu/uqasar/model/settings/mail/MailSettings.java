/*
 */
package eu.uqasar.model.settings.mail;

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


import eu.uqasar.model.settings.Settings;

/**
 *
 *
 */
public class MailSettings extends Settings {

	private static final String BASE = "setting.mail";
	private static final String SMTP_HOST = BASE + "smtp.host";
	private static final String SMTP_PORT = BASE + "smtp.port";
	private static final String SMTP_AUTH_REQUIRED = BASE + "smtp.auth.required";
	private static final String SMTP_AUTH_TLS = BASE + "smtp.auth.tls";
	private static final String SMTP_AUTH_USER = BASE + "smtp.auth.user";
	private static final String SMTP_AUTH_PASS = BASE + "smtp.auth.password";
	private static final String SMTP_FROM_USER = BASE + "smtp.from.user";
	private static final String SMTP_FROM_ADDRESS = BASE + "smtp.from.address";

	private static final String[] keys = {
		SMTP_HOST, 
		SMTP_PORT, 
		SMTP_AUTH_REQUIRED,
		SMTP_AUTH_TLS, 
		SMTP_AUTH_USER, 
		SMTP_AUTH_PASS, 
		SMTP_FROM_USER, 
		SMTP_FROM_ADDRESS
	};

	@Override
	public String[] getKeys() {
		return keys;
	}

	public String getHost() {
		return getValue(SMTP_HOST);
	}

	public void setHost(final String host) {
		setValue(SMTP_HOST, host);
	}

	public int getPort() {
		return Integer.parseInt(getValue(SMTP_PORT));
	}

	public void setPort(int port) {
		setValue(SMTP_PORT, String.valueOf(port));
	}

	public String getAuthUser() {
		return getValue(SMTP_AUTH_USER);
	}

	public void setAuthUser(final String user) {
		setValue(SMTP_AUTH_USER, user);
	}

	public void setAuthPassword(final String password) {
		setValue(SMTP_AUTH_PASS, password);
	}

	public String getAuthPassword() {
		return getValue(SMTP_AUTH_PASS);
	}

	public String getFromUser() {
		return getValue(SMTP_FROM_USER);
	}

	public void setFromUser(final String user) {
		setValue(SMTP_FROM_USER, user);
	}

	public String getFromAddress() {
		return getValue(SMTP_FROM_ADDRESS);
	}

	public void setFromAddress(final String address) {
		setValue(SMTP_FROM_ADDRESS, address);
	}

	public boolean getAuthenticationRequired() {
		return Boolean.parseBoolean(getValue(SMTP_AUTH_REQUIRED));
	}

	public void setAuthenticationRequired(final boolean required) {
		setValue(SMTP_AUTH_REQUIRED, String.valueOf(required));
	}

	public boolean getTlsRequired() {
		return Boolean.parseBoolean(getValue(SMTP_AUTH_TLS));
	}

	public void setTlsRequired(final boolean required) {
		setValue(SMTP_AUTH_TLS, String.valueOf(required));
	}

}
