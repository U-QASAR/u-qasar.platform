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


import eu.uqasar.auth.strategies.IRoleCheckingStrategy;
import eu.uqasar.auth.strategies.AbstractAuthorizationStrategy;
import eu.uqasar.model.role.Role;
import java.util.EnumSet;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.request.component.IRequestableComponent;

/**
 *
 *
 */
public class MetaDataAuthorizationStrategy extends AbstractAuthorizationStrategy {

	private static final MetaDataKey<ActionPermissions> ACTION_PERMISSIONS = new MetaDataKey<ActionPermissions>() {
		private static final long serialVersionUID = -6034806047968773277L;

	};

	/**
	 * Application meta data key for actions/roles information. Typically, you
	 * do not need to use this meta data key directly, but instead use one of
	 * the bind methods of this class.
	 */
	private static final MetaDataKey<InstantiationPermissions> INSTANTIATION_PERMISSIONS = new MetaDataKey<InstantiationPermissions>() {
		private static final long serialVersionUID = 5526377385090628897L;
	};

	/**
	 * Construct.
	 *
	 * @param checkingStrategy the authorizer object
	 */
	public MetaDataAuthorizationStrategy(final IRoleCheckingStrategy checkingStrategy) {
		super(checkingStrategy);
	}

	/**
	 * Authorizes the given roles to create component instances of type
	 * componentClass. This authorization is added to any previously authorized
	 * roles.
	 *
	 * @param <T>
	 *
	 * @param componentClass The component type that is subject for the
	 * authorization
	 * @param roles The roles that are authorized to create component instances
	 * of type componentClass
	 */
	private static <T extends Component> void authorize(final Class<T> componentClass, final Role... roles) {
		final Application application = Application.get();
		InstantiationPermissions permissions = application.getMetaData(INSTANTIATION_PERMISSIONS);
		if (permissions == null) {
			permissions = new InstantiationPermissions();
			application.setMetaData(INSTANTIATION_PERMISSIONS, permissions);
		}
		permissions.authorize(componentClass, roles);
	}

	public static <T extends Component> void authorizeIf(final IAuthorizationCondition condition, final Class<T> componentClass,
                                                         final Role... roles) {
		if (condition.isAuthorized()) {
			authorize(componentClass, roles);
		}
	}

	/**
	 * Authorizes the given roles to create component instances of type
	 * componentClass. This authorization is added to any previously authorized
	 * roles.
	 *
	 * @param <T>
	 *
	 * @param componentClass The component type that is subject for the
	 * authorization
	 * @param roles The roles that are authorized to create component instances
	 * of type componentClass
	 */
	private static <T extends Component> void authorize(final Class<T> componentClass, final EnumSet<Role> roles) {
		authorize(componentClass, roles.toArray(new Role[roles.size()]));
	}

	public static <T extends Component> void authorizeIf(final IAuthorizationCondition condition, final Class<T> componentClass,
                                                         final EnumSet<Role> roles) {
		if (condition.isAuthorized()) {
			authorize(componentClass, roles);
		}
	}

	/**
	 * Authorizes the given roles to perform the given action on the given
	 * component.
	 *
	 * @param component The component that is subject to the authorization
	 * @param action The action to authorize
	 * @param roles The roles to authorize
	 */
	private static void authorize(final Component component, final Action action, final Role... roles) {
		ActionPermissions permissions = component.getMetaData(ACTION_PERMISSIONS);
		if (permissions == null) {
			permissions = new ActionPermissions();
			component.setMetaData(ACTION_PERMISSIONS, permissions);
		}
		permissions.authorize(action, roles);
	}

	public static void authorizeIf(final IAuthorizationCondition condition, final Component component, final Action action,
                                   final Role... roles) {
		if (condition.isAuthorized()) {
			authorize(component, action, roles);
		}
	}

	/**
	 * Authorizes the given roles to perform the given action on the given
	 * component.
	 *
	 * @param component The component that is subject to the authorization
	 * @param action The action to authorize
	 * @param roles The roles to authorize
	 */
	private static void authorize(final Component component, final Action action, final EnumSet<Role> roles) {
		authorize(component, action, roles.toArray(new Role[roles.size()]));
	}

	public static void authorizeIf(final IAuthorizationCondition condition, final Component component, final Action action,
                                   final EnumSet<Role> levels) {
		if (condition.isAuthorized()) {
			authorize(component, action, levels);
		}
	}

