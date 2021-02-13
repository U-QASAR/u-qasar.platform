/*
 */
package eu.uqasar.util.ldap;

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


import eu.uqasar.model.settings.ldap.LdapSettings;
import static eu.uqasar.model.settings.ldap.LdapSettings.getDefault;
import eu.uqasar.service.settings.LdapSettingsService;
import eu.uqasar.util.ldap.LdapUser.LdapUserComparator;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapReferralException;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 */
@Getter(AccessLevel.PRIVATE)
public class LdapManager implements Serializable {

    private static final Logger logger = Logger.getLogger(LdapManager.class);
    private static final String EMPTY_FILTER = "(objectClass=*)";

    private final LdapSettings settings;
    private transient LdapContext context;

    private static final transient Comparator<LdapUser> userComparator = new LdapUser.LdapUserComparator();
    private static final transient Comparator<LdapGroup> groupComparator = new LdapGroup.LdapGroupComparator();

    private LdapManager(LdapSettings settings) throws NamingException {
        this.settings = settings;
        context = getConnection();
    }

    public static LdapManager getInstance(LdapSettings settings) throws NamingException {
        return new LdapManager(settings);
    }

    public static LdapManager getInstance(LdapSettingsService service) throws NamingException {
        LdapSettings settings = getDefault();
        settings = service.get(settings);
        return new LdapManager(settings);
    }

    public void closeConnection() throws NamingException {
        context.close();
    }

    public boolean authenticateBySAMAccountName(final String userName, final String password) throws NamingException {
        LdapUser user = getUserBySAMAccountName(userName);
        if (user == null) {
            return false;
        }
        return getConnection(settings, user.getDN(), password) != null;
    }

    public List<LdapGroup> getGroupsList(int maximum) throws NamingException {
        return filterGroupsList(getLdapEntities(maximum, settings.
                getGroupFilterBaseDN(), settings.getGroupFilter(),
                LdapGroup.class, groupComparator));
    }

    public List<LdapUser> getUsersList(int maximum) throws NamingException {
        return getLdapEntities(maximum, settings.getUserFilterBaseDN(), settings.
                getUserFilter(),
                LdapUser.class, userComparator);
    }

    public int getUsersCount(int maximum) throws NamingException {
        return countLdapEntities(maximum, settings.getUserFilterBaseDN(), settings.
                getUserFilter());
    }

