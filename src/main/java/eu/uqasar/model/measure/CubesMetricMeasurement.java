package eu.uqasar.model.measure;

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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;

import com.google.gson.annotations.SerializedName;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;

@Entity
@XmlRootElement
@Table(name = "cubesmetricmeasurement")
@Indexed
public class CubesMetricMeasurement extends AbstractEntity {

	private static final long serialVersionUID = -906748642415985975L;

	private String cubesMetric;		// Cubes name of the metric
	private String value;			// Retrieved value of the metric
	private String self;			// URL of the metric used to retrieve the value
	
	@SerializedName("key")
	private String cubeName;  		// Cube name
	private Date timeStamp;			// timestamp
	@Lob
	private String jsonContent; 	// Content in JSON format, pointed by a URL (self)
	@ManyToOne
	private Project project;		// 
	@ManyToOne(fetch = FetchType.LAZY)
	private AdapterSettings adapter; // To which adapter the measurement belongs
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public String getSelf() {
		return self;
	}
	
	/**
	 * 
	 * @param self
	 */
	public void setSelf(String self) {
		this.self = self;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCubeName() {
		return cubeName;
	}
	
	/**
	 * 
	 * @param key
	 */
	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}
	
	/**
	 * @return the metric
	 */
	public String getCubesMetric() {
		return cubesMetric;
	}

	/**
	 * @param metric the metric to set
	 */
	public void setCubesMetric(String cubesMetric) {
		this.cubesMetric = cubesMetric;
	}

	/**
	 * @return the timeStamp
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the jsonContent
	 */
	public String getJsonContent() {
		return jsonContent;
	}

	/**
	 * @param jsonContent the jsonContent to set
	 */
	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return the adapter
	 */
	public AdapterSettings getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(AdapterSettings adapter) {
		this.adapter = adapter;
	}

	@Override
	public String toString() {
		return "CubesMetricMeasurement [self=" + self 
				+", cubeName=" + cubeName  
				+", cubesMetric=" + cubesMetric 
				+", timeStamp=" + timeStamp
				+", jsonContent=" + jsonContent
				+"]";
	}
}
