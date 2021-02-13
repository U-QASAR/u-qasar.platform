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
package ro.fortsoft.wicket.dashboard.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.DashboardUtils;
import ro.fortsoft.wicket.dashboard.Widget;

/**
 * @author Decebal Suiu
 */
public class DashboardPanel extends GenericPanel<Dashboard> implements DashboardContextAware {
	
	private static final long serialVersionUID = 1L;

	private transient DashboardContext dashboardContext;
	
	private List<DashboardColumnPanel> columnPanels;
	
	public DashboardPanel(String id, IModel<Dashboard> model) {
		super(id, model);			

		addColumnsPanel();
		
		add(new DashboardResourcesBehavior());
	}
		
	public Dashboard getDashboard() {
		return getModelObject();
	}
	
	@Override
	public void setDashboardContext(DashboardContext dashboardContext) {
		this.dashboardContext = dashboardContext;
	}

	/**
	 * Used by children.
	 */
	public DashboardContext getDashboardContext() {
		return dashboardContext;
	}

	@Override
	public void onEvent(IEvent<?> event) {
		super.onEvent(event);
		
		if (event.getPayload() instanceof DashboardEvent) {
			DashboardEvent dashboardEvent = (DashboardEvent) event.getPayload();
			DashboardEvent.EventType eventType = dashboardEvent.getType();
			if (DashboardEvent.EventType.WIDGET_ADDED == eventType) {
				onWidgetAdded(dashboardEvent);
			} else if (DashboardEvent.EventType.WIDGET_REMOVED == eventType) {
				onWidgetRemoved(dashboardEvent);
			} else if (DashboardEvent.EventType.WIDGETS_SORTED == eventType) {
				onWidgetsSorted(dashboardEvent);
			}
		}
	}

	private void onWidgetAdded(DashboardEvent dashboardEvent) {
		Widget addedWidget = (Widget) dashboardEvent.getDetail();
		Dashboard dashboard = getDashboard();
		DashboardUtils.updateWidgetLocations(dashboard, dashboardEvent);
		dashboard.addWidget(addedWidget);
		dashboardContext.getDashboardPersiter().save(dashboard);
	}

	private void onWidgetRemoved(DashboardEvent dashboardEvent) {
		Widget removedWidget = (Widget) dashboardEvent.getDetail();
		Dashboard dashboard = getDashboard();
		DashboardUtils.updateWidgetLocations(dashboard, dashboardEvent);
		dashboard.deleteWidget(removedWidget.getId());
		dashboardContext.getDashboardPersiter().save(dashboard);
	}

	private void onWidgetsSorted(DashboardEvent dashboardEvent) {
		Dashboard dashboard = getDashboard();
		DashboardUtils.updateWidgetLocations(dashboard, dashboardEvent);
		dashboardContext.getDashboardPersiter().save(dashboard);		
	}

	private void addColumnsPanel() {
		if (getDashboard() != null) {
			final int columnCount = getDashboard().getColumnCount();
			Loop columnsView = new Loop("columns", columnCount) {
				
				private static final long serialVersionUID = 1L;

				@Override
				protected void onBeforeRender() {
					if (!hasBeenRendered()) {
						columnPanels = new ArrayList<>();
					}
					
					super.onBeforeRender();
				}

				@Override
				protected void populateItem(LoopItem item) {
				    float columnPanelWidth = 100f / columnCount;
			    	DashboardColumnPanel columnPanel = new DashboardColumnPanel("column", getModel(), item.getIndex());
			    	columnPanel.setRenderBodyOnly(true);
			    	columnPanel.getColumnContainer().add(AttributeModifier.replace("style", "width: " + columnPanelWidth + "%;"));		    	
			    	item.add(columnPanel);
			    	
			    	columnPanels.add(columnPanel);
				}
				
			};
			
			add(columnsView);
		}
	}

}
