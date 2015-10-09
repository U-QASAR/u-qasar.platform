package eu.uqasar.util.io.exporter;

import java.io.IOException;

import org.apache.wicket.util.file.File;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import eu.uqasar.model.qmtree.QModel;

public class QModelJsonWriter {

	private File file;

	/**
	 * Default Constructor.
	 */
	public QModelJsonWriter() {}

	/**
	 * Create a json file containing the quality model received.
	 * @param p quality model to parse
	 * @return json file
	 * @throws IOException exception
	 */
	public File createJsonFile(QModel p) throws IOException {

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
