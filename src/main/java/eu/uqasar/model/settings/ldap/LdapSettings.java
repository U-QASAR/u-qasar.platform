/*
 */
package eu.uqasar.model.settings.ldap;

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


import eu.uqasar.model.settings.Settings;
import java.util.regex.Pattern;

/**
 *
 *
 */
public class LdapSettings extends Settings {

	private final static String REGX_LOOKUP_FIELD = "\\{([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*)*+\\}";
	private final static Pattern PATTERN_LOOKUP_FIELD = Pattern.compile(REGX_LOOKUP_FIELD);

	private static final String BASE = "setting.ldap.";
	private static final String HOST = BASE + "host";
	private static final String PORT = BASE + "port";
	private static final String AUTH_USER_DN = BASE + "auth.user.dn";
	private static final String AUTH_USER_PASS = BASE + "auth.user.pass";

	private static final String USER_FILTER = BASE + "filter.user";
	private static final String USER_FILTER_BASE_DN = USER_FILTER + "base.dn";
	private static final String USER_FIELD_MAPPING_USERNAME = BASE + "mapping.user.username";
	private static final String USER_FIELD_MAPPING_MAIL = BASE + "mapping.user.mail";
	private static final String USER_FIELD_MAPPING_FIRSTNAME = BASE + "mapping.user.firstname";
	private static final String USER_FIELD_MAPPING_LASTNAME = BASE + "mapping.user.lastname";
	private static final String USER_FIELD_MAPPING_PHOTO = BASE + "mapping.user.photo";

	private static final String GROUP_FILTER = BASE + "filter.group";
	private static final String GROUP_FILTER_BASE_DN = BASE + "base.dn";
	private static final String GROUP_FIELD_MAPPING_NAME = BASE + "mapping.group.name";
	private static final String GROUP_FIELD_MAPPING_MEMBER = BASE + "mapping.group.member";
	private static final String GROUP_FIELD_MAPPING_DESCRIPTION = BASE + "mapping.group.description";

	private static final String[] keys = {
		HOST,
		PORT,
		AUTH_USER_DN,
		AUTH_USER_PASS,
		USER_FILTER,
		USER_FILTER_BASE_DN,
		USER_FIELD_MAPPING_USERNAME,
		USER_FIELD_MAPPING_MAIL,
		USER_FIELD_MAPPING_FIRSTNAME,
		USER_FIELD_MAPPING_LASTNAME,
		GROUP_FILTER,
		GROUP_FILTER_BASE_DN,
		GROUP_FIELD_MAPPING_NAME,
		GROUP_FIELD_MAPPING_MEMBER,};

	private static final LdapSettings defSettings = new LdapSettings();

	static {
		defSettings.setPort(389);

		defSettings.setGroupFilter("(&(objectCategory=Group)(member=*))");
		defSettings.setGroupNameMapping("{cn}");
		defSettings.setGroupMemberMapping("{member}");
		defSettings.setGroupDescriptionMapping("{description}");

		defSettings.setUserFilter("(objectCategory=person)");
		defSettings.setUserFirstNameMapping("{givenName}");
		defSettings.setUserLastNameMapping("{sn}");
		defSettings.setUserMailMapping("{mail}");
		defSettings.setUserUserNameMapping("{sAMAccountName}");
		defSettings.setUserPhotoMapping("{thumbnailPhoto}");
	}

	public static LdapSettings getDefault() {
		return defSettings;
	}

	public static boolean isStaticField(final String field) {
		return field != null && !field.isEmpty() && !isLookupField(field);
	}

	public static boolean isLookupField(final String field) {
		if (field == null || field.trim().isEmpty()) {
			return false;
		}
		return PATTERN_LOOKUP_FIELD.matcher(field.trim()).matches();
	}

	public static String getLookupFieldName(final String field) {
		return field.substring(1, field.length() - 1);
	}

	@Override
	public String[] getKeys() {
		return keys;
	}

