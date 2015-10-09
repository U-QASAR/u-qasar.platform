/*
 */

package eu.uqasar.web.security;

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
		boolean authorized = provider.getLoggedInUser().hasAnyRoles(roles);
		return authorized;
	}
}
