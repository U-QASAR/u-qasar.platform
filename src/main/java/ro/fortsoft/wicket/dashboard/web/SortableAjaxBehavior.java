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

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;

import ro.fortsoft.wicket.dashboard.WidgetLocation;

import com.google.gson.Gson;

/**
 * @author Decebal Suiu
 */
public abstract class SortableAjaxBehavior extends AbstractDefaultAjaxBehavior {

	private static final long serialVersionUID = 1L;

	/** Sorted identifiant into the request */
	private static final String JSON_DATA = "data";
	
	public abstract void onSort(AjaxRequestTarget target, Map<String, WidgetLocation> widgetLocations);
		
    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);

        StringBuilder buffer = new StringBuilder();
        buffer.append("var data = serializeWidgetLocations();");
        buffer.append("return {'" + JSON_DATA + "': data};");

        attributes.getDynamicExtraParameters().add(buffer);
    }

	@Override
	protected void respond(AjaxRequestTarget target) {
		String jsonData = getComponent().getRequest().getRequestParameters().getParameterValue(JSON_DATA).toString();
		Item[] items = getItems(jsonData);
		Map<String, WidgetLocation> locations = new HashMap<>();
		for (Item item : items) {
			WidgetLocation location = new WidgetLocation(item.column, item.sortIndex);
			locations.put(item.widget, location);
		}
		
		onSort(target, locations);
	}

	private Item[] getItems(String jsonData) {
		Gson gson = new Gson();
		/*
		System.out.println(items.length);
		for (Item item : items) {
			System.out.println(item);
		}
		*/
		
		return gson.fromJson(jsonData, Item[].class);
	}
		
	static class Item {
				
		public int column;
		public String widget;
		public int sortIndex;
		
		@Override
		public String toString() {
            String buffer = "Item[" +
                    "column = " + column +
                    " widget = " + widget +
                    " sortIndex = " + sortIndex +
                    "]";

            return buffer;
		}

	}
	
}
