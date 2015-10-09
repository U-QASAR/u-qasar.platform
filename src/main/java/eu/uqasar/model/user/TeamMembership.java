/*
 */
package eu.uqasar.model.user;

import static javax.persistence.CascadeType.MERGE;
import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.role.Role;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.annotations.Cascade;

/**
 *
 *
 */
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
	
	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	
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
