package com.googlecode.wickedcharts.highcharts.options;

import java.io.Serializable;
import java.util.List;

/**
 * Defines the configuration of the "lang" option.
 * 
 * WARNING: this class was manually added in order to overwrite the class 
 * provided by the current (as of 2013-11-01) version to be
 * compliant with the latest 'lang' object from highcharts.
 * 
 * @see <a
 *      href="http://api.highcharts.com/highcharts#lang">http://api.highcharts.com/highcharts#lang</a>
 * 
 */
public class LanguageOptions implements Serializable {

	private static final long serialVersionUID = 1L;

	private String contextButtonTitle;

	private String decimalPoint;

	private String downloadJPEG;

	private String downloadPDF;

	private String downloadPNG;

	private String downloadSVG;

	private String loading;

	private List<String> months;

	private String printChart;

	private String resetZoom;

	private String resetZoomTitle;

	private List<String> shortMonths;

	private String thousandsSep;

	private List<String> weekdays;

	public String getDecimalPoint() {
		return this.decimalPoint;
	}

	public String getDownloadJPEG() {
		return this.downloadJPEG;
	}

	public String getDownloadPDF() {
		return this.downloadPDF;
	}

	public String getDownloadPNG() {
		return this.downloadPNG;
	}

	public String getDownloadSVG() {
		return this.downloadSVG;
	}

	public String getContextButtonTitle() {
		return this.contextButtonTitle;
	}

	public String getLoading() {
		return this.loading;
	}

	public List<String> getMonths() {
		return this.months;
	}

	public String getPrintChart() {
		return this.printChart;
	}

	public String getResetZoom() {
		return this.resetZoom;
	}

	public String getResetZoomTitle() {
		return this.resetZoomTitle;
	}

	public List<String> getShortMonths() {
		return this.shortMonths;
	}

	public String getThousandsSep() {
		return this.thousandsSep;
	}

	public List<String> getWeekdays() {
		return this.weekdays;
	}

	public LanguageOptions setDecimalPoint(final String decimalPoint) {
		this.decimalPoint = decimalPoint;
		return this;
	}

	public LanguageOptions setDownloadJPEG(final String downloadJPEG) {
		this.downloadJPEG = downloadJPEG;
		return this;
	}

	public LanguageOptions setDownloadPDF(final String downloadPDF) {
		this.downloadPDF = downloadPDF;
		return this;
	}

	public LanguageOptions setDownloadPNG(final String downloadPNG) {
		this.downloadPNG = downloadPNG;
		return this;
	}

	public LanguageOptions setDownloadSVG(final String downloadSVG) {
		this.downloadSVG = downloadSVG;
		return this;
	}

	public LanguageOptions setContextButtonTitle(final String contextButtonTitle) {
		this.contextButtonTitle = contextButtonTitle;
		return this;
	}

	public LanguageOptions setLoading(final String loading) {
		this.loading = loading;
		return this;
	}

	public LanguageOptions setMonths(final List<String> months) {
		this.months = months;
		return this;
	}

	public LanguageOptions setPrintChart(final String printChart) {
		this.printChart = printChart;
		return this;
	}

	public LanguageOptions setResetZoom(final String resetZoom) {
		this.resetZoom = resetZoom;
		return this;
	}

	public LanguageOptions setResetZoomTitle(final String resetZoomTitle) {
		this.resetZoomTitle = resetZoomTitle;
		return this;
	}

	public LanguageOptions setShortMonths(final List<String> shortMonths) {
		this.shortMonths = shortMonths;
		return this;
	}

	public LanguageOptions setThousandsSep(final String thousandsSep) {
		this.thousandsSep = thousandsSep;
		return this;
	}

	public LanguageOptions setWeekdays(final List<String> weekdays) {
		this.weekdays = weekdays;
		return this;
	}

}
