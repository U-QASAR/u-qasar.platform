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


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.quality.indicator.Purpose;
import junit.framework.Assert;

public class QualityObjectiveTest {

	private Project proj;
	private QualityObjective qo;
	private QualityIndicator qi;
	private Metric metric;

	@Before
	public void setUp() throws Exception {
		proj = new Project("Test Project", "Test");
		proj.setLifeCycleStage(LifeCycleStage.Testing);
		qo = new QualityObjective("QO", proj);
		qo.setLifeCycleStage(LifeCycleStage.Testing);
		qi = new QualityIndicator("QO", qo);
		qi.setIndicatorPurpose(Purpose.Product);
		metric  = new Metric("Metric", qi);
		metric.setMetricSource(MetricSource.Manual);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetIconType() {
		Assert.assertNotNull(qo.getIconType());
	}


	@Test
	public void testGetQualityPurpose() {
		Assert.assertNotNull(qo.getQualityPurpose());
	}

	@Test
	public void testSetQualityPurpose() {
		Purpose p = Purpose.Process;
		qo.setQualityPurpose(p);
		Assert.assertEquals(p, qo.getQualityPurpose());
	}

	@Test
	public void testGetChildType() {
		Assert.assertEquals(QualityIndicator.class, qo.getChildType());
	}

	@Test
	public void testGetQmObjective() {
		Assert.assertNull(qo.getQmObjective());
	}

	@Test
	public void testSetQmObjective() {
		QMQualityObjective qmqo = new QMQualityObjective();
		qo.setQmObjective(qmqo);
		Assert.assertEquals(qmqo, qo.getQmObjective());
	}

	@Test
	public void testGetViewFormula() {
		Assert.assertNull(qo.getViewFormula());
	}

	@Test
	public void testSetViewFormula() {
		qo.setViewFormula("1+2");
		Assert.assertEquals("1+2", qo.getViewFormula());
	}

	@Test
	public void testGetUseFormula() {
		// Default is true
		Assert.assertTrue(qo.getUseFormula());
	}

	@Test
	public void testSetUseFormula() {
		qo.setUseFormula(false);
		Assert.assertFalse(qo.getUseFormula());
	}

	@Test
	public void testCompareTo() {
		Assert.assertEquals(0, qo.compareTo(qo));
	}

}
