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


import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.wicket.util.file.File;
import org.jboss.solder.logging.Logger;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.lifecycle.RupStage;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.measure.Scale;
import eu.uqasar.model.measure.Unit;
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.qmtree.QModelStatus;
import eu.uqasar.model.quality.indicator.Domain;
import eu.uqasar.model.quality.indicator.Paradigm;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Version;
import eu.uqasar.model.role.Role;

public class QModelJsonWriterTest extends TestCase {

	private static Logger logger = Logger.getLogger(QModelJsonWriterTest.class);

	private QModel qm;
	private QModelJsonWriter writer;
	private File file;
	
	
	@Before
	public void setUp() {
		writer = new QModelJsonWriter();
		qm = createQModel();
	};
	
	@Test
	public void testCreateJsonFile() {
		logger.info("testCreateJsonFile start");
			try {
				file = writer.createJsonFile(qm);
				Assert.assertNotNull(file);
			} catch (IOException e) {
				Assert.assertTrue(false);
			}
	}
	
	/**
	 * 
	 * @return
	 */
	private QModel createQModel() {
		List<Role> roles = Role.getAllRoles();
		List<Domain> domains = Domain.getAllDomains();
		
		QModel qm1 = new QModel("Quality Model A", "Quality Model A, U-QASAR");
		qm1.setIsActive(QModelStatus.NotActive);
		qm1.setEdition("0.0");
		qm1.setUpdateDate(DateTime.now().toDate());
		
		QMQualityObjective qo1 = new QMQualityObjective("Code coverage above 80%", qm1);
		qo1.setTargetAudience(roles);	
		// Set Objective "Code coverage above 80%" description
		qo1.setDescription("Average degree to which the source code  is tested by the set of Test Cases designed for the system at all Test Levels (i.e. Unit, Integration and Acceptance)");
		qo1.setLowerLimit((double) 80);
		qo1.setUpperLimit((double) 90);
		qo1.setDomain(domains);
		qo1.setIndicatorPurpose(Purpose.getAllPurposes().get(0));
		qo1.setParadigm(Paradigm.getAllParadigms().get(0));
		qo1.setCompleted(true);
		
		QMQualityIndicator qi1 = new QMQualityIndicator("Unit Test coverage", qo1);
		qi1.setTargetAudience(roles);
		qi1.setDescription("Average degree to which the source code  is tested by the set of Unit Tests. It should be above 60%");
		qi1.setLowerLimit((double) Float.MIN_VALUE);
		qi1.setUpperLimit((double) 100);
		qi1.setIndicatorPurpose(qo1.getIndicatorPurpose());
		qi1.setParadigm(qo1.getParadigm());
		qi1.setLifeCycleStage(LifeCycleStage.getAllLifeCycleStages().get(0));
		qi1.setRupStage(RupStage.getAllRupStages().get(0));
		qi1.setVersion(Version.getAllVersions().get(0));
		qi1.setCompleted(true);
		
		QMMetric m1 = new QMMetric("Lines covered by Unit Tests", qi1);
		m1.setDescription("Number of lines of code covered by Unit Tests");
		m1.setSource(MetricSource.getAllMetricSources().get(0));
		m1.setScale(Scale.getAllScales().get(0));
		m1.setUnit(Unit.getAllUnits().get(0));
		m1.setCompleted(true);
		
		qm1.setCompleted(true);
		
		return qm1;
	}

	@After
	public void tearDown() {
  		file.delete();
	}
	
}
