package eu.uqasar.web.provider;

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

import org.apache.wicket.cdi.CdiContainer;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import eu.uqasar.model.AbstractEntity;

/**
 *
 * 
 */
public abstract class EntityProvider<T extends AbstractEntity> extends
		SortableDataProvider<T, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7053242016934984848L;

	public EntityProvider() {
		CdiContainer.get().getNonContextualManager().inject(this);
	}

	public IModel<T> model(T entity) {
		return Model.of(entity);
	}

}
