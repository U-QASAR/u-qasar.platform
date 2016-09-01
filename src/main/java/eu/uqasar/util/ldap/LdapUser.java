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
import eu.uqasar.model.user.User;
import java.util.Comparator;
import javax.naming.directory.Attributes;

import lombok.Getter;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;

@Getter
public class LdapUser extends LdapEntity {

	private String userName, firstName, lastName, mail;
	private byte[] picture;

	public static boolean hasValidUserNameValue(Attributes attr, LdapSettings settings) {
		return getMappedValue(settings.getUserUserNameMapping(), attr) != null;
	}

	public static boolean hasValidMailValue(Attributes attr, LdapSettings settings) {
		return getMappedValue(settings.getUserMailMapping(), attr) != null;
	}

	public LdapUser(Attributes attr, LdapSettings settings) {
		super(attr, settings);

	}

	public String getFullName() {
		if (getFirstName() == null && getLastName() == null) {
			return null;
		}
		if (getFirstName() != null && getLastName() != null) {
			return getFirstName() + " " + getLastName();
		}
		if (getFirstName() != null) {
			return getFirstName();
		}
		if (getLastName() != null) {
			return getLastName();
		}
		return null;
	}

	public boolean hasProfilePicture() {
		return getProfilePicture() != null && getProfilePicture().length > 0;
	}
	
	public byte[] getProfilePicture() {
		if (picture == null) {
			byte[] bytes = getByteArrayValue(settings.getUserPhotoMapping());
			if (bytes == null) {
				picture = new byte[0];
			} else {
				picture = bytes;
			}
		}
		return picture;
	}

	public IResource getProfilePictureImage() {
		final byte[] pictureData = getProfilePicture();
		if (pictureData.length > 0) {
			DynamicImageResource dir = new DynamicImageResource() {
				@Override
				protected byte[] getImageData(IResource.Attributes attributes) {
					return pictureData;
				}
			};
			dir.setFormat("image/png");
			return dir;
		} else {
			return User.getAnonymousPicture();
		}
	}

	@Override
	public String toString() {
		return String.format("%s %s (%s) - %s", getFirstName(), getLastName(), getUserName(), getMail());
	}
    
    public User toUser() {
        return toUser(this);
    }

    private static User toUser(LdapUser ldapUser) {
        User user = new User();
        user.setFirstName(ldapUser.getFirstName());
        user.setLastName(ldapUser.getLastName());
        user.setMail(ldapUser.getMail());
        user.setUserName(ldapUser.getUserName());
        // TODO what todo with the users profile picture?!
        return user;
    }
    
	public static class LdapUserComparator implements Comparator<LdapUser> {

		@Override
		public int compare(LdapUser o1, LdapUser o2) {
			if (o1.equals(o2)) {
				return 0;
			} else if (o1 == null) {
				return -1;
			} else if (o2 == null) {
				return 1;
			} else {
				int userName = LdapEntity.compare(o1.getUserName(), o2.getUserName(), false);
				if (userName != 0) {
					return userName;
				}
				int lastName = LdapEntity.compare(o1.getLastName(), o2.getLastName(), false);
				if (lastName != 0) {
					return lastName;
				}
				int firstName = LdapEntity.compare(o1.getFirstName(), o2.getFirstName(), false);
				if (firstName != 0) {
					return firstName;
				}
				int mail = LdapEntity.compare(o1.getMail(), o2.getMail(), false);
				if (mail != 0) {
					return mail;
				}
                return LdapEntity.compare(o1.getDN(), o2.getDN(), false);
			}
		}

	}

}
