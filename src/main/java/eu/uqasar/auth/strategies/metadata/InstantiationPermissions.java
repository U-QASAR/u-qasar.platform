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
import org.apache.wicket.Component;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.util.io.IClusterable;

/**
 *
 *
 */
class InstantiationPermissions implements IClusterable {

	/**
	 * Holds roles objects for component classes
	 */
	private final Map<Class<? extends Component>, EnumSet<Role>> authorizedRolesForComponentClass = new HashMap<>();

	/**
	 * Gives the given role permission to instantiate the given class.
	 *
	 * @param <T>
	 * @param componentClass The component class
	 * @param rolesToAuthorize The roles that will be authorized to instantiate
	 * the given class
	 */
	public final <T extends Component> void authorize(final Class<T> componentClass, final Role... rolesToAuthorize) {
		if (componentClass == null) {
			throw new IllegalArgumentException("Argument componentClass cannot be null");
		}

		if (rolesToAuthorize == null) {
			throw new IllegalArgumentException("Argument rolesToAuthorize cannot be null");
		}

		EnumSet<Role> roles = authorizedRolesForComponentClass.get(componentClass);
		if (roles == null) {
			roles = EnumSet.noneOf(Role.class);
			authorizedRolesForComponentClass.put(componentClass, roles);
		}
		roles.addAll(Arrays.asList(rolesToAuthorize));
	}

	/**
	 * Gives the given roles permission to instantiate the given class.
	 *
	 * @param <T>
	 * @param componentClass The component class
	 * @param rolesToAuthorize The roles that will be authorized to instantiate
	 * the given class
	 */
	public final <T extends Component> void authorize(final Class<T> componentClass, final EnumSet<Role> rolesToAuthorize) {
		authorize(componentClass, rolesToAuthorize.toArray(new Role[rolesToAuthorize.size()]));
	}

	/**
	 * Gives all roles permission to instantiate the given class. Note that this
	 * is only relevant if a role was previously unauthorized for that class. If
	 * some roles where previously unauthorized, the effect of the authorizeAll
	 * call is that all roles will be authorized for that class.
	 *
	 * @param <T>
	 * @param componentClass The component class
	 */
	public final <T extends Component> void authorizeAll(final Class<T> componentClass) {
		if (componentClass == null) {
			throw new IllegalArgumentException("Argument componentClass cannot be null");
		}
		authorizedRolesForComponentClass.remove(componentClass);
	}

	/**
	 * Gets the roles that have a binding with the given component class.
	 *
	 * @param <T>
	 *
	 * @param componentClass the component class
	 * @return the roles that have a binding with the given component class, or
	 * null if no entries are found
	 */
	public <T extends IRequestableComponent> EnumSet<Role> authorizedRoles(final Class<T> componentClass) {
		if (componentClass == null) {
			throw new IllegalArgumentException("Argument componentClass cannot be null");
		}

		return authorizedRolesForComponentClass.get(componentClass);
	}

	/**
	 * Removes permission for the given roles to instantiate the given class.
	 *
	 * @param <T>
	 *
	 * @param componentClass The class
	 * @param rolesToUnauthorize The roles to deny instantiation of the given class
	 */
	public final <T extends Component> void unauthorize(final Class<T> componentClass, final Role... rolesToUnauthorize) {
		if (componentClass == null) {
			throw new IllegalArgumentException("Argument componentClass cannot be null");
		}

		if (rolesToUnauthorize == null) {
			throw new IllegalArgumentException("Argument rolesToUnauthorize cannot be null");
		}

		EnumSet<Role> roles = authorizedRolesForComponentClass.get(componentClass);
		if (roles != null) {
			roles.removeAll(Arrays.asList(rolesToUnauthorize));
		} else {
			roles = EnumSet.noneOf(Role.class);
			authorizedRolesForComponentClass.put(componentClass, roles);
		}

		// If we removed the last authorized role, we authorize the empty level
		// so that removing authorization can't suddenly open something up to
		// everyone.
		if (roles.size() == 0) {
			roles.add(Role.NoRole);
		}
	}

	/**
	 * Removes permission for the given roles to instantiate the given class.
	 *
	 * @param <T>
	 *
	 * @param componentClass The class
	 * @param rolesToUnauthorize The roles to deny instantiation of the given class
	 */
	public final <T extends Component> void unauthorize(final Class<T> componentClass, final EnumSet<Role> rolesToUnauthorize) {
		unauthorize(componentClass, rolesToUnauthorize.toArray(new Role[rolesToUnauthorize.size()]));
	}

	/**
	 * @return gets map with role objects for a component classes
	 */
	protected final Map<Class<? extends Component>, EnumSet<Role>> getAuthorizedRolesPerComponentClass() {
		return authorizedRolesForComponentClass;
	}

}