	/**
	 * Grants permission to all roles to create instances of the given component
	 * class.
	 *
	 * @param <T>
	 *
	 * @param componentClass The component class
	 */
	private static <T extends Component> void authorizeAll(final Class<T> componentClass) {
		Application application = Application.get();
		InstantiationPermissions authorizedLevels = application.getMetaData(INSTANTIATION_PERMISSIONS);
		if (authorizedLevels != null) {
			authorizedLevels.authorizeAll(componentClass);
		}
	}

	public static <T extends Component> void authorizeAllIf(final IAuthorizationCondition condition, final Class<T> componentClass) {
		if (condition.isAuthorized()) {
			authorizeAll(componentClass);
		}
	}

	/**
	 * Grants permission to all roles to perform the given action on the given
	 * component.
	 *
	 * @param component The component that is subject to the authorization
	 * @param action The action to authorize
	 */
	private static void authorizeAll(final Component component, final Action action) {
		ActionPermissions permissions = component.getMetaData(ACTION_PERMISSIONS);
		if (permissions != null) {
			permissions.authorizeAll(action);
		}
	}

	public static void authorizeAllIf(final IAuthorizationCondition condition, final Component component, final Action action) {
		if (condition.isAuthorized()) {
			authorizeAll(component, action);
		}
	}

	/**
	 * Removes permission for the given roles to create instances of the given
	 * component class. There is no danger in removing authorization by calling
	 * this method. If the last authorization grant is removed for a given
	 * componentClass, the internal level Role.NoRole will automatically be
	 * added, effectively denying access to all roles (if this was not done, all
	 * levels would suddenly have access since no authorization is equivalent to
	 * full access).
	 *
	 * @param <T>
	 *
	 * @param componentClass The component type
	 * @param roles The set of roles that are no longer to be authorized to
	 * create instances of type componentClass
	 */
	private static <T extends Component> void unauthorize(final Class<T> componentClass, final Role... roles) {
		final InstantiationPermissions permissions = Application.get().getMetaData(INSTANTIATION_PERMISSIONS);
		if (permissions != null) {
			permissions.unauthorize(componentClass, roles);
		}
	}

	public static <T extends Component> void unauthorizeIf(final IAuthorizationCondition condition, final Class<T> componentClass,
                                                           final Role... roles) {
		if (condition.isAuthorized()) {
			unauthorize(componentClass, roles);
		}
	}

	/**
	 * Removes permission for the given roles to create instances of the given
	 * component class. There is no danger in removing authorization by calling
	 * this method. If the last authorization grant is removed for a given
	 * componentClass, the internal role Role.NoRole will be automatically
	 * added, effectively denying access to all roles (if this was not done, all
	 * roles would suddenly have access since no authorization is equivalent to
	 * full access).
	 *
	 * @param <T>
	 *
	 * @param componentClass The component type
	 * @param roles The set of roles that are no longer to be authorized to
	 * create instances of type componentClass
	 */
	private static <T extends Component> void unauthorize(final Class<T> componentClass, final EnumSet<Role> roles) {
		unauthorize(componentClass, roles.toArray(new Role[roles.size()]));
	}

	public static <T extends Component> void unauthorizeIf(final IAuthorizationCondition condition, final Class<T> componentClass,
                                                           final EnumSet<Role> roles) {
		if (condition.isAuthorized()) {
			unauthorize(componentClass, roles);
		}
	}

	/**
	 * Removes permission for the given roles to perform the given action on the
	 * given component. There is no danger in removing authorization by calling
	 * this method. If the last authorization grant is removed for a given
	 * action, the internal role Role.NoRole will automatically be added,
	 * effectively denying access to all roles (if this was not done, all roles
	 * would suddenly have access since no authorization is equivalent to full
	 * access).
	 *
	 * @param component The component
	 * @param action The action
	 * @param roles The set of roles that are no longer allowed to perform the
	 * given action
	 */
	private static void unauthorize(final Component component, final Action action, final Role... roles) {
		final ActionPermissions permissions = component.getMetaData(ACTION_PERMISSIONS);
		if (permissions != null) {
			permissions.unauthorize(action, roles);
		}
	}

	public static void unauthorizeIf(final IAuthorizationCondition condition, final Component component, final Action action,
                                     final Role... roles) {
		if (condition.isAuthorized()) {
			unauthorize(component, action, roles);
		}
	}

