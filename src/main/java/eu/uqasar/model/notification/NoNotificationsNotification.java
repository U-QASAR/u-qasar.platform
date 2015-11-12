package eu.uqasar.model.notification;

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
