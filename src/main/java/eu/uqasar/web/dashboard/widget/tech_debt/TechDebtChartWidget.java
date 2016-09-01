package eu.uqasar.web.dashboard.widget.tech_debt;

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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
//import org.wicketstuff.googlecharts.AbstractChartData;
//import org.wicketstuff.googlecharts.Chart;
//import org.wicketstuff.googlecharts.ChartAxis;
//import org.wicketstuff.googlecharts.ChartAxisType;
//import org.wicketstuff.googlecharts.ChartDataEncoding;
//import org.wicketstuff.googlecharts.ChartProvider;
//import org.wicketstuff.googlecharts.ChartType;
//import org.wicketstuff.googlecharts.IChartGrid;

import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.CreditOptions;
import com.googlecode.wickedcharts.highcharts.options.Cursor;
import com.googlecode.wickedcharts.highcharts.options.DataLabels;
import com.googlecode.wickedcharts.highcharts.options.Function;
import com.googlecode.wickedcharts.highcharts.options.Global;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Labels;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.LegendLayout;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.Overflow;
import com.googlecode.wickedcharts.highcharts.options.PlotOptions;
import com.googlecode.wickedcharts.highcharts.options.PlotOptionsChoice;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.Tooltip;
import com.googlecode.wickedcharts.highcharts.options.VerticalAlignment;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.functions.PercentageAndValueFormatter;
import com.googlecode.wickedcharts.highcharts.options.functions.PercentageFormatter;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;

import eu.uqasar.model.measure.JiraMetricMeasurement;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.tree.TreeNodeService;

/**
 *  
 */
public class TechDebtChartWidget extends AbstractWidget{

	/**
	 * 
	 */
	private static final long serialVersionUID = 376923874435436400L;
	private static final String BAR_TYPE = "BAR";
	private static final String PIE_TYPE = "PIE";

	public static final List<String> TYPES = Arrays.asList(PIE_TYPE,
            BAR_TYPE);

	private static TechDebtChartFactory chartDataFactory;	

	public TechDebtChartWidget() {
		super();
		title = "Technical Debt Overview";
	}

	@Override
	public void init() {
		if (!settings.containsKey("chartType")) {
			settings.put("chartType", TechDebtChartWidget.BAR_TYPE);
		}
	}

	public static TechDebtChartFactory getChartDataFactory() {
		return chartDataFactory;
	}

	public static void setChartDataFactory(TechDebtChartFactory chartDataFactory) {
		TechDebtChartWidget.chartDataFactory = chartDataFactory;
	}

	public String getChartData() {
		if (chartDataFactory == null) {
			throw new RuntimeException("ChartDataFactory cannot be null. Use ChartWidget.setChartDataFactory(...)");
		}

		return chartDataFactory.createChart(this);
	}

	public WidgetView createView(String viewId) {
		return new TechDebtChartWidgetView(viewId, new Model<Widget>(this));
	}

	@Override
	public boolean hasSettings() {
		return true;
	}

	@Override
	public Panel createSettingsPanel(String settingsPanelId) {
		return new TechDebtChartSettingsPanel(settingsPanelId, new Model<>(this));
	}


