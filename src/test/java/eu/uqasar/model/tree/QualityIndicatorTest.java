package eu.uqasar.model.tree;

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
