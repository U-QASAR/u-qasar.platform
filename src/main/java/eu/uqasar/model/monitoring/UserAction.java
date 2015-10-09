/*
 */
package eu.uqasar.model.monitoring;

import eu.uqasar.model.AbstractEntity;
import org.apache.wicket.markup.html.WebPage;

/**
 *
 *
 */
public enum UserAction {

	View,
	Create,
	Delete,
	Edit,
	Unknown;

	public UserActionLog generate() {
		return new UserActionLog(this);
	}

	public <P extends WebPage> UserActionLog generate(P page) {
		return new UserActionLog(this, page);
	}

	public <E extends AbstractEntity> UserActionLog generate(E entity) {
		return new UserActionLog(this, entity);
	}

	public <P extends WebPage, E extends AbstractEntity> UserActionLog generate(P page, E entity) {
		return new UserActionLog(this, page, entity);
	}

}
