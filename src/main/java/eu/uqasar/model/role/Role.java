package eu.uqasar.model.role;

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


import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.model.user.User;
import eu.uqasar.util.resources.ResourceBundleLocator;

public enum Role {

	NoRole("norole"),
	User("user"),
	Tester("tester"),
	Developer("developer"),
	ReqsEngineer("engineer.reqs"),
	DesignEngineer("engineer.design"),
	ScrumMaster("scrummaster"),
	ProductManager("manager.product"),
	ProcessManager("manager.process"),
	Administrator("admin"),;

	private final String labelKey;

	Role(final String labelKey) {
		this.labelKey = labelKey;
	}
	
	public static Role[] userAssignableRoles() {
		EnumSet<Role> teamRoles = EnumSet.allOf(Role.class);
		teamRoles.remove(NoRole);
		return teamRoles.toArray(new Role[0]);
	}
	
	public static Role[] teamAssignableRoles() {
		EnumSet<Role> teamRoles = EnumSet.allOf(Role.class);
		teamRoles.remove(Administrator);
		teamRoles.remove(NoRole);
		return teamRoles.toArray(new Role[0]);
	}

	@Override
	public String toString() {
		return getLabelModel().getObject();
	}

	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(Role.class, "label.role." + labelKey);
	}
	
	public static List<Role> getAllRoles(){
		return Arrays.asList(values());
	}

	public boolean hasAnyRoles(Role role) {
		return hasAnyRoles(EnumSet.of(role));
	}

	public boolean hasAnyRoles(Role... roles) {
		return hasAnyRoles(EnumSet.copyOf(Arrays.asList(roles)));
	}

	private boolean hasAnyRoles(EnumSet<Role> allowedRoles) {
		return allowedRoles.contains(this);
	}

	public static boolean hasAnyRoles(Role role, EnumSet<Role> allowedRoles) {
		return role.hasAnyRoles(allowedRoles);
	}
	
	    public static List<Role> getAllRolesWithLoggedInUser(User user) {
        // check user role
        if (user.getRole() != null) {
            if (user.getRole() != Role.Administrator) {
                List<Role> roles = new LinkedList<>();
                for (Role role : getAllRoles()) {
                    if (role.compareTo(Administrator) != 0) {
                        roles.add(role);
                    }
                }

                return roles;
            }
        }
        return Arrays.asList(values());
    }
}
