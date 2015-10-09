/*
 */
package eu.uqasar.auth.strategies;

import eu.uqasar.model.role.Role;
import java.util.EnumSet;

/**
 *
 *
 */
public interface IRoleCheckingStrategy {

	/**
	 * Whether any of the given roles matches. For example, if a user has role
	 * ReqsEngineer and the provided levels are {ReqsEngineer, ProductOwner}
	 * this method should return true as the user has at least one of the levels
	 * that were provided.
	 *
	 * @param roles the roles to check
	 * @return true if a user or whatever subject this implementation wants to
	 * work with has at least on of the provided roles
	 */
	boolean hasAnyRole(EnumSet<Role> roles);

	/**
	 * Whether any of the given roles matches. For example, if a user has role
	 * ReqsEngineer and the provided levels are {ReqsEngineer, ProductOwner}
	 * this method should return true as the user has at least one of the levels
	 * that were provided.
	 *
	 * @param roles the roles to check
	 * @return true if a user or whatever subject this implementation wants to
	 * work with has at least on of the provided roles
	 */
	boolean hasAnyRole(Role... roles);
}
