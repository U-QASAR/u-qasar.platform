package eu.uqasar.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.logging.Logger;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class UQasarUtilTest {

	private static Logger logger = Logger.getLogger(UQasarUtilTest.class);
	
	@Test
	public void testGetTempDirPath() {
		logger.info("Testing get temp dir path");
		String tempDir = UQasarUtil.getTempDirPath();
		Assert.assertNotNull(tempDir);
		Assert.assertTrue(tempDir.length() > 0);
		logger.info("Temp dir: " +tempDir);
	}

	@Test
	public void testGetDataDirPath() {
		logger.info("Testing get data dir path");
		String dataDir = UQasarUtil.getDataDirPath();
		Assert.assertNotNull(dataDir);
		Assert.assertTrue(dataDir.length() > 0);
		logger.info("Data dir: " +dataDir);		
	}

	@Test
	public void testGetProperties() {
		logger.info("Testing get properties");
		Assert.assertNotNull(UQasarUtil.getProperties());
	}
	
	@Test
	public void testConstructFormula() {
		logger.info("Testing construct formula");

		List<Object> testVarsAndOpers = new ArrayList<>();
		testVarsAndOpers.add(5);
		testVarsAndOpers.add("*");
		testVarsAndOpers.add(3);		
		String expected = "5*3";
		String res = UQasarUtil.constructFormula(testVarsAndOpers);
		Assert.assertEquals(expected, res);
	}
	
	@Test
	public void testRound() {
		logger.info("Testing round");
		
		Double expected = 3.0;
		Double res = UQasarUtil.round(3.4567);
		Assert.assertEquals(expected, res);
	}
	
	@Test
	public void testSanitizeName() {
		logger.info("Testing SanitizeName");
		
		String expected = "Number_of_comment_lines_";
		String res = UQasarUtil.sanitizeName("Number of comment lines");
		Assert.assertEquals(expected, res);
	}

	@Test
	public void testSanitizeNameWithNoWhitespaces() {
		logger.info("testSanitizeNameWithNoWhitespaces()");
		
		String expected = "MetricName";
		String actual = UQasarUtil.sanitizeName("MetricName");
		Assert.assertEquals(expected, actual);		
	}
	
	@Test
	public void testUnSanitizeName() {
		logger.info("Testing unSanitizeName");
		
		String expected = "Number of comment lines";
		String res = UQasarUtil.unSanitizeName("Number_of_comment_lines_");
		Assert.assertEquals(expected, res);
	}

	
	@Test
	public void testGetJiraMetricNames() {
		logger.info("Testing getJiraMetricNames");
		
		List<String> res = UQasarUtil.getJiraMetricNames();
		Assert.assertNotNull(res);
		Assert.assertTrue(res.size()>0);
	}
	
	
	@Test
	public void testGetJiraMetricNamesAbbreviated() {
		logger.info("Testing getJiraMetricNamesAbbreviated");
		
		List<String> res = UQasarUtil.getJiraMetricNamesAbbreviated();
		Assert.assertNotNull(res);
		Assert.assertTrue(res.size()>0);
	}
	
	@Test
	public void testGetSonarMetricNames() {
		logger.info("Testing getSonarMetricNames");
		
		List<String> res = UQasarUtil.getSonarMetricNames();
		Assert.assertNotNull(res);
		Assert.assertTrue(res.size()>0);
	}
	
	@Test
	public void testGetCubesMetricNames() {
		logger.info("Testing getCubesMetricNames");
		
		List<String> res = UQasarUtil.getCubesMetricNames();
		Assert.assertNotNull(res);
		Assert.assertTrue(res.size()>0);
	}
	
	@Test
	public void testGetGitlabMetricNames() {
		logger.info("Testing getGitlabMetricNames");
		
		List<String> res = UQasarUtil.getGitlabMetricNames();
		Assert.assertNotNull(res);
		Assert.assertTrue(res.size()>0);
	}
	
	@Test
	public void testGetTestLinkNames() {
		logger.info("Testing getTestLinkMetricNames");
		
		List<String> res = UQasarUtil.getTestLinkMetricNames();
		Assert.assertNotNull(res);
		Assert.assertTrue(res.size()>0);
	}

	@Test
	public void testMinsToMs() {
		logger.info("Testing minsToMs");
		
		int expected = 60000;
		int res = UQasarUtil.minsToMs(1);
		Assert.assertEquals(expected, res);
	}
	
	@Test
	public void testIsDateEqualKO() {
		logger.info("Testing isDateEqual KO");
		
		Date dateOne = DateTime.now().toDate();
		Date dateTwo = (DateTime.now().plusDays(-2)).toDate();
		boolean res = UQasarUtil.isDateEqual(dateOne, dateTwo);
		Assert.assertFalse(res);
	}
	
	@Test
	public void testIsDateEqual() {
		logger.info("Testing isDateEqual ");
		
		Date dateOne = DateTime.now().toDate();
		Date dateTwo = (DateTime.now()).toDate();
		boolean res = UQasarUtil.isDateEqual(dateOne, dateTwo);
		Assert.assertTrue(res);
	}
	
	
}
