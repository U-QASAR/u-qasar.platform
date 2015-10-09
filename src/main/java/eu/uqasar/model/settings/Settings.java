/*
 */
package eu.uqasar.model.settings;

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

	protected Map<String, String> settings = new HashMap<>();

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
