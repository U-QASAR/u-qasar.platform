package eu.uqasar.web.dashboard.widget.datadeviation;

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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;

import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.AxisType;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.CssStyle;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Labels;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;

import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;
import eu.uqasar.service.HistoricalDataService;
import eu.uqasar.service.tree.TreeNodeService;

/**
 * 
 *
 *
 */
public class DataDeviationWidget extends AbstractWidget{

	private final Project project;
	private static DataDeviationFactory chartDataFactory;	
	private static final long serialVersionUID = -2447400380886027022L;
	private Title chartTitle = new Title();	
		
	public DataDeviationWidget() {
		super();
		title = "Data Deviation Visualization";	
		project = getProject(settings.get("project"));
	}

	@Override
	public void init() {
		List<List<String>> list = recreateAllProjectTreeChildrenForProject(project);
		
		if (list != null) {
			if(!settings.containsKey("qualityParams")){
				// set the first OBJ as default
				settings.put("qualityParams", list.get(0).get(0));
			}
		}
	}

	public static DataDeviationFactory getChartDataFactory() {
		return chartDataFactory;
	}

	public static void setChartDataFactory(DataDeviationFactory chartDataFactory) {
		DataDeviationWidget.chartDataFactory = chartDataFactory;
	}

	public String getChartData() {
		if (chartDataFactory == null) {
			throw new RuntimeException("ChartDataFactory cannot be null. Use ChartWidget.setChartDataFactory(...)");
		}

		return chartDataFactory.createChart(this);
	}

	public WidgetView createView(String viewId) {
		return new DataDeviationWidgetView(viewId, new Model<Widget>(this));
	}

	@Override
	public boolean hasSettings() {
		return true;
	}

	@Override
	public Panel createSettingsPanel(String settingsPanelId) {
		return new DataDeviationSettingsPanel(settingsPanelId, new Model<>(this));
	}
		
	
	public Options getQualityParameterOptions(){
		
		Project proj = null;
		String projName = "";
		String qualityParameterChoice = "";

		proj = getProject(settings.get("project"));
		if (proj != null) {
			projName = proj.getName();
			System.out.println("projName: "+ projName);
		}
		
		qualityParameterChoice = settings.get("qualityParams");
//		if (qualityParameterChoice == null || qualityParameterChoice.isEmpty()) {			
//			qualityParameterChoice = OBJS;
//		}	
		
		System.out.println("qualityParameterChoice: "+ qualityParameterChoice);
		
		Options options = new Options();
		ChartOptions chartOptions =  new ChartOptions();
			
		// DATA
		List<HistoricValuesBaseIndicator> historicValues = getHistoricalValues();
		List<HistoricValuesBaseIndicator> qualityParam = new LinkedList<>();	
		
		List<Float> baseIndicatorValues = new LinkedList<>();
		// get and save parameter values
		for(HistoricValuesBaseIndicator hv : historicValues){
			if(hv.getBaseIndicator().getName().equals(qualityParameterChoice)){	
				qualityParam.add(hv);
				baseIndicatorValues.add(hv.getBaseIndicator().getValue());
			} 
		}
		
		// calculate deviations
		// test: overwrite values
//		baseIndicatorValues.set(0, 40087.0f);
//		baseIndicatorValues.set(1, 39457.0f);
//		baseIndicatorValues.set(2, 41347.0f);
//		baseIndicatorValues.set(3, 44777.0f);

		System.out.println("baseIndicatorValues: "+baseIndicatorValues);
		List<Float> deviations = calculateDeviations(baseIndicatorValues);
		System.out.println("deviations: "+deviations);
		
		SeriesType seriesType = SeriesType.SPLINE; 
		chartOptions.setType(seriesType);    
		chartTitle = new Title("Deviations of " + qualityParameterChoice);
		options.setTitle(chartTitle);		
		PointSeries series = new PointSeries();	
		series.setType(seriesType);
		
		List<String> xAxisLabels = new ArrayList<>();

		for(int f = 0; f < deviations.size(); f++){		
			String name = qualityParam.get(f).getBaseIndicator().getName();
			float value = deviations.get(f);
			series.addPoint(new Point(name, value));		
			//xAxis Label
			DateTime dateTime = new DateTime(qualityParam.get(f).getDate());
			String dateFormated = 	String.valueOf(dateTime.getMonthOfYear()) + "/" +
									String.valueOf(dateTime.dayOfMonth().get()) + "/" +
									String.valueOf(dateTime.getYear()) + " - " + 
									String.valueOf(dateTime.getHourOfDay()) + ":" + 
									String.valueOf(dateTime.getMinuteOfHour());
			xAxisLabels.add(dateFormated);
			
		}
		
		// Date on xAxis
		Axis xAxis = new Axis();
		xAxis.setType(AxisType.DATETIME);
		xAxis.setCategories(xAxisLabels);
		xAxis.setLabels(new Labels()
	            .setRotation(-60)
	            .setAlign(HorizontalAlignment.RIGHT)
	            .setStyle(new CssStyle()
	                .setProperty("font-size", "10px")
	                .setProperty("font-family", "Verdana, sans-serif")));
		options.setxAxis(xAxis);
		
		options.addSeries(series);
		options.setChartOptions(chartOptions);
		
		return options;
	}
	

