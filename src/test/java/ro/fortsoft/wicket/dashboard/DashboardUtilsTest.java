package ro.fortsoft.wicket.dashboard;

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

import junit.framework.Assert;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.jboss.solder.logging.Logger;
import org.junit.Before;
import org.junit.Test;

import ro.fortsoft.wicket.dashboard.web.DashboardEvent;
import ro.fortsoft.wicket.dashboard.web.DashboardEvent.EventType;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class DashboardUtilsTest {

	private String id;
	private String title;
	private AbstractWidget widget;
	private DefaultDashboard defaultDashboard; 
	private DashboardEvent eventAdded, eventRemoved, eventSorted;
	private AjaxRequestTarget target;
	int row;
	private Logger logger = Logger.getLogger(DashboardUtilsTest.class);
	
	@Before
	public void setUp() {
		widget = new AbstractWidget() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public WidgetView createView(String viewId) {
				return null;
			}
		};
		id = "DefaultDash";
		title  = "This is Dashboard";
		row = widget.getLocation().getRow();
		
		defaultDashboard = new DefaultDashboard(id, title);
		defaultDashboard.setTitle(title);
		ArrayList<Widget> wList = new ArrayList<Widget>();
		wList.add(widget);
		defaultDashboard.setWidgets(wList);
		eventAdded = new DashboardEvent(target, EventType.WIDGET_ADDED, widget);
		eventRemoved = new DashboardEvent(target, EventType.WIDGET_REMOVED, widget);
		eventSorted = new DashboardEvent(target, EventType.WIDGETS_SORTED, widget);
		
	};

	@Test
	public void testUpdateWidgetLocationsAdded() {
		logger.info("Testing updateWidgetLocations widget added");
		DashboardUtils.updateWidgetLocations(defaultDashboard, eventAdded);
		Assert.assertEquals(row + 1, defaultDashboard.getWidgets().get(0).getLocation().getRow());
		
	}

	@Test
	public void testUpdateWidgetLocationsRemoved() {
		logger.info("Testing updateWidgetLocations widget removed");
		DashboardUtils.updateWidgetLocations(defaultDashboard, eventRemoved);

		Assert.assertEquals(row, defaultDashboard.getWidgets().get(0).getLocation().getRow());
	}
	
	

}
