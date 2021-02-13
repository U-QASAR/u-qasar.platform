/*
 */
package eu.uqasar.model.user;

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


import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.role.Role;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 *
 *
 */
@Setter
@Getter
@Entity(name = "TeamMemberships")
@XmlRootElement
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class TeamMembership extends AbstractEntity {
	
	@ManyToOne
	private User member;
	
	private Role userRole = Role.User;

	
	@ManyToOne
	@JoinColumn
	private Team team;

	public User getUser() {
		return member;
	}

	public void setUser(User user) {
		this.member = user;
	}

	public Role getRole() {
		return userRole;
	}

	public void setRole(Role role) {
		this.userRole = role;
	}
}
