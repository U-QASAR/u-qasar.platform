package eu.uqasar.web.provider.ldap;

import eu.uqasar.util.ldap.LdapGroup;
import eu.uqasar.util.ldap.LdapManager;
import java.util.List;
import javax.naming.NamingException;

/**
 *
 *
 */
public class LdapGroupProvider extends LdapEntityProvider<LdapGroup> {

	public LdapGroupProvider(LdapManager manager, int max) {
		super(manager, max);
	}

	@Override
	public List<LdapGroup> getEntities(LdapManager manager, int max) throws NamingException {
		return manager.getGroupsList(max);
	}
}
