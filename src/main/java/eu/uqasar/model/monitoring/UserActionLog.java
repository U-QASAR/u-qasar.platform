/*
 */
package eu.uqasar.model.monitoring;

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


import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.Namable;
import eu.uqasar.model.user.User;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.pages.BasePage;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 *
 *
 */
@Setter
@Getter
@Entity
public class UserActionLog extends AbstractEntity {

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ActionTimestamp")
	private Date timestamp;
	private String url, pageClassName, pageTitle;
	private Long userId;
	private String userMail;
	@Column(name = "ActionPerformed")
	@Enumerated(EnumType.STRING)
	private UserAction action;
	private String actionableItemClass;
	private String actionableItemName;
	private Long actionableItemId;
	@ManyToOne
	private WebEnvironment environment;

	public UserActionLog() {
		this(UserAction.Unknown, null, null);
	}

	public UserActionLog(UserAction action) {
		this(action, null, null);
	}

	public <P extends WebPage> UserActionLog(P page) {
		this(UserAction.Unknown, page, null);
	}

	public <P extends WebPage> UserActionLog(UserAction action, P page) {
		this(action, page, null);
	}

	public <E extends AbstractEntity> UserActionLog(UserAction action, E entity) {
		this(action, null, entity);
	}

	public <P extends WebPage, E extends AbstractEntity> UserActionLog(UserAction action, P page, E entity) {
		timestamp = new Date();
		this.action = action;
		fillUrl();
		fillUserDetails();
		fillEnvironment();
		fillActionableItem(entity);
		fillPage(page);
	}

	public <P extends WebPage> void setPageDetails(P page) {
		fillPage(page);
	}

	public <E extends AbstractEntity> void setActionableItemDetails(E entity) {
		fillActionableItem(entity);
	}

	private void setUserDetails(User user) {
		if (user != null) {
			this.userId = user.getId();
			this.userMail = user.getMail();
		}
	}

	private void fillEnvironment() {
		if (UQSession.exists() && UQSession.get() != null) {
			this.environment = UQSession.get().initializeEnvironment();
		}
	}

	private <P extends WebPage> void fillPage(P page) {
		if (page != null) {
			pageClassName = page.getClass().getCanonicalName();
			if (BasePage.class.isAssignableFrom(page.getClass())) {
				pageTitle = ((BasePage) page).getPageTitle();
			}
		}
	}

	private <E extends AbstractEntity> void fillActionableItem(E entity) {
		if (entity != null) {
			actionableItemId = entity.getId();
			actionableItemClass = entity.getClass().getCanonicalName();
			if (Namable.class.isAssignableFrom(entity.getClass())) {
				actionableItemName = ((Namable) entity).getUniqueName();
			} else {
				actionableItemName = String.valueOf(entity);
			}
		}
	}

	private void fillUrl() {
		RequestCycle cycle = RequestCycle.get();
		if (cycle != null && cycle.getRequest() != null) {
			Url clientUrl = cycle.getRequest().getClientUrl();
			url = RequestCycle.get().getUrlRenderer().renderFullUrl(clientUrl);
		}
	}

	private void fillUserDetails() {
		if (UQSession.exists() && UQSession.get().isAuthenticated()) {
			User user = UQSession.get().getLoggedInUser();
			setUserDetails(user);
		}
	}
}
