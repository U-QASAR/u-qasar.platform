package eu.uqasar.util;

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


import java.io.FileOutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.inject.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.wicket.model.IModel;
import org.jboss.solder.logging.Logger;
import org.topbraid.spin.arq.ARQ2SPIN;
import org.topbraid.spin.arq.ARQFactory;
import org.topbraid.spin.model.Select;
import org.topbraid.spin.system.SPINModuleRegistry;

import thewebsemantic.Bean2RDF;
import thewebsemantic.Sparql;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.util.FileUtils;

import eu.uqasar.model.formula.Formula;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.meta.ContinuousIntegrationTool;
import eu.uqasar.model.meta.CustomerType;
import eu.uqasar.model.meta.IssueTrackingTool;
import eu.uqasar.model.meta.MetaData;
import eu.uqasar.model.meta.ProgrammingLanguage;
import eu.uqasar.model.meta.ProjectType;
import eu.uqasar.model.meta.QModelTagData;
import eu.uqasar.model.meta.SoftwareDevelopmentMethodology;
import eu.uqasar.model.meta.SoftwareLicense;
import eu.uqasar.model.meta.SoftwareType;
import eu.uqasar.model.meta.SourceCodeManagementTool;
import eu.uqasar.model.meta.StaticAnalysisTool;
import eu.uqasar.model.meta.TestManagementTool;
import eu.uqasar.model.meta.Topic;
import eu.uqasar.model.notification.INotification;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.model.user.User;
import eu.uqasar.service.dataadapter.AdapterDataService;
import eu.uqasar.service.meta.ContinuousIntegrationToolService;
import eu.uqasar.service.meta.CustomerTypeService;
import eu.uqasar.service.meta.IssueTrackingToolService;
import eu.uqasar.service.meta.ProgrammingLanguageService;
import eu.uqasar.service.meta.ProjectTypeService;
import eu.uqasar.service.meta.QModelTagDataService;
import eu.uqasar.service.meta.SoftwareDevelopmentMethodologyService;
import eu.uqasar.service.meta.SoftwareLicenseService;
import eu.uqasar.service.meta.SoftwareTypeService;
import eu.uqasar.service.meta.SourceCodeManagementToolService;
import eu.uqasar.service.meta.StaticAnalysisToolService;
import eu.uqasar.service.meta.TestManagementToolService;
import eu.uqasar.service.meta.TopicService;
import eu.uqasar.service.similarity.ProjectSimilarityService;
import eu.uqasar.service.similarity.QualityObjectiveSimilarityService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.util.resources.ResourceBundleLocator;

/**
 * Various utilities used on the platform 
 *
 */
@Setter
@Getter
@Singleton
public class UQasarUtil {

	final static String SEPARATOR = 
			java.nio.file.FileSystems.getDefault().getSeparator(); 
	final static String ONTOLOGYFILE = "uq-ontology-model.rdf";


	protected static ResourceBundle res = null;
	private static Logger logger = Logger.getLogger(UQasarUtil.class);

	// List containing the notifications triggered by the rules' engine
	@Setter
	@Getter
	private static List<INotification> notifications = new ArrayList<>();
	
	@Setter
	@Getter
	private static Date latestTreeUpdateDate = new Date();
	
	@Setter
	@Getter
	private static Model uqModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	// Enumeration for the various suggestion types
	public enum SuggestionType {
		
		NO_SUGGESTION("no_suggestion"),
		QO_REMOVE("qo.remove"),
		QO_REPLACE("qo.replace"),
		METRIC_REMOVE("metric.remove"),

		;

		private final String labelKey;

		SuggestionType(final String labelKey) {
			this.labelKey = labelKey;
		}

		@Override
		public String toString() {
			return getLabelModel().getObject();
		}
		
		public IModel<String> getLabelModel() {
			return ResourceBundleLocator.getLabelModel(UQasarUtil.class, "suggestion." + labelKey);
		}
		
		public static List<SuggestionType> getAllSuggestionTypes(){
			return Arrays.asList(values());
		}
	}
	

	/**
	 * Get the temp directory to be used; in case JBoss is used, use 
	 * its temp dir, otherwise the user temp.
	 * @return
	 */
	public static String getTempDirPath() {
		String tempDirectory = null;

		Properties systemProp = System.getProperties();
		// In case JBoss is used, use the JBoss temp dir
		if (systemProp.containsKey("jboss.server.temp.dir")) {
			tempDirectory = 
					systemProp.getProperty("jboss.server.temp.dir");
			if (!tempDirectory.endsWith(SEPARATOR)) {
				tempDirectory += SEPARATOR;
			}
		} 
		// Otherwise use a temp directory
		else {
			tempDirectory = System.getProperty("java.io.tmpdir");
			if (!tempDirectory.endsWith(SEPARATOR)) {
				tempDirectory += SEPARATOR;
			}
		}

		return tempDirectory;
	}


