/*
 */
package eu.uqasar.service.monitoring;

import eu.uqasar.model.monitoring.UserActionLog;
import eu.uqasar.service.AbstractService;
import javax.ejb.Stateless;

/**
 *
 *
 */
@Stateless
public class UserActionLoggingService extends AbstractService<UserActionLog> {

	public UserActionLoggingService() {
		super(UserActionLog.class);
	}

}
