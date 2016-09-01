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


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import lombok.NoArgsConstructor;
import org.jboss.solder.logging.Logger;

import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;

/**
 * Methods for parsing and evaluating a formula
 *
 */
@NoArgsConstructor
public class Formula {

	private static final Logger logger = Logger.getLogger(Formula.class);
	
	/**
	 * Evaluate a String containing a complete formula by using the JS engine.
	 * @param formula the formula to be evaluated
	 * @return the Object resulting from the eval
	 */
	public static Object evalFormulaStr(String formula) {
		Object res = null;
		if (formula != null) {
			try {
				ScriptEngine engine = new ScriptEngineManager()
						.getEngineByName("JavaScript");
				res = engine.eval(formula);				
			} catch (Exception e) {
				e.printStackTrace();
				return res;
			}
		}
		return res;
	}

	/**
	 * 
	 * @return
	 * @throws ScriptException
	 */
	public static Float evalFormula(String formula) {
		Float result = null;
		if (formula != null) {
			try {
				ScriptEngine engine = new ScriptEngineManager()
						.getEngineByName("JavaScript");
				logger.debug("Evaluating formula "  +formula);
				Object res = engine.eval(formula);
				logger.debug("Evaluated result: " +res);
				if (res != null) {
					double resultDouble = (double) res;
					result = (float) resultDouble;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return result;
			}
		}
		return result;
	}	


	/**
	 * This method parses the formula provided in the String format
	 * The entries are separated by one or more whitespace(s). The 
	 * names of the metrics/indicators have been processed so that the 
	 * whitespaces have been replaced with an underscore ('_'). 
	 * e.g. Number_of_code
	 * @param formula that has to be parsed (names of the entries to be resolved etc.)
	 * @return formula that can be evaluated 
	 */
	public static String parseFormula(String formula) {
		String formulaToEval = "";
		if (formula != null) {
			String[] splitted = formula.split("\\s");
			try {
				InitialContext ic = new InitialContext();
				TreeNodeService treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
				for (int x = 0;  x < splitted.length; x++) {
					logger.debug("splitted[" +x +"]: " +splitted[x]);
					// If the separator character is found in the end of the string,
					// attempt to fetch the actual object and its value for 
					// the actual formula to be evaluated.
					if (splitted[x].endsWith("_")) {
						String nameToSearch = UQasarUtil.unSanitizeName(splitted[x]);
						Object node = treeNodeService.getTreeNodeByName(nameToSearch);
						if (node instanceof QualityObjective) {
							QualityObjective qo = (QualityObjective) node;
							formulaToEval += qo.getValue();
						}
						else if (node instanceof QualityIndicator) {
							QualityIndicator qi = (QualityIndicator) node;
							formulaToEval += qi.getValue();
						} else if (node instanceof Metric) {
							Metric m = (Metric) node;
							formulaToEval += m.getValue();
						}
					} else {
						formulaToEval += splitted[x];
					}
				}
				
			} catch (NamingException e) {
				e.printStackTrace();
			}

			logger.debug("Formula to evaluate: " +formulaToEval);
		}
		return formulaToEval;
	}
}
