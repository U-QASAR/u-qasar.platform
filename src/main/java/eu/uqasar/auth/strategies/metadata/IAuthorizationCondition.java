/*
 */
package eu.uqasar.auth.strategies.metadata;

import java.io.Serializable;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;

/**
 *
 *
 */
public interface IAuthorizationCondition extends Serializable {

	/**
	 *
	 * @return <code>true</code> if this condition authorizes a specific
	 * {@link Action} for a {@link Component}, <code>false</code> otherwise.
	 */
	public boolean isAuthorized();
}
