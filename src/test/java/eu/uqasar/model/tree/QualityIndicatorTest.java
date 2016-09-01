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
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Type;
import junit.framework.Assert;

public class QualityIndicatorTest {

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
	public void testGetLifeCycleStage() {
		Assert.assertNotNull(qi.getLifeCycleStage());
	}

	@Test
	public void testGetQualityObjective() {
		Assert.assertEquals(qo, qi.getQualityObjective());
	}

	@Test
	public void testGetIconType() {
		Assert.assertNotNull(qi.getIconType());
	}

	@Test
	public void testGetIndicatorPurpose() {
		Assert.assertNotNull(qi.getIndicatorPurpose());
		Assert.assertEquals(Purpose.Product, qi.getIndicatorPurpose());
	}

	@Test
	public void testGetIndicatorType() {
		Assert.assertNotNull(qi.getIndicatorType());
		Assert.assertEquals(Type.Automatic, qi.getIndicatorType());
	}

	@Test
	public void testGetChildType() {
		Assert.assertEquals(Metric.class, qi.getChildType());
	}

	@Test
	public void testGetQmIndicator() {
		Assert.assertNull(qi.getQmIndicator());
	}

	@Test
	public void testSetQmIndicator() {
		QMQualityIndicator qmqi = new QMQualityIndicator();
		qi.setQmIndicator(qmqi);
		Assert.assertEquals(qmqi, qi.getQmIndicator());
	}

	@Test
	public void testGetViewFormula() {
		Assert.assertNull(qi.getViewFormula());
	}

	@Test
	public void testSetViewFormula() {
		String formula = "1+2";
		qi.setViewFormula(formula);
		Assert.assertEquals(formula, qi.getViewFormula());
	}

	@Test
	public void testGetUseFormula() {
		// default is true
		Assert.assertTrue(qi.getUseFormula());
	}

	@Test
	public void testSetUseFormula() {
		qi.setUseFormula(false);
		Assert.assertFalse(qi.getUseFormula());
	}

}
