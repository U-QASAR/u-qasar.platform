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
