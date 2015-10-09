package eu.uqasar.web.pages.admin.users.panels;

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
