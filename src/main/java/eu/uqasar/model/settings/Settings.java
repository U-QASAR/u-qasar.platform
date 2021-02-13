/*
 */
package eu.uqasar.model.settings;

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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public abstract class Settings implements Serializable {

	private Map<String, String> settings = new HashMap<>();

	public abstract String[] getKeys();

	public void clear() {
		settings = new HashMap<>();
	}

	public String getValue(final String key) {
		return settings.get(key);
	}

	public void setValue(final String key, final String value) {
		settings.put(key, value);
	}

	public void setValues(Collection<Setting> settings) {
		for (Setting setting : settings) {
			setValue(setting.getKey(), setting.getValue());
		}
	}

	public Collection<Setting> getValues() {
		List<Setting> settings = new ArrayList<>();
		for (String key : getKeys()) {
			settings.add(new Setting(key, getValue(key)));
		}
		return settings;
	}

}
