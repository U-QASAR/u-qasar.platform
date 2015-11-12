/*
 */

package eu.uqasar.message.auth.register;

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
import eu.uqasar.model.user.User;
import eu.uqasar.web.pages.auth.login.LoginPage;
import java.util.Map;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;

/**
 *
 *
 */
public class RegistrationConfirmedMessage extends UQasarMessage {

	public RegistrationConfirmedMessage(Session session, User recipient, User from) throws MessagingException {
		super(session, recipient, from);
	}

	public RegistrationConfirmedMessage(Session session, User recipient, final String from) throws MessagingException {
		super(session, recipient, from);
	}

	public RegistrationConfirmedMessage(Session session, User recipient, Address from) throws MessagingException {
		super(session, recipient, from);
	}
	
	@Override
	public Object getDataModel() {
		Map<String, Object> defaultModel = super.getDefaultModel();
		defaultModel.put("user", getRecipient());
		defaultModel.put("link", getUrlProvider().urlFor(LoginPage.class));
		return defaultModel;
	}
}