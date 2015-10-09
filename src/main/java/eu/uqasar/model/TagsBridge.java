/**
 * 
 */
package eu.uqasar.model;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.search.bridge.StringBridge;

/**
 *
 * 
 */
public class TagsBridge implements StringBridge {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hibernate.search.bridge.StringBridge#objectToString(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String objectToString(Object object) {
		if (object instanceof String) {
			return (String) object;
		} else if (object instanceof Collection) {
			List<String> tags = (List<String>) object;
			return StringUtils.join(tags, ',');
		}
		return null;
	}

}
