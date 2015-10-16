/**
 * 
 */
package eu.uqasar.util.io.exporter;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.wicket.util.file.File;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jboss.solder.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import eu.uqasar.model.tree.Project;
import junit.framework.Assert;

public class QProjectJsonWriterTest {
	
	private static Logger logger = Logger.getLogger(QProjectJsonWriterTest.class);

	private Project project;
	private QProjectJsonWriter writer;
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
		
		writer = new QProjectJsonWriter();
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
	public void testCreateJsonFile() {
		
		try {
			File file = writer.createJsonFile(project);
			Assert.assertNotNull(file);
			Assert.assertNotNull(file.getPath());
			Assert.assertTrue(file.isFile());
			logger.info("File created " +file.getAbsolutePath());
			
			String jsonSource = file.readString();
			logger.info("File content: " +jsonSource);
			// Attempt to parse the file content
		    new JsonParser().parse(jsonSource);
			
		} catch (JsonGenerationException e) {
			e.printStackTrace();
			fail();
		} catch (JsonMappingException e) {
			e.printStackTrace();
			fail();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

}
