package eu.uqasar.util.io.exporter;

import java.io.IOException;

import org.apache.wicket.util.file.File;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import eu.uqasar.model.tree.Project;



public class QProjectJsonWriter extends QProjectWriter {

	private File file;
	
	public QProjectJsonWriter() {}
	
	public File createJsonFile(Project p) throws JsonGenerationException, JsonMappingException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(SerializationConfig.Feature.USE_ANNOTATIONS, true);
		mapper.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, true);

		mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);

		file =  new File(p.getName()+".json");
		// write JSON to a file
		mapper.writeValue(file, p);

		return file;
	}

}
