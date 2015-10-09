package eu.uqasar.util.resources;

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

	public static <T> ResourceBundle getResourceBundle(Class<T> clazz,
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

	public static <T> IModel<String> getLabelModel(Class<T> clazz, Locale locale,
			final String key) {
		ResourceBundle bundle = getResourceBundle(clazz, locale);
		return Model.of(bundle.getString(key));
	}
}
