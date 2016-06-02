/*
 */
package eu.uqasar.model.settings.platform;

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
