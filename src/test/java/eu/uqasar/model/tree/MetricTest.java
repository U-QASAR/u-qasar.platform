package eu.uqasar.model.tree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.quality.indicator.Purpose;
import junit.framework.Assert;

public class MetricTest {

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
		LifeCycleStage stage = LifeCycleStage.Testing;
		metric.setLifeCycleStage(stage);
		Assert.assertEquals(stage, metric.getLifeCycleStage());
	}

	@Test
	public void testGetQualityObjective() {
		Assert.assertEquals(qo, metric.getQualityObjective());
	}

	@Test
	public void testGetQualityIndicator() {
		Assert.assertEquals(qi,  metric.getQualityIndicator());
	}	

	@Test
	public void testGetIconType() {
		Assert.assertNotNull(metric.getIconType());
	}

	@Test
	public void testGetIndicatorPurpose() {
		Assert.assertNotNull(metric.getIndicatorPurpose());
	}

	@Test
	public void testGetMetricSource() {
		Assert.assertEquals(MetricSource.Manual, metric.getMetricSource());
	}

	@Test
	public void testGetChildType() {
		Assert.assertEquals(TreeNode.class, metric.getChildType());
	}

	@Test
	public void testGetMetricType() {
		Assert.assertNull(metric.getMetricType());
	}

	@Test
	public void testSetMetricType() {
		metric.setMetricType("Test type");
		Assert.assertEquals("Test type", metric.getMetricType());
	}

	@Test
	public void testGetUnit() {
		Assert.assertEquals("Unit", metric.getUnit());
	}

	@Test
	public void testSetUnit() {
		String testUnit = "Test Unit";
		metric.setUnit(testUnit);
		Assert.assertEquals(testUnit, metric.getUnit());
	}

	@Test
	public void testGetQmMetric() {
		Assert.assertNull(metric.getQmMetric());
		QMMetric qmm = new QMMetric();
		qmm.setName("Test");
		metric.setQmMetric(qmm);
		Assert.assertEquals(qmm, metric.getQmMetric());
	}

	@Test
	public void testSetQmMetric() {
		QMMetric qmm = new QMMetric();
		qmm.setName("New Test");
		metric.setQmMetric(qmm);
		Assert.assertEquals(qmm, metric.getQmMetric());
	}
}
