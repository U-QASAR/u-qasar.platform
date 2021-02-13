package eu.uqasar.web.components.charts;

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


import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.wicket.Session;

import com.googlecode.wickedcharts.highcharts.options.LanguageOptions;
import com.googlecode.wickedcharts.highcharts.options.Options;
import eu.uqasar.util.resources.ResourceBundleLocator;

public class DefaultChartOptions extends Options {

	private static final long serialVersionUID = -8502713639546429538L;

	public static LanguageOptions getLocalizedLanguageOptions() {
		LanguageOptions lang = new LanguageOptions();
		ResourceBundle bundle;

		try {
			bundle = ResourceBundleLocator.getResourceBundle(DefaultChartOptions.class);
			lang.setMonths(getLocalizedMonthNames());
			lang.setWeekdays(getLocalizedWeekDays());
			lang.setDecimalPoint(getDecimalSeparator());
			lang.setThousandsSep(getGroupingSeparator());
			lang.setDownloadJPEG(bundle.getString("chart.button.download.jpeg"));
			lang.setDownloadPDF(bundle.getString("chart.button.download.pdf"));
			lang.setDownloadPNG(bundle.getString("chart.button.download.png"));
			lang.setDownloadSVG(bundle.getString("chart.button.download.svg"));
			lang.setContextButtonTitle(bundle
					.getString("chart.button.context.title"));
			lang.setLoading(bundle.getString("chart.label.status.loading"));
			lang.setPrintChart(bundle.getString("chart.button.title.print"));
			lang.setResetZoom(bundle.getString("chart.button.reset.zoom"));
			lang.setResetZoomTitle(bundle
					.getString("chart.button.title.reset.zoom"));
		} catch (MissingResourceException ignored) {
		}

		return lang;
	}

	protected ResourceBundle getDefaultChartOptionsBundle() {
		return ResourceBundleLocator.getResourceBundle(DefaultChartOptions.class);
	}

	private static String getDecimalSeparator() {
		Locale locale = Session.get().getLocale();
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(
				locale);
		return String.valueOf(decimalFormatSymbols.getDecimalSeparator());
	}

	private static String getGroupingSeparator() {
		Locale locale = Session.get().getLocale();
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(
				locale);
		return String.valueOf(decimalFormatSymbols.getGroupingSeparator());
	}

	private static List<String> getLocalizedWeekDays() {
		Locale locale = Session.get().getLocale();
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		return Arrays.asList(dateFormatSymbols.getShortWeekdays());
	}

	private static List<String> getLocalizedMonthNames() {
		Locale locale = Session.get().getLocale();
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		return Arrays.asList(dateFormatSymbols.getShortMonths());

	}

}
