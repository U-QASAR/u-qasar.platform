package eu.uqasar.web.dashboard.widget.jenkins;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.model.IModel;
import org.json.JSONArray;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.AxisType;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.CssStyle;
import com.googlecode.wickedcharts.highcharts.options.Cursor;
import com.googlecode.wickedcharts.highcharts.options.Labels;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.PlotOptions;
import com.googlecode.wickedcharts.highcharts.options.PlotOptionsChoice;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.VerticalAlignment;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.color.NullColor;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;
import com.googlecode.wickedcharts.highcharts.options.series.Series;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import eu.uqasar.model.measure.JenkinsMetricMeasurement;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.dataadapter.JenkinsDataService;
import eu.uqasar.service.tree.TreeNodeService;

/**
 * 
 *
 *
 */
public class JenkinsWidgetView extends WidgetView {

	private static final long serialVersionUID = 7504539323135550980L;
	private String metricName, projectName;
	private Project project;
	private final JenkinsMetricMeasurement lastBuildSuccess;
	private final Map<Number, String> last100Builds;
	private final JenkinsWidget widget;
	private List<Entry<Number, String>> sortedLast100Builds;
	
    public JenkinsWidgetView(String id, IModel<Widget> model) {
        super(id, model);

        String noOfItemsAsStr = getModelObject().getSettings().get("noOfItems");
        if (noOfItemsAsStr == null) {
            noOfItemsAsStr = "10";
        }
        widget = (JenkinsWidget) model.getObject();

        add(new ContextImage("logo", "assets/img/jenkins_logo.jpg"));

        if (widget.getSettings().get("project") != null) {
            projectName = widget.getSettings().get("project");
            if (projectName.equals("uqasar")) {
                projectName = "U-QASAR Platform Development";
            }
            project = getProject(projectName);
            if (project == null) {
                project = getProject("U-QASAR Platform Development");
            }

        } else {
            projectName = "U-QASAR Platform Development";
            project = getProject(projectName);
        }

        metricName = widget.getSettings().get("metric");
        if (metricName == null) {
            metricName = "JENKINS_LATEST_BUILD_SUCCESS";
        }

        // get JENKINS_LATEST_BUILD_SUCCESS
        lastBuildSuccess = getLastBuildSuccessStatus(project);

        // FIXMED && TODO: get build history
        last100Builds = getLast100Builds(project);
        
        // sort Map by keys Descending 
        if (last100Builds != null && last100Builds.size() != 0) {
            // sortedLast100Builds = new TreeMap<Number, String>(last100Builds);
            Set<Entry<Number, String>> entrySet = last100Builds.entrySet();
            sortedLast100Builds = new ArrayList<>(entrySet);
            Collections.sort(sortedLast100Builds, new SortByKeyDescending());
        }

        // Option: JENKINS_LATEST_BUILD_SUCCESS
        int noOfItemsInt = Integer.parseInt(noOfItemsAsStr);
        if (metricName.equals("JENKINS_LATEST_BUILD_SUCCESS") && sortedLast100Builds != null) {
            Chart chart = new Chart("chart", getTrafficLightOptions());
            add(chart);

            // TODO: Option: JENKINS_BUILD_HISTORY
        } else if (metricName.equals("JENKINS_BUILD_HISTORY") && sortedLast100Builds != null) {
            Chart chart = new Chart("chart", getBuildHistoryOptions(noOfItemsInt));
            add(chart);

        } else { // TODO: if no adapter data are there on the platform, Now its an empty chart
            Chart chart = new Chart("chart", getOptions());
            add(chart);
        }
    }
	
	

	private Options getTrafficLightOptions(){
		Options options = new Options();
		if(lastBuildSuccess != null){
		    HexColor color = getTrafficLightColor(lastBuildSuccess.getValue());
		
    		options.setChartOptions(new ChartOptions()
    	        .setPlotBackgroundColor(new NullColor())
    	        .setPlotBorderWidth(null)
    	        .setHeight(250)
    	        .setPlotShadow(Boolean.FALSE));
    		options.setTitle(new Title("Latest Build Status"));
    		options.setSubtitle(new Title(projectName));
    		
    		options.setPlotOptions(new PlotOptionsChoice()
    	        .setPie(new PlotOptions()
    	            .setAllowPointSelect(Boolean.FALSE)
    	            .setBorderWidth(0) // to make it look like a "traffic light"
    	            .setCursor(Cursor.POINTER)));
     
    		options.addSeries(new PointSeries()
    	        .setType(SeriesType.PIE)
    	        .addPoint(new Point(lastBuildSuccess.getValue(), 100).setColor(color)));
		}
		return options;
	}
	
	private HexColor getTrafficLightColor(String status) {
		//use hex color values from bootstrap in order to match the layouting
		if(status.toLowerCase().equals("stable")){
			return new HexColor("#41bb19");
		} else if(status.toLowerCase().equals("unstable")){
			return new HexColor("#ffe314");
		} else if(status.toLowerCase().equals("broken")){ 
			return new HexColor("#f50f43");
		}
		// gray for unknown
		else {
			return new HexColor("#445");
		}
	}

