/**
 * 
 */
package eu.uqasar.util.io.exporter;

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
