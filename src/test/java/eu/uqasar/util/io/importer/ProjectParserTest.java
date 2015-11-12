package eu.uqasar.util.io.importer;

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
