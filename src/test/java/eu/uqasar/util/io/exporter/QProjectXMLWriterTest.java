/**
 * 
 */
package eu.uqasar.util.io.exporter;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.wicket.util.file.File;
import org.jboss.solder.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.uqasar.model.tree.Project;
import eu.uqasar.util.io.importer.QProjectXmlDomParser;

public class QProjectXMLWriterTest {
	
	private static Logger logger = Logger.getLogger(QProjectXMLWriterTest.class);

	private Project project;
	private QProjectXmlWriter writer;
	private File file; 
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		project = new Project();
		project.setName("Project for testing export");
		project.setShortName("TEST");
		project.setDescription("This sample project is created for unit tests.");
		project.setId(100l);
		
		writer = new QProjectXmlWriter();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		if (file != null) {
			file.delete();
		}
	}

	@Test
	public void testCreateXmlFile() {
		
		try {
			File file = writer.createXmlFile(project);
			Assert.assertNotNull(file);
			Assert.assertNotNull(file.getPath());
			Assert.assertTrue(file.isFile());
			logger.info("File created " +file.getAbsolutePath());
			QProjectXmlDomParser.parseFile(file);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
