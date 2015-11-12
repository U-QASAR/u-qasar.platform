package eu.uqasar.web.provider.ldap;

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


import eu.uqasar.util.ldap.LdapGroup;
import eu.uqasar.util.ldap.LdapManager;
import java.util.List;
import javax.naming.NamingException;

/**
 *
 *
 */
public class LdapGroupProvider extends LdapEntityProvider<LdapGroup> {

	public LdapGroupProvider(LdapManager manager, int max) {
		super(manager, max);
	}

	@Override
	public List<LdapGroup> getEntities(LdapManager manager, int max) throws NamingException {
		return manager.getGroupsList(max);
	}
}
