package eu.uqasar.web.dashboard.widget.projectqualitychart;

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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.AxisType;
import com.googlecode.wickedcharts.highcharts.options.Background;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.CssStyle;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Labels;
import com.googlecode.wickedcharts.highcharts.options.MinorTickInterval;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.Pane;
import com.googlecode.wickedcharts.highcharts.options.PlotBand;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.TickPosition;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.Tooltip;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.color.LinearGradient;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;

import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.historic.HistoricValuesProject;
import eu.uqasar.service.HistoricalDataService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;

/**
 * A sample widget built on WickedCharts 
 * <https://code.google.com/p/wicked-charts/> 
 * that in turn is based on the JavaScript library HighCharts.
 */
public class ProjectQualityChartWidget extends AbstractWidget{

	private static final String GAUGE_TYPE = "GAUGE";
	private static final String BAR_TYPE = "BAR";
	private static final String LINE_TYPE = "LINE";

	public static final List<String> TYPES = Arrays.asList(LINE_TYPE,
            BAR_TYPE,
            GAUGE_TYPE);

	private static ProjectQualityChartFactory chartDataFactory;	
	private static final long serialVersionUID = -2447400380886027022L;
	
	public ProjectQualityChartWidget() {
		super();
		title = "Project Quality";
	}

	@Override
	public void init() {
		if (!settings.containsKey("chartType")) {
			settings.put("chartType", ProjectQualityChartWidget.GAUGE_TYPE);
		}
	}

	public static ProjectQualityChartFactory getChartDataFactory() {
		return chartDataFactory;
	}

	public static void setChartDataFactory(ProjectQualityChartFactory chartDataFactory) {
		ProjectQualityChartWidget.chartDataFactory = chartDataFactory;
	}

	public String getChartData() {
		if (chartDataFactory == null) {
			throw new RuntimeException("ChartDataFactory cannot be null. Use ChartWidget.setChartDataFactory(...)");
		}

		return chartDataFactory.createChart(this);
	}

	public WidgetView createView(String viewId) {
		return new ProjectQualityChartWidgetView(viewId, new Model<Widget>(this));
	}

	@Override
	public boolean hasSettings() {
		return true;
	}

	@Override
	public Panel createSettingsPanel(String settingsPanelId) {
		return new ProjectQualityChartSettingsPanel(settingsPanelId, new Model<>(this));
	}

