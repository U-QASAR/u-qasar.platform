package eu.uqasar.model.analytic;

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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.search.annotations.IndexedEmbedded;

import eu.uqasar.model.AbstractEntity;

/**
 *
 *
 */

@Entity
public class Dimension extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5412778455201051728L;
	private String name;
	
	@ManyToOne
	@IndexedEmbedded
	private Analysis analysis;

	public Dimension() {

	}

	public Dimension(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
