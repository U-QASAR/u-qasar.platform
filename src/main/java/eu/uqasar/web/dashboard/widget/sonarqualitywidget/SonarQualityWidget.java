package eu.uqasar.web.dashboard.widget.sonarqualitywidget;

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
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;

import eu.uqasar.model.measure.SonarMetricMeasurement;
import eu.uqasar.service.PlatformSettingsService;
import eu.uqasar.service.dataadapter.SonarDataService;
import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

/**
 * A sample widget fetching sample data from a Sonar instance. 
 * The used Sonar installation can be changed in the settings. 
 *
 */
public class SonarQualityWidget extends AbstractWidget {

	/**
	 * 
	 */
	private static final long serialVersionUID = -105357652483079696L;
	private String project; 
	private static SonarQualityFactory chartDataFactory;

	public SonarQualityWidget() {
		super();
		title = "Source code quality";
	}
	
	@Override
	public void init() {
		
		try {
			InitialContext ic = new InitialContext();
			PlatformSettingsService service = (PlatformSettingsService) ic.lookup("java:module/PlatformSettingsService");
			project = service.getValueByKey("sonar_project");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
		if (!settings.containsKey("project")) {
			settings.put("project", "U-QASAR");
		}
		this.setTitle("Source code quality");
	}

	@Override
	public WidgetView createView(String viewId) {
		return new SonarQualityWidgetView(viewId, new Model<Widget>(this));
	}
	
	public static SonarQualityFactory getChartDataFactory() {
		return chartDataFactory;
	}

	public static void setChartDataFactory(SonarQualityFactory chartDataFactory) {
		SonarQualityWidget.chartDataFactory = chartDataFactory;
	}

	public String getChartData() {
		if (chartDataFactory == null) {
			throw new RuntimeException("ChartDataFactory cannot be null. Use ChartWidget.setChartDataFactory(...)");
		}

		return chartDataFactory.createChart(this);
	}
	
	@Override
	public boolean hasSettings() {
		return true;
	}
	

	@Override
	public Panel createSettingsPanel(String settingsPanelId) {
		return new SonarQualityWidgetSettingsPanel(settingsPanelId,
                new Model<>(this));
	}

	
	/**
	 * Get the measurements
	 * @return
	 */
	public List<SonarMetricMeasurement> getMeasurements(String period) {

		List<SonarMetricMeasurement> measurements = new ArrayList<>();

		if (settings.get("project") != null) {
			project = settings.get("project");
		}
		
		try {
			InitialContext ic = new InitialContext();
			SonarDataService dataService = (SonarDataService) ic.lookup("java:module/SonarDataService");
			
			Date latestSnapshotDate = dataService.getLatestDate();
			if (latestSnapshotDate != null) {
			    if(period.compareToIgnoreCase("Latest") == 0){
			        measurements = dataService.getLatestMeasurementByProject(project);
			    }else{
			        measurements = dataService.getMeasurementsForProjectByPeriod(project, period);
			    }
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return measurements;
	}
	
	
	/**
	 * 
	 * @param 
	 * @return
	 */
	public Options getChartOptions(List<SonarMetricMeasurement> metrics ){			
			
			String group;
			if (settings.get("metric") == null) {
				group = "Code Lines related";
			} else {
				group = settings.get("metric");
			}
			
			// get latest
			List<SonarMetricMeasurement> latestMetrics = getLatestMetricDataSet(metrics);
			List<SonarMetricMeasurement> metricGroup = getMetricsForGroup(group, latestMetrics);
		
			Options options = new Options();
			ChartOptions chartOptions =  new ChartOptions();
			SeriesType seriesType = SeriesType.COLUMN; 
			chartOptions.setType(seriesType);    
			Title chartTitle = new Title(title + " of " + group + " Metrics");
			options.setTitle(chartTitle);

			for(SonarMetricMeasurement metric: metricGroup){				
				PointSeries series = new PointSeries();	
				series.setType(seriesType);
				series.addPoint(new Point(metric.getSonarMetric(), new Double(metric.getValue())));
				series.setName(metric.getSonarMetric());
				options.addSeries(series);
			}
			options.setChartOptions(chartOptions);	
			
			return options;
	}
	
	
    public Options getChartOptionsDifferently(List<SonarMetricMeasurement> metrics,String individualMetric) {

        String group;
        if (settings.get("metric") == null) {
            group = "Code Lines related";
        } else {
            group = settings.get("metric");
        }

        // get latest
        //List<SonarMetricMeasurement> latestMetrics = getLatestMetricDataSet(metrics);
       // List<SonarMetricMeasurement> metricGroup = getMetricsForGroup(group, latestMetrics);

        Options options = new Options();
        ChartOptions chartOptions = new ChartOptions();
        SeriesType seriesType = SeriesType.COLUMN;
        chartOptions.setType(seriesType);
        Title chartTitle = new Title(title + " of " + group + " Metrics");
        options.setTitle(chartTitle);

        for (SonarMetricMeasurement metric : metrics) {
            
            if(metric.getSonarMetric().compareToIgnoreCase(individualMetric) == 0){
            PointSeries series = new PointSeries();
            series.setType(seriesType);
            series.addPoint(new Point(metric.getSonarMetric(), new Double(metric.getValue())));
            series.setName(metric.getSonarMetric());
            options.addSeries(series);
            }
        }
        options.setChartOptions(chartOptions);

        return options;
    }

	
	

	private List<SonarMetricMeasurement> getLatestMetricDataSet(List<SonarMetricMeasurement> metrics) {
		// incoming metrics are already sorted by timestamp descending (newest at beginning)
		List<SonarMetricMeasurement> latestMetrics = new ArrayList<>();
		if (metrics != null && !metrics.isEmpty()) {
            for (SonarMetricMeasurement metric : metrics) {
                latestMetrics.add(metric);
            }
		}
		return latestMetrics;
	}

	private List<SonarMetricMeasurement> getMetricsForGroup(String group, List<SonarMetricMeasurement> metrics) {

		List<SonarMetricMeasurement> 	lines = new ArrayList<>(),
										complex = new ArrayList<>(),
										struct = new ArrayList<>(),
										density = new ArrayList<>(),
										test = new ArrayList<>();
		
		if (metrics != null && !metrics.isEmpty()) {
            switch (group) {
                case "Code Lines related":
                    for (SonarMetricMeasurement m : metrics) {
                        if (m.getSonarMetric().contains("LINE") && !m.getSonarMetric().contains("DENSITY") || m.getSonarMetric().equals("NCLOC") || m.getSonarMetric().equals("STATEMENTS")) {
                            lines.add(m);
                        }
                    }
                    return lines;
                case "Complexity related":
                    for (SonarMetricMeasurement m : metrics) {
                        if (m.getSonarMetric().contains("COMPLEXITY") && !m.getSonarMetric().contains("DENSITY")) {
                            complex.add(m);
                        }
                    }
                    return complex;
                case "Test related":
                    for (SonarMetricMeasurement m : metrics) {
                        if (m.getSonarMetric().contains("TEST") && !m.getSonarMetric().contains("DENSITY")) {
                            test.add(m);
                        }
                    }
                    return test;
                case "Density related":
                    for (SonarMetricMeasurement m : metrics) {
                        if (m.getSonarMetric().contains("DENSITY")) {
                            System.out.println(m.getSonarMetric().contains("DENSITY"));
                            System.out.println(m.getSonarMetric());
                            density.add(m);
                        }
                    }
                    return density;
                default:
                    for (SonarMetricMeasurement m : metrics) {
                        if (!m.getSonarMetric().contains("LINE") &&
                                !m.getSonarMetric().contains("COMPLEXITY") &&
                                !m.getSonarMetric().contains("TEST") &&
                                !m.getSonarMetric().contains("DENSITY") &&
                                !m.getSonarMetric().equals("NCLOC") &&
                                !m.getSonarMetric().equals("STATEMENTS")) {
                            struct.add(m);
                        }
                    }
                    return struct;
            }
		}
		return new ArrayList<>();
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	private List<SonarMetricMeasurement> removeDuplicates(List<SonarMetricMeasurement> list){
		Set<SonarMetricMeasurement> set = new LinkedHashSet<>(list);
		list.clear();
		list.addAll(set);
		return list;
	}	
}
