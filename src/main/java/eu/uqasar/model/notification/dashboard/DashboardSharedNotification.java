package eu.uqasar.model.notification.dashboard;

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

import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.notification.Notification;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class DashboardSharedNotification extends Notification {

	@Override
	public String toString() {
		return "DashboardSharedNotification [dashboard=" + dashboard
				+ ", getCreationDate()=" + getCreationDate()
				+ ", getNotificationType()=" + getNotificationType()
				+ ", getUser()=" + getUser() + ", getReadDate()="
				+ getReadDate() + ", getId()=" + getId() + "]";
	}

	private static final long serialVersionUID = 6838387940459194342L;

	@ManyToOne
	private DbDashboard dashboard;
}
