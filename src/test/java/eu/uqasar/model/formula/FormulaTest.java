package eu.uqasar.model.formula;

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


import org.jboss.solder.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

public class FormulaTest {

	private static Logger logger = Logger.getLogger(FormulaTest.class);

	/**
	 * Test evaluation of a valid formula string. 
	 */
	@Test
	public void testEvalFormulaStr() {

		logger.info("Test eval formula String");
		
		Double result;
		Double expected = 15.0;
		String evalStr = "5*3";
		Object resObject = Formula.evalFormulaStr(evalStr);
		Assert.assertNotNull(resObject);
		if (resObject instanceof Integer) {
			result = ((Integer) resObject).doubleValue();
		} else {
			result = (Double) resObject;
		}
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
