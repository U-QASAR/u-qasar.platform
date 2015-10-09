package eu.uqasar.util.io.exporter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.wicket.util.file.File;

import eu.uqasar.model.tree.Project;


public class QProjectXmlWriter extends QProjectWriter {
	
	private File file;
	
	/**
	 * Default Constructor.
	 */
	public QProjectXmlWriter() {}

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
