/*
 */
package eu.uqasar.util.ldap;

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


import eu.uqasar.model.settings.ldap.LdapSettings;
import java.io.Serializable;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 *
 *
 */
public class LdapEntity implements Serializable, Comparable<LdapEntity> {

	private String dn;
	private final Attributes attributes;
	final LdapSettings settings;

	LdapEntity(Attributes attr, LdapSettings settings) {
		this.attributes = attr;
		this.settings = settings;
	}

	public String getDN() {
		if(dn == null) {
			dn = getValue("distinguishedName");
		}
		return dn;
	}

	private static <T> T getValue(final String attributeName, final Attributes attr) {
		try {
			Attribute attribute = attr.get(attributeName);
			if (attribute != null) {
				return (T) attribute.get();
			}
		} catch (NamingException ignored) {
		}
		return null;
	}

	private <T> T getValue(final String attributeName) {
		try {
			Attribute attribute = attributes.get(attributeName);
			if (attribute != null) {
				return (T) attribute.get();
			}
		} catch (NamingException ignored) {
		}
		return null;
	}

	public Attribute getAttribute(final String attributeName) {
		return attributes.get(attributeName);
	}

	public Attribute getMappedAttribute(final String fieldMapping) {
		if (LdapSettings.isLookupField(fieldMapping)) {
			final String attributeName = LdapSettings.getLookupFieldName(fieldMapping);
			return attributes.get(attributeName);
		}
		return null;
	}

	static <T> T getMappedValue(final String fieldMapping, final Attributes attr) {
		if (LdapSettings.isLookupField(fieldMapping)) {
			final String attributeName = LdapSettings.getLookupFieldName(fieldMapping);
			return getValue(attributeName, attr);
		}
		return null;
	}

	public <T> T getMappedValue(final String fieldMapping) {
		if (LdapSettings.isLookupField(fieldMapping)) {
			final String attributeName = LdapSettings.getLookupFieldName(fieldMapping);
			return getValue(attributeName);
		}
		return null;
	}

	byte[] getByteArrayValue(final String fieldMapping) {
		if (LdapSettings.isLookupField(fieldMapping)) {
			final String attributeName = LdapSettings.getLookupFieldName(fieldMapping);
			return getValue(attributeName);
		}
		return null;
	}

	protected String getStringValue(final String fieldMapping) {
		if (LdapSettings.isStaticField(fieldMapping)) {
			return fieldMapping;
		} else if (LdapSettings.isLookupField(fieldMapping)) {
			final String attributeName = LdapSettings.getLookupFieldName(fieldMapping);
			return getValue(attributeName);
		}
		return null;
	}

	@Override
	public String toString() {
		return getDN();
	}

	@Override
	public int compareTo(LdapEntity o) {
		return compare(getDN(), o.getDN(), false);
	}

	static int compare(String s1, String s2, boolean caseSensitive) {
		if (s1 == s2) {
			return 0;
		} else if (s1 == null) {
			return -1;
		} else if (s2 == null) {
			return 1;
		} else {
			return caseSensitive ? s1.compareTo(s2) : s1.compareToIgnoreCase(s2);
		}
	}
}
