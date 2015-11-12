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
import org.apache.wicket.protocol.http.ClientProperties;

/**
 *
 *
 */
@Entity
public class WebEnvironment extends AbstractEntity {

	@Enumerated(EnumType.STRING)
	private Browser browser;
	private int browserVersionMajor, browserVersionMinor;
	private String navigatorPlatform, navigatorUserAgent, navigatorLanguage;
	private String remoteAddress, hostname;
	private boolean cookiesEnabled, javaEnabled;
	private int screenWidth, screenHeight, colorDepth;

	public WebEnvironment() {

	}

	public WebEnvironment(ClientProperties props) {
		populateFromClientProperties(props);
	}

	public Browser getBrowser() {
		return browser;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	public int getBrowserVersionMajor() {
		return browserVersionMajor;
	}

	public void setBrowserVersionMajor(int browserVersionMajor) {
		this.browserVersionMajor = browserVersionMajor;
	}

	public int getBrowserVersionMinor() {
		return browserVersionMinor;
	}

	public void setBrowserVersionMinor(int browserVersionMinor) {
		this.browserVersionMinor = browserVersionMinor;
	}

	public String getNavigatorPlatform() {
		return navigatorPlatform;
	}

	public void setNavigatorPlatform(String navigatorPlatform) {
		this.navigatorPlatform = navigatorPlatform;
	}

	public String getNavigatorUserAgent() {
		return navigatorUserAgent;
	}

	public void setNavigatorUserAgent(String navigatorUserAgent) {
		this.navigatorUserAgent = navigatorUserAgent;
	}

	public String getNavigatorLanguage() {
		return navigatorLanguage;
	}

	public void setNavigatorLanguage(String navigatorLanguage) {
		this.navigatorLanguage = navigatorLanguage;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public boolean isCookiesEnabled() {
		return cookiesEnabled;
	}

	public void setCookiesEnabled(boolean cookiesEnabled) {
		this.cookiesEnabled = cookiesEnabled;
	}

	public boolean isJavaEnabled() {
		return javaEnabled;
	}

	public void setJavaEnabled(boolean javaEnabled) {
		this.javaEnabled = javaEnabled;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getColorDepth() {
		return colorDepth;
	}

	public void setColorDepth(int colorDepth) {
		this.colorDepth = colorDepth;
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

	public static enum Browser {

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
