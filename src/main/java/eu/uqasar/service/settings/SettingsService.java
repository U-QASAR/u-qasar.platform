/*
 */
package eu.uqasar.service.settings;

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
	
	public Setting getByKey(final String key) {
		return em.find(Setting.class, key);
	}
	
	public Collection<Setting> getAll(final Collection<String> keys) {
		return getAll(keys.toArray(new String[0]));
	}
	
	public Collection<Setting> getAll(final String... keys) {
		List<Setting> settings = new ArrayList<>();
		for (String key : keys) {
			settings.add(getByKey(key));
		}
		return settings;
	}
	
	public Setting create(Setting setting) {
		return update(setting);
	}
	
	public Setting update(Setting setting) {
		setting = em.merge(setting);
		return setting;
	}
	
	public void delete(Setting setting) {
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