	/**
	 * Get the data dir to be used; in case JBoss is used, use its data dir, 
	 * otherwise the user temp.
	 * @return
	 */
	public static String getDataDirPath() {
		String dataDirectory = null;
		Properties systemProp = System.getProperties();
		// In case JBoss is used
		if (systemProp.containsKey("jboss.server.data.dir")) {
			dataDirectory = systemProp.getProperty("jboss.server.data.dir");
			if (!dataDirectory.endsWith(SEPARATOR)) {
				dataDirectory += SEPARATOR;
			}
		} 
		// Otherwise use a temp directory
		else {
			dataDirectory = System.getProperty("java.io.tmpdir");
			if (!dataDirectory.endsWith(SEPARATOR)) {
				dataDirectory += SEPARATOR;
			}			
		}

		return dataDirectory;
	}

	/**
	 * Construct a formula string to be evaluated.
	 * @param varsAndOpers
	 * @return
	 */
	public static String constructFormula(List<Object> varsAndOpers) {
		String formula = "";

		for (Object object : varsAndOpers) {
			formula += object;
		}

		logger.info("Constructed formula to be evaluated: " +formula);
		return formula;
	}

	/**
	 * Get properties resource
	 * @return
	 */
	public static ResourceBundle getProperties() {
		if (res == null) {
			loadProperties();
		}
		return res;
	}

	/**
	 * Load properties file
	 */
	private static void loadProperties() {
		res = ResourceBundle.getBundle("uqasar");
	}


	/**
	 * Round double to two decimals
	 * @param number
	 * @return
	 */
	public static Double round(double number) {
		DecimalFormat df = new DecimalFormat("#,##");
		df.setRoundingMode(RoundingMode.DOWN);
		String s = df.format(number);
		return Double.valueOf(s);		
	}


	/**
	 * Replace whitespace chars with the defined separator and escape the 
	 * String  
	 * @param name
	 */
	public static String sanitizeName(String name) {
		final String SEPARATOR = "_";
		if (name.contains(" ")) {
			String processed = name.replaceAll(" ", SEPARATOR).concat(SEPARATOR);
			return StringEscapeUtils.escapeJavaScript(processed);
		}
		// Otherwise return the actual string
		return name;
	}


	/**
	 * Replace the separator with whitespaces and unescape the String
	 * @param name
	 * @return
	 */
	public static String unSanitizeName(String name) {
		final String SEPARATOR = "_";
		if (name.contains(SEPARATOR)) {
			return StringEscapeUtils.unescapeJavaScript(name)
					.replaceAll(SEPARATOR, " ").trim();
		}
		return StringEscapeUtils.unescapeJavaScript(name).trim();
	}

	/**
	 * Get the pwd used for encrypting and decrypting adapter passwords
	 * TODO: Where could this be saved securely 
	 * @return
	 */
	public static String getEncDecPwd() {
        return "jvnqeiPIqX_6cpdOYfDc7sHk7nEIv4zRn5984JwIHnq4AX7XQt";
	}

	/**
	 * Get the Jira metric names
	 * TODO: Fetch the list of metrics from uQasarMetric
	 * @return
	 */
	public static List<String> getJiraMetricNames() {
		String metrics[] = {
				"PROJECTS_PER_SYSTEM_INSTANCE",
				"ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE",
				"FIXED_ISSUES_PER_PROJECT",
				"UNRESOLVED_ISSUES_PER_PROJECT",
				"UNRESOLVED_BUG_ISSUES_PER_PROJECT",
				"UNRESOLVED_TASK_ISSUES_PER_PROJECT"
		};

		return Arrays.asList(metrics);
	}

	public static List<String> getJiraMetricNamesAbbreviated() {
		String metrics[] = {
				"Projects",
				"Issues",
				"Fixed issues",
				"Unresolved issues"
		};

		return Arrays.asList(metrics);
	}


