/*
 */

package eu.uqasar.auth.strategies.annotation;

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

	private boolean isActionAuthorized(final Class<?> componentClass, final Action action) {
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
				if ((!isEmpty(deniedRoles)) && hasAny(deniedRoles)) {
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