	private List<Float> calculateDeviations(List<Float> baseIndicatorValues) {
        List<Float> deviations = new LinkedList<>();
		for(int f = 1; f < baseIndicatorValues.size(); f++){
			Float delta = baseIndicatorValues.get(f) - baseIndicatorValues.get(f-1);
			deviations.add(delta);
		}
		return deviations;
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


	private List<HistoricValuesBaseIndicator> getHistoricalValues() {
		HistoricalDataService historicalDataService = null;
		List<HistoricValuesBaseIndicator> histValues = new ArrayList<>();
		try {
			InitialContext ic = new InitialContext();
			historicalDataService = (HistoricalDataService) ic.lookup("java:module/HistoricalDataService");	
			histValues  = historicalDataService.getAllHistoricalDataObjects(); 
			
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return histValues;
	}
	

	/**
	 * 
	 * @param proj
	 * @return
	 */
	private Map<String, List<TreeNode>> getQualityParametersFromProject(Project proj) {

		List<TreeNode> Qmodels = new LinkedList<>(),
					   Qobjectives = proj.getChildren(), 
					   Qindicators = new LinkedList<>(), 
					   Qmetrics = new LinkedList<>();						
		Map<String, List<TreeNode>> map = new HashMap<>();

		Qmodels.add(proj.getParent());

        for (TreeNode Qobjective : Qobjectives) {
            int QOsize = Qobjective.getChildren().size();
            for (int indicator = 0; indicator < QOsize; indicator++) {
                Qindicators.add(Qobjective.getChildren().get(indicator));
            }
        }

        for (TreeNode Qindicator : Qindicators) {
            int QIsize = Qindicator.getChildren().size();
            for (int metric = 0; metric < QIsize; metric++) {
                Qmetrics.add(Qindicator.getChildren().get(metric));
            }
        }
		
		map.put("Quality Models", Qmodels);
		map.put("Quality Objecvtives", Qobjectives);
		map.put("Quality Indicators", Qindicators);
		map.put("Quality Metrics", Qmetrics);
		
		return map;
	}
	private List<List<String>> recreateAllProjectTreeChildrenForProject(Project proj) {	
		if (proj != null) {
			List<TreeNode> objs = proj.getChildren();
			List<String> objNames = new LinkedList<>(),
						 indiNames = new LinkedList<>(),		
					 	 metricNames = new LinkedList<>();		
	
			// Objectives of Project	 	 
			for(TreeNode obj : objs){
				objNames.add(obj.getName());				
				List<TreeNode> indicatorsOfObj = obj.getChildren();
				// Indicators of Objective
				for(TreeNode ind: indicatorsOfObj){
					indiNames.add(ind.getName());				
					List<TreeNode> metricsOfIndis = ind.getChildren();
					// Metrics Of Indicator
					for(TreeNode metric : metricsOfIndis){
						metricNames.add(metric.getName());
					}
				}
			}
	
			return Arrays.asList(removeDuplicates(objNames), removeDuplicates(indiNames), removeDuplicates(metricNames));
		}
		return null;

	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	private List<String> removeDuplicates(List<String> list){
		Set<String> set = new LinkedHashSet<>(list);
		list.clear();
		list.addAll(set);
		return list;
	}	
	
}
