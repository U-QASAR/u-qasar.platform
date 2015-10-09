package eu.uqasar.util.io.importer;

import java.io.IOException;

import org.apache.wicket.util.file.File;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.uqasar.model.tree.Project;



public class QProjectJsonParser {

	/**
	 * Parse file received to generate an instance of quality model.
	 * @param file containing the model in json format
	 * @return QModel imported
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static Project parseFile(File file) throws JsonParseException, JsonMappingException, IOException{
		
		Project proj;
        
        ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.AUTO_DETECT_CREATORS,true);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS, false);
		
		proj = mapper.readValue(file, Project.class);
        
		return proj;
	}
	
}
