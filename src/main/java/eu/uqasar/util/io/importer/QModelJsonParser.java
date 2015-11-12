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


import java.io.IOException;

import org.apache.wicket.util.file.File;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;

import eu.uqasar.model.qmtree.QModel;

public class QModelJsonParser {

	/**
	 * Parse file received to generate an instance of quality model.
	 * @param file containing the model in json format
	 * @return QModel imported
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static QModel parseFile(File file) throws JsonParseException, JsonMappingException, IOException{
		
		QModel qm;
        ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.AUTO_DETECT_CREATORS,true);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS, false);
		
		qm = mapper.readValue(file, QModel.class);
        
		if (qm!=null && (null == qm.getName() || qm.getName().equals("") )) {
			qm.setName("Quality Model Imported"+DateTime.now().toDate());
		}
		return qm;
	}

}