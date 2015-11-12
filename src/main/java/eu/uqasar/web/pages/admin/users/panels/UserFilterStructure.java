package eu.uqasar.web.pages.admin.users.panels;

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
import eu.uqasar.model.user.RegistrationStatus;
import eu.uqasar.model.user.UserSource;
import java.io.Serializable;

/**
 *
 *
 */
public class UserFilterStructure implements Serializable {

	private Role role;
	private UserSource source;
	private RegistrationStatus status;

	public UserFilterStructure() {

	}

	public UserFilterStructure(UserFilterPanel panel) {
		this.role = panel.getRole();
		this.name = panel.getName();
		this.source = panel.getSource();
		this.status = panel.getStatus();
	}

	@Override
	public String toString() {
		return "UserFilterStructure{" + "role=" + role + ", source=" + source + ", status=" + status + ", name=" + name + '}';
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public UserSource getSource() {
		return source;
	}

	public void setSource(UserSource source) {
		this.source = source;
	}

	public RegistrationStatus getStatus() {
		return status;
	}

	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	private String name;

}