	private Options getBuildHistoryOptions(int limitingNumber){
        Options options = new Options();
        String title = "Build History";
        options.setChartOptions(new ChartOptions().setPlotBackgroundColor(new NullColor()).setPlotBorderWidth(null)
            .setHeight(250).setPlotShadow(Boolean.FALSE));
        options.setTitle(new Title(title));
        options.setSubtitle(new Title(projectName));
        options.setPlotOptions(new PlotOptionsChoice().setPie(new PlotOptions().setAllowPointSelect(Boolean.FALSE).setCursor(
            Cursor.POINTER)));

        Series<Number> history = new SimpleSeries();
        history.setType(SeriesType.AREA);
        history.setName(title);
        // TODO:
        // y-axis: load BuildStatus from last100Builds here --> PLEASE OPTIMIZE HERE!
        // add the correct labels on the axes (0 = stable, 1=unstable, 2=broken, 3=Unknown
        // add the correct BuildNumber on the x-axis (not 0-99, but 634-734)
        List<Number> data = new ArrayList<>();
        List<String> yAxisLabels = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();
        
        int counter = 0;
         
        for (Map.Entry<Number, String> e : sortedLast100Builds) {
            if(counter <= limitingNumber){      //make sure the counter is smaller than limiting number
                if (e.getValue().toLowerCase().equals("stable")) {
                     data.add(0);
                } else if (e.getValue().toLowerCase().equals("unstable")) {
                     data.add(1);
                } else if (e.getValue().toLowerCase().equals("broken")) {
                     data.add(2);
                } else {
                     data.add(3);
                }
                 xAxisLabels.add(String.valueOf(e.getKey()));
                 counter++;
            }
        }
        history.setData(data);

        

        // Numbers on xAxis
        Axis xAxis = new Axis();
        xAxis.setType(AxisType.DATETIME);
        xAxis.setCategories(xAxisLabels);
        xAxis.setLabels(new Labels().setVerticalAlign(VerticalAlignment.BOTTOM)
            .setStyle(new CssStyle().setProperty("font-size", "10px").setProperty("font-family", "Verdana, sans-serif")));
        options.setxAxis(xAxis);

        // Labels as String on yAxis
        yAxisLabels.add("stable");
        yAxisLabels.add("unstable");
        yAxisLabels.add("broken");
        yAxisLabels.add("out-of-scope");
        Axis yAxis = new Axis();
        yAxis.setType(AxisType.DATETIME);
        yAxis.setCategories(yAxisLabels);
        yAxis.setLabels(new Labels().setVerticalAlign(VerticalAlignment.BOTTOM)
            .setStyle(new CssStyle().setProperty("font-size", "10px").setProperty("font-family", "Verdana, sans-serif")));
        options.setyAxis(yAxis);
        
         options.addSeries(history);

        return options;
	}
	
	private Options getOptions(){
        return new Options();
	}
	
	private JenkinsMetricMeasurement getLastBuildSuccessStatus(Project project){
		JenkinsDataService jenkinsDataService = null;
		try {
			InitialContext ic = new InitialContext();
			jenkinsDataService = (JenkinsDataService) ic.lookup("java:module/JenkinsDataService");
			List<JenkinsMetricMeasurement> allJenkinsMetricObjects = jenkinsDataService.getAllJenkinsMetricObjects();
			for(JenkinsMetricMeasurement m : allJenkinsMetricObjects){
				if(project.equals(m.getProject()) 
						&& m.getJenkinsMetric().equals("JENKINS_LATEST_BUILD_SUCCESS")){
					return m;
				}
				
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Map<Number, String> getLast100Builds(Project project2) {
		JenkinsDataService jenkinsDataService = null;
		Map<Number, String> last100 = new ConcurrentHashMap<>();		
		try {
			InitialContext ic = new InitialContext();
			jenkinsDataService = (JenkinsDataService) ic.lookup("java:module/JenkinsDataService");
			List<JenkinsMetricMeasurement> allJenkinsMetricObjects = jenkinsDataService.getAllJenkinsMetricObjects();
			for(JenkinsMetricMeasurement m : allJenkinsMetricObjects){
				if(project.equals(m.getProject()) 
						&& m.getJenkinsMetric().equals("JENKINS_BUILD_HISTORY")){
		
					JSONArray json = new JSONArray(m.getValue());					
					for (int i = 0; i < json.length()-1; i++) {
						last100.put(Integer.valueOf(json.getJSONObject(i).getString("BuildNumber")), json.getJSONObject(i).getString("BuildStatus")); // 
					}
					return last100;
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Project getProject(String projName) {
		Project pro = null;
		if (projName == null || projName.isEmpty()) {
			projName = "U-QASAR Platform Development";
		}
		TreeNodeService treeNodeService = null;
		try {
			InitialContext ic = new InitialContext();
			treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
			pro = treeNodeService.getProjectByName(projName);		
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return pro;
	}
	
	private class SortByKeyDescending implements Comparator<Map.Entry<Number, String>>{
	    
	    @Override
	    public int compare( Map.Entry<Number,String> entry1, Map.Entry<Number,String> entry2){
	         return ((Integer)entry2.getKey()).compareTo( (Integer)entry1.getKey() );
	    }
 
	 }
}