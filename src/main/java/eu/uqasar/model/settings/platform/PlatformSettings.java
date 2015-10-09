/*
 */
package eu.uqasar.model.settings.platform;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.Namable;

@Entity
@XmlRootElement
@Table(name = "project_settings")
@Indexed
public class PlatformSettings extends AbstractEntity implements Namable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String settingKey;
    private String settingValue;

    public PlatformSettings(String key, String value) {
        this.settingKey = key;
        this.settingValue = value;
    }

    public PlatformSettings() { }
    

    public String getSettingKey() {
		return settingKey;
	}

	public void setSettingKey(String settingKey) {
		this.settingKey = settingKey;
	}

	public String getSettingValue() {
		return settingValue;
	}

	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
	}

	@Override
    public String getUniqueName() {
        return settingKey;
    }

}
