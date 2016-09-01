/*
 * Copyright 2012 Decebal Suiu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with
 * the License. You may obtain a copy of the License in the LICENSE file, or at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ro.fortsoft.wicket.dashboard;

import java.io.Serializable;
import java.util.Map;

import org.apache.wicket.markup.html.panel.Panel;

import ro.fortsoft.wicket.dashboard.web.WidgetView;

/**
 * @author Decebal Suiu
 */
public interface Widget extends Serializable {

	String getId();
	
	void setId(String id);
	
	String getTitle();
	
	void setTitle(String title);
	
	WidgetLocation getLocation();
	
	void setLocation(WidgetLocation location);
	
	WidgetView createView(String viewId);
	
	boolean isCollapsed();
	
	void setCollapsed(boolean collapsed);
	
	void init();
	
	boolean hasSettings();
	
	Map<String, String> getSettings();
	
	void setSettings(Map<String, String> settings);
	
	Panel createSettingsPanel(String settingsPanelId);
	
}
