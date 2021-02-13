package eu.uqasar.model;

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

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.search.bridge.StringBridge;

/**
 *
 * 
 */
class TagsBridge implements StringBridge {

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
