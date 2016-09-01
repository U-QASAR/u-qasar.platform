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

import eu.uqasar.model.tree.Project;

@NoArgsConstructor
public class QProjectXmlWriter extends QProjectWriter {
	
	private File file;
	
	/**
	 * Create a xml file containing the quality project.
	 * @param p Project
	 * @return File
	 * @throws JAXBException exception
	 */
	public File createXmlFile(Project p) throws JAXBException {
		
		file = new File(p.getName()+".xml");
		JAXBContext context = JAXBContext.newInstance(Project.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		//m.marshal(p, System.out);

		// Write to File
		m.marshal(p, file);

		return file;	
		
	}
}
