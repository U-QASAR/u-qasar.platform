package eu.uqasar.web.components.charts;

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
		} catch (MissingResourceException e) {
		}

		return lang;
	}

	protected ResourceBundle getDefaultChartOptionsBundle() {
		return ResourceBundleLocator.getResourceBundle(DefaultChartOptions.class);
	}

	protected static String getDecimalSeparator() {
		Locale locale = Session.get().getLocale();
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(
				locale);
		return String.valueOf(decimalFormatSymbols.getDecimalSeparator());
	}

	protected static String getGroupingSeparator() {
		Locale locale = Session.get().getLocale();
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(
				locale);
		return String.valueOf(decimalFormatSymbols.getGroupingSeparator());
	}

	protected static List<String> getLocalizedWeekDays() {
		Locale locale = Session.get().getLocale();
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		return Arrays.asList(dateFormatSymbols.getShortWeekdays());
	}

	protected static List<String> getLocalizedMonthNames() {
		Locale locale = Session.get().getLocale();
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		return Arrays.asList(dateFormatSymbols.getShortMonths());

	}

}