	/**
	 * 
	 * @return
	 */
	public Options getOptions() {
		
		TreeNodeService dataService = null;
		Project proj = null;
		Double value = 0.0;
		String projName = "";
		try {
			InitialContext ic = new InitialContext();
			dataService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");			
			// Obtain project from the settings
			if (settings.get("project") != null) {
				proj = dataService.getProjectByName(settings.get("project"));
			} else {
				if (dataService != null) {
					proj = dataService.getProjectByName("U-QASAR Platform Development");			
				}				
			}
			if (proj != null && proj.getValue() != null && !proj.getValue().isInfinite()) {
				value = UQasarUtil.round(proj.getValue());					
				projName = " for " +proj.getName();
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		// Check, whether the series type has been setup in the settings.
		SeriesType seriesType = SeriesType.GAUGE; 
		if (SeriesType.valueOf(settings.get("chartType")) != null) {
			seriesType = SeriesType.valueOf(settings.get("chartType"));
		}
		Options options = new Options();
		options.setChartOptions(new ChartOptions()
		.setType(seriesType)
		.setPlotBackgroundColor(new NullColor())
		.setPlotBackgroundImage(null)
		.setPlotBorderWidth(0)
		.setPlotShadow(Boolean.FALSE));

		options.setTitle(new Title("Current Project Quality"));

		options.setPane(new Pane()
		.setStartAngle(-150)
		.setEndAngle(150)
		.addBackground(new Background()
		.setBackgroundColor(new LinearGradient(0, 0, 0, 1)
		.addStop(0, HexColor.fromString("#FFFFFF"))
		.addStop(1, HexColor.fromString("#333333")))
		.setBorderWidth(0))
		.addBackground(new Background()
		.setBackgroundColor(new LinearGradient(0, 1, 0, 1)
		.addStop(0, HexColor.fromString("#333333"))
		.addStop(1, HexColor.fromString("#FFFFFF")))
		.setBorderWidth(1))
		.addBackground(Background.DEFAULT_BACKGROUND)
		.addBackground(new Background()
		.setBackgroundColor(HexColor.fromString("#DDDDDD"))
		.setBorderWidth(0)));

		options.addyAxis(new Axis()
		.setMin(0)
		.setMax(100)
		.setMinorTickInterval(new MinorTickInterval().setAuto(Boolean.TRUE))
		.setMinorTickWidth(1)
		.setMinorTickLength(10)
		.setMinorTickPosition(TickPosition.INSIDE)
		.setMinorTickColor(HexColor.fromString("#666666"))
		.setTickPixelInterval(30)
		.setTickWidth(2)
		.setTickPosition(TickPosition.INSIDE)
		.setTickLength(10)
		.setTickColor(HexColor.fromString("#666666"))
		.setLabels(new Labels().setStep(2))
		.setTitle(new Title("quality"))
		.addPlotBand(new PlotBand()
		.setFrom(0)
		.setTo(33)
		.setColor(HexColor.fromString("#DF5353")))
		.addPlotBand(new PlotBand()
		.setFrom(34)
		.setTo(67)
		.setColor(HexColor.fromString("#DDDF0D")))
		.addPlotBand(new PlotBand()
		.setFrom(68)
		.setTo(100)
		.setColor(HexColor.fromString("#55bf3b"))));

		options.addSeries(new SimpleSeries()
		.addPoint(value)
		.setName("Quality")
		.setTooltip(new Tooltip().setValueSuffix(" %")));

		return options;
	}	

	/**
	 * 
	 * @return
	 */
	public Options getOptionsForHistoricalChart() {
		
		TreeNodeService dataService = null;
		Project proj = null;
		try {
			InitialContext ic = new InitialContext();
			dataService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");			
			// Obtain project from the settings
			if (settings.get("project") != null) {
				proj = dataService.getProjectByName(settings.get("project"));
			} else {
				if (dataService != null) {
					proj = dataService.getProjectByName("U-QASAR Platform Development");			
				}				
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		
		Options options = new Options();
		ChartOptions chartOptions =  new ChartOptions();

		// Obtain the historic values for the project
		List<HistoricValuesProject> projectHistoricvalues = getHistoricalValues(proj);
		Collections.sort(projectHistoricvalues);
		
		
		SeriesType seriesType = SeriesType.LINE; 
		chartOptions.setType(seriesType);    
		options.setTitle(new Title("Historical Project Quality"));
		
		PointSeries series = new PointSeries();	
		series.setType(seriesType);

		List<String> xAxisLabels = new ArrayList<>();

		for (HistoricValuesProject historicValue : projectHistoricvalues) {
			float value = historicValue.getValue();
			System.out.println("Value: " +value);
			series.addPoint(new Point(proj.getAbbreviatedName(), value));			

			// xAxis Label
			Date date = historicValue.getDate();
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			xAxisLabels.add(dt1.format(date));
		}
		
		// Date on xAxis
		Axis xAxis = new Axis();
		xAxis.setType(AxisType.DATETIME);
		xAxis.setCategories(xAxisLabels);
		xAxis.setLabels(new Labels()
	            .setRotation(-60)
	            .setAlign(HorizontalAlignment.RIGHT)
	            .setStyle(new CssStyle()
	                .setProperty("font-size", "9px")
	                .setProperty("font-family", "Verdana, sans-serif")));
		options.setxAxis(xAxis);	
		
		options.addyAxis(new Axis()
		.setMin(0)
		.setMax(100));
		
		options.addSeries(series);
		options.setChartOptions(chartOptions);
		
		return options;
	}	

	
	private List<HistoricValuesProject> getHistoricalValues(Project proj) {
		HistoricalDataService historicalDataService = null;
		List<HistoricValuesProject> histValues = new ArrayList<>();
		if (proj != null) {
			try {
				InitialContext ic = new InitialContext();
				historicalDataService = (HistoricalDataService) ic.lookup("java:module/HistoricalDataService");
				int valuesCount = historicalDataService.countHistValuesForProject(proj.getId());
				histValues  = historicalDataService.getAllHistValuesForProject(proj.getId(), 0, valuesCount); 
				
			} catch (NamingException e) {
				e.printStackTrace();
			}			
		}

		return histValues;
	}
}
