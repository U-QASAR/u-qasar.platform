package eu.uqasar.util.io.importer;

import eu.uqasar.model.tree.Project;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import junit.framework.Assert;
import org.apache.wicket.util.file.File;
import org.jboss.solder.logging.Logger;
import org.junit.Before;
import org.junit.Test;

public class ProjectParserTest {

	private static Logger logger = Logger.getLogger(ProjectParserTest.class);

	private Project qm;

	private File xmlFile, jsonFile; 

	@Before
	public void setUp() {

		//get the servlet context
		URL resourceUrl = getClass().getResource("/files/");

		try {
			Path resourcePath = Paths.get(resourceUrl.toURI());
			xmlFile = new File(resourcePath.toString()+"/project.xml");
			jsonFile = new File(resourcePath.toString()+"/project.json");

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};


	@Test
	public void testParseFileXml() {
		logger.info("testParseFile start");

		try {
			qm = QProjectXmlDomParser.parseFile(xmlFile);
			Assert.assertNotNull(qm);
			Assert.assertNotNull(qm.getChildren());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testParseFileJson() {
		logger.info("testParseFileJson start");
		try {
			qm = QProjectJsonParser.parseFile(jsonFile);
			Assert.assertNotNull(qm);
			Assert.assertNotNull(qm.getChildren());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

	}

}
