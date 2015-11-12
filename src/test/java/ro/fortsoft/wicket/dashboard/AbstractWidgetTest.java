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


import java.util.HashMap;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jboss.solder.logging.Logger;
import org.junit.Before;
import org.junit.Test;

import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class AbstractWidgetTest extends TestCase {

	private AbstractWidget widget, widgetTest;
	private Logger logger = Logger.getLogger(AbstractWidgetTest.class);
	
	@Before
	public void setUp() {
		widget = new AbstractWidget() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public WidgetView createView(String viewId) {
				return null;
			}
		};
		
		widgetTest = new AbstractWidget() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public WidgetView createView(String viewId) {
				return null;
			}
		};
	};
	
	@Test
	public void testSetId() {
		logger.info("Testing set id");
		assertNotNull(widget);
		final String id = "1";
		widget.setId(id);
		assertEquals(id, widget.getId());		
	}

	@Test
	public void testSetTitle() {
		logger.info("Testing set title");
		assertNotNull(widget);
		final String title = "TestTitle";
		widget.setTitle(title);
		assertEquals(title, widget.getTitle());
	}
	
	@Test
	public void testSetCollapsed() {
		logger.info("Testing set collapsed");
		assertNotNull(widget);
		assertEquals(false, widget.isCollapsed());
		final boolean collapsed = true;
		widget.setCollapsed(collapsed);
		assertEquals(collapsed, widget.isCollapsed());
	}
	
	@Test
	public void testSetLocation() {
		logger.info("Testing set location");
		assertNotNull(widget);
		assertNotNull(widget.getLocation()); // default is 0,0
		WidgetLocation location = new WidgetLocation();
		location.setColumn(1);
		location.setRow(0);
		widget.setLocation(location);
		assertEquals(location, widget.getLocation());
	}
	
	@Test
	public void testSetSettings() {
		logger.info("Testing set settings");
		assertNotNull(widget);
		assertNotNull(widget.getSettings()); // default settings
		HashMap<String, String> settings = new HashMap<String, String>();
		settings.put("test1", "testvalue1");
		widget.setSettings(settings);
		assertEquals(settings, widget.getSettings());
	}
	
	@Test
	public void testEqualsNull() {
		logger.info("Testing equals null");
		assertFalse(widget.equals(null));
	}

	@Test
	public void testEqualsFalse() {
		logger.info("Testing equals false");
		assertFalse(widgetTest.equals(widget));
		
		widgetTest.setTitle("Wdiget Testing");
		assertFalse(widgetTest.equals(widget));
	}

	@Test
	public void testEquals() {
		logger.info("Testing equals true");
		assertTrue(widget.equals(widget));
	}
	
	@Test
	public void testHashCode() {
		logger.info("Testing hashcode");
		widgetTest.setId("23");
		widgetTest.setTitle("Title");
		int res = widgetTest.hashCode();
		Assert.assertEquals(80868375, res);
	}
	
	

}
