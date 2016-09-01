package eu.uqasar.web.dashboard.widget.reportingwidget;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.json.JSONArray;
import org.json.JSONObject;

import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.Cursor;
import com.googlecode.wickedcharts.highcharts.options.DataLabels;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.PlotOptions;
import com.googlecode.wickedcharts.highcharts.options.PlotOptionsChoice;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.Tooltip;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.functions.PercentageAndValueFormatter;
import com.googlecode.wickedcharts.highcharts.options.functions.PercentageFormatter;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;

import eu.uqasar.model.measure.ReportMeasurement;
import eu.uqasar.util.reporting.Util;

public class ReportingWidget extends AbstractWidget {

    /**
	 * 
	 */
    private static final long serialVersionUID = -105357652483079696L;

    public static final String COLUMN_TYPE = "COLUMN";
    private static final String PIE_TYPE = "PIE";

    public static final List<String> TYPES = 
    		Arrays.asList(COLUMN_TYPE, PIE_TYPE);

    private static ReportingFactory chartDataFactory;

    private String cube_table;

    public String getCube_table() {
        return cube_table;
    }

    private void setCube_table(String cube_table) {
        this.cube_table = cube_table;
    }

    public ReportingWidget() {
        super();
        title = "Reporting Widget";
        cube_table = "";
    }

    @Override
    public void init() {
        this.setTitle("Reporting Widget");
        if (!settings.containsKey("chartType")) {
            settings.put("chartType", ReportingWidget.COLUMN_TYPE);
        }
    }

    @Override
    public WidgetView createView(String viewId) {
        return new ReportingWidgetView(viewId, new Model<Widget>(this));
    }

    public static ReportingFactory getChartDataFactory() {
        return chartDataFactory;
    }

    public static void setChartDataFactory(ReportingFactory chartDataFactory) {
        ReportingWidget.chartDataFactory = chartDataFactory;
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
        return new ReportingSettingsPanel(settingsPanelId, new Model<>(this));
    }

    /**
     * Get the measurements
     * 
     * @return
     */
    public List<ReportMeasurement> getMeasurements(String cube, String additionalUrlToLoad) {

        List<ReportMeasurement> measurements = new ArrayList<>();

        // generate cube Matrics
        if (cube != null) {

            String cubeurl = "http://uqasar.pythonanywhere.com/cube/" + cube.toLowerCase();
            if (additionalUrlToLoad != null) {

                String urlToLoad = cubeurl + additionalUrlToLoad;

                JSONObject cuberesponse = Util.readJsonFromUrl(urlToLoad);

                if (cuberesponse.has("error")) {
                    System.out.println("Exception during retrieval !");

                } else {
                    // finds the cells in the JSON response and returnds the Array of cells
                    JSONArray cuberesponse_arr = cuberesponse.getJSONArray("cells");

                    System.out.println("cuberesponse as cuberesponse_arr:");
                    System.out.println(cuberesponse_arr);

                    // if cells are not zero
                    if (cuberesponse_arr.length() > 0) {
                        List<String> facts = Util.retrieveDimensions(cubeurl + "/model");

                        // for cube table info.
                        cube_table = "<table frame=\"box\" cellpadding='10'>";
                        cube_table += "<tr>";
                        String reportType = "";
                        for (String fact : facts) {

                            if (cuberesponse_arr.optJSONObject(0).has(fact)) {
                                cube_table += "<th>" + fact + "</th>";
                                // set report Type
                                reportType += "-" + fact;
                            }

                        }
                        cube_table += "<th>count</th>";
                        cube_table += "</tr>";

                        // for all individual JSON cells..
                        for (int i = 0; i < cuberesponse_arr.length(); i++) {

                            ReportMeasurement rm = new ReportMeasurement();

                            cube_table += "<tr>";
                            String factid = "";

                            for (String fact : facts) {

                                if (cuberesponse_arr.optJSONObject(i).has(fact)) {

                                    // factid.add(cuberesponse_arr.getJSONObject(i).get(fact).toString());
                                    // factTemp.add(fact);
                                    factid += "-" + cuberesponse_arr.getJSONObject(i).get(fact).toString();
                                    cube_table += "<td>" + cuberesponse_arr.getJSONObject(i).get(fact).toString() + "</td>";
                                }

                            }

                            int countID = 0;
                            if (cuberesponse_arr.getJSONObject(i).has("count")) {
                                countID = cuberesponse_arr.getJSONObject(i).getInt("count");
                                cube_table += "<td>" + countID + "</td>";
                            }

                            cube_table += "</tr>";

                            if (factid != null) {

                                rm.setReportType(reportType);
                                rm.setReportValue(factid);
                                rm.setCount(countID);
                                measurements.add(rm);
                            }

                        } // End of for loop iteration.

                        cube_table += "</table>";

                        setCube_table(cube_table);

                    }
                }
            }

        } // End of if(cube != null)

        return measurements;
    }

