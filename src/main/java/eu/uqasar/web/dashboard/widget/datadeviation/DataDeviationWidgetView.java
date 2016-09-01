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


import org.apache.wicket.model.IModel;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

/**
 * 
 *
 *
 */
public class DataDeviationWidgetView extends WidgetView {

	private static final long serialVersionUID = 7504539323135550980L;
	public DataDeviationWidgetView(String id, IModel<Widget> model) {
		super(id, model);

		DataDeviationWidget widget = (DataDeviationWidget) model.getObject();

		Chart chart = new Chart("chart", widget.getQualityParameterOptions()); 
		add(chart);
	}
}