	/**
	 * TODO: Fetch the list of metrics from uQasarMetric
	 * Get the Sonar metric names
	 * @return
	 */
	public static List<String> getSonarMetricNames() {
		
		String metrics[] =  {
				"NCLOC",                    // lines of code (NC=non comment)
			    "LINES",                    // lines (i.e. all lines)
			    "COMMENT_LINES",            // number of comment lines
			    "STATEMENTS",               // number of statements
			    "CLASSES",                  // number of classes
			    "FILES",                    // number of files
			    "DIRECTORIES",              // number of directories
			    "FUNCTIONS",                // number of functions
			    "COMMENT_LINES_DENSITY",    // the density of comment lines (number in [0..100])

			    "DUPLICATED_LINES",         // number of duplicated lines
			    "DUPLICATED_LINES_DENSITY", // the density of duplicated lines (number in [0..100])
			    "COMPLEXITY",               // cyclomatic complexity of the project
			    "FUNCTION_COMPLEXITY",      // the average cyclomatic complexity of a function
			    "FILE_COMPLEXITY",          // the average cyclomatic complexity of a source code file
			    "CLASS_COMPLEXITY",         // the average cyclomatic complexity of a class
			    "UT_COVERAGE",              // code coverage by unit tests
			    "AT_COVERAGE",              // code coverage by acceptance tests
			    "OVERALL_COVERAGE",         // code coverage by both, unit and acceptance tests
			    "PACKAGE_TANGLES",          // Number of file dependencies to cut in order to remove all cycles between directories.
			    "PACKAGE_TANGLE_INDEX",     // Level of directory interdependency. Best value (0%) means that there is no cycle and worst value (100%) means that directories are really tangled.
			    "TEST_SUCCESS_DENSITY",     // ratio of successfully executed tests to the overall number of te
			    "TEST_FAILURES",            // number of failed tests (failure = test could be run and is failed)
			    "TEST_ERRORS",              // number of errored tests (error = test could not be run)
			    "TESTS",                     // number of tests

				// Sonar, available metrics that can be used if they are implemented on the adapter

				//			      /* core statistics */
				//				"LINES",
				//				"NCLOC",
				//				"CLASSES",
				//				"FILES",
				//				"DIRECTORIES",
				//				"PACKAGES",
				//				"FUNCTIONS",
				//				"ACCESSORS",
				//				"STATEMENTS",
				//				"PUBLIC_API",
				//			      
				//			      /* complexity */
				//				"COMPLEXITY",
				//				"CLASS_COMPLEXITY",
				//				"FUNCTION_COMPLEXITY",
				//				"FILE_COMPLEXITY", 
				//				"CLASS_COMPLEXITY_DISTRIBUTION",
				//				"FUNCTION_COMPLEXITY_DISTRIBUTION",
				//			      
				//			      /* comments */
				//				"COMMENT_LINES",
				//				"COMMENT_LINES_DENSITY",
				//				"PUBLIC_DOCUMENTED_API_DENSITY",
				//				"PUBLIC_UNDOCUMENTED_API",
				//				"COMMENTED_OUT_CODE_LINES",
				//			      
				//			      /* unit tests */
				//				"TESTS",
				//				"TEST_EXECUTION_TIME",
				//				"TEST_ERRORS",
				//				"SKIPPED_TESTS",
				//				"TEST_FAILURES",
				//				"TEST_SUCCESS_DENSITY",
				//				"TEST_DATA",
				//			      
				//			      /* code coverage by unit tests */
				//				"COVERAGE",
				//				"LINES_TO_COVER",
				//				"UNCOVERED_LINES",
				//				"LINE_COVERAGE",
				//				"COVERAGE_LINE_HITS_DATA",
				//			      
				//				"CONDITIONS_TO_COVER",
				//				"UNCOVERED_CONDITIONS",
				//				"BRANCH_COVERAGE",
				//				"BRANCH_COVERAGE_HITS_DATA",
				//			      
				//				"UNCOVERED_COMPLEXITY_BY_TESTS",
				//			      
				//			      /* duplicated lines */
				//				"DUPLICATED_LINES", 
				//				"DUPLICATED_BLOCKS",
				//				"DUPLICATED_FILES",
				//				"DUPLICATED_LINES_DENSITY",
				//				"DUPLICATIONS_DATA",
				//			      
				//			      /* coding rules */
				//				"USABILITY",
				//				"RELIABILITY",
				//				"EFFICIENCY",
				//				"PORTABILITY",
				//				"MAINTAINABILITY",
				//			      
				//				"WEIGHTED_VIOLATIONS",
				//				"VIOLATIONS_DENSITY",
				//				"VIOLATIONS",
				//				"BLOCKER_VIOLATIONS",
				//				"CRITICAL_VIOLATIONS",
				//				"MAJOR_VIOLATIONS",
				//				"MINOR_VIOLATIONS",
				//				"INFO_VIOLATIONS",
		};

		return Arrays.asList(metrics);
	}


	/**
	 * TODO: Fetch the list of metrics from uQasarMetric
	 * Get the TestLink metric names
	 * @return 
	 */
	public static List<String> getTestLinkMetricNames() {
		String metrics[] =  {
				"TEST_P",
				"TEST_F",
				"TEST_B",
				"TEST_N",
				"TEST_TOTAL"
		};

		return Arrays.asList(metrics);
	}

	/**
	 * TODO: Fetch the list of metrics from uQasarMetric
	 * Get the Cubes direct Metric names
	 * @return Cubes metric Names
	 */
	public static List<String> getCubesMetricNames() {
		String metrics[] =  {
				"cube/jira/aggregate?cut=Status:Done",
				"cube/jira/aggregate?cut=Type:Bug",
				"cube/jira/aggregate?cut=Status:To%20Do",
				"cube/jira/aggregate?cut=Priority:Blocker",
				"cube/jira/aggregate?cut=Priority:Critical",
				"cube/jira/aggregate?cut=Priority:Major",
				"cube/jira/aggregate?cut=Priority:Minor",
				"cube/jira/aggregate?cut=Status:Open&cut=Priority:Major",
//				"cube/jira/fact/UQ-1", 
//				"cube/jira/model",
//				"cube/jira/cell",
//				"cube/jira/members/Status",
//				"cube/jira/facts",
//				"cubes"
		};

		return Arrays.asList(metrics);
	}

	
	public static List<String> getGitlabMetricNames() {
		
		String metrics[] =  {
				"GIT_COMMITS",
				"GIT_PROJECTS"				
		};
		
		return Arrays.asList(metrics);		
	}
	
	/**
	 * Convert minutes to ms
	 * @param minutes
	 * @return
	 */
	public static int minsToMs(int minutes) {
		final int SEC = 60;
		final int MS = 1000;
        return minutes * SEC * MS;
	}


