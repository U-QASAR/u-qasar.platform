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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.tree.Project;

/**
 *
 *
 */
@NoArgsConstructor
@Setter
@Getter
@Entity
@XmlTransient
@Table(name = "analysis")
@Indexed
public class Analysis extends AbstractEntity {

	private static final long serialVersionUID = -3427732934809404752L;

	@NotNull
	@Size(min = 2 , max = 256)
	private String name;
	private String description;
	
	private Date date = new Date();

	@ElementCollection(targetClass=Dimensions.class)
	private Set<Dimensions> dimensions = new HashSet<>();

	@NotNull
	@ManyToOne
	@IndexedEmbedded
	private Project project;

	private Boolean deleted = Boolean.FALSE;

	public Analysis(String name) {
		super();
		this.name = name;
	}

	public Analysis(String name, Project project) {
		super();
		this.name = name;
		this.setProject(project);
	}

	/**
	 * @param dim
	 *            a dimension to the list
	 */
	public void addDimmension(Dimensions dim) {
		this.dimensions.add(dim);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Analysis [name=" + name + ", description=" + description
				+ ", date=" + date + ", dimensions=" + dimensions
				+ ", project=" + project + ", deleted=" + deleted + "]";
	}

}
