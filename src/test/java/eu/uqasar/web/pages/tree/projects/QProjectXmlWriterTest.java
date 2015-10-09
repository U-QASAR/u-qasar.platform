package eu.uqasar.web.pages.tree.projects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.apache.wicket.util.file.File;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.logging.Logger;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.uqasar.model.company.Company;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.process.Process;
import eu.uqasar.model.product.Product;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.Threshold;
import eu.uqasar.model.tree.historic.HistoricValuesProject;
import eu.uqasar.model.tree.historic.Snapshot;
import eu.uqasar.util.UQasarUtil;
import eu.uqasar.util.io.exporter.QProjectXmlWriter;


@RunWith(Arquillian.class)
public class QProjectXmlWriterTest {

		private static Logger logger = Logger.getLogger(QProjectXmlWriterTest.class);

		private Project qp;
		private QProjectXmlWriter writer;
		private File file;
		
		
		@Before
		public void setUp() {
		
			writer = new QProjectXmlWriter();
			qp = createProject();
		};
		
		
		@Test
		public void testCreateXmlFile() {
			logger.info("testCreateXmlFile start");
			try {
				file = writer.createXmlFile(qp);
				Assert.assertNotNull(file);

			} catch (JAXBException e) {
				Assert.assertTrue(false);
			}
		}
		
		/**
		 * Create project for testing purposes.
		 * @return QProject.
		 */
		private Project createProject() {
			List<Role> roles = new ArrayList<Role>();
			roles.add(Role.Developer);

			Project project1 = new Project("U-QASAR Platform Development", "U-QASAR");
	        project1.setLifeCycleStage(LifeCycleStage.Implementation);
	        project1.setDescription("Quality Project: U-QASAR Platform Development");
	        project1.setId((long)7484849);
	        project1.setFormulaToView("");
	        project1.setFormulaToEval("");
	        project1.setFormulaAverage(false);
	        project1.setValue(34.0);
	        project1.setLastUpdated(DateTime.now().toDate());
	        project1.setHistoricValues(new ArrayList<HistoricValuesProject>());
	        project1.setSnapshot(new ArrayList<Snapshot>());
	        
	        Calendar cal = Calendar.getInstance();
	        cal.set(2013, 9, 01);
	        project1.setStartDate(cal.getTime());
	        cal.add(Calendar.YEAR, 3);
	        project1.setEndDate(cal.getTime());
	        
	        // Set Thresholds
	        project1.setThreshold(new Threshold(40,80));
	    
	        QualityObjective qo1 = new QualityObjective("Code coverage above 80%", project1);
			qo1.setDescription("Average degree to which the source code  is tested by the set of Test Cases designed for the system at all Test Levels (i.e. Unit, Integration and Acceptance)");
			qo1.setThreshold(new Threshold(60,80));
			
			QualityIndicator qi1 = new QualityIndicator("Unit Test coverage", qo1);
			qi1.setDescription("Average degree to which the source code  is tested by the set of Unit Tests. It should be above 60%");
			qi1.setThreshold(new Threshold(35,60));
	        qi1.setTargetValue((float)70);
	        qi1.setLastUpdated(new Date());
	        Metric m1 = new Metric("Lines covered by Unit Tests", qi1);
	        m1.setDescription("Lines covered by Unit Tests");
	        m1.setMetricSource(MetricSource.Manual);
	        m1.setValue((float)10000);
	        m1.setLifeCycleStage(LifeCycleStage.Testing);
	        m1.setLastUpdated(new Date());
	        Metric m2 = new Metric("Lines of code", qi1);
	        m2.setLifeCycleStage(project1.getLifeCycleStage());
	        m2.setMetricSource(MetricSource.StaticAnalysis);
	        m2.setMetricType("NCLOC");
	        m2.setLastUpdated(new Date());
			qi1.setViewFormula(UQasarUtil.sanitizeName(m1.getName()) +" / " +UQasarUtil.sanitizeName(m2.getName()) +" * 100");
	        qi1.setLastUpdated(new Date());

	        // Formula for the QO1
			qo1.setViewFormula("( " +UQasarUtil.sanitizeName(qi1.getName()) + ")");

	        project1.setDefaultFormulas();
	        project1.setCompany(this.createCompany());
	        project1.setProcess(new Process());
	        project1.setProduct(new Product());
			return project1;
		}


		/**
		 * Create Company for testing purposes.
		 * @return Company
		 */
		private Company createCompany() {
			Company testComp = new Company("Test company");
			testComp.setShortName("TEST");
			testComp.setPhone("");
			testComp.setFax("");
			testComp.setStreet("");
			testComp.setZipcode(12345);
			testComp.setCity("Test");
			testComp.setCountry("UK");
			testComp.setId((long)11111);

			return testComp;
		}

		@After
		public void tearDown() {
	  		file.delete();
		}
		
}