	/**
	 * Compare two dates returning true if equal and false if not. This comparison omits milliseconds
	 * @param dateOne
	 * @param dateTwo
	 * @return
	 */
	public static boolean isDateEqual(Date dateOne, Date dateTwo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date1 = sdf.format(dateOne);
		String date2 = sdf.format(dateTwo);

		if (date1.equals(date2)) {
			return true;
		}

		return false;
	}
	
	/**
	 * Takes care of computing the tree values (update metrics and 
	 * compute QO/QI values based on that) starting from the root node.
	 * @param projectRoot Node
	 */
	public static void updateTree(TreeNode projectRoot) {
		// Create a timestamp at the start of the tree update so that 
		// all the updated entities have the same value for the date. 
		setLatestTreeUpdateDate(new Date());
		logger.info("Updating the QA Project tree...");
		postorder(projectRoot);
	}

	/**
	 * Traverse the tree in postorder and update tree values
	 * @param node
	 */
	private static void postorder(TreeNode node) {

		if (node == null) {
			return;
		}		

		logger.debug("------------postorder: " +node.getName() +"---------------");

		// Iterate the node children
		for (Object o : node.getChildren()) {
			TreeNode nodeChild = (TreeNode) o;
			UQasarUtil.postorder(nodeChild);
		}
		logger.debug("Traversing project tree in postorder..."  +node.toString());
		// Update the value
		try {
			InitialContext ic = new InitialContext();
			AdapterDataService adapterDataService = new AdapterDataService();
			TreeNodeService treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
			
			if (node instanceof Metric) {
				Metric metric = (Metric) node;
				logger.debug("Recomputing the value of the Metric " +node);
				Float value = null;
				if (metric.getMetricSource() == MetricSource.Manual) {
					metric.updateQualityStatus();	
				} else {
					value = adapterDataService.getMetricValue(metric.getMetricSource(), metric.getMetricType(), metric.getProject());
					metric.setValue(value);
				}
				metric.setLastUpdated(getLatestTreeUpdateDate());
				metric.addHistoricValue();
				// End Metric node treatment 
			} else if (node instanceof QualityIndicator) {
				logger.info("Recomputing the value of the Quality Indicator " +node);
				QualityIndicator qi = (QualityIndicator) node;
				if(qi.getUseFormula()){
					String formulaToEval = Formula.parseFormula(qi.getViewFormula());
					if (formulaToEval != null && !formulaToEval.isEmpty()) {

						Float computedValue = Formula.evalFormula(formulaToEval);
						if (computedValue != null && !computedValue.isNaN()) {
							qi.setValue(computedValue);
							qi.setLastUpdated(getLatestTreeUpdateDate());
							treeNodeService.update(qi);
						}

					}
				} else {
					float achieved = 0;
					float denominator = 0;
					for (final TreeNode me : qi.getChildren()) {
						float weight = ((Metric)me).getWeight();
						if (me.getQualityStatus() == QualityStatus.Green) {
							achieved += weight;
						}
						denominator +=weight;
					}
					if(denominator == 0 )qi.getChildren().size();
					qi.setValue(achieved * 100 / denominator);
				}
				qi.setLastUpdated(getLatestTreeUpdateDate());
				qi.addHistoricValue();
				// End Q.Indicator node treatment 
			} else if (node instanceof QualityObjective) {
				logger.info("Recomputing the value of the Quality Objective " +node);
				QualityObjective qo = (QualityObjective) node;
				if(qo.getUseFormula()){
					String formulaToEval = Formula.parseFormula(qo.getViewFormula());
					if (formulaToEval != null && !formulaToEval.isEmpty()) {

						Float computedValue = Formula.evalFormula(formulaToEval);
						if (computedValue != null && !computedValue.isNaN()) {
							qo.setValue(computedValue);
							qo.setLastUpdated(getLatestTreeUpdateDate());
						}							

					}
				} else {
					float denominator = 0;
					float achieved= 0;
					for(final TreeNode qi : qo.getChildren()){
						float weight = ((QualityIndicator)qi).getWeight();
						if (qi.getQualityStatus() == QualityStatus.Green){
							achieved += weight;
						}
						denominator +=weight;
					}
					qo.setValue(achieved * 100 / denominator);
				}
				qo.setLastUpdated(getLatestTreeUpdateDate());
				qo.addHistoricValue();
				// End Quality Objective node treatment 
			} else if (node instanceof Project) {
				logger.info("Recomputing the value of the Project " +node);
				Project prj = (Project) node;
				double qoValueSum = 0;
				double denominator = 0;
				for (Object o : node.getChildren()) {
					QualityObjective qo = (QualityObjective) o;
					if (qo.getWeight() == 0) {
						continue;
					}
					qoValueSum += qo.getValue() * (prj.isFormulaAverage()? qo.getWeight() : 1);
					denominator+= prj.isFormulaAverage()? qo.getWeight() : 1;
				}

				// bad idea to divide something under 0
				if (denominator == 0) {
					denominator = 1;
				}

				Double computedValue = qoValueSum/denominator;

				if (computedValue != null && !computedValue.isNaN() && !computedValue.isInfinite()) {
					prj.setValue(computedValue);
				}

				prj.setLastUpdated(getLatestTreeUpdateDate());
				prj.addHistoricValue();
				logger.debug(" [" + qoValueSum + "] denominator [" + denominator + "] " + computedValue);
				// End Project node treatment 
			}
			
			// Get a (possible) suggestion for the tree node
			Multimap<?, ?> suggestions = getSuggestionForNode(node);
			//TODO: take all the suggestions into account
			Object[] types = suggestions.keys().toArray();
			Object[] suggestionsValues = suggestions.values().toArray();
			if (types.length > 0) {
				// for now use the first item as suggestion
				SuggestionType stype = (SuggestionType) types[0];
				node.setSuggestionType(stype);
				if (suggestionsValues[0] != null && !suggestionsValues[0].equals("")) {
					node.setSuggestionValue((String) suggestionsValues[0]);
				}						
			}
			
			treeNodeService.update(node);

		} catch (NamingException e) {
			e.printStackTrace();
		}

		return;
	}

	
	/**
     * Takes care of computing the tree values (update metrics and 
     * compute QO/QI values based on that) starting from the root node.
     * @param projectRoot Node
     */
    public static void updateTreeWithParticularNode(TreeNode projectRoot,TreeNode projectTreeNode) {
        // Create a timestamp at the start of the tree update so that 
        // all the updated entities have the same value for the date. 
        setLatestTreeUpdateDate(new Date());
        logger.info("Updating the QA Project tree...");
        postorderWithParticularNode(projectRoot,projectTreeNode);
    }

	
    /**
     * Traverse the tree in postorder and update tree values
     * @param node
     */
    private static void postorderWithParticularNode(TreeNode node,TreeNode projectTreeNode) {

        if (node == null) {
            return;
        }  
        
        if (projectTreeNode == null) {
            return;
        }

        logger.debug("------------postorder: " +projectTreeNode.getName() +"---------------");

       
         
        logger.debug("Traversing project tree in postorder..."  +projectTreeNode.toString());
        // Update the value
        try {
            InitialContext ic = new InitialContext();
            AdapterDataService adapterDataService = new AdapterDataService();
            TreeNodeService treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
            
            if (projectTreeNode instanceof Metric) {
                Metric metric = (Metric) projectTreeNode;
                logger.debug("Recomputing the value of the Metric " +projectTreeNode);
                Float value = null;
                if (metric.getMetricSource() == MetricSource.Manual) {
                    metric.updateQualityStatus();   
                } else {
                    value = adapterDataService.getMetricValue(metric.getMetricSource(), metric.getMetricType(), metric.getProject());
                    metric.setValue(value);
                }
                metric.setLastUpdated(getLatestTreeUpdateDate());
                metric.addHistoricValue();
                // End Metric node treatment 
            } else if (projectTreeNode instanceof QualityIndicator) {
                logger.info("Recomputing the value of the Quality Indicator " +projectTreeNode);
                QualityIndicator qi = (QualityIndicator) projectTreeNode;
                if(qi.getUseFormula()){
                    String formulaToEval = Formula.parseFormula(qi.getViewFormula());
                    if (formulaToEval != null && !formulaToEval.isEmpty()) {

                        Float computedValue = Formula.evalFormula(formulaToEval);
                        if (computedValue != null && !computedValue.isNaN()) {
                            qi.setValue(computedValue);
                            qi.setLastUpdated(getLatestTreeUpdateDate());
                            treeNodeService.update(qi);
                        }

                    }
                } else {
                    float achieved = 0;
                    float denominator = 0;
                    for (final TreeNode me : qi.getChildren()) {
                        float weight = ((Metric)me).getWeight();
                        if (me.getQualityStatus() == QualityStatus.Green) {
                            achieved += weight;
                        }
                        denominator +=weight;
                    }
                    if(denominator == 0 )qi.getChildren().size();
                    qi.setValue(achieved * 100 / denominator);
                }
                qi.setLastUpdated(getLatestTreeUpdateDate());
                qi.addHistoricValue();
                // End Q.Indicator node treatment 
            } else if (projectTreeNode instanceof QualityObjective) {
                logger.info("Recomputing the value of the Quality Objective " +projectTreeNode);
                QualityObjective qo = (QualityObjective) projectTreeNode;
                if(qo.getUseFormula()){
                    String formulaToEval = Formula.parseFormula(qo.getViewFormula());
                    if (formulaToEval != null && !formulaToEval.isEmpty()) {

                        Float computedValue = Formula.evalFormula(formulaToEval);
                        if (computedValue != null && !computedValue.isNaN()) {
                            qo.setValue(computedValue);
                            qo.setLastUpdated(getLatestTreeUpdateDate());
                        }                           

                    }
                } else {
                    float denominator = 0;
                    float achieved= 0;
                    for(final TreeNode qi : qo.getChildren()){
                        float weight = ((QualityIndicator)qi).getWeight();
                        if (qi.getQualityStatus() == QualityStatus.Green){
                            achieved += weight;
                        }
                        denominator +=weight;
                    }
                    qo.setValue(achieved * 100 / denominator);
                }
                qo.setLastUpdated(getLatestTreeUpdateDate());
                qo.addHistoricValue();
                // End Quality Objective node treatment 
            } else if (projectTreeNode instanceof Project) {
                logger.info("Recomputing the value of the Project " +projectTreeNode);
                Project prj = (Project) projectTreeNode;
                double qoValueSum = 0;
                double denominator = 0;
                for (Object o : projectTreeNode.getChildren()) {
                    QualityObjective qo = (QualityObjective) o;
                    if (qo.getWeight() == 0) {
                        continue;
                    }
                    qoValueSum += qo.getValue() * (prj.isFormulaAverage()? qo.getWeight() : 1);
                    denominator+= prj.isFormulaAverage()? qo.getWeight() : 1;
                }

                // bad idea to divide something under 0
                if (denominator == 0) {
                    denominator = 1;
                }

                Double computedValue = qoValueSum/denominator;

                if (computedValue != null && !computedValue.isNaN() && !computedValue.isInfinite()) {
                    prj.setValue(computedValue);
                }

                prj.setLastUpdated(getLatestTreeUpdateDate());
                prj.addHistoricValue();
                logger.debug(" [" + qoValueSum + "] denominator [" + denominator + "] " + computedValue);
                // End Project node treatment 
            }
            
            // Get a (possible) suggestion for the tree node
            Multimap<?, ?> suggestions = getSuggestionForNode(projectTreeNode);
            //TODO: take all the suggestions into account
            Object[] types = suggestions.keys().toArray();
            Object[] suggestionsValues = suggestions.values().toArray();
            if (types.length > 0) {
                // for now use the first item as suggestion
                SuggestionType stype = (SuggestionType) types[0];
                projectTreeNode.setSuggestionType(stype);
                if (suggestionsValues[0] != null && !suggestionsValues[0].equals("")) {
                    projectTreeNode.setSuggestionValue((String) suggestionsValues[0]);
                }                       
            }
            
            treeNodeService.update(projectTreeNode);

        } catch (NamingException e) {
            e.printStackTrace();
        }

        
        // Iterate the node children
        TreeNode nodeChild = projectTreeNode.getParent();
        UQasarUtil.postorderWithParticularNode(projectTreeNode,nodeChild);
        
        return;
    }


