package eu.uqasar.web.provider;

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

	Collection<User> users;

	/**
	 * Constructs a new user provider that provides a data view to ALL users in
	 * the system except the currently logged in user, if <code>includeMe</code>
	 * is set fo false.
	 *
	 * @param includeMe <code>true</code> if the currently logged in user should
	 * be part of the data view of ALL users, <code>false</code> otherwise.
	 */
	public UserEntityProvider(boolean includeMe) {
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
