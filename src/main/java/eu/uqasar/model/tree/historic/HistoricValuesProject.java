package eu.uqasar.model.tree.historic;

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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import eu.uqasar.model.tree.Project;
@NoArgsConstructor
@Setter
@Getter
@Entity
@Indexed
public class HistoricValuesProject extends AbstractHistoricValues {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3410829444860479588L;
	@ManyToOne
	@IndexedEmbedded
	private Project project;

	public HistoricValuesProject(Project project) {
		super();
		super.setDate(project.getLastUpdated());
		super.setValue(Float.valueOf(project.getValue().toString()));
		super.setQualityStatus(project.getQualityStatus());
		super.setLowerAcceptanceLimit(project.getThreshold().getLowerAcceptanceLimit());
		super.setUpperAcceptanceLimit(project.getThreshold().getUpperAcceptanceLimit());
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HistoricValuesProject [project=" + project + "]";
	}

}