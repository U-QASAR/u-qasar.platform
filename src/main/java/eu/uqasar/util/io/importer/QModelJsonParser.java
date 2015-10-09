package eu.uqasar.util.io.importer;

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