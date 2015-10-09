/*
 */
package eu.uqasar.util.ldap;

import eu.uqasar.model.settings.ldap.LdapSettings;
import java.util.Comparator;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 *
 *
 */
public class LdapGroup extends LdapEntity {

	private String name, description;
	private transient List<LdapUser> users;

	public LdapGroup(Attributes attr, LdapSettings settings) {
		super(attr, settings);
	}

	public String getDescription() {
		if (description == null) {
			description = getStringValue(settings.getGroupDescriptionMapping());
		}
		return description;
	}

	public String getName() {
		if (name == null) {
			name = getStringValue(settings.getGroupNameMapping());
		}
		return name;
	}

	public List<LdapUser> getMembers() throws NamingException {
		if(users == null || users.isEmpty()) {
			LdapManager manager = LdapManager.getInstance(settings);
			users = manager.getUsersFromGroup(Integer.MAX_VALUE, this);
		}
		return users;
	}

	public static class LdapGroupComparator implements Comparator<LdapGroup> {

		@Override
		public int compare(LdapGroup o1, LdapGroup o2) {
			if (o1 == o2) {
				return 0;
			} else if (o1 == null) {
				return -1;
			} else if (o2 == null) {
				return 1;
			} else {
				int name = LdapEntity.compare(o1.getName(), o2.getName(), false);
				if (name != 0) {
					return name;
				}
				int dn = LdapEntity.compare(o1.getDN(), o2.getDN(), false);
				return dn;
			}
		}

	}
}
