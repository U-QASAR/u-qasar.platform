package eu.uqasar.web.i18n;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.wicket.Session;
import org.joda.time.DateTime;

public enum Language {

	German(Locale.GERMAN),

	English(Locale.ENGLISH),

	Finnish(Locale.forLanguageTag("FI")),
	
//	Spanish(Locale.forLanguageTag("ES")),
//	
//	Greek(Locale.forLanguageTag("EL")),
	
	;

	private Locale locale;

	Language(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return this.locale;
	}

	/**
	 * Returns the String pattern for formatting and parsing dates, depending on
	 * the given style, for example "M/d/yy" for style {@link DateFormat#SHORT}
	 * in the US locale.
	 * 
	 * @param style
	 *            the given formatting style. For example, SHORT for "M/d/yy" in
	 *            the US locale.
	 * @see DateFormat#SHORT
	 * @see DateFormat#MEDIUM
	 * @see DateFormat#LONG
	 * @return
	 */
	public String getDatePattern(int style) {
		SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat
				.getDateInstance(style, locale);
		return dateFormat.toPattern();
	}

	public String getDatePattern() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", locale);
		return dateFormat.toPattern();
	}

	public String getShortDatePattern() {
		return getDatePattern(DateFormat.SHORT);
	}

	public String getLocalizedDatePattern() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", locale);
		if (isGerman()) {
			return dateFormat.toLocalizedPattern().replace('u', 'j');
		}
		if (isFinnish()) {
			return dateFormat.toLocalizedPattern().replace('a', 'v').replace('j', 'p').replace('n', 'k');
		}
		return dateFormat.toLocalizedPattern();
	}

	public String getLocalizedShortDatePattern() {
		return getLocalizedDatePattern(DateFormat.SHORT);
	}

	public String getLocalizedDatePattern(int style) {
		SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat
				.getDateInstance(style, locale);
		return isGerman() ? dateFormat.toLocalizedPattern().replace('u', 'j')
				: dateFormat.toLocalizedPattern();
	}

	public String formatDateTimeLocalized(Long millis) {
		return formatDateTimeLocalized(new Date(millis));
	}

	public String formatDateTimeLocalized(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", locale);
		return dateFormat.format(date);
	}
	
	public String formatDateTimeLocalized(DateTime date) {
		return formatDateTimeLocalized(date.toDate());
	}


	public static Language fromSession() {
		Locale sessionLocale = Session.get().getLocale();
		for (Language language : values()) {
			if (language.getLocale().getLanguage()
					.equals(sessionLocale.getLanguage()))
				return language;
		}
		return Language.English;
	}

	private boolean isGerman() {
		return this.locale.getLanguage().equals(Locale.GERMAN.getLanguage());
	}
	
	private boolean isFinnish() {
		return this.locale.getLanguage().equals(Locale.forLanguageTag("FI").toString());
	}
}
