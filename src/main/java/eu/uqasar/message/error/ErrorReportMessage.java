/*
 */
package eu.uqasar.message.error;

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
import java.util.Map;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

/**
 *
 *
 */
@Setter
@Getter
public class ErrorReportMessage extends UQasarMessage {

	private String report, reporter, errorMessage, markup, stacktrace;

	public ErrorReportMessage(Session session, String recipientAddress, String fromAddress) throws MessagingException {
		super(session, recipientAddress, fromAddress);
	}

	public ErrorReportMessage(Session session, String recipientAddress, Address fromAddress) throws MessagingException {
		super(session, recipientAddress, fromAddress);
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
