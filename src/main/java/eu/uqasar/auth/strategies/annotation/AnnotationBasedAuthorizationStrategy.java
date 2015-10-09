/*
 */

package eu.uqasar.auth.strategies.annotation;

import eu.uqasar.auth.strategies.IRoleCheckingStrategy;
import eu.uqasar.auth.strategies.AbstractAuthorizationStrategy;
import eu.uqasar.model.role.Role;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.request.component.IRequestableComponent;

public class AnnotationBasedAuthorizationStrategy extends AbstractAuthorizationStrategy {
	/**
	 * Construct.
	 * 
	 * @param checkingStrategy
	 *            the authorizer delegate
	 */
	public AnnotationBasedAuthorizationStrategy(final IRoleCheckingStrategy checkingStrategy) {
		super(checkingStrategy);
	}

	/**
	 * @see org.apache.wicket.authorization.IAuthorizationStrategy#isInstantiationAuthorized(java.lang.Class)
	 */
	@Override
	public <T extends IRequestableComponent> boolean isInstantiationAuthorized(final Class<T> componentClass) {
		// We are authorized unless we are found not to be
		boolean authorized = true;

		// Check class annotation first because it is more specific than package
		// annotation
		final AuthorizeInstantiation classAnnotation = componentClass.getAnnotation(AuthorizeInstantiation.class);
		if (classAnnotation != null) {
			authorized = hasAny(classAnnotation.value());
		} else {
			// Check package annotation if there is no one on the the class
			final Package componentPackage = componentClass.getPackage();
			if (componentPackage != null) {
				final AuthorizeInstantiation packageAnnotation = componentPackage.getAnnotation(AuthorizeInstantiation.class);
				if (packageAnnotation != null) {
					authorized = hasAny(packageAnnotation.value());
				}
			}
		}

		return authorized;
	}

	@Override
	public boolean isActionAuthorized(final Component component, final Action action) {
		// Get component's class
		final Class<?> componentClass = component.getClass();

		return isActionAuthorized(componentClass, action);
	}

	protected boolean isActionAuthorized(final Class<?> componentClass, final Action action) {
		// Check for a single action
		if (!check(action, componentClass.getAnnotation(AuthorizeAction.class))) {
			return false;
		}

		// Check for multiple actions
		final AuthorizeActions authorizeActionsAnnotation = componentClass.getAnnotation(AuthorizeActions.class);
		if (authorizeActionsAnnotation != null) {
			for (final AuthorizeAction authorizeActionAnnotation : authorizeActionsAnnotation.actions()) {
				if (!check(action, authorizeActionAnnotation)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * @param action
	 *            The action to check
	 * @param authorizeActionAnnotation
	 *            The annotations information
	 * @return False if the action is not authorized
	 */
	private boolean check(final Action action, final AuthorizeAction authorizeActionAnnotation) {
		if (authorizeActionAnnotation != null) {
			if (action.getName().equals(authorizeActionAnnotation.action())) {
				Role[] deniedRoles = authorizeActionAnnotation.deny();
				if ((isEmpty(deniedRoles) == false) && hasAny(deniedRoles)) {
					return false;
				}

				Role[] acceptedRoles = authorizeActionAnnotation.roles();
				if (!(isEmpty(acceptedRoles) || hasAny(acceptedRoles))) {
					return false;
				}
			}
		}
		return true;
	}
}