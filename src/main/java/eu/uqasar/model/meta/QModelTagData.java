package eu.uqasar.model.meta;

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



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class QModelTagData extends MetaData {

	private static final long serialVersionUID = 1L;

	private Long tagId;

	private String className;

	public QModelTagData(String name) {
		super(name);
	}

	public QModelTagData(MetaData ent) {
		super(ent.getName());
		className = ent.getClass().getName();
		tagId = ent.getId();
	}

	public QModelTagData(String name, String cName, Long tId) {
		super(name);
		className = cName;
		tagId = tId;
	}

}
