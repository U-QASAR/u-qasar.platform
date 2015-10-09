package eu.uqasar.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.inject.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.model.IModel;
import org.jboss.solder.logging.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import eu.uqasar.model.formula.Formula;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.notification.INotification;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.service.dataadapter.AdapterDataService;
import eu.uqasar.service.similarity.ProjectSimilarityService;
import eu.uqasar.service.similarity.QualityObjectiveSimilarityService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.resources.ResourceBundleLocator;

/**
 * Various utilities used on the platform 
 *
 */
@Singleton
public class UQasarUtil {

	final static String SEPARATOR = 
			java.nio.file.FileSystems.getDefault().getSeparator(); 

	protected static ResourceBundle res = null;
	private static Logger logger = Logger.getLogger(UQasarUtil.class);

	// List containing the notifications triggered by the rules' engine
	private static List<INotification> notifications = new ArrayList<>();
	
	private static Date latestTreeUpdateDate = new Date(); 
	
	// Enumeration for the various suggestion types
	public enum SuggestionType {
		
		NO_SUGGESTION("no_suggestion"),
		QO_REMOVE("qo.remove"),
		QO_REPLACE("qo.replace"),
		METRIC_REMOVE("metric.remove"),

		;

		private final String labelKey;

		private SuggestionType(final String labelKey) {
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
	 * @param vars
	 * @param operators
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
		final String MASTERPWD = 
				"jvnqeiPIqX_6cpdOYfDc7sHk7nEIv4zRn5984JwIHnq4AX7XQt";
		return MASTERPWD;
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
		int ms = minutes * SEC * MS;
		return ms;
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
						if (((Metric) me).getQualityStatus() == QualityStatus.Green) {
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
							qo.setValue((float) computedValue);
							qo.setLastUpdated(getLatestTreeUpdateDate());
						}							

					}
				} else {
					float denominator = 0;
					float achieved= 0;
					for(final TreeNode qi : qo.getChildren()){
						float weight = ((QualityIndicator)qi).getWeight();
						if (((QualityIndicator)qi).getQualityStatus() == QualityStatus.Green){
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
					prj.setValue((double)computedValue);
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
                        if (((Metric) me).getQualityStatus() == QualityStatus.Green) {
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
                            qo.setValue((float) computedValue);
                            qo.setLastUpdated(getLatestTreeUpdateDate());
                        }                           

                    }
                } else {
                    float denominator = 0;
                    float achieved= 0;
                    for(final TreeNode qi : qo.getChildren()){
                        float weight = ((QualityIndicator)qi).getWeight();
                        if (((QualityIndicator)qi).getQualityStatus() == QualityStatus.Green){
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
                    prj.setValue((double)computedValue);
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
        TreeNode nodeChild = (TreeNode)projectTreeNode.getParent();
        UQasarUtil.postorderWithParticularNode(projectTreeNode,nodeChild);
        
        return;
    }


	/**
	 * 
	 * @param node Node whose value is studied
	 * @param value Value of a measurement/computed value
	 * @return
	 */
	public static Multimap<SuggestionType, Object> getSuggestionForNode(TreeNode node) {

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
	 * @return the notifications
	 */
	public static List<INotification> getNotifications() {
		return notifications;
	}


	/**
	 * @param notifications the notifications to set
	 */
	public static void setNotifications(List<INotification> notifications) {
		UQasarUtil.notifications = notifications;
	}


	/**
	 * @return the latestTreeUpdateDate
	 */
	public static Date getLatestTreeUpdateDate() {
		return latestTreeUpdateDate;
	}


	/**
	 * @param latestTreeUpdateDate the latestTreeUpdateDate to set
	 */
	public static void setLatestTreeUpdateDate(Date latestTreeUpdateDate) {
		UQasarUtil.latestTreeUpdateDate = latestTreeUpdateDate;
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
}
