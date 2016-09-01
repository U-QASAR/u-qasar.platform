package eu.uqasar.model.product;

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


import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.Namable;
import eu.uqasar.model.tree.Project;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;
@NoArgsConstructor
@Setter
@Getter
@Entity
@XmlRootElement
@Table(name = "product")
@Indexed
public class Product extends AbstractEntity implements Namable {

	private static final long serialVersionUID = 1724244299796140695L;
	public static IconType ICON = new IconType("sitemap");

	@NotNull
	@Size(min = 2, max = 1024)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String name;
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String description;
	@NotNull
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String version;

	@Temporal(javax.persistence.TemporalType.DATE)
	@NotNull
	private Date releaseDate;

	@OneToMany
	@JoinColumn(name = "project_id", nullable = true)
	private Set<Project> projects = new HashSet<>();

	public Product(final String name) {
		this.setName(name);
	}

	/**
	 *
	 * @param maxLength
	 * @return
	 */
    private String getAbbreviatedName(int maxLength) {
		return StringUtils.abbreviate(getName(), maxLength);
	}

	/**
	 *
	 * @return
	 */
	public String getAbbreviatedName() {
		return getAbbreviatedName(48);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getUniqueName() {
		return name;
	}
}
