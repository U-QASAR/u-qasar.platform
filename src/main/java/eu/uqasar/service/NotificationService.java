package eu.uqasar.service;

import java.util.Date;
import javax.ejb.Stateless;
import eu.uqasar.model.notification.Notification;

/**
 *
 *
 */
@Stateless
public class NotificationService extends AbstractService<Notification> {

	public NotificationService() {
		super(Notification.class);
	}

	public <Type extends Notification> Type  markAsRead(Type notification) {
		if(notification.getReadDate() != null) return notification;
		notification.setReadDate(new Date());
		notification = (Type) update(notification);
		return notification;
	}
	
}
