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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;

import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Type;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;
import eu.uqasar.util.UQasarUtil;

@Entity
@XmlTransient
public abstract class BaseIndicator extends TreeNode {

	private static final long serialVersionUID = -6441961332152113221L;

	@XmlTransient
    private Purpose indicatorPurpose = Purpose.Process;

	@XmlTransient
    private Type indicatorType = Type.Automatic;
	
	@XmlTransient
    private Date lastUpdated = DateTime.now().toDate();
	
	@XmlTransient
	private float value;
	
	private float targetValue;
	
    // Computed value for similarity when compared to another entities; 
	// do not persist to the DB as this is computed on-the-fly
	@XmlTransient
	@Transient
    private Integer similarityValue = 0; 
	
	@XmlTransient
	@OneToMany(cascade = CascadeType.ALL)
	private List<HistoricValuesBaseIndicator> historicValues = new ArrayList<>();
	
	@XmlTransient
	@Enumerated(EnumType.STRING)
	private MetricSource metricSource;
	
	@OneToOne(cascade=CascadeType.ALL)
	private Threshold threshold = new Threshold();
	
	private float weight = 1;
	
	protected BaseIndicator() {
	}

	public BaseIndicator(final TreeNode parent) {
		super(parent);
	}

	public Purpose getIndicatorPurpose() {
		if (indicatorPurpose == null) 
			indicatorPurpose = Purpose.Process;
		return indicatorPurpose;
	}

	@JsonIgnore
	public void setIndicatorPurpose(Purpose indicatorPurpose) {
		this.indicatorPurpose = indicatorPurpose;
	}

	public Type getIndicatorType() {
		if (indicatorType == null)
			indicatorType = Type.Automatic;
		return indicatorType;
	}

	@JsonIgnore
    void setIndicatorType(Type indicatorType) {
		this.indicatorType = indicatorType;
	}

	public MetricSource getMetricSource() {
		return metricSource;
	}

	@JsonIgnore
	public void setMetricSource(MetricSource metricSource) {
		this.metricSource = metricSource;
	}
	
	/**
	 * @return
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated
	 */
	@JsonIgnore
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	/**
	 * 
	 * @return the value
	 */
	public float getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	@XmlElement(name="value")
	@JsonIgnore
	public void setValue(float value) {
		this.value = value;
		
		// Update the quality status
		updateQualityStatus();
	}

	/**
	 * update the quality status of the project
	 */
	public void updateQualityStatus(){
		this.setQualityStatus(QualityStatus.getQualityStatusForValue((double)value, threshold));
	}


	/**
	 * @return the targetValue
	 */
	public float getTargetValue() {
		return targetValue;
	}

	/**
	 * @param targetValue the targetValue to set
	 */
	@JsonIgnore
	public void setTargetValue(float targetValue) {
		this.targetValue = targetValue;
	}

	/**
	 * @return a List with all the historic values
	 */
	public List<HistoricValuesBaseIndicator> getHistoricValues() {
		Collections.sort(this.historicValues, Collections.reverseOrder());
		return historicValues;
	}

	/**
	 * @param historicValues
	 */
	@JsonIgnore
    void setHistoricValues(List<HistoricValuesBaseIndicator> historicValues) {
		this.historicValues = historicValues;
	}

	/**
	 * @param historicValues
	 */
    private void addHistoricValues(final HistoricValuesBaseIndicator historicValues){
		this.historicValues.add(historicValues);
	}
	
	/**
	 * @param date
	 * @return 
	 */
	public float getHistoricValue(final Date date) {
		for (final HistoricValuesBaseIndicator h : historicValues) {
			if (UQasarUtil.isDateEqual(h.getDate(), date)) {
				return h.getValue();
			} 
		}
		return 0;
	}

	/**
	 * @param date
	 * @return 
	 */
	public HistoricValuesBaseIndicator getHistoricObject(final Date date) {
		for (final HistoricValuesBaseIndicator h : historicValues) {
			if (UQasarUtil.isDateEqual(h.getDate(), date)) {
				return h;
			} 
		}
		return null;
	}
	
	/**
     */
	private void addHistoricValues(final BaseIndicator baseIndicator) {
		final HistoricValuesBaseIndicator historicValues = new HistoricValuesBaseIndicator(
		baseIndicator);
		
		addHistoricValues(historicValues);
	}
	
	/**
	 * 
	 */
	public void addHistoricValue() {
		addHistoricValues(this);
	}
	
	/**
	 * @return the number of registered historical values
	 */
	public Integer getNoOfHistoricValues() {
		return this.historicValues.size();
	}
	
	/**
	 * @return the threshold
	 */
	public Threshold getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(Threshold threshold) {
		this.threshold = threshold;
	}

	/**
	 * @return the weight
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	@JsonIgnore
    void setWeight(float weight) {
		this.weight = weight;
	}

	/**
	 * @return the similarityValue
	 */
	public Integer getSimilarityValue() {
		return similarityValue;
	}

	/**
	 * @param similarityValue the similarityValue to set
	 */
	@JsonIgnore
	public void setSimilarityValue(Integer similarityValue) {
		this.similarityValue = similarityValue;
	}
	
	/**
	 * Increment the similarityValue
	 */
	public void incrementSimilarityValue(){
		this.similarityValue++;
	}	
}
