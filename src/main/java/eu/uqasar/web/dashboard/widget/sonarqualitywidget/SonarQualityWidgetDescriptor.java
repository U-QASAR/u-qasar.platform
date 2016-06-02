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


import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class SonarQualityWidgetDescriptor implements WidgetDescriptor {

	private static final long serialVersionUID = -8991404810105901232L;

	@Override
	public String getTypeName() {
		return "widget.sonarqualitywidget";
	}

	@Override
	public String getName() {
		return "Sonar Code Quality";
	}

	@Override
	public String getProvider() {
		return "U-Qasar widget for static code analysis";
	}

	@Override
	public String getDescription() {
		return "A widget providing Sonar code analysis data to the user.";
	}

	@Override
	public String getWidgetClassName() {
		return SonarQualityWidget.class.getName();
	}

}
