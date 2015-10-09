package eu.uqasar.model.formula;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.uqasar.model.formula.Formula;

@RunWith(Arquillian.class)
public class FormulaTest {

	private static Logger logger = Logger.getLogger(FormulaTest.class);

	/**
	 * Test evaluation of a valid formula string. 
	 */
	@Test
	public void testEvalFormulaStr() {

		logger.info("Test eval formula String");

		Double expected = 15.0;
		String evalStr = "5*3";
		Object resObject = Formula.evalFormulaStr(evalStr);
		Assert.assertNotNull(resObject);
		Double result = (Double) resObject;
		Assert.assertEquals(expected, result);
	}
	
	/**
	 * Test a null formula 
	 */
	@Test
	public void testNullFormula() {
		logger.info("Test null Formula");
		String formula = null;
		Object resObject = Formula.evalFormula(formula);
		Assert.assertNull(resObject);
	}
	
	/**
	 * Test an invalid formula; should return null value
	 */
	public void testInvalidFormula() {
		logger.info("Test invalid Formula");
		String formula = "ax";
		Object resObject = Formula.evalFormula(formula);
		Assert.assertNull(resObject);
	}
}
