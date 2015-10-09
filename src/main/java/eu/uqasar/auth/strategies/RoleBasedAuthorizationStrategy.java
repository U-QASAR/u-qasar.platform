/*
 */
package eu.uqasar.auth.strategies;

import eu.uqasar.auth.strategies.annotation.AnnotationBasedAuthorizationStrategy;
import eu.uqasar.auth.strategies.metadata.MetaDataAuthorizationStrategy;
import org.apache.wicket.authorization.strategies.CompoundAuthorizationStrategy;

/**
 *
 *
 */
public class RoleBasedAuthorizationStrategy extends CompoundAuthorizationStrategy {

	public RoleBasedAuthorizationStrategy(final IRoleCheckingStrategy checkingStrategy) {
		add(new AnnotationBasedAuthorizationStrategy(checkingStrategy));
		add(new MetaDataAuthorizationStrategy(checkingStrategy));
	}
}
