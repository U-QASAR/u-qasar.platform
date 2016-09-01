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
package eu.uqasar.model.dashboard;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.WidgetComparator;
import eu.uqasar.model.AbstractEntity;
@NoArgsConstructor
@Setter
@Getter
@Entity
@XmlRootElement
public class DbDashboard extends AbstractEntity implements Dashboard {

	private static final long serialVersionUID = 1L;
	
	private String dashboardId;
	private String title;
	private int columnCount;
	
	private String sharedBy;
	
	@ElementCollection
	@Lob
	private List<Widget> widgets = new ArrayList<>();
	
	public DbDashboard(String id, String title) {
		this.dashboardId = id;
		this.title = title;
		this.columnCount = 2;
		this.widgets = new ArrayList<>();
		this.sharedBy = null;
	}

	public DbDashboard(String id, String title, int columnCount) {
		this.dashboardId = id;
		this.title = title;
		this.columnCount = columnCount;
		this.widgets = new ArrayList<>();
		this.sharedBy = null;
	}
	
	// Create dashboard from a copy
	public DbDashboard(DbDashboard copy) {
		this.dashboardId = String.valueOf(new Date().getTime());
		this.title = copy.getTitle();
		this.columnCount = copy.getColumnCount();
		this.widgets = new ArrayList<>();
		this.widgets.addAll(copy.getWidgets()); 
		this.sharedBy = copy.getSharedBy();
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
				"id = " + dashboardId +
				" title = " + title +
				" widgets = " + widgets +
				"]";

		return buffer;
	}
	
}
