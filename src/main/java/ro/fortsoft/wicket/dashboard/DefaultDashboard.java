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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Decebal Suiu
 */
public class DefaultDashboard implements Dashboard {

	private static final long serialVersionUID = 1L;
	
	private final String dashboardId;
	private String title;
	private int columnCount;
	private List<Widget> widgets;
	
	public DefaultDashboard(String id, String title) {
		this.dashboardId = id;
		this.title = title;
		columnCount = 2;
		widgets = new ArrayList<>();
	}

	@Override
	public String getDashboardId() {
		return dashboardId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}        

	@Override
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}
	
	@Override
	public List<Widget> getWidgets() {
		return widgets;
	}
	
	@Override
	public List<Widget> getWidgets(int column) {
		List<Widget> columnWidgets = new ArrayList<>();
		for (Widget widget : widgets) {
			if (column == widget.getLocation().getColumn()) {
				columnWidgets.add(widget);
			}
		}
		
		// sort widgets by row
		Collections.sort(columnWidgets, new WidgetComparator());
		
		return columnWidgets;
	}
	
	@Override
	public Widget getWidget(String widgetId) {
		for (Widget widget : widgets) {
			if (widget.getId().equals(widgetId)) {
				return widget;
			}
		}
		
		return null;
	}

	public void setWidgets(List<Widget> widgets) {
		this.widgets = widgets;
	}

	@Override
	public void addWidget(Widget widget) {
		// With some widgets there has been the problem that 
		// duplicates are added simultaneously to the dashboard. 
		// TODO: Try to replace this with a more elegant solution; 
		// Now do not add widget, if there already is one with the 
		// given id.
		boolean exists = false;
        for (Widget widget1 : widgets) {
            if (widget1.getId().equals(widget.getId())) {
                exists = true;
                break;
            }
        }
		if (!exists) {
			widgets.add(widget);
		}
	}
	
	@Override
	public void deleteWidget(String widgetId) {
		Widget widget = getWidget(widgetId);
		if (widget != null) {
			widgets.remove(widget);
		}
	}	
	
	@Override
	public String toString() {
        String buffer = "DefaultDashboard[" +
                "dashboardId = " + dashboardId +
                " title = " + title +
                " widgets = " + widgets +
                "]";

        return buffer;
	}
	
}
