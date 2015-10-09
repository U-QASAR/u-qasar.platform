package eu.uqasar.util.io.importer;

import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.wicket.util.file.File;
import org.joda.time.DateTime;

import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;

public class QModelXmlDomParser {

	/**
	 * Parse file received to generate an instance of quality model.
	 * @param file File to parse
	 * @return QModel created
	 * @throws JAXBException
	 */
	public static QModel parseFile(File file) throws JAXBException  {
		QModel qm;

		//parsear de xml
		JAXBContext context = JAXBContext.newInstance(QModel.class);
		Unmarshaller un = context.createUnmarshaller();
		qm = (QModel) un.unmarshal(file);
		
		if (qm!=null && (null == qm.getName() || qm.getName().equals("") )) {
			qm.setName("Quality Model Imported"+DateTime.now().toDate());
		}
		
		if (qm!=null){
			boolean qmCompleted = true;
			if (qm.getChildren()!=null && qm.getChildren().size()>0){
				Iterator<QMTreeNode> itObj = qm.getChildren().iterator();
				while (itObj.hasNext()) {
					QMTreeNode obj = (QMTreeNode) itObj.next();
					boolean qobjCompleted = true;
					if (obj.getChildren()!=null && obj.getChildren().size()>0){
						Iterator<QMTreeNode> itInd = obj.getChildren().iterator();
						while (itInd.hasNext()){
							QMTreeNode ind = (QMTreeNode) itInd.next();
							if (ind.getChildren() != null && ind.getChildren().size()>0){
								ind.setIsCompleted(true);
							}
							qobjCompleted = qobjCompleted && ind.getIsCompleted();
						}
						obj.setIsCompleted(qobjCompleted);
					}
					qmCompleted = qmCompleted && obj.getIsCompleted();
					qm.setIsCompleted(qmCompleted);
				}
			}
		}
		
		return qm;
	}

}