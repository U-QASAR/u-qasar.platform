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


import junit.framework.TestCase;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

public class WidgetLocationTest extends TestCase {

	private static Logger logger = Logger.getLogger(WidgetLocationTest.class);
	private WidgetLocation location;
	private WidgetLocation locationPos;
	

	@Before
	public void setUp() {
		location = new WidgetLocation();
		locationPos = new WidgetLocation(1,1);
	}

	@Test
	public void testSetColumn() {
		logger.info("Testing set column");
		assertNotNull(location);
		location.setColumn(2);
		assertEquals(2, location.getColumn());
	}
	
	@Test
	public void testSetRow() {
		logger.info("Testing set row");
		assertNotNull(location);
		location.setRow(2);
		assertEquals(2, location.getRow());
	}

	@Test
	public void testDecrementRow() {
		logger.info("Testing decrement row");
		assertNotNull(location);
		location.setRow(1);
		assertEquals(1, location.getRow());
		location.decrementRow();
		assertEquals(0, location.getRow());
	}
	
	@Test
	public void testIncrementRow() {
		logger.info("Testing increment row");
		assertNotNull(location);
		location.setRow(1);
		assertEquals(1, location.getRow());
		location.incrementRow();
		assertEquals(2, location.getRow());		
	}
	
	@Test
	public void testEqualsFalse() {
		logger.info("Testing equals false");
		location.setColumn(0);
		locationPos.setColumn(1);
		assertFalse(locationPos.equals(location));
		
		location.setColumn(1);
		location.setRow(2);
		locationPos.setColumn(1);
		locationPos.setRow(0);
		assertFalse(locationPos.equals(location));
		
		assertFalse(location.equals(logger));
		
	}
	
	@Test
	public void testEqualsNull() {
		logger.info("Testing equals null");
		assertFalse(locationPos.equals(null));
	}
	
}