    /**
     * 
     * @param
     * @return
     */
    public Options getChartOptions(List<ReportMeasurement> metrics, String chartType) {

        if (chartType.compareToIgnoreCase(SeriesType.COLUMN.toString()) == 0) {
            // ---------add COLUMN Chart ---------------
            Options options = new Options();
            ChartOptions chartOptions = new ChartOptions();
            SeriesType seriesType = SeriesType.COLUMN;
            chartOptions.setType(seriesType);
            Title chartTitle = new Title("Dynamic cubes query - COLUMN Chart View");
            options.setTitle(chartTitle);

            for (ReportMeasurement metric : metrics) {
                PointSeries series = new PointSeries();
                series.setType(seriesType);
                series.addPoint(new Point(metric.getReportValue(), metric.getCount()));
                series.setName(metric.getReportValue());
                options.addSeries(series);
            }
            options.setChartOptions(chartOptions);
            return options;
        } else {
            // ---------add PieChart ---------------
            Options pieOptions = new Options();

            pieOptions.setChartOptions(new ChartOptions().setPlotBackgroundColor(new NullColor()).setPlotBorderWidth(null)
                .setPlotShadow(Boolean.FALSE));

            pieOptions.setTitle(new Title("Dynamic cubes query - Pie Chart View"));

            pieOptions.setTooltip(new Tooltip().setFormatter(new PercentageFormatter()).setPercentageDecimals(1));

            pieOptions.setPlotOptions(new PlotOptionsChoice().setPie(new PlotOptions()
                .setAllowPointSelect(Boolean.TRUE)
                .setCursor(Cursor.POINTER)
                .setShowInLegend(Boolean.TRUE)
                .setDataLabels(
                    new DataLabels().setEnabled(Boolean.TRUE).setColor(new HexColor("#000000"))
                        .setConnectorColor(new HexColor("#000000")).setFormatter(new PercentageAndValueFormatter()))));

            // Collect Points for Series
            List<Point> pointList = new LinkedList<>();
            List<String> names = new LinkedList<>();
            for (ReportMeasurement metric : metrics) {
                pointList.add(new Point(metric.getReportValue(), metric.getCount()));
                names.add(metric.getReportType());
            }
            // Add all those points to one series
            PointSeries series = new PointSeries();
            series.setType(SeriesType.PIE);
            for (Point aPointList : pointList) {
                series.addPoint(aPointList);
            }
            pieOptions.addSeries(series);
            return pieOptions;
        }
    }

    public Map<String, List<String>> getRulesMap(List<String> projects) {

        Map<String, List<String>> rulesMap = new HashMap<>(); // map:rule->additionalRules

        // Add Rules and Additional Rules as DropDownList
        rulesMap.put("drillDown", Arrays.asList("Project", "Type", "Status", "Priority", "Resolution", "Assignee", "Reporter",
            "Creator", "Created", "Labels", "Affects_Versions", "Fix_Versions", "Components", "Epic_Link"));
        rulesMap.put("Project", projects);
        rulesMap.put("Type", Arrays.asList("Bug", "Epic", "New Feature", "Story", "Sub-Task", "Task"));
        rulesMap.put("Status", Arrays.asList("Closed", "In Progress", "Open", "Reopened", "Resolved"));
        rulesMap.put("Priority", Arrays.asList("Blocker", "Creteria", "Minor", "Major", "Trivial"));
        rulesMap.put("Resolution",
            Arrays.asList("Null", "Cannot Rep", "Done", "Duplicate", "Fixed", "Incomplete", "Open", "Won't Fix"));
        rulesMap
            .put("Assignee", Arrays.asList("ATB Team", "Contact", "Innopole", "Intrasof", "MTP Team", "Uqasar U", "Vaibmu"));
        rulesMap
            .put("Reporter", Arrays.asList("ATB Team", "Contact", "Innopole", "Intrasof", "MTP Team", "Uqasar U", "Vaibmu"));
        rulesMap.put("Creator", Arrays.asList("ATB Team", "Contact", "Innopole", "Intrasof", "MTP Team", "Uqasar U", "Vaibmu"));
        rulesMap.put("Created", Arrays.asList("2013-12-13", "2014-01-28", "2014-02-17"));
        rulesMap.put("Labels", Arrays.asList("context", "dashboard,widget", "deploy,,mysql,", "JIRA,,mysql,",
            "JIRA,Sonar,dashboard,widget", "Label1,Label2,Label3", "metric", "mysql", "QA_Project"));
        rulesMap.put("Affects_Versions", Arrays.asList("Early Prototype", "Early PrototypeFull Proto", "Full Prototype",
            "Integrated Platform", "Validation"));
        rulesMap.put("Fix_Versions", Arrays.asList("Early Prototype", "Full Prototype", "Integrated Platform"));
        rulesMap.put("Components", Arrays.asList("AnalyticalService,QualityProject",
            "AnalyticalService,QualityProjectAnalyticalServices,QualityModel", "AnalyticalServices,QualityModel",
            "enhancementServices", "MonitoringServices,CUBESWrapper", "MonitoringServices,JIRAWrapper",
            "MonitoringServices,SONARWrapper", "MonitoringServices,TESTLINKWrapper", "ReportingServices", "Team Building",
            "UtilityServices", "UX"));
        rulesMap.put("Epic_Link", Arrays.asList("Null", "UQ-127", "UQ-130", "UQ-155", "UQ-170", "UQ-220"));

        return rulesMap;
    }
}