	/**
	 * 
	 * @param node Node whose value is studied
	 * @return
	 */
	private static Multimap<SuggestionType, Object> getSuggestionForNode(TreeNode node) {

		// For storing the suggestion type and the payload
		Multimap<SuggestionType,Object> suggestionsMultimap = ArrayListMultimap.create();
		
		final String EMPTY = "";

		try {
			InitialContext ic = new InitialContext();
			ProjectSimilarityService projectSimilarityService = (ProjectSimilarityService) ic.lookup("java:module/ProjectSimilarityService");
			QualityObjectiveSimilarityService qoSimilarityService = (QualityObjectiveSimilarityService) ic.lookup("java:module/QualityObjectiveSimilarityService");
			//TODO: Finding similar metrics
			
			if (node instanceof QualityObjective) {
				QualityObjective qo = (QualityObjective) node;
				Float value = qo.getValue();
				
				// If the value of a quality objective cannot be computed, 
				// attempt to obtain a suggestion 				
				if (value == null || value.isNaN()) {
					// Attempt to find a suitable project 
					Project proj = qo.getProject();
					Project similarProject = null; 
					if (proj != null) {
						// Get a list of similar projects
						List<Project> similarProjects = projectSimilarityService.getSimilarProjects(proj);
						if (similarProjects != null && similarProjects.size() > 0) {
							// For now obtain the first suitable project
							similarProject = similarProjects.get(0);
						}
					}

					// If a suitable similar project was found, search for similar Quality Objective(s) to be suggested
					if (similarProject != null) {	
						// Get similar QOs from a similar project
						List<QualityObjective> qos = qoSimilarityService.getSimilarQOs(qo, similarProject);
						// If results were found, construct a suggestion (for now obtain the first one). 
						if (qos != null && qos.size() > 0) {
							suggestionsMultimap.put(SuggestionType.QO_REPLACE, qos.get(0));
							return suggestionsMultimap;
						}
					} 

					// Otherwise suggest the QO to be removed
					suggestionsMultimap.put(SuggestionType.QO_REMOVE, null);
					return suggestionsMultimap;
				}
			}
			// if a metric has no value / the value is 0, return a suggestion to 
			// remove the metric 
			if (node instanceof Metric) {
				
				Metric metric = (Metric) node;
				Float value = metric.getValue();
				
				if (value == null || value.isNaN() || value.isInfinite()) {
					suggestionsMultimap.put(SuggestionType.METRIC_REMOVE, null);
					return suggestionsMultimap;
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		// If no suggestion is to be provided, return an empty string 
		// (overwrite the possible existing suggestion). 
		suggestionsMultimap.put(SuggestionType.NO_SUGGESTION, "");
		return suggestionsMultimap;
	}


	/**
	 * Get a list of Jenkins metrics
	 * @return
	 */
	public static List<String> getJenkinsMetricNames() {
		String metrics[] = {
				"JENKINS_PROJECTS",        			// Jenkins projects
				"JENKINS_LATEST_BUILD_SUCCESS",     // State of the latest build
				"JENKINS_BUILD_HISTORY",  
		};

		return Arrays.asList(metrics);
	}


	/**
	 * Uses the classes from the DB to write the RDF files based on those.
	 */
	public static void writeSemanticModelFiles() {
		logger.debug("Writing the context model to a file");

		// TODO: Complement the model with the other required entities 
		OntModel usersModel = writeUserEntries();
		OntModel projectsModel = writeProjectEntries();
		OntModel metadataModel = writeMetaDataModelEntries();

		//	uqModel.write(System.out, "RDF/XML");
		//	RDFDataMgr.write(System.out, uqModel, RDFFormat.RDFXML_PRETTY);

		// Write the individual models to a single file
		// holding all the triples
		final OntModel combined = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		for (final OntModel part : new OntModel[] { usersModel, projectsModel, metadataModel } ) {
			combined.add(part);
		}
		try {
			String dataDir = UQasarUtil.getDataDirPath();
			// TODO: Find out why this does not work in Linux
//			String modelPath = "file:///" + dataDir + ONTOLOGYFILE;
			String modelPath = dataDir + ONTOLOGYFILE;
			
			combined.write(new FileOutputStream(modelPath, false));
			logger.debug("Context Model written to file " +modelPath);
			UQasarUtil.setUqModel(combined);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the user entities to rdf
	 */
	private static OntModel writeUserEntries() {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		try {
			thewebsemantic.Bean2RDF writer = new Bean2RDF(model);
			InitialContext ic = new InitialContext();
			UserService userService = (UserService) ic.lookup("java:module/UserService");
			List<User> users = userService.getAll();
			for (User u : users) {
				writer.save(u);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}


	/**
	 * Writes the project entities to rdf
	 */
	private static OntModel writeProjectEntries() {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		try {
			thewebsemantic.Bean2RDF writer = new Bean2RDF(model);
			InitialContext ic = new InitialContext();
			TreeNodeService treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
			List<Project> projects = treeNodeService.getAllProjects();
			for (Project proj : projects) {
				writer.save(proj);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}


	/**
	 * Write metadata entities from the beans to rdf model
	 */
	private static OntModel writeMetaDataModelEntries() {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		thewebsemantic.Bean2RDF writer = new Bean2RDF(model);
		try {
			InitialContext ic = new InitialContext();
			for (final Class clazz : MetaData.getAllClasses()) {
				if (ContinuousIntegrationTool.class.isAssignableFrom(clazz)) {
					ContinuousIntegrationToolService cits = (ContinuousIntegrationToolService) ic.lookup("java:module/ContinuousIntegrationToolService");
					List<ContinuousIntegrationTool> continuousIntegrationTools = cits.getAll();
					for (ContinuousIntegrationTool continuousIntegrationTool : continuousIntegrationTools) {
						writer.save(continuousIntegrationTool);
					}	            	
				} else if (CustomerType.class.isAssignableFrom(clazz)) {
					CustomerTypeService cts = (CustomerTypeService) ic.lookup("java:module/CustomerTypeService");
					List<CustomerType> customerTypes = cts.getAll();
					for (CustomerType customerType : customerTypes) {
						writer.save(customerType);
					}
				} else if (IssueTrackingTool.class.isAssignableFrom(clazz)) {
					IssueTrackingToolService itts = (IssueTrackingToolService) ic.lookup("java:module/IssueTrackingToolService");
					List<IssueTrackingTool> issueTrackingTools = itts.getAll();
					for (IssueTrackingTool issueTrackingTool : issueTrackingTools) {
						writer.save(issueTrackingTool);
					}
				} else if (ProgrammingLanguage.class.isAssignableFrom(clazz)) {
					ProgrammingLanguageService pls = (ProgrammingLanguageService) ic.lookup("java:module/ProgrammingLanguageService");
					List<ProgrammingLanguage> programmingLanguages = pls.getAll();
					for (ProgrammingLanguage programmingLanguage : programmingLanguages) {
						writer.save(programmingLanguage);
					}
				} else if (ProjectType.class.isAssignableFrom(clazz)) {
					ProjectTypeService pts = (ProjectTypeService) ic.lookup("java:module/ProjectTypeService");
					List<ProjectType> projectTypes = pts.getAll();
					for (ProjectType projectType : projectTypes) {
						writer.save(projectType);
					}
				} else if (SoftwareLicense.class.isAssignableFrom(clazz)) {
					SoftwareLicenseService sls = (SoftwareLicenseService) ic.lookup("java:module/SoftwareLicenseService");
					List<SoftwareLicense> softwareLicenses = sls.getAll();
					for (SoftwareLicense softwareLicense : softwareLicenses) {
						writer.save(softwareLicense);
					}	            	
				} else if (SoftwareType.class.isAssignableFrom(clazz)) {
					SoftwareTypeService sts = (SoftwareTypeService) ic.lookup("java:module/SoftwareTypeService");
					List<SoftwareType> softwareTypes = sts.getAll();
					for (SoftwareType softwareType : softwareTypes) {
						writer.save(softwareType);
					}
				} else if (SourceCodeManagementTool.class.isAssignableFrom(clazz)) {
					SourceCodeManagementToolService scmts = (SourceCodeManagementToolService) ic.lookup("java:module/SourceCodeManagementToolService");
					List<SourceCodeManagementTool> sourceCodeManagementTools = scmts.getAll();
					for (SourceCodeManagementTool sourceCodeManagementTool : sourceCodeManagementTools) {
						writer.save(sourceCodeManagementTool);
					}	            	
				} else if (StaticAnalysisTool.class.isAssignableFrom(clazz)) {
					StaticAnalysisToolService sats = (StaticAnalysisToolService) ic.lookup("java:module/StaticAnalysisToolService");
					List<StaticAnalysisTool> staticAnalysisTools = sats.getAll();
					for (StaticAnalysisTool staticAnalysisTool : staticAnalysisTools) {
						writer.save(staticAnalysisTool);
					}
				} else if (TestManagementTool.class.isAssignableFrom(clazz)) {
					TestManagementToolService tmts = (TestManagementToolService) ic.lookup("java:module/TestManagementToolService");
					List<TestManagementTool> testManagementTools = tmts.getAll();
					for (TestManagementTool testManagementTool : testManagementTools) {
						writer.save(testManagementTool);
					}
				} else if (Topic.class.isAssignableFrom(clazz)) {
					TopicService ts = (TopicService) ic.lookup("java:module/TopicService");
					List<Topic> topics = ts.getAll();
					for (Topic topic : topics) {
						writer.save(topic);
					}
				} else if (SoftwareDevelopmentMethodology.class.isAssignableFrom(clazz)) {
					SoftwareDevelopmentMethodologyService sdms = (SoftwareDevelopmentMethodologyService) ic.lookup("java:module/SoftwareDevelopmentMethodologyService");
					List<SoftwareDevelopmentMethodology> softwareDevelopmentMethodologies = sdms.getAll();
					for (SoftwareDevelopmentMethodology softwareDevelopmentMethodology : softwareDevelopmentMethodologies) {
						writer.save(softwareDevelopmentMethodology);
					}
				} else if (QModelTagData.class.isAssignableFrom(clazz)) {
					QModelTagDataService qmtds = (QModelTagDataService) ic.lookup("java:module/QModelTagDataService");
					List<QModelTagData> qmodelTagData = qmtds.getAll();
					for (QModelTagData qmtd : qmodelTagData) {
						writer.save(qmtd);
					}            	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}


	/**
	 * Read the RDF model from files.
	 */
	public static void readSemanticModelFiles() {
		logger.debug("Reading the model from a file");
		// Read the model to an existing model
		String dataDir = UQasarUtil.getDataDirPath();
		String modelPath = "file:///" + dataDir + ONTOLOGYFILE;
//		String modelPath = "file:///C:/nyrhinen/Programme/jboss-as-7.1.1.Final/standalone/data/uq-ontology-model.rdf";

		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		RDFDataMgr.read(model, modelPath);
		// Test output to standard output
		//		RDFDataMgr.write(System.out, uqModel, RDFFormat.RDFXML_PRETTY);
		logger.debug("Model read from file " +modelPath);
		UQasarUtil.setUqModel(model);
		System.out.println("Reading done.");
	}



	public static String execSparQLQuery(String query) {
		System.out.println("execSPINQuery");
		Model model = getUqModel();

		// Register system functions (such as sp:gt (>))
		SPINModuleRegistry.get().init();

		Query arqQuery = ARQFactory.get().createQuery(model, query);
		ARQ2SPIN arq2SPIN = new ARQ2SPIN(model);
		Select spinQuery = (Select) arq2SPIN.createQuery(arqQuery, null);

		System.out.println("SPIN query in Turtle:");
		model.write(System.out, FileUtils.langTurtle);

		System.out.println("-----");
		String str = spinQuery.toString();
		System.out.println("SPIN query:\n" + str);

		// Now turn it back into a Jena Query
		Query parsedBack = ARQFactory.get().createQuery(spinQuery);
		System.out.println("Jena query:\n" + parsedBack);
		
		com.hp.hpl.jena.query.Query arq = ARQFactory.get().createQuery(spinQuery);
		QueryExecution qexec = ARQFactory.get().createQueryExecution(arq, model);
		QuerySolutionMap arqBindings = new QuerySolutionMap();
		arqBindings.add("predicate", RDFS.label);
		qexec.setInitialBinding(arqBindings); // Pre-assign the arguments
		ResultSet rs = qexec.execSelect();
		
//		System.out.println("#####################################################################");
//		
//		if (rs.hasNext()) {
//			QuerySolution row = rs.next();
//			System.out.println("Row: " +row.toString());
//			RDFNode user = row.get("User");
//			Literal label = row.getLiteral("label");
//			System.out.println(user.toString());
//		}
//		RDFNode object = rs.next().get("object");
//		System.out.println("Label is " + object);


		
		
		
		Collection<User> users = Sparql.exec(getUqModel(), User.class, query);
		
		String usersString = "";
		for (User user : users) {
			System.out.println("User: " +user.toString());
			usersString += user.toString() +"<br/>";
		}

		
		
		System.out.println("execSPINQuery() done.");
		return usersString;
	}


}
