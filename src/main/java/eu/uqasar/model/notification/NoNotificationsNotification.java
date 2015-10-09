package eu.uqasar.model.notification;

import java.util.Date;


import eu.uqasar.model.user.User;

public final class NoNotificationsNotification implements INotification {

	private static final long serialVersionUID = 8981163061645932845L;
	private Date creationDate = new Date();
	private NotificationType notificationType = NotificationType.NO_NOTIFICATIONS;
	private User user;
	
	@Override
	public User getUser() {
		return user;
	}
	
	
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public Date getReadDate() {
		return null;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @param notificationType the notificationType to set
	 */
	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	@Override
	public Date getCreationDate() {
		return this.creationDate;
	}

	@Override
	public NotificationType getNotificationType() {
		return this.notificationType;
	}

}
