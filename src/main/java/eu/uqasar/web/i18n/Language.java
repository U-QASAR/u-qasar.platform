package eu.uqasar.web.i18n;

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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;
import org.apache.wicket.Session;
import org.joda.time.DateTime;

@Getter
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
    private String getDatePattern(int style) {
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

	private String getLocalizedDatePattern(int style) {
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
