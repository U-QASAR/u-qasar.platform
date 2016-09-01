/*
 */
package eu.uqasar.auth.strategies;

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
import java.util.EnumSet;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.util.lang.Args;

/**
 *
 *
 */
public abstract class AbstractAuthorizationStrategy implements IAuthorizationStrategy {

	/**
	 * Role checking strategy.
	 */
	private final IRoleCheckingStrategy checkingStrategy;

	/**
	 * Construct.
	 *
	 * @param checkingStrategy the authorizer delegate
	 */
	protected AbstractAuthorizationStrategy(IRoleCheckingStrategy checkingStrategy) {
		Args.notNull(checkingStrategy, "checkingStrategy");
		this.checkingStrategy = checkingStrategy;
	}

	/**
	 * Gets whether any of the given roles applies to the authorizer.
	 *
	 * @param roles the roles to check
	 * @return whether any of the given roles applies to the authorizer
	 */
	protected final boolean hasAny(Role... roles) {
		if ((roles == null) || (roles.length == 0)) {
			return true;
		} else {
			return checkingStrategy.hasAnyRole(roles);
		}
	}

	/**
	 * Gets whether any of the given roles applies to the authorizer.
	 *
	 * @param roles the roles to check
	 * @return whether any of the given roles applies to the authorizer
	 */
	protected final boolean hasAny(EnumSet<Role> roles) {
		if ((roles == null) || (roles.isEmpty())) {
			return true;
		} else {
			return checkingStrategy.hasAnyRole(roles);
		}
	}

	/**
	 * Conducts a check to see if the roles object is empty. Since the roles
	 * object does not contain any null values and will always hold an empty
	 * string, an extra test is required beyond roles.isEmpty().
	 *
	 * @param roles the roles object to test
	 * @return true if the object holds no real roles
	 */
	protected final boolean isEmpty(Role... roles) {
		return (roles == null) || (roles.length == 0);
	}

	/**
	 * Conducts a check to see if the roles object is empty. Since the roles
	 * object does not contain any null values and will always hold an empty
	 * string, an extra test is required beyond roles.isEmpty().
	 *
	 * @param roles the roles object to test
	 * @return true if the object holds no real roles
	 */
	protected final boolean isEmpty(EnumSet<Role> roles) {
		return (roles == null) || (roles.isEmpty());
	}
}
