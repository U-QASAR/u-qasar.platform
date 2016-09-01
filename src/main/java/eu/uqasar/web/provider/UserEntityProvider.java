package eu.uqasar.web.provider;

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


import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.UQasar;
import java.util.Collection;
import java.util.Iterator;
import javax.inject.Inject;

/**
 *
 *
 */
public class UserEntityProvider extends EntityProvider<User> {

	@Inject
	UserService userService;

	private final Collection<User> users;

	/**
	 * Constructs a new user provider that provides a data view to ALL users in
	 * the system except the currently logged in user, if <code>includeMe</code>
	 * is set fo false.
	 *
	 * @param includeMe <code>true</code> if the currently logged in user should
	 * be part of the data view of ALL users, <code>false</code> otherwise.
	 */
    private UserEntityProvider(boolean includeMe) {
		if (!includeMe) {
			this.users = userService.getAllExceptOne(UQasar.getSession().getLoggedInUser());
		} else {
			this.users = userService.getAll();
		}
	}

	/**
	 * Constructs a new UserEntityProvider that by default provides a data view
	 * to ALL users registered in the system, including the one currently logged
	 * in.
	 */
	public UserEntityProvider() {
		this(true);
	}

	/**
	 * Constructs a new UserEntityProvider that provides a data view to the
	 * users given.
	 *
	 * @param users the collection of users this provider will provides a data
	 * view to.
	 */
	public UserEntityProvider(Collection<User> users) {
		this.users = users;
	}

	@Override
	public Iterator<? extends User> iterator(long first, long count) {
		return users.iterator();
	}

	@Override
	public long size() {
		return users.size();
	}

}
