/*
 */
package eu.uqasar.model.monitoring;

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


import eu.uqasar.model.AbstractEntity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.wicket.protocol.http.ClientProperties;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class WebEnvironment extends AbstractEntity {

	@Enumerated(EnumType.STRING)
	private Browser browser;
	private int browserVersionMajor, browserVersionMinor;
	private String navigatorPlatform, navigatorUserAgent, navigatorLanguage;
	private String remoteAddress, hostname;
	private boolean cookiesEnabled, javaEnabled;
	private int screenWidth, screenHeight, colorDepth;

	public WebEnvironment(ClientProperties props) {
		populateFromClientProperties(props);
	}

	private void populateFromClientProperties(ClientProperties props) {
		setBrowser(Browser.getFromClientProperties(props));
		setBrowserVersionMajor(props.getBrowserVersionMajor());
		setBrowserVersionMinor(props.getBrowserVersionMinor());
		setColorDepth(props.getScreenColorDepth());
		setCookiesEnabled(props.isCookiesEnabled());
		setHostname(props.getHostname());
		setJavaEnabled(props.isJavaEnabled());
		setNavigatorLanguage(props.getNavigatorLanguage());
		setNavigatorPlatform(props.getNavigatorPlatform());
		setNavigatorUserAgent(props.getNavigatorUserAgent());
		setRemoteAddress(props.getRemoteAddress());
		setScreenHeight(props.getScreenHeight());
		setScreenWidth(props.getScreenWidth());
	}

	public static WebEnvironment fromClientProperties(ClientProperties properties) {
		return new WebEnvironment(properties);
	}

	public enum Browser {

		Chrome,
		InternetExplorer,
		Konqueror,
		Mozilla,
		MozillaFirefox,
		Opera,
		Safari,
		Unknown;

		public static Browser getFromClientProperties(ClientProperties client) {
			if (client.isBrowserChrome()) {
				return Chrome;
			} else if (client.isBrowserInternetExplorer()) {
				return InternetExplorer;
			} else if (client.isBrowserKonqueror()) {
				return Konqueror;
			} else if (client.isBrowserMozilla()) {
				return Mozilla;
			} else if (client.isBrowserMozillaFirefox()) {
				return MozillaFirefox;
			} else if (client.isBrowserOpera()) {
				return Opera;
			} else if (client.isBrowserSafari()) {
				return Safari;
			}
			return Unknown;
		}
	}
}
