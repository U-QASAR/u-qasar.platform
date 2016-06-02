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
