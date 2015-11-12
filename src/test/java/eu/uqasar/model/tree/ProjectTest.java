package eu.uqasar.model.tree;

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
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jcabi.log.Logger;

import eu.uqasar.model.company.Company;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.product.Product;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.user.Team;

public class ProjectTest {

	private Project testProject;
	
	@Before
	public void setUp() throws Exception {
		testProject = new Project();
		testProject.setDescription("descr");
		testProject.setLifeCycleStage(LifeCycleStage.Implementation);
		testProject.setName("ASDFGHJKL");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetDescription() {
		Assert.assertNotNull(testProject.getDescription());
		Assert.assertEquals("descr", testProject.getDescription());
	}

	@Test
	public void testSetDescription() {
		final String DESCR = "new description";
		testProject.setDescription(DESCR);
		Assert.assertNotNull(testProject.getDescription());
		Assert.assertEquals(DESCR, testProject.getDescription());
	}

	@Test
	public void testGetLifeCycleStage() {
		Assert.assertNotNull(testProject.getLifeCycleStage());
		Assert.assertEquals(LifeCycleStage.Implementation, testProject.getLifeCycleStage());
	}

	@Test
	public void testGetIconType() {
		Assert.assertNotNull(testProject.getIconType());
	}


	@Test
	public void testGetCompany() {
		Assert.assertNull(testProject.getCompany());
	}

	@Test
	public void testSetCompany() {
		Company c = new Company("Test C");
		testProject.setCompany(c);
		Assert.assertNotNull(testProject.getCompany());
		Assert.assertEquals(c, testProject.getCompany());
	}

	@Test
	public void testGetTeams() {
		Assert.assertTrue(testProject.getTeams().isEmpty());
	}

	@Test
	public void testSetTeams() {
		List<Team> teams = new ArrayList<>();
		teams.add(new Team());
		testProject.setTeams(teams);
		Assert.assertEquals(teams, testProject.getTeams());
	}

	@Test
	public void testGetAbbreviatedNameInt() {
		// Max length of four tested
		Logger.info("Abbreviated name: ", testProject.getAbbreviatedName(4));
		Assert.assertEquals(4, testProject.getAbbreviatedName(4).length());
	}

	@Test
	public void testGetAbbreviatedName() {
		// The method returns a string with a max length of 48
		Logger.info("Abbreviated name: ", testProject.getAbbreviatedName());
		Assert.assertEquals("ASDFGHJKL", testProject.getAbbreviatedName());
	}

	@Test
	public void testGetStartDate() {
		Assert.assertNotNull(testProject.getStartDate());
	}

	@Test
	public void testSetStartDate() {
		Date d = new Date();
		testProject.setStartDate(d);
		Assert.assertEquals(d, testProject.getStartDate());
	}

	@Test
	public void testGetEndDate() {
		Assert.assertNotNull(testProject.getEndDate());
	}

	@Test
	public void testSetEndDate() {
		Date d = new Date();
		testProject.setEndDate(d);
		Assert.assertEquals(d, testProject.getEndDate());
	}

	@Test
	public void testIsRunning() {
		Date sDate = DateTime.now().toDate(); 
		testProject.setStartDate(sDate);
		Date eDate = DateTime.now().plusDays(3).toDate();
		testProject.setEndDate(eDate);
		Assert.assertTrue(testProject.isRunning());
	}

	@Test
	public void testGetElapsedVsRemainingString() {
		Date sDate = DateTime.now().toDate(); 
		testProject.setStartDate(sDate);
		Date eDate = DateTime.now().plusDays(3).toDate();
		testProject.setEndDate(eDate);
		Assert.assertNotNull(testProject.getElapsedVsRemainingString());
	}

	@Test
	public void testGetElapsedVsOverallString() {
		Date sDate = DateTime.now().toDate(); 
		testProject.setStartDate(sDate);
		Date eDate = DateTime.now().plusDays(3).toDate();
		testProject.setEndDate(eDate);
		Assert.assertNotNull(testProject.getElapsedVsOverallString());
	}

	@Test
	public void testGetElapsedVsRemainingWithPercentageString() {
		Date sDate = DateTime.now().toDate(); 
		testProject.setStartDate(sDate);
		Date eDate = DateTime.now().plusDays(3).toDate();
		testProject.setEndDate(eDate);
		Assert.assertNotNull(testProject.getElapsedVsRemainingWithPercentageString());
	}

	@Test
	public void testGetDurationInDays() {
		Date sDate = DateTime.now().toDate(); 
		testProject.setStartDate(sDate);
		Date eDate = DateTime.now().plusDays(3).toDate();
		testProject.setEndDate(eDate);
		Assert.assertNotNull(testProject.getDurationInDays());
		Assert.assertEquals(3, testProject.getDurationInDays());
	}

	@Test
	public void testGetElapsedDays() {
		Date sDate = DateTime.now().toDate(); 
		testProject.setStartDate(sDate);
		Date eDate = DateTime.now().plusDays(3).toDate();
		testProject.setEndDate(eDate);
		Assert.assertEquals(0, testProject.getElapsedDays());
	}

	@Test
	public void testGetRemainingDays() {
		int durationInDays = 30;
		Date sDate = DateTime.now().toDate(); 
		testProject.setStartDate(sDate);
		Date eDate = DateTime.now().plusDays(durationInDays).plusHours(1).toDate();		
		testProject.setEndDate(eDate);
		Assert.assertEquals(durationInDays, testProject.getRemainingDays());
	}

	@Test
	public void testGetDateProgressInPercent() {
		Date sDate = DateTime.now().toDate(); 
		testProject.setStartDate(sDate);
		Date eDate = DateTime.now().plusDays(3).toDate();
		testProject.setEndDate(eDate);
		Assert.assertEquals(0, testProject.getDateProgressInPercent().intValue());
	}

	@Test
	public void testGetQmodel() {
		Assert.assertNull(testProject.getQmodel());
	}

	@Test
	public void testSetQmodel() {
		QModel newQ = new QModel();
		testProject.setQmodel(newQ);
		Assert.assertEquals(newQ, testProject.getQmodel());
	}

	@Test
	public void testGetProcess() {
		Assert.assertNull(testProject.getProcess());
	}

	@Test
	public void testSetProcess() {
		eu.uqasar.model.process.Process testProcess = new eu.uqasar.model.process.Process();
		testProject.setProcess(testProcess);
		Assert.assertEquals(testProcess, testProject.getProcess());
	}

	@Test
	public void testGetProduct() {
		Assert.assertNull(testProject.getProduct());
	}

	@Test
	public void testSetProduct() {
		Product testProduct = new Product();
		testProject.setProduct(testProduct);
		Assert.assertEquals(testProduct, testProject.getProduct());
	}

	@Test
	public void testGetValue() {
		Assert.assertNull(testProject.getValue());
	}

	@Test
	public void testSetValue() {
		final Double VAL = 15.2;
		testProject.setValue(VAL);
		Assert.assertEquals(VAL, testProject.getValue());
	}

	@Test
	public void testGetLastUpdated() {
		Assert.assertNotNull(testProject.getLastUpdated());		
	}

	@Test
	public void testSetLastUpdated() {
		Date now = DateTime.now().toDate();
		testProject.setLastUpdated(now);
		Assert.assertEquals(now, testProject.getLastUpdated());
	}
}
