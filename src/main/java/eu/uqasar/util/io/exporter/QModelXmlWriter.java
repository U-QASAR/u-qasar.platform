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


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import lombok.NoArgsConstructor;
import org.apache.wicket.util.file.File;

import eu.uqasar.model.qmtree.QModel;
@NoArgsConstructor
public class QModelXmlWriter {

	private File file;
    
	/**
	 * Create a xml file containing the quality model.
	 * @param model QModel
	 * @return File
	 * @throws JAXBException exception
	 */
//	 * @throws IOException exception
//	public File createXmlFile(QModel model) throws JAXBException, IOException {
	public File createXmlFile(QModel model) throws JAXBException {
			
		file = new File(model.getName()+".xml");
		JAXBContext context = JAXBContext.newInstance(QModel.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		//m.marshal(model, System.out);

		// Write to File
		m.marshal(model, file);

		return file;	
	}
	
}
