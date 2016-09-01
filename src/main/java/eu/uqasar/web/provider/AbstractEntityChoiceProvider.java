
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


import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;
import eu.uqasar.model.AbstractEntity;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 *
 */
@Setter(AccessLevel.PROTECTED)
public abstract class AbstractEntityChoiceProvider<Type extends AbstractEntity>
		extends TextChoiceProvider<Type> {

	private List<Type> allEntities = new ArrayList<>();
	protected AbstractEntityChoiceProvider(){
		
	}
	
	private AbstractEntityChoiceProvider(List<Type> allEntities,
                                         Type... entitiesToExclude) {
		this.allEntities = allEntities;

		if ((entitiesToExclude != null) && (entitiesToExclude.length > 0)) {
			// remove the provided entities from the suggestion list
			this.allEntities.removeAll(Arrays.asList(entitiesToExclude));
		}
	}

	protected AbstractEntityChoiceProvider(List<Type> entities) {
		this(entities, (Type[]) null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaynberg.wicket.select2.TextChoiceProvider#getId(java.lang.Object)
	 */
	@Override
	protected Object getId(Type entity) {
		return entity.getId();
	}

	@Override
	public void query(String term, int page, Response<Type> response) {
		response.addAll(queryMatches(term, page, 10));
		response.setHasMore(response.size() == 10);
	}

	@Override
	public Collection<Type> toChoices(Collection<String> ids) {
		List<Type> entities = new ArrayList<>();
		for (String id : ids) {
			entities.add(findEntityById(Long.valueOf(id)));
		}
		return entities;
	}

	/**
	 * Queries {@code pageSize} the list of entities, starting with
	 * {@code page * pageSize} offset. Entities should be matched on a String
	 * attribute containing {@code term}
	 * 
	 * @param term
	 *            search term
	 * @param page
	 *            starting page
	 * @param pageSize
	 *            items per page
	 * @return list of matches
	 */
    private List<Type> queryMatches(String term, final int page,
                                    final int pageSize) {
		List<Type> result = new ArrayList<>();
		final int offset = page * pageSize;
		int matched = 0;
		for (Type entity : allEntities) {
			if (result.size() == pageSize) {
				break;
			}
			if (queryMatches(entity, term)) {
				matched++;
				if (matched > offset) {
					result.add(entity);
				}
			}
		}
		return result;
	}

	/**
	 * Determines whether the given entity is matched by the given term.
	 * 
	 * @param entity
	 * @param term
	 * @return <code>true</code> if the given entity can be found by the given
	 *         termn, <code>false</code> otherwise.
	 */
	protected abstract boolean queryMatches(Type entity, String term);

	/**
	 * 
	 * @param id
	 * @return
	 */
	private Type findEntityById(final Long id) {
		for (Type type : allEntities) {
			if (type.getId().equals(id)) {
				return type;
			}
		}
		return null;
	}

}
