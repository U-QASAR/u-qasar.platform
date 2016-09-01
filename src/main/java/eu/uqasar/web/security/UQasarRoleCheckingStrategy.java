/*
 */

package eu.uqasar.web.security;

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


import eu.uqasar.auth.strategies.IRoleCheckingStrategy;
import eu.uqasar.model.role.Role;
import eu.uqasar.web.provider.LoggedInUserProvider;
import java.io.Serializable;
import java.util.EnumSet;
import javax.inject.Inject;
import org.apache.wicket.cdi.CdiContainer;

/**
 *
 *
 */
public class UQasarRoleCheckingStrategy implements IRoleCheckingStrategy, Serializable {

	@Inject
	LoggedInUserProvider provider;

	@Override
	public boolean hasAnyRole(EnumSet<Role> roles) {
		return hasAnyRole(roles.toArray(new Role[roles.size()]));
	}

	@Override
	public boolean hasAnyRole(Role... roles) {
		CdiContainer.get().getNonContextualManager().inject(this);
		if ((provider == null) || (provider.getLoggedInUser() == null)) {
			return false;
		}
        return provider.getLoggedInUser().hasAnyRoles(roles);
	}
}
