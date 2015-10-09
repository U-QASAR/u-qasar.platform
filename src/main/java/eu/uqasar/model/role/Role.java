package eu.uqasar.model.role;

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

	private Role(final String labelKey) {
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

	public boolean hasAnyRoles(EnumSet<Role> allowedRoles) {
		return allowedRoles.contains(this);
	}

	public static boolean hasAnyRoles(Role role, EnumSet<Role> allowedRoles) {
		return role.hasAnyRoles(allowedRoles);
	}
	
	    public static List<Role> getAllRolesWithLoggedInUser(User user) {
        // check user role
        if (user.getRole() != null) {
            if (user.getRole() != Role.Administrator) {
                List<Role> roles = new LinkedList<Role>();
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
