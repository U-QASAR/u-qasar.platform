/*
 */
package eu.uqasar.web;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;

/**
 *
 *
 */
public class WebConstants {

	public static AutoCompleteSettings getDefaultAutoCompleteSettings() {
		AutoCompleteSettings settings = new AutoCompleteSettings()
				.setPreselect(true)
				.setThrottleDelay(100)
				.setShowListOnFocusGain(true)
				;
		return settings;
	}

}
