/*
 */

package eu.uqasar.auth.strategies;

import eu.uqasar.web.security.UQasarRoleCheckingStrategy;
import org.apache.wicket.authorization.strategies.CompoundAuthorizationStrategy;

/**
 *
 *
 */
public class UQasarAuthorizationStrategy extends CompoundAuthorizationStrategy {
	
	public UQasarAuthorizationStrategy() {
		add(new RoleBasedAuthorizationStrategy(new UQasarRoleCheckingStrategy()));
		add(new UQasarRedirectWithoutLoginStrategy());
	}
}
