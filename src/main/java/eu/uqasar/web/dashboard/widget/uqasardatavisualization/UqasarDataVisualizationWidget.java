package eu.uqasar.web.dashboard.widget.uqasardatavisualization;

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

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;

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
import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

/**
 * 
 *
 *
 */
public class UqasarDataVisualizationWidget extends AbstractWidget{

	private final Project project;
	private static UqasarDataVisualizationFactory chartDataFactory;	
	private static final long serialVersionUID = -2447400380886027022L;
	private Title chartTitle = new Title();	
	
	public static final List<String> ALL = Arrays.asList("All Quality Parameters");
	
	public UqasarDataVisualizationWidget() {
		super();
		title = "Project Tree Visualization";	
		project = getProject(settings.get("project"));
	}

	@Override
	public void init() {
		if(!settings.containsKey("qualityParams")){
			settings.put("qualityParams", UqasarDataVisualizationWidget.ALL.get(0));
		}		
	}

	public static UqasarDataVisualizationFactory getChartDataFactory() {
		return chartDataFactory;
	}

	public static void setChartDataFactory(UqasarDataVisualizationFactory chartDataFactory) {
		UqasarDataVisualizationWidget.chartDataFactory = chartDataFactory;
	}

	public String getChartData() {
		if (chartDataFactory == null) {
			throw new RuntimeException("ChartDataFactory cannot be null. Use ChartWidget.setChartDataFactory(...)");
		}

		return chartDataFactory.createChart(this);
	}

	public WidgetView createView(String viewId) {
		return new UqasarDataVisualizationWidgetView(viewId, new Model<Widget>(this));
	}

	@Override
	public boolean hasSettings() {
		return true;
	}

	@Override
	public Panel createSettingsPanel(String settingsPanelId) {
		return new UqasarDataVisualizationSettingsPanel(settingsPanelId, new Model<>(this));
	}
		
	
	public Options getQualityParameterOptions(){
		
		Project proj = null;
		String projName = "";
		Map<String, List<TreeNode>> qualityParameters = new HashMap<>();		
		String qualityParameterChoice = "";

		proj = getProject(settings.get("project"));
		if (proj != null) {
			projName = proj.getName();
		}
		
		qualityParameterChoice = settings.get("qualityParams");
		if (qualityParameterChoice == null || qualityParameterChoice.isEmpty()) {
			qualityParameterChoice = ALL.get(0); // all params
		}	
		System.out.println("qualityParameterChoice: "+ qualityParameterChoice);
		
		Options options = new Options();
		ChartOptions chartOptions =  new ChartOptions();
		
		if(qualityParameterChoice.equals(ALL.get(0))){
			// DATA
			qualityParameters = getQualityParametersFromProject(proj);
			SeriesType seriesType = SeriesType.COLUMN; 
			// CHART
			chartOptions.setType(seriesType);    
			chartTitle = new Title(ALL.get(0) + " of " + projName);
			options.setTitle(chartTitle);		
			for (String key : qualityParameters.keySet()){
				PointSeries series = new PointSeries();	
				series.setType(seriesType);
				int siz = qualityParameters.get(key).size(); 
				series.addPoint(new Point(key, siz));
				series.setName(key);
				options.addSeries(series);
			}
			options.setChartOptions(chartOptions);	
		} else{
			
			// DATA
			List<HistoricValuesBaseIndicator> HistoricValuesBaseIndicator = getHistoricalValues();
			List<HistoricValuesBaseIndicator> data = new LinkedList<>();	
			
			for(HistoricValuesBaseIndicator hv : HistoricValuesBaseIndicator){
				if(hv.getBaseIndicator().getName().equals(qualityParameterChoice)){
					data.add(hv);
				} 
			}
			
			SeriesType seriesType = SeriesType.SPLINE; 
			chartOptions.setType(seriesType);    
			chartTitle = new Title(qualityParameterChoice + " of " + projName);
			options.setTitle(chartTitle);		
			PointSeries series = new PointSeries();	
			series.setType(seriesType);
			
			List<String> xAxisLabels = new ArrayList<>();

			for(HistoricValuesBaseIndicator dat : data){
				String name = dat.getBaseIndicator().getName();
				float value = dat.getBaseIndicator().getValue();
				series.addPoint(new Point(name, value));
				
				//xAxis Label
				//xAxis Label
				DateTime dateTime = new DateTime(dat.getDate());
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
		}
		
		return options;
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
			histValues  = historicalDataService.getAllHistoricalDataObjects(); //TODO: get latest objects!
			
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

		Map<String, List<TreeNode>> map = new HashMap<>();

		if (proj != null) {
			List<TreeNode> Qmodels = new LinkedList<>(),
						   Qobjectives = proj.getChildren(), 
						   Qindicators = new LinkedList<>(), 
						   Qmetrics = new LinkedList<>();						
	
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
			map.put("Quality Objectives", Qobjectives);
			map.put("Quality Indicators", Qindicators);
			map.put("Quality Metrics", Qmetrics);			
		}
		
		return map;

	}
	
}
