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


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.apache.wicket.util.file.File;
import org.jboss.solder.logging.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.uqasar.model.qmtree.QModel;

public class QModelParserTest {

	private static Logger logger = Logger.getLogger(QModelXmlDomParser.class);

	private QModel qm;
	
	private File jsonFile, xmlFile, xmlFileNM, xmlFileNI, xmlFileNO;
	
	
	@Before
	public void setUp() {
		
		//get the servlet context
 		URL resourceUrl = getClass().getResource("/files/");

		try {
			Path resourcePath = Paths.get(resourceUrl.toURI());
			xmlFile = new File(resourcePath.toString()+"/qmodel.xml");
			xmlFileNM = new File(resourcePath.toString()+"/qmodel_nometric.xml");
			xmlFileNI = new File(resourcePath.toString()+"/qmodel_noindicator.xml");
			xmlFileNO = new File(resourcePath.toString()+"/qmodel_noobjective.xml");
			//xmlFileMand = new File(resourcePath.toString()+"/qmodel_mandatoryfields.xml");
			jsonFile = new File(resourcePath.toString()+"/qmodel.json");
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	
	@Test
	public void testParseFileXml() {
		logger.info("testParseFileXml start");

		try {
			qm = QModelXmlDomParser.parseFile(xmlFile);
			assertNotNull(qm);
		} catch (JAXBException e) {
			assertTrue(false);
		}
	}
	
	
	@Test
	public void testParseFileNoMetricXml() {
		logger.info("testParseFileNoMetricXml start");
		try {
			qm = QModelXmlDomParser.parseFile(xmlFileNM);
			Assert.assertNotNull(qm);
			Assert.assertFalse(qm.isCompleted());
			Assert.assertFalse(qm.getChildren().get(2).isCompleted());
			Assert.assertFalse((qm.getChildren().get(2)).getChildren().get(0).isCompleted());
		} catch (JAXBException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testParseFileNoIndicatorXml() {
		logger.info("testParseFileNoIndicatorXml start");
	
		try {
			qm = QModelXmlDomParser.parseFile(xmlFileNI);
			Assert.assertNotNull(qm);
			Assert.assertFalse(qm.isCompleted());
			Assert.assertFalse(qm.getChildren().get(0).isCompleted());
		} catch (JAXBException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testParseFileNoObjectiveXml() {
		logger.info("testParseFileNoObjectiveXml start");
	
		try {
			qm = QModelXmlDomParser.parseFile(xmlFileNO);
			Assert.assertNotNull(qm);
			Assert.assertFalse(qm.isCompleted());
		} catch (JAXBException e) {
			assertTrue(false);
		}
		
	}

	@Test
	public void testParseFileQModelEmptyNameXML() {
		logger.info("testParseFileQModelEmptyNameXML start");
			try {
				
				String xmlToImport = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><qModel><name></name><description> Importpre-loaded quality model</description><nodeKey>Importedqm</nodeKey><edition>1.0</edition><companyId>175</companyId></qModel>";
				File fl = new File("xmlToImport.xml");
				fl.createNewFile();
				fl.setWritable(true);
				fl.write(xmlToImport);
				
				qm = QModelXmlDomParser.parseFile(fl);
				Assert.assertNotNull(qm);
				Assert.assertNotNull(qm.getName());
				Assert.assertTrue(qm.getName().contains("Quality Model Imported"));
				
				fl.delete();
			} catch (IOException e) {
				e.printStackTrace();
				Assert.assertTrue(false);
			} catch (JAXBException e) {
				e.printStackTrace();
				Assert.assertTrue(false);
			} 
	}
	
	@Test
	public void testParseFileJson() {
		logger.info("testParseFileJson start");
		try {
			qm = QModelJsonParser.parseFile(jsonFile);
			Assert.assertNotNull(qm);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void testParseFileQModelEmptyNameJSON() {
		logger.info("testParseFileQModelEmptyNameJSON start");
			try {
				
				
				String jsonToImport = "{\"@class\" : \"eu.uqasar.model.qmtree.QModel\",\"companyId\" : 173, \"description\" : \"Quality Model pre-loaded\", \"name\" : \"\", \"nodeKey\" : \"Quality Model A, U-QASAR\"}";
				
				File fl = new File("xmlToImport.json");
				fl.createNewFile();
				fl.setWritable(true);
				fl.write(jsonToImport);
				
				qm = QModelJsonParser.parseFile(fl);
				Assert.assertNotNull(qm);
				Assert.assertNotNull(qm.getName());
				Assert.assertTrue(qm.getName().contains("Quality Model Imported"));
				fl.delete();
			} catch (IOException e) {
				e.printStackTrace();
				Assert.assertTrue(false);
			}
	}


}
