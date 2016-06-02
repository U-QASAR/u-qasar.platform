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


import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class ProjectQualityChartWidgetDescriptor implements WidgetDescriptor {

	private static final long serialVersionUID = -7046897884371063291L;

	@Override
	public String getTypeName() {
		return "widget.projectqualitychart";
	}

	@Override
	public String getName() {
		return "Chart (ProjectQualityChart)";
	}

	@Override
	public String getProvider() {
		return "U-QASAR Project Quality Widget";
	}

	@Override
	public String getDescription() {
		return "A chart widget illustrating the overall project quality";
	}

	@Override
	public String getWidgetClassName() {
		return ProjectQualityChartWidget.class.getName();
	}
}
