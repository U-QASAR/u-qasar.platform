package eu.uqasar.util.io.exporter;

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
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.apache.wicket.util.file.File;
import org.jboss.solder.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.uqasar.model.company.Company;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.qmtree.QModelStatus;
import eu.uqasar.model.quality.indicator.Domain;
import eu.uqasar.model.role.Role;

public class QModelXmlWriterTest {

	private static Logger logger = Logger.getLogger(QModelXmlWriterTest.class);

	private QModel qm;
	private QModelXmlWriter writer;
	private File file;
	
	
	@Before
	public void setUp() {
	
		writer = new QModelXmlWriter();
		qm = createQModel();
	};
	
	
	@Test
	public void testCreateXmlFile() {
		logger.info("testCreateXmlFile start");
		try {
			file = writer.createXmlFile(qm);
			Assert.assertNotNull(file);
		} catch (JAXBException e) {
			Assert.assertTrue(false);
		}
	}
		
		
	/**
	 * 
	 * @return
	 */
	private QModel createQModel() {
		List<Role> roles = new ArrayList<Role>();
		roles.add(Role.Developer);
		List<Domain> domains = new ArrayList<Domain>();
		domains.add(Domain.Bank);
		
		QModel qm1 = new QModel("Quality Model A", "Quality Model A, U-QASAR");
		qm1.setIsActive(QModelStatus.NotActive);
		qm1.setEdition("0.0");
		qm1.setCompany(createCompany());
		qm1.setCompanyId(createCompany().getId());
		
		QMQualityObjective qo1 = new QMQualityObjective("Code coverage above 80%", qm1);
		qo1.setTargetAudience(roles);	
		// Set Objective "Code coverage above 80%" description
		qo1.setDescription("Average degree to which the source code  is tested by the set of Test Cases designed for the system at all Test Levels (i.e. Unit, Integration and Acceptance)");
		qo1.setLowerLimit((double) 80);
		qo1.setUpperLimit((double) 90);
		qo1.setDomain(domains);
		qo1.setIndicatorPurpose(null);
		qo1.setParadigm(null);
		qo1.setCompleted(true);
		
		QMQualityIndicator qi1 = new QMQualityIndicator("Unit Test coverage", qo1);
		qi1.setTargetAudience(roles);
		qi1.setDescription("Average degree to which the source code  is tested by the set of Unit Tests. It should be above 60%");
		qi1.setLowerLimit(Double.MIN_VALUE);
		qi1.setUpperLimit((double) 100);
		qi1.setIndicatorPurpose(qo1.getIndicatorPurpose());
		qi1.setParadigm(qo1.getParadigm());
		qi1.setLifeCycleStage(null);
		qi1.setCompleted(true);
		
		QMMetric m1 = new QMMetric("Lines covered by Unit Tests", qi1);
		m1.setDescription("Number of lines of code covered by Unit Tests");
		m1.setSource(MetricSource.StaticAnalysis);
		m1.setScale(null);
		m1.setUnit(null);
		m1.setCompleted(true);
		
		qm1.setCompleted(true);
		
		return qm1;
	}

	/**
	 * Create company.
	 * @return company
	 */
	private Company createCompany() {
		Company testComp = new Company("Test company");
		testComp.setShortName("TEST");
		testComp.setPhone("");
		testComp.setFax("");
		testComp.setStreet("");
		testComp.setZipcode(12345);
		testComp.setCity("Test");
		testComp.setCountry("UK");
		testComp.setId((long)11111);
    	
		return testComp;
	}
	
	@After
	public void tearDown() {
  		file.delete();
	}
	
}
