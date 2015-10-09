package eu.uqasar.util.io.importer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.wicket.util.file.File;

import eu.uqasar.model.tree.Project;



public class QProjectXmlDomParser {

		
	public static Project parseFile(File file) throws Exception  {
		Project proj;
		
		//parsear de xml
		JAXBContext context = JAXBContext.newInstance(Project.class);
		Unmarshaller un = context.createUnmarshaller();
		proj = (Project) un.unmarshal(file);
		
		return proj;
	}

}
