/*
 */
package eu.uqasar.auth.strategies.metadata;

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


import eu.uqasar.model.role.Role;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.util.io.IClusterable;

/**
 *
 *
 */
class ActionPermissions implements IClusterable {

	/**
	 * Map from an action to a set of roles
	 */
	private final Map<Action, EnumSet<Role>> authorizedRolesForAction = new HashMap<>();

	/**
	 * Gives permission for the given roles to perform the given action
	 *
	 * @param action The action
	 * @param rolesToAuthorize The roles that will be authorized to perform the
	 * action
	 */
	public final void authorize(final Action action, Role... rolesToAuthorize) {
		if (action == null) {
			throw new IllegalArgumentException("Argument action cannot be null");
		}

		if (rolesToAuthorize == null) {
			throw new IllegalArgumentException("Argument rolesToAuthorize cannot be null");
		}

		EnumSet<Role> roleslevels = authorizedRolesForAction.get(action);
		if (roleslevels == null) {
			roleslevels = EnumSet.noneOf(Role.class);
		}
		roleslevels.addAll(Arrays.asList(rolesToAuthorize));
		authorizedRolesForAction.put(action, roleslevels);
	}

	/**
	 * Gives permission for the given roles to perform the given action
	 *
	 * @param action The action
	 * @param rolesToAuthorize The roles that will be authorized to perform the
	 * action
	 */
	public final void authorize(final Action action, EnumSet<Role> rolesToAuthorize) {
		this.authorize(action, rolesToAuthorize.toArray(new Role[rolesToAuthorize.size()]));
	}

	/**
	 * Remove all authorization roles for the given action.
	 *
	 * @param action The action to clear
	 */
	public final void authorizeAll(final Action action) {
		if (action == null) {
			throw new IllegalArgumentException("Argument action cannot be null");
		}

		authorizedRolesForAction.remove(action);
	}

	/**
	 * Gets the roles that have a binding for the given action.
	 *
	 * @param action The action
	 * @return The roles authorized for the given action
	 */
	public final EnumSet<Role> rolesFor(final Action action) {
		if (action == null) {
			throw new IllegalArgumentException("Argument action cannot be null");
		}

		return authorizedRolesForAction.get(action);
	}

	/**
	 * Remove the given authorized roles from an action. Note that this is only
	 * relevant if a role was previously authorized for that action. If no roles
	 * where previously authorized the effect of the unauthorize call is that no
	 * roles at all will be authorized for that action.
	 *
	 * @param action The action
	 * @param rolesToUnauthorize The list of roles that will no longer be
	 * authorized to perform the action
	 */
	public final void unauthorize(final Action action, final Role... rolesToUnauthorize) {
		if (action == null) {
			throw new IllegalArgumentException("Argument action cannot be null");
		}

		if (rolesToUnauthorize == null) {
			throw new IllegalArgumentException("Argument rolesToUnauthorize cannot be null");
		}

		EnumSet<Role> roleslevels = authorizedRolesForAction.get(action);
		if (roleslevels != null) {
			roleslevels.removeAll(Arrays.asList(rolesToUnauthorize));
		} else {
			roleslevels = EnumSet.noneOf(Role.class);
		}

		// If we removed the last authorized role, we authorize the empty role
		// so that removing authorization can't suddenly open something up to
		// everyone.
		if (roleslevels.size() == 0) {
			roleslevels.add(Role.NoRole);
		}
		authorizedRolesForAction.put(action, roleslevels);
	}

	/**
	 * Remove the given authorized roles from an action. Note that this is only
	 * relevant if a role was previously authorized for that action. If no roles
	 * where previously authorized the effect of the unauthorize call is that no
	 * roles at all will be authorized for that action.
	 *
	 * @param action The action
	 * @param rolesToUnauthorize The list of roles that will no longer be
	 * authorized to perform the action
	 */
	public final void unauthorize(final Action action, final EnumSet<Role> rolesToUnauthorize) {
		this.unauthorize(action, rolesToUnauthorize.toArray(new Role[rolesToUnauthorize.size()]));
	}
}
