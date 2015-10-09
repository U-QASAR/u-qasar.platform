package eu.uqasar.model.tree;

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
