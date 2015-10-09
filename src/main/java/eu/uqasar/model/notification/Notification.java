package eu.uqasar.model.notification;

import eu.uqasar.model.AbstractEntity;
import java.io.Serializable;
import java.util.Date;


import eu.uqasar.model.user.User;

public abstract class Notification extends AbstractEntity implements INotification, Serializable {

	private static final long serialVersionUID = 3334130299513791349L;

	protected Date creationDate = new Date();

	protected NotificationType notificationType = NotificationType.UNDEFINED;

	protected User user;

	protected Date readDate;

	/**
	 * @return the creationDate
	 */
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the notificationType
	 */
	@Override
	public NotificationType getNotificationType() {
		return notificationType;
	}

	/**
	 * @param notificationType
	 *            the notificationType to set
	 */
	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	/**
	 * @return the user
	 */
	@Override
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the readDate
	 */
	@Override
	public Date getReadDate() {
		return readDate;
	}

	/**
	 * @param readDate
	 *            the readDate to set
	 */
	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}

}
