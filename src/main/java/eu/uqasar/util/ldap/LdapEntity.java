/*
 */
package eu.uqasar.util.ldap;

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

	protected String dn;
	protected final Attributes attributes;
	protected LdapSettings settings;

	public LdapEntity(Attributes attr, LdapSettings settings) {
		this.attributes = attr;
		this.settings = settings;
	}

	public String getDN() {
		if(dn == null) {
			dn = getValue("distinguishedName");
		}
		return dn;
	}

	public static <T> T getValue(final String attributeName, final Attributes attr) {
		try {
			Attribute attribute = attr.get(attributeName);
			if (attribute != null) {
				return (T) attribute.get();
			}
		} catch (NamingException e) {
		}
		return null;
	}

	public <T> T getValue(final String attributeName) {
		try {
			Attribute attribute = attributes.get(attributeName);
			if (attribute != null) {
				return (T) attribute.get();
			}
		} catch (NamingException e) {
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

	public static <T> T getMappedValue(final String fieldMapping, final Attributes attr) {
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

	protected byte[] getByteArrayValue(final String fieldMapping) {
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

	public static int compare(String s1, String s2, boolean caseSensitive) {
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
