package eu.uqasar.model.tree;

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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.hibernate.search.annotations.Indexed;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;

@Entity
@XmlRootElement
@Indexed
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name","description","targetValue", "threshold", "useFormula",
		"value", "viewFormula", "weight", "children"})
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
public class QualityObjective extends BaseIndicator implements Comparable<QualityObjective>{

	private static final long serialVersionUID = 9113178213997749526L;
	private static final IconType ICON = new IconType("tasks");

	@XmlTransient
	@ManyToOne
	private QMQualityObjective qmObjective;
	
	@XmlTransient
	private Purpose qualityPurpose = Purpose.Process;
	
	@Lob
	private String viewFormula;
	
	// Set calculation mode:
	// 	false: percentage of Indicators which achieved the "good" threshold
	//		true: use formula
	private boolean useFormula = true;
	
	protected QualityObjective() {
	}

	@JsonCreator
	public QualityObjective(
			@JsonProperty("name") final String name,
			@JsonProperty("children") final List<QualityIndicator> children,
			@JsonProperty("description") final String description,
			@JsonProperty("targetValue") final float targetValue,
			@JsonProperty("threshold") final Threshold threshold,
			@JsonProperty("useFormula") final boolean useFormula,
			@JsonProperty("value") final float value,
			@JsonProperty("viewFormula") final String viewFormula,
			@JsonProperty("weight") final float weight) {
		
		this.setName(name);
		this.setDescription(description);
		this.setTargetValue(targetValue);
		this.setThreshold(threshold);
		this.setUseFormula(useFormula);
		this.setValue(value);
		this.setViewFormula(viewFormula);
		this.setWeight(weight);
		
		Iterator<QualityIndicator> it = children.iterator();
		List<TreeNode> nodes = new LinkedList<>();
		while (it.hasNext()){
			QualityIndicator qi = it.next();
			nodes.add(qi);
		}
		this.setChildren(nodes);
	}
	
	
	public QualityObjective(final String name, final Project parent) {
		super(parent);
		this.name = name;
	}
		
	public QualityObjective(final QMQualityObjective qmqo, final Project parent) {
		this (qmqo.getName(), parent);
		this.setDescription(qmqo.getDescription());
		this.setLifeCycleStage(qmqo.getLifeCycleStage());
		this.setQualityPurpose(qmqo.getIndicatorPurpose());
		this.getThreshold().setUpperAcceptanceLimit(qmqo.getUpperLimit());
		this.getThreshold().setLowerAcceptanceLimit(qmqo.getLowerLimit());
		this.setTargetValue(qmqo.getTargetValue());
		this.setWeight(qmqo.getWeight());
		this.setQmObjective(qmqo);
	}
	
	
	/**
	 * Constructor for creating a copy of entity QualityObjective.
	 * @param copy QualityObjective
	 */
	public QualityObjective(final QualityObjective copy) {
		super(copy.getParent());
		
		this.setName("Copy of "+ copy.getName());
		this.setDescription(copy.getDescription());
		this.setQmObjective(copy.getQmObjective());
		this.setIndicatorPurpose(copy.getIndicatorPurpose());
		this.setIndicatorType(copy.getIndicatorType());
		this.setLifeCycleStage(copy.getLifeCycleStage());
		this.getThreshold().setLowerAcceptanceLimit(copy.getThreshold().getLowerAcceptanceLimit());
		this.getThreshold().setUpperAcceptanceLimit(copy.getThreshold().getUpperAcceptanceLimit());
		this.setTargetValue(copy.getTargetValue());
		//TODO if metrics are not copied, the value cannot be resolved
//		this.setValue(copy.getValue());
		this.setHistoricValues(new ArrayList<HistoricValuesBaseIndicator>());
		this.setViewFormula(copy.getViewFormula());
		this.setWeight(copy.getWeight());
	}

	@JsonIgnore
	@Override
	public IconType getIconType() {
		return ICON;
	}

	public Purpose getQualityPurpose() {
		return qualityPurpose;
	}

	@JsonIgnore
	public void setQualityPurpose(Purpose qualityPurpose) {
		this.qualityPurpose = qualityPurpose;
	}

	@JsonIgnore
	@Override
	public Class<? extends TreeNode> getChildType() {
		return QualityIndicator.class;
	}

	/**
	 * @return the qmObjective
	 */
	@JsonIgnore
	public QMQualityObjective getQmObjective() {
		return qmObjective;
	}

	/**
	 * @param qmObjective the qmObjective to set
	 */
	@JsonIgnore
	public void setQmObjective(QMQualityObjective qmObjective) {
		this.qmObjective = qmObjective;
	}

	/**
	 * @return the viewFormula
	 */
	public String getViewFormula() {
		return viewFormula;
	}

	/**
	 * @param viewFormula the viewFormula to set
	 */
	@JsonIgnore
	public void setViewFormula(String viewFormula) {
		this.viewFormula = viewFormula;
	}

	/**
	 * @return the useFormula
	 */
	public boolean getUseFormula() {
		return useFormula;
	}

	/**
	 * @param useFormula the useFormula to set
	 */
	public void setUseFormula(boolean useFormula) {
		this.useFormula = useFormula;
	}

	@Override
	public int compareTo(QualityObjective o) {
		return getSimilarityValue().compareTo(o.getSimilarityValue());
	}
}
