package eu.uqasar.web.provider.ldap;

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


import eu.uqasar.util.ldap.LdapEntity;
import eu.uqasar.util.ldap.LdapManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingException;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 *
 * @param <Entity>
 */
@Setter
@Getter
public abstract class LdapEntityProvider<Entity extends LdapEntity> implements IDataProvider<Entity> {

	protected int maximumNoOfEntities;
	protected LdapManager ldapManager;
	protected boolean initialized;
	protected List<Entity> entities = new ArrayList<>();

	LdapEntityProvider(LdapManager manager, int max) {
		this.ldapManager = manager;
		this.maximumNoOfEntities = max;
	}

	public void update(LdapManager manager) throws NamingException {
		this.ldapManager = manager;
		entities = getEntities(manager, maximumNoOfEntities);
		this.initialized = true;
	}

	public void reset() {
		this.initialized = false;
	}

	@Override
	public Iterator<Entity> iterator(long first, long count) {
		if (initialized) {
			return entities.subList((int) first, (int) Math.min(first + count, entities.size())).iterator();
		}
		return Collections.emptyIterator();
	}

	protected abstract List<Entity> getEntities(LdapManager manager, int max) throws NamingException;

	@Override
	public long size() {
		if (initialized) {
			return entities != null ? entities.size() : 0;
		}
		return 0;
	}

	@Override
	public IModel<Entity> model(Entity object) {
		return Model.of(object);
	}

	@Override
	public void detach() {

	}
}