	/*
	public Chart createChart() {


		AbstractChartData data = new AbstractChartData(ChartDataEncoding.TEXT, 100) {

			private static final long serialVersionUID = 1L;
			double met1 = 0; // Critical Issues
			double met2 = 0; // Blocking Issues
			double met3 = 0; // Resolved Issues

			public double[][] getData() {

				
				try {
					Project project = null;
					InitialContext ic = new InitialContext();
					TreeNodeService dataService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");			
					// Obtain project from the settings
					if (settings.get("project") != null) {
						project = dataService.getProjectByName(settings.get("project"));
					} else {
						if (dataService != null) {
							project = dataService.getProjectByName("U-QASAR Platform Development");			
						}				
					}

					Iterator itr = project.getJirameasurements().iterator();

					while (itr.hasNext()) {
						JiraMetricMeasurement jmeas = (JiraMetricMeasurement) itr.next();
						String metric = jmeas.getJiraMetric();
						if (metric.equalsIgnoreCase("UNRESOLVED_TASK_ISSUES_PER_PROJECT"))
							met1++;
						if (metric.equalsIgnoreCase("UNRESOLVED_ISSUES_PER_PROJECT"))
							met2++;
						if (metric.equalsIgnoreCase("FIXED_ISSUES_PER_PROJECT"))
							met3++;
					}// while

				} catch (NamingException e) {
					e.printStackTrace();
				}
				
				System.out.println("met1 (unresolved task issues): " +met1);
				System.out.println("met2 (unresolved issues): " +met2);
				System.out.println("met3 (fixed issues): " +met3);

				return new double[][]{{met1}, {met2}, {met3}};
			}
		};

		ChartProvider provider = new ChartProvider(new Dimension(300, 150), ChartType.BAR_HORIZONTAL_GROUP, data);
		provider.setLegend(new String[]{"Unresolved Task Issues", "Unresolved Issues", "Fixed Issues"});
		provider.setColors(new java.awt.Color[]{java.awt.Color.RED, java.awt.Color.GREEN, java.awt.Color.BLUE});
		provider.setBarGroupSpacing(0);
		provider.setTitle("Technical Debt");
        ChartAxis axis2 = new ChartAxis(ChartAxisType.BOTTOM);
        axis2.setPositions(new double[]{0, 50, 100});
        provider.addAxis(axis2);
                
        Chart chart = new org.wicketstuff.googlecharts.Chart("jiratdChart", provider);
		return chart;
	}
*/
	/**
	 * 
	 * @return
	 */
	public Options getOptions() {

		TreeNodeService dataService = null;
		Project project = null;

		try {
			InitialContext ic = new InitialContext();
			dataService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");			
			// Obtain project from the settings
			if (settings.get("project") != null) {
				project = dataService.getProjectByName(settings.get("project"));
			} else {
				if (dataService != null) {
					project = dataService.getProjectByName("U-QASAR Platform Development");			
				}				
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		if (project == null)  {
			return new Options();
		}

		// Use bar chart as default, but attempt to get the value from the settings
		SeriesType seriesType = SeriesType.BAR; 
		if (SeriesType.valueOf(settings.get("chartType")) != null) {
			seriesType = SeriesType.valueOf(settings.get("chartType"));
		}

		Iterator itr = project.getJirameasurements().iterator();
		int met1 = 0; // Critical Issues
		int met2 = 0; // Blocking Issues
		int met3 = 0; // Resolved Issues

		while (itr.hasNext()) {
			JiraMetricMeasurement jmeas = (JiraMetricMeasurement) itr.next();
			String metric = jmeas.getJiraMetric();
			if (metric.equalsIgnoreCase("UNRESOLVED_TASK_ISSUES_PER_PROJECT"))
				met1++;
			if (metric.equalsIgnoreCase("UNRESOLVED_ISSUES_PER_PROJECT"))
				met2++;
			if (metric.equalsIgnoreCase("FIXED_ISSUES_PER_PROJECT"))
				met3++;
		}// while


		// Bar chart
		if (seriesType.equals(SeriesType.BAR)) {
			// --------------- Add Bar Chart for Issues classification ---------------
			Options barOptions = new Options();
			barOptions.setChartOptions(new ChartOptions().setType(SeriesType.BAR));
			barOptions.setGlobal(new Global().setUseUTC(Boolean.TRUE));
			barOptions.setTitle(new Title("Technical Dept Overview - Absolute View"));

			List<String> barcategories = new ArrayList();
			barcategories.add("Category of Issues");
			barOptions.setxAxis(new Axis().setCategories(barcategories).setTitle(
					new Title(null)));

			barOptions.setyAxis(new Axis().setTitle(
					new Title("Number of issues")
					.setAlign(HorizontalAlignment.HIGH)).setLabels(
							new Labels().setOverflow(Overflow.JUSTIFY)));

			barOptions.setTooltip(new Tooltip().setFormatter(new Function(
					"return ''+this.series.name +': '+ this.y;")));

			barOptions.setPlotOptions(new PlotOptionsChoice()
			.setBar(new PlotOptions().setDataLabels(new DataLabels()
			.setEnabled(Boolean.TRUE))));

			barOptions.setLegend(new Legend().setLayout(LegendLayout.VERTICAL)
					.setAlign(HorizontalAlignment.RIGHT)
					.setVerticalAlign(VerticalAlignment.TOP).setX(-50).setY(180)
					.setFloating(Boolean.TRUE).setBorderWidth(1)
					.setBackgroundColor(new HexColor("#ffffff"))
					.setShadow(Boolean.TRUE));

			barOptions.setCredits(new CreditOptions().setEnabled(Boolean.FALSE));

			List<Number> numlist1 = new ArrayList();
			numlist1.add(met1);

			barOptions.addSeries(new SimpleSeries().setName("Blocking Issues")
					.setData(met1));

			barOptions.addSeries(new SimpleSeries().setName("Critical Issues")
					.setData(met2));

			barOptions.addSeries(new SimpleSeries().setName("Resolved Issues")
					.setData(met3));
			return barOptions;
		}

		// ---------add PieChart ---------------
		Options pieOptions = new Options();

		pieOptions.setChartOptions(new ChartOptions()
		.setPlotBackgroundColor(new NullColor())
		.setPlotBorderWidth(null).setPlotShadow(Boolean.FALSE));

		pieOptions.setTitle(new Title(
				"Technical Dept Overview - Normalized View"));

		pieOptions.setTooltip(new Tooltip().setFormatter(
				new PercentageFormatter()).setPercentageDecimals(1));

		pieOptions
		.setPlotOptions(new PlotOptionsChoice()
		.setPie(new PlotOptions()
		.setAllowPointSelect(Boolean.TRUE)
		.setCursor(Cursor.POINTER)
		.setShowInLegend(Boolean.TRUE)
		.setDataLabels(
				new DataLabels()
				.setEnabled(Boolean.TRUE)
				.setColor(
						new HexColor("#000000"))
						.setConnectorColor(
								new HexColor("#000000"))
								.setFormatter(
										new PercentageAndValueFormatter()))));

		pieOptions.addSeries(new PointSeries()
		.setType(SeriesType.PIE)
		.setName("Issues")
		.addPoint(new Point("Blocking Issues", met1))
		.addPoint(new Point("Critical Issues", met2))
		.addPoint(
				new Point("Resolved Issues", met3).setSliced(
						Boolean.TRUE).setSelected(Boolean.TRUE)));


		return pieOptions;
	}	
}
