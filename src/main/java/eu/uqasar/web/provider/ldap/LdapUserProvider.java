
package eu.uqasar.web.provider.ldap;

import eu.uqasar.util.ldap.LdapManager;
import eu.uqasar.util.ldap.LdapUser;
import java.util.List;
import javax.naming.NamingException;

/**
 *
 *
 */
public class LdapUserProvider extends LdapEntityProvider<LdapUser> {

	public LdapUserProvider(LdapManager manager, int max) {
		super(manager, max);
	}
	
	@Override
	public List<LdapUser> getEntities(LdapManager manager, int max) throws NamingException {
		return manager.getUsersList(max);
	}
	
}