	public void setHost(final String host) {
		setValue(HOST, host);
	}

	public String getHost() {
		return getValue(HOST);
	}

	private void setPort(final int port) {
		setValue(PORT, String.valueOf(port));
	}

	public int getPort() {
		return Integer.parseInt(getValue(PORT));
	}

	public void setAuthUserDN(final String userDN) {
		setValue(AUTH_USER_DN, userDN);
	}

	public String getAuthUserDN() {
		return getValue(AUTH_USER_DN);
	}

	public void setAuthUserPassword(final String password) {
		setValue(AUTH_USER_PASS, password);
	}

	public String getAuthUserPassword() {
		return getValue(AUTH_USER_PASS);
	}

	private void setUserFilter(final String userFilter) {
		setValue(USER_FILTER, userFilter);
	}

	public String getUserFilter() {
		return getValue(USER_FILTER);
	}

	public void setUserFilterBaseDN(final String baseDN) {
		setValue(USER_FILTER_BASE_DN, baseDN);
	}

	public String getUserFilterBaseDN() {
		return getValue(USER_FILTER_BASE_DN);
	}

	private void setUserUserNameMapping(final String userUserNameMapping) {
		setValue(USER_FIELD_MAPPING_USERNAME, userUserNameMapping);
	}

	public String getUserUserNameMapping() {
		final String value = getValue(USER_FIELD_MAPPING_USERNAME);
		if (value == null || value.isEmpty()) {
			return "uid";
		}
		return value;
	}

	private void setUserMailMapping(final String userMailMapping) {
		setValue(USER_FIELD_MAPPING_MAIL, userMailMapping);
	}

	public String getUserMailMapping() {
		return getValue(USER_FIELD_MAPPING_MAIL);
	}

	private void setUserFirstNameMapping(final String userFirstNameMapping) {
		setValue(USER_FIELD_MAPPING_FIRSTNAME, userFirstNameMapping);
	}

	public String getUserFirstNameMapping() {
		return getValue(USER_FIELD_MAPPING_FIRSTNAME);
	}

	private void setUserLastNameMapping(final String userLastNameMapping) {
		setValue(USER_FIELD_MAPPING_LASTNAME, userLastNameMapping);
	}

	public String getUserLastNameMapping() {
		return getValue(USER_FIELD_MAPPING_LASTNAME);
	}

	private void setUserPhotoMapping(final String userPhotoMapping) {
		setValue(USER_FIELD_MAPPING_PHOTO, userPhotoMapping);
	}

	public String getUserPhotoMapping() {
		return getValue(USER_FIELD_MAPPING_PHOTO);
	}

	private void setGroupFilter(final String groupFilter) {
		setValue(GROUP_FILTER, groupFilter);
	}

	public String getGroupFilter() {
		return getValue(GROUP_FILTER);
	}
	
	public void setGroupFilterBaseDN(final String groupFilterBaseDN) {
		setValue(GROUP_FILTER_BASE_DN, groupFilterBaseDN);
	}

	public String getGroupFilterBaseDN() {
		return getValue(GROUP_FILTER_BASE_DN);
	}

	private void setGroupNameMapping(final String groupNameMapping) {
		setValue(GROUP_FIELD_MAPPING_NAME, groupNameMapping);
	}

	public String getGroupNameMapping() {
		return getValue(GROUP_FIELD_MAPPING_NAME);
	}

	private void setGroupMemberMapping(final String groupMemberMapping) {
		setValue(GROUP_FIELD_MAPPING_MEMBER, groupMemberMapping);
	}

	public String getGroupMemberMapping() {
		return getValue(GROUP_FIELD_MAPPING_MEMBER);
	}

	private void setGroupDescriptionMapping(final String groupDescriptionMapping) {
		setValue(GROUP_FIELD_MAPPING_DESCRIPTION, groupDescriptionMapping);
	}

	public String getGroupDescriptionMapping() {
		return getValue(GROUP_FIELD_MAPPING_DESCRIPTION);
	}

}
