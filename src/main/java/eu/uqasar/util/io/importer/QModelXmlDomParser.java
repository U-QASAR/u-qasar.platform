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
                for (QMTreeNode obj : qm.getChildren()) {
                    boolean qobjCompleted = true;
                    if (obj.getChildren() != null && obj.getChildren().size() > 0) {
                        for (QMTreeNode ind : obj.getChildren()) {
                            if (ind.getChildren() != null && ind.getChildren().size() > 0) {
                                ind.setCompleted(true);
                            }
                            qobjCompleted = qobjCompleted && ind.isCompleted();
                        }
                        obj.setCompleted(qobjCompleted);
                    }
                    qmCompleted = qmCompleted && obj.isCompleted();
                    qm.setCompleted(qmCompleted);
                }
			}
		}
		
		return qm;
	}

}