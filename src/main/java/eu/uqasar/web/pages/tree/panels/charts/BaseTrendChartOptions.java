package eu.uqasar.web.pages.tree.panels.charts;

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
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.LegendLayout;
import com.googlecode.wickedcharts.highcharts.options.PlotLine;
import com.googlecode.wickedcharts.highcharts.options.PlotOptions;
import com.googlecode.wickedcharts.highcharts.options.PlotOptionsChoice;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.Tooltip;
import com.googlecode.wickedcharts.highcharts.options.VerticalAlignment;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.series.Series;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;

import eu.uqasar.model.tree.BaseIndicator;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;
import eu.uqasar.service.HistoricalDataService;
import eu.uqasar.web.components.charts.DefaultChartOptions;

public class BaseTrendChartOptions<Type extends TreeNode> extends
		DefaultChartOptions {

	private static final long serialVersionUID = 1L;
	private static final int MONTHS_TO_SHOW = 6;

	
	private HistoricalDataService dataService;
	
	public BaseTrendChartOptions(Component chartContainer, IModel<Type> model) {
		TreeNode object = model.getObject();

		ChartOptions chartOptions = new ChartOptions();

		chartOptions.setType(SeriesType.COLUMN);
		chartOptions.setMarginRight(130);
		chartOptions.setMarginBottom(25);
		setChartOptions(chartOptions);

		Title title = new Title("");
		title.setX(-20);
		title.setEnabled(false);
		setTitle(title);

		SimpleDateFormat df = new SimpleDateFormat("MMM", Session.get()
				.getLocale());

		DateTime now = DateTime.now();
		String[] monthNames = new String[MONTHS_TO_SHOW];
		for (int i = MONTHS_TO_SHOW - 1; i >= 0; i--) {
			DateTime dt = now.minusMonths(i);
			monthNames[(MONTHS_TO_SHOW - 1) - i] = df.format(dt.toDate());
		}
		Axis xAxis = new Axis();
		xAxis.setCategories(Arrays.asList(monthNames));
		setxAxis(xAxis);

		PlotLine plotLines = new PlotLine();
		plotLines.setValue(0f);
		plotLines.setWidth(1);
		plotLines.setColor(new HexColor("#999999"));

		Axis yAxis = new Axis();
		yAxis.setTitle(new Title(new StringResourceModel("label.quality.value",
				chartContainer, null).getString()));
		yAxis.setPlotLines(Collections.singletonList(plotLines));
		yAxis.setMax(100);
		yAxis.setMin(0);
		setyAxis(yAxis);

		Legend legend = new Legend();
		legend.setLayout(LegendLayout.VERTICAL);
		legend.setAlign(HorizontalAlignment.RIGHT);
		legend.setVerticalAlign(VerticalAlignment.TOP);
		legend.setX(-10);
		legend.setY(100);
		legend.setBorderWidth(0);
		setLegend(legend);

		@SuppressWarnings("unchecked")
		Series<Number>[] seriesS = new Series[object.getChildren().size()];
		int count = 0;
		List<Number[]> randomNumbers = new ArrayList<>();
		
		for (Object childElement : object.getChildren()) {
			if (childElement instanceof TreeNode) {
				TreeNode child = (TreeNode) childElement;
				Series<Number> series = new SimpleSeries();
				series.setType(SeriesType.COLUMN);
				series.setName(child.getName());
				Number[] objectiveQualityValues = getRealNumbers(child, MONTHS_TO_SHOW); 
//				Number[] objectiveQualityValues = getRandomNumbersPlusLastValue(
//						child, MONTHS_TO_SHOW);
//				Number[] objectiveQualityValues = getRandomNumbers(
//						child.getQualityStatus(), MONTHS_TO_SHOW);
				series.setData(Arrays.asList(objectiveQualityValues));
				seriesS[count++] = series;
				randomNumbers.add(objectiveQualityValues);
			}
		}

		setPlotOptions(new PlotOptionsChoice().setAreaspline(new PlotOptions()
				.setFillOpacity(0.5f)));

		Series<Number> average = new SimpleSeries();
		average.setType(SeriesType.AREASPLINE);
		average.setName(getDefaultChartOptionsBundle().getString(
				"chart.series.legend.average"));
		average.setData(Arrays.asList(getAverage(randomNumbers)));
		Tooltip averageTooltip = new Tooltip();
		averageTooltip.setValueDecimals(2);
		average.setTooltip(averageTooltip);
		addSeries(average);

		for (int i = 0; i < count; i++) {
			addSeries(seriesS[i]);
		}

	}

	private Number[] getAverage(List<Number[]> seriesValues) {
		if (seriesValues.size() > 0) {
			int seriesElementCount = seriesValues.get(0).length;
			Float[] averages = new Float[seriesElementCount];

			for (int i = 0; i < seriesElementCount; i++) {
				int seriesValueSum = 0;
				for (Number[] seriesValue : seriesValues) {
					seriesValueSum += seriesValue[i].intValue();
				}
				averages[i] = (float) seriesValueSum
						/ (float) seriesValues.size();
			}
			return averages;
		} else {
			return new Float[0];
		}
	}

	protected Number[] getRandomNumbers(QualityStatus status, int elementCount) {
		Number[] numbers = new Number[elementCount];
		for (int i = 0; i < elementCount; i++) {
			numbers[i] = status.getRandomQualityValue();
		}
		return numbers;
	}

	protected Number[] getRandomNumbersPlusLastValue(TreeNode node, int elementCount) {
		BaseIndicator baseIndicator = (BaseIndicator) node;
		QualityStatus status = node.getQualityStatus();
		
		Number[] numbers = new Number[elementCount];
		for (int i = 0; i < elementCount; i++) {
			numbers[i] = status.getRandomQualityValue();
		}
		numbers[elementCount-1] = baseIndicator.getValue();
		
		return numbers;
	}
	
	private Number[] getRealNumbers(TreeNode node, int elementCount){
		Number[] numbersHalfYear = new Number[elementCount];
		Number[] numbersFullYear = new Number[2*elementCount];
		BaseIndicator baseIndicator = (BaseIndicator) node;
	
		try {
			InitialContext ic = new InitialContext();
			dataService = (HistoricalDataService) ic.lookup("java:module/HistoricalDataService");	
		} catch (NamingException e) {
			e.printStackTrace();
		}

		// get all historical values and timestamps for given baseindicator
		List<HistoricValuesBaseIndicator> all = dataService.getAllHistValuesForBaseIndAsc(baseIndicator.getId());
		int nowMonth = DateTime.now().getMonthOfYear();

		if(!all.isEmpty()){
			for(HistoricValuesBaseIndicator hv : all){			
				
				int month = new DateTime(hv.getDate()).getMonthOfYear();
				for (int i=1; i<=numbersFullYear.length; i++) {
					if(month==i){	
						numbersFullYear[i-1] = hv.getValue();
					} 
				}	
			} 
			//remove nulls
			for (int i=1; i<=numbersFullYear.length; i++) {
				if(numbersFullYear[i-1]==null){
					numbersFullYear[i-1] = 0.0f;
				}
			}
			// select half year before
			System.arraycopy(numbersFullYear, nowMonth - elementCount + 0, numbersHalfYear, 0, elementCount - 1 + 1);
		} else{
			for (int i=0; i<=numbersFullYear.length-1; i++) {
				numbersFullYear[i] = 0.0f;
			}
			for (int i=0; i<=elementCount-1; i++) {
				numbersHalfYear[i] = 0.0f;
			}	
			
		}
		System.out.println(Arrays.asList(numbersHalfYear));
		return numbersHalfYear;
	}
	
	
}
