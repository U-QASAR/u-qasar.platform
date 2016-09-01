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


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
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
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Type;

@Entity
@XmlRootElement
@Indexed
@XmlType(propOrder = {"name","description","indicatorPurpose", "indicatorType",
		"lifeCycleStage", "targetValue", "threshold", "useFormula", "value", "viewFormula", "weight", "children"})
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
public class QualityIndicator extends BaseIndicator {

	private static final long serialVersionUID = -3419215509636045908L;
	private static final IconType ICON = new IconType("dashboard");
	
	// The formula in the format that meant for viewing. In order to compute
	// a value the formula is parsed and eval'd
	@Lob
	private String viewFormula; 
	
	// Set calculation mode:
	// 	false: percentage of Indicators which achieved the "good" threshold
	//		true: use formula
	private boolean useFormula = true;
	
	@XmlTransient
	@ManyToOne
	private QMQualityIndicator qmIndicator;

	@JsonCreator
	public QualityIndicator(
			@JsonProperty("name") final String name,
			@JsonProperty("children") final List<Metric> children,
			@JsonProperty("description") final String description,
			@JsonProperty("indicatorPurpose") final Purpose indicatorPurpose,
			@JsonProperty("indicatorType") final Type indicatorType,
			@JsonProperty("lifeCycleStage") final LifeCycleStage lifeCycleStage,
			@JsonProperty("targetValue") final float targetValue,
			@JsonProperty("threshold") final Threshold threshold,
			@JsonProperty("useFormula") final boolean useFormula,
			@JsonProperty("value") final float value,
			@JsonProperty("viewFormula") final String viewFormula,
			@JsonProperty("weight") final float weight) {
		
		this.setName(name);
		this.setDescription(description);
		this.setIndicatorPurpose(indicatorPurpose);
		this.setIndicatorType(indicatorType);
		this.setLifeCycleStage(lifeCycleStage);
		this.setTargetValue(targetValue);
		this.setThreshold(threshold);
		this.setValue(value);
		this.setViewFormula(viewFormula);
		this.setWeight(weight);
		
		Iterator<Metric> it = children.iterator();
		List<TreeNode> nodes = new LinkedList<>();
		while (it.hasNext()){
			Metric met = it.next();
			nodes.add(met);
		}
		this.setChildren(nodes);
	}
	
	protected QualityIndicator() {
	}
	
	public QualityIndicator(final String name, final QualityObjective parent) {
		super(parent);
		this.name = name;
	}

	public QualityIndicator(final QMQualityIndicator qmqi, final QualityObjective parent) {
		this (qmqi.getName(), parent);
		this.setIndicatorPurpose(qmqi.getIndicatorPurpose());
		this.setLifeCycleStage(qmqi.getLifeCycleStage());
		this.setQmIndicator(qmqi);
		this.getThreshold().setLowerAcceptanceLimit(qmqi.getLowerLimit());
		this.getThreshold().setUpperAcceptanceLimit(qmqi.getUpperLimit());
		this.setTargetValue(qmqi.getTargetValue());
		this.setWeight(qmqi.getWeight());
	}

	/**
	 * Constructor for creating a copy of entity QualityIndicator.
	 * @param copy QualityIndicator
	 */
	public QualityIndicator(final QualityIndicator copy) {
		super(copy.getParent());
		
		this.setName("Copy of "+ copy.getName());
		this.setDescription(copy.getDescription());
		this.setQmIndicator(copy.getQmIndicator());
		this.setIndicatorPurpose(copy.getIndicatorPurpose());
		this.setIndicatorType(copy.getIndicatorType());
		this.setLifeCycleStage(copy.getLifeCycleStage());
		this.getThreshold().setLowerAcceptanceLimit(copy.getThreshold().getLowerAcceptanceLimit());
		this.getThreshold().setUpperAcceptanceLimit(copy.getThreshold().getUpperAcceptanceLimit());
		this.setTargetValue(copy.getTargetValue());
		//TODO if metrics are not copied, the value cannot be resolved
//		this.setValue(copy.getValue());
		this.setViewFormula(copy.getViewFormula());
		this.setWeight(copy.getWeight());
	}

	
	@JsonIgnore
	@Override
	public IconType getIconType() {
		return ICON;
	}

	@JsonIgnore
	@Override
	public QualityObjective getQualityObjective() {
		return (QualityObjective) getParent();
	}

	@JsonIgnore
	@Override
	public Class<Metric> getChildType() {
		return Metric.class;
	}

	/**
	 * @return the qmIndicator
	 */
	@JsonIgnore
	public QMQualityIndicator getQmIndicator() {
		return qmIndicator;
	}

	/**
	 * @param qmIndicator the qmIndicator to set
	 */
	@JsonIgnore
	public void setQmIndicator(QMQualityIndicator qmIndicator) {
		this.qmIndicator = qmIndicator;
	}

	/**
	 * @return the formula to be viewed
	 */
	public String getViewFormula() {
		return viewFormula;
	}

	/**
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
	
	@XmlElement(name="lifeCycleStage")
	public LifeCycleStage getLifeCycleStage() {
		return super.getLifeCycleStage();
	}

	@XmlElement(name="indicatorPurpose")
	public Purpose getIndicatorPurpose() {
		return super.getIndicatorPurpose();
	}
	
	@XmlElement(name="indicatorType")
	public Type getIndicatorType() {
		return super.getIndicatorType();
	}

}
