package eu.uqasar.util.io.exporter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.wicket.util.file.File;

import eu.uqasar.model.qmtree.QModel;

public class QModelXmlWriter {

	private File file;
    
	/**
	 * Default Constructor.
	 */
	public QModelXmlWriter() {}
	
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