	/**
	 * Removes permission for the given roles to perform the given action on
	 * the given component. There is no danger in removing authorization by
	 * calling this method. If the last authorization grant is removed for a
	 * given action, the internal role Role.NoRole will automatically be added,
	 * effectively denying access to all roles (if this was not done, all
	 * roles would suddenly have access since no authorization is equivalent to
	 * full access).
	 *
	 * @param component The component
	 * @param action The action
     */
	private static void unauthorize(final Component component, final Action action, final EnumSet<Role> roleslevels) {
		unauthorize(component, action, roleslevels.toArray(new Role[roleslevels.size()]));
	}

	public static void unauthorizeIf(final IAuthorizationCondition condition, final Component component, final Action action,
                                     final EnumSet<Role> roles) {
		if (condition.isAuthorized()) {
			unauthorize(component, action, roles);
		}
	}

	/**
	 * Grants authorization to instantiate the given class to just the role
	 * Role.NoRole, effectively denying all other roles.
	 *
	 * @param <T>
	 *
	 * @param componentClass The component class
	 */
	private static <T extends Component> void unauthorizeAll(Class<T> componentClass) {
		authorizeAll(componentClass);
		authorize(componentClass, Role.NoRole);
	}

	public static <T extends Component> void unauthorizeAllIf(final IAuthorizationCondition condition, Class<T> componentClass) {
		if (condition.isAuthorized()) {
			unauthorizeAll(componentClass);
		}
	}

	/**
	 * Grants authorization to perform the given action to just the role
	 * Role.NoRole, effectively denying all other roles.
	 *
	 * @param component the component that is subject to the authorization
	 * @param action the action to authorize
	 */
	private static void unauthorizeAll(final Component component, final Action action) {
		authorizeAll(component, action);
		authorize(component, action, Role.NoRole);
	}

	public static void unauthorizeAllIf(final IAuthorizationCondition condition, final Component component, final Action action) {
		if (condition.isAuthorized()) {
			unauthorizeAll(component, action);
		}
	}

	/**
	 * Uses component level meta data to match levels for component action
	 * execution.
	 *
	 * @see
	 * org.apache.wicket.authorization.IAuthorizationStrategy#isActionAuthorized(org.apache.wicket.Component,
	 * org.apache.wicket.authorization.Action)
	 */
	@Override
	public boolean isActionAuthorized(final Component component, final Action action) {
		if (component == null) {
			throw new IllegalArgumentException("argument component has to be not null");
		}
		if (action == null) {
			throw new IllegalArgumentException("argument action has to be not null");
		}

		final EnumSet<Role> roles = levelsAuthorizedToPerformAction(component, action);
		if (roles != null) {
			return hasAny(roles);
		}
		return true;
	}

	/**
	 * Uses application level meta data to match levels for component
	 * instantiation.
	 *
	 * @see
	 * org.apache.wicket.authorization.IAuthorizationStrategy#isInstantiationAuthorized(java.lang.Class)
	 */
	@Override
	public <T extends IRequestableComponent> boolean isInstantiationAuthorized(final Class<T> componentClass) {
		if (componentClass == null) {
			throw new IllegalArgumentException("argument componentClass cannot be null");
		}

		// as long as the interface does not use generics, we should check this
		if (!Component.class.isAssignableFrom(componentClass)) {
			throw new IllegalArgumentException("argument componentClass must be of type " + Component.class.getName());
		}

		final EnumSet<Role> roles = rolesAuthorizedToInstantiate(componentClass);
		if (roles != null) {
			return hasAny(roles);
		}
		return true;
	}

	/**
	 * Gets the roles for creation of the given component class, or null if
	 * none were registered.
	 *
	 * @param <T>
	 *
	 * @param componentClass the component class
	 * @return the roles that are authorized for creation of the
	 * componentClass, or null if no specific authorization was configured
	 */
	private static <T extends IRequestableComponent> EnumSet<Role> rolesAuthorizedToInstantiate(final Class<T> componentClass) {
		final InstantiationPermissions permissions = Application.get().getMetaData(INSTANTIATION_PERMISSIONS);
		if (permissions != null) {
			return permissions.authorizedRoles(componentClass);
		}
		return null;
	}

	/**
	 * Gets the roles for the given action/component combination.
	 *
	 * @param component the component
	 * @param action the action
	 * @return the roles for the action as defined with the given component
	 */
	private static EnumSet<Role> levelsAuthorizedToPerformAction(final Component component, final Action action) {
		final ActionPermissions permissions = component.getMetaData(ACTION_PERMISSIONS);
		if (permissions != null) {
            return permissions.rolesFor(action);
		}
		return null;
	}

}
