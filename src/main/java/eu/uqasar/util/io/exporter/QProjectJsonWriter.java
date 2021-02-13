package eu.uqasar.util.io.exporter;

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

import lombok.NoArgsConstructor;
import org.apache.wicket.util.file.File;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import eu.uqasar.model.tree.Project;

@NoArgsConstructor
public class QProjectJsonWriter extends QProjectWriter {

	private File file;
	
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
