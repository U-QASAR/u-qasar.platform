package eu.uqasar.util.resources;

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


import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import eu.uqasar.web.UQasar;

public class ResourceBundleLocator {

	private static final Locale DEFAULT_LOCALE = new Locale("en");

	public static <T> ResourceBundle getResourceBundle(Class<T> clazz) {
		return getResourceBundle(clazz, UQasar.getSession().getLocale());
	}

	private static <T> ResourceBundle getResourceBundle(Class<T> clazz,
                                                        Locale locale) {

		ResourceBundle bundle;
		ResourceBundle.Control xmlLocator = new XMLResourceBundleControl();
		try {
			bundle = ResourceBundle.getBundle(clazz.getName(), locale, xmlLocator);
		} catch (MissingResourceException e) {
			try {
				bundle = ResourceBundle.getBundle(clazz.getName(), DEFAULT_LOCALE, xmlLocator);
			} catch (MissingResourceException e2) {
				try {
					bundle = ResourceBundle.getBundle(clazz.getName(), locale);
				} catch (MissingResourceException e3) {
					bundle = ResourceBundle.getBundle(clazz.getName(), DEFAULT_LOCALE);
				}
			}
		}
		return bundle;
	}

	public static <T> IModel<String> getLabelModel(Class<T> clazz,
			IResourceKeyProvider provider) {
		return getLabelModel(clazz, provider.getKey());
	}

	public static <T> IModel<String> getLabelModel(Class<T> clazz, Locale locale,
			IResourceKeyProvider provider) {
		return getLabelModel(clazz, locale, provider.getKey());
	}

	public static <T> IModel<String> getLabelModel(Class<T> clazz, final String key) {
		return getLabelModel(clazz, UQasar.getSession().getLocale(), key);
	}

	private static <T> IModel<String> getLabelModel(Class<T> clazz, Locale locale,
                                                    final String key) {
		ResourceBundle bundle = getResourceBundle(clazz, locale);
		return Model.of(bundle.getString(key));
	}
}
