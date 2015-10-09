/*
 */
package eu.uqasar.model.settings;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 */
@Entity
@XmlRootElement
@Table(name = "setting")
public class Setting implements Serializable {

	@Id
	private String _key;

	private String _value;

	public Setting() {

	}

	public Setting(String key, String value) {
		this._key = key;
		this._value = value;
	}

	public String getKey() {
		return _key;
	}

	public void setKey(String key) {
		this._key = key;
	}

	public String getValue() {
		return _value;
	}

	public void setValue(String value) {
		this._value = value;
	}

}