    public List<LdapUser> getUsersFromGroup(int maximum, LdapGroup group) throws NamingException {
        List<LdapUser> users = new ArrayList<>();
        final String mapping = settings.getGroupMemberMapping();
        javax.naming.directory.Attribute members = group.
                getMappedAttribute(mapping);
        if (members == null) {
            return users;
        }
        NamingEnumeration<?> results = members.getAll();
        while (results.hasMoreElements() && users.size() < maximum) {
            try {
                final String userDN = (String) results.next();
                LdapUser user = getUserByDNAndFilter(userDN, settings.
                        getUserFilter());
                if (user != null) {
                    users.add(user);
                }
            } catch (LdapReferralException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
        Collections.sort(users, new LdapUserComparator());
        return users;
    }

    private List<LdapGroup> filterGroupsList(List<LdapGroup> groups) throws NamingException {
        Iterator<LdapGroup> iterator = groups.iterator();
        while (iterator.hasNext()) {
            LdapGroup group = iterator.next();
            if (!groupHasUserMembers(group)) {
                iterator.remove();
            }
        }
        return groups;
    }

    private boolean groupHasUserMembers(LdapGroup group) throws NamingException {
        return !group.getMembers().isEmpty();
    }

    private NamingEnumeration<SearchResult> searchLDAP(final String baseDN, final String preferredFilter) throws NamingException {
        final String filter = preferredFilter == null || preferredFilter.isEmpty() ? EMPTY_FILTER : preferredFilter;
        return getContext().
                search(baseDN, filter, getDefaultSearchControls());
    }

    private SearchControls getDefaultSearchControls() {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SUBTREE_SCOPE);
        controls.setReturningAttributes(null);
        controls.setReturningObjFlag(true);
        return controls;
    }

    private LdapUser getUserByDN(final String userDN) throws NamingException {
        NamingEnumeration<SearchResult> answer = getContext().search(settings.
                getUserFilterBaseDN(), "(distinguishedName=" + userDN + ")", getDefaultSearchControls());
        while (answer.hasMoreElements()) {
            Attributes attr = answer.next().getAttributes();
            if (hasRequiredUserAttributesFilled(attr, settings)) {
                return new LdapUser(attr, settings);
            }
        }
        return null;
    }

    private LdapUser getUserBySAMAccountName(final String sAMAccountName) throws NamingException {
        NamingEnumeration<SearchResult> answer = getContext().search(settings.
                getUserFilterBaseDN(), "(sAMAccountName=" + sAMAccountName + ")", getDefaultSearchControls());
        while (answer.hasMoreElements()) {
            Attributes attr = answer.next().getAttributes();
            if (hasRequiredUserAttributesFilled(attr, settings)) {
                return new LdapUser(attr, settings);
            }
        }
        return null;
    }

    private LdapUser getUserByDNAndFilter(final String userDN, final String filter) throws NamingException {
        final String dnFilter = "(distinguishedName=" + userDN + ")";
        boolean conjunction = filter.startsWith("(&(") && filter.endsWith("))");
        String endFilter;
        if (conjunction) {
            endFilter = filter.substring(0, filter.length() - 1) + dnFilter + ")";
        } else {
            endFilter = dnFilter;
        }
        NamingEnumeration<SearchResult> answer = getContext().search(settings.
                getUserFilterBaseDN(), endFilter, getDefaultSearchControls());
        while (answer.hasMoreElements()) {
            Attributes attr = answer.next().getAttributes();
            if (hasRequiredUserAttributesFilled(attr, settings)) {
                return new LdapUser(attr, settings);
            }
        }
        return null;
    }

    private boolean hasRequiredUserAttributesFilled(Attributes attrs, LdapSettings settings) {
        return LdapUser.hasValidMailValue(attrs, settings) && LdapUser.
                hasValidUserNameValue(attrs, settings);
    }

    private int countLdapEntities(int maximum, final String baseDN, final String preferredFilter) throws NamingException {
        if (maximum <= 0) {
            return 0;
        }
        int count = 0;
        NamingEnumeration<SearchResult> results = searchLDAP(baseDN, preferredFilter);
        while (results.hasMoreElements() && count < maximum) {
            try {
                results.next();
                count++;
            } catch (LdapReferralException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
        return count;
    }

    private <T extends LdapEntity> List<T> getLdapEntities(int maximum, final String baseDN, final String preferredFilter,
            Class<T> clazz, Comparator<T> comparator) throws NamingException {
        if (maximum <= 0) {
            return Collections.emptyList();
        }
        List<T> entities = new ArrayList<>();
        NamingEnumeration<SearchResult> results = searchLDAP(baseDN, preferredFilter);
        while (results.hasMoreElements() && entities.size() < maximum) {
            try {
                SearchResult group = results.next();
                Constructor<T> constructor = clazz.
                        getConstructor(Attributes.class, LdapSettings.class);
                T entity = constructor.
                        newInstance(group.getAttributes(), settings);
                entities.add(entity);
            } catch (LdapReferralException ex) {
                logger.warn(ex.getMessage(), ex);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        Collections.sort(entities, comparator);
        return entities;
    }

    private LdapContext getConnection(LdapSettings settings) throws NamingException {
        return getConnection(settings, null, null);
    }

    private LdapContext getConnection(LdapSettings settings, final String userName, final String password) throws CommunicationException, NamingException {
        Validate.notEmpty(settings.getAuthUserDN());

        // bind by using the specified username/password
        Properties props = new Properties();
        props.put(Context.SECURITY_PRINCIPAL, userName == null ? settings.getAuthUserDN() : userName);
        if (settings.getAuthUserPassword() != null || password != null) {
            props.put(Context.SECURITY_CREDENTIALS, password == null ? settings.
                    getAuthUserPassword() : password);
        }

        // ensures that objectSID attribute values
        // will be returned as a byte[] instead of a String
        props.put("java.naming.ldap.attributes.binary", "objectSID");

        // the following is helpful in debugging errors
        // props.put("com.sun.jndi.ldap.trace.ber", System.err);
        String ldapURL = String.
                format("ldap://%s:%s", settings.getHost(), settings.getPort());
        props.
                put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, ldapURL);
        props.put(Context.REFERRAL, "follow");
        try {
            return new InitialLdapContext(props, null);
        } catch (CommunicationException e) {
            logger.warn(String.format("Failed to connect to %s:%s", settings.
                    getHost(), settings.getPort()), e);
            throw e;
        } catch (NamingException e) {
            logger.warn(String.format("Failed to authenticate %s:%s", settings.
                    getHost(), settings.getPort()), e);
            throw e;
        }
    }

    private LdapContext getConnection()
            throws CommunicationException, NamingException {
        return getConnection(this.settings);
    }
}
