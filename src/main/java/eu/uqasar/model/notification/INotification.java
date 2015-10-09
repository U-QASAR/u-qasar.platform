package eu.uqasar.model.notification;

import java.io.Serializable;
import java.util.Date;


import eu.uqasar.model.user.User;

public interface INotification extends Serializable {

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate();

	/**
	 * @return the notificationType
	 */
	public NotificationType getNotificationType();

	/**
	 * @return the user
	 */
	public User getUser();

	/**
	 * @return the readDate
	 */
	public Date getReadDate();

}
