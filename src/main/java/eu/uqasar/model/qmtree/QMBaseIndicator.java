package eu.uqasar.model.qmtree;

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


import static javax.persistence.CascadeType.MERGE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.lifecycle.RupStage;
import eu.uqasar.model.meta.QModelTagData;
import eu.uqasar.model.quality.indicator.Paradigm;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Version;
import eu.uqasar.model.role.Role;
@NoArgsConstructor
@Setter
@Getter
@Entity
@XmlTransient
public abstract class QMBaseIndicator extends QMTreeNode {

	
	private static final long serialVersionUID = -6441961332152113221L;

	@Enumerated(EnumType.STRING)
	private Purpose indicatorPurpose = Purpose.Process;

	@Enumerated(EnumType.STRING)
	private Paradigm paradigm = Paradigm.Waterfall;

	@XmlTransient	
	@Enumerated(EnumType.STRING)
	private LifeCycleStage lifeCycleStage = LifeCycleStage.Requirements;

	@XmlTransient
	@Enumerated(EnumType.STRING)
	private RupStage rupStage = RupStage.Inception;

	@Enumerated(EnumType.STRING)
	private Version version = Version.Prototype;
	
	@XmlTransient
	@Column
    @ElementCollection(targetClass=Role.class)
	private List<Role> targetAudience;

	private Double lowerLimit = (double) 0;
	
	private Double upperLimit = (double) 0;
	
	//Sprint 2. Added QProject attributes
	@XmlElement
	private float weight = 1;
	
	//Sprint 2. Added QProject attributes
	@XmlElement
	private float targetValue = 0;
	
	//Sprint 3. Tags for Quality Model entities
	@XmlTransient
	@ManyToMany(cascade = MERGE)
	private Set<QModelTagData> qModelTagData = new HashSet<>();
	
	QMBaseIndicator(final QMTreeNode parent) {
		super(parent);
	}

	/**
	 * getQModelTagData get the Quality Model tags.
	 * @return collection of tags
	 */
	@JsonIgnore
	public Set<QModelTagData> getQModelTagData() {
		return qModelTagData;
	}

	/**
	 * setQModelTagData set the Quality Model tags.
	 * @param qmtd collection of tags
	 */
	@JsonIgnore
	public void setQModelTagData(Set<QModelTagData> qmtd) {
		qModelTagData = qmtd;
	}	
	
}