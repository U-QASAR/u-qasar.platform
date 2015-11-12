/**
 * 
 */
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

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.tree.Project;

/**
 *
 *
 */

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

	
	public Analysis() {
	}

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

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the dimension
	 */
	public Set<Dimensions> getDimensions() {
		return dimensions;
	}

	/**
	 * @param dimension
	 *            the dimension to set
	 */
	public void setDimensions(Set<Dimensions> dimension) {
		this.dimensions = dimension;
	}

	/**
	 * @param Adds
	 *            a dimension to the list
	 */
	public void addDimmension(Dimensions dim) {
		this.dimensions.add(dim);
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return the deleted
	 */
	public Boolean getDeleted() {
		return deleted;
	}

	/**
	 * @param deleted
	 *            the deleted to set
	 */
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
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
