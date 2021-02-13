package eu.uqasar.web.dashboard.projectqualitygooglechart;

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

import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;

/**
 * A sample widget using Google Charts API <https://developers.google.com/chart/>
 */
@Setter
@Getter
public class ProjectQualityGoogleChartWidget extends AbstractWidget {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2172703196794098298L;

	@Setter
	@Getter
	private static ProjectQualityGoogleChartFactory chartDataFactory;
	
	public static final String GAUGE_TYPE = "GAUGE"; 
	public static final String BAR_TYPE = "BAR";
	public static final String LINE_TYPE = "LINE";

	public static final List<String> TYPES = Arrays.asList(LINE_TYPE,
            BAR_TYPE,
            GAUGE_TYPE);

	
	public ProjectQualityGoogleChartWidget() {
		super();
		title = "Project Quality";
	}

	@Override
	public void init() {
	}

	public String getChartData() {
		if (chartDataFactory == null) {
			throw new RuntimeException("ChartDataFactory cannot be null. Use ChartWidget.setChartDataFactory(...)");
		}

		return chartDataFactory.createChart(this);
	}

	public WidgetView createView(String viewId) {
		return new ProjectQualityGoogleChartWidgetView(viewId, new Model<Widget>(this));
	}

	@Override
	public boolean hasSettings() {
		return true;
	}

	@Override
	public Panel createSettingsPanel(String settingsPanelId) {
		return new ProjectQualityGoogleChartSettingsPanel(settingsPanelId, new Model<>(this));
	}

	public Double returnProjectQualityValue(){
		TreeNodeService dataService = null;

		Project proj = null;
		Double value = (double) 0;
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
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return value;

	}
}
