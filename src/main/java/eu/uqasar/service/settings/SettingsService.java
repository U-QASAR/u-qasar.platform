/*
 */
package eu.uqasar.service.settings;

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


import eu.uqasar.model.settings.Setting;
import eu.uqasar.model.settings.Settings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 * @param <T>
 */
@ApplicationScoped
public abstract class SettingsService<T extends Settings> {

	// inject a logger
	@Inject
	protected Logger logger;
	
	@Inject
	protected EntityManager em;
	
	private Setting getByKey(final String key) {
		return em.find(Setting.class, key);
	}
	
	public Collection<Setting> getAll(final Collection<String> keys) {
		return getAll(keys.toArray(new String[0]));
	}
	
	private Collection<Setting> getAll(final String... keys) {
		List<Setting> settings = new ArrayList<>();
		for (String key : keys) {
			settings.add(getByKey(key));
		}
		return settings;
	}
	
	public Setting create(Setting setting) {
		return update(setting);
	}
	
	private Setting update(Setting setting) {
		setting = em.merge(setting);
		return setting;
	}
	
	private void delete(Setting setting) {
		em.remove(setting);
	}
	
	public void create(T settingsContainer) {
		update(settingsContainer);
	}
	
	public void update(T settingsContainer) {
		for(String key : settingsContainer.getKeys()) {
			Setting setting = new Setting(key, settingsContainer.getValue(key));
			update(setting);
		}
	}
	
	public void delete(T settingsContainer) {
		for(String key : settingsContainer.getKeys()) {
			Setting setting = new Setting(key, settingsContainer.getValue(key));
			delete(setting);
		}
	}
	
	public T get(T settingsContainer) {
		for(String key : settingsContainer.getKeys()) {
			Setting setting = getByKey(key);
			if(setting != null) {
				settingsContainer.setValue(key, setting.getValue());
			}
		}
		return settingsContainer;
	}
}