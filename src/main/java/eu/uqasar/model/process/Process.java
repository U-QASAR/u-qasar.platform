package eu.uqasar.model.process;

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
import java.util.HashMap;
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
@Table(name = "process")
@Indexed
public class Process extends AbstractEntity implements Namable {

	private static final long serialVersionUID = -4632410408985834678L;

	public static IconType ICON = new IconType("sitemap");

	@NotNull
	@Size(min = 2, max = 1024)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String name;
	private HashMap<Integer, String> stages;
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String description;
	@NotNull
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date startDate;
	@NotNull
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date endDate;

	@OneToMany
	@JoinColumn(name = "project_id", nullable = true)
	private Set<Project> projects = new HashSet<>();

	/**
	 *
	 * @param name
	 */
	public Process(final String name) {
		this.setName(name);
	}

	/**
	 *
	 * @param maxLength
	 * @return the abbreviated name
	 */
    private String getAbbreviatedName(int maxLength) {
		return StringUtils.abbreviate(getName(), maxLength);
	}

	/**
	 *
	 * @return the abbreviated name
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
