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
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 *
 *
 */
@Getter
public class LdapGroup extends LdapEntity {

	private String name, description;
	private transient List<LdapUser> users;

	public LdapGroup(Attributes attr, LdapSettings settings) {
		super(attr, settings);
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
                return LdapEntity.compare(o1.getDN(), o2.getDN(), false);
			}
		}

	}
}
