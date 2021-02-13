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


import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import eu.uqasar.model.measure.SonarMetricMeasurement;

public class SonarQualityWidgetView extends WidgetView {

	private static final long serialVersionUID = 2880002420318496717L;
	private transient List<SonarMetricMeasurement> measurements;
	private final String period;
	private final String individualMetric;
	public SonarQualityWidgetView(String id, IModel<Widget> model) {
		super(id, model);

		final SonarQualityWidget qualityWidget = (SonarQualityWidget) model.getObject();
				
		if(qualityWidget.getSettings().get("period")!=null){
			period = qualityWidget.getSettings().get("period");
		} else {
		    // if there is no selection of period then it should be the latest values 
			period = "Latest";
		}
		
		System.out.println("period: "+period);
		
		LoadableDetachableModel<List<SonarMetricMeasurement>> mdl = new LoadableDetachableModel<List<SonarMetricMeasurement>>() {
			private static final long serialVersionUID = 1L;

			protected List<SonarMetricMeasurement> load() {
				measurements = qualityWidget.getMeasurements(period);
				System.out.println("measurements : "+measurements );
				return measurements;
			}
		};
		
		Chart chart;
		individualMetric = qualityWidget.getSettings().get("individualMetric"); 
		if( individualMetric  !=null){
		    chart = new Chart("chart", qualityWidget.getChartOptionsDifferently(mdl.getObject(),individualMetric ));
        } 
        else {
            chart = new Chart("chart", qualityWidget.getChartOptions(mdl.getObject()));
        }
		

		add(chart);

	}	
}
