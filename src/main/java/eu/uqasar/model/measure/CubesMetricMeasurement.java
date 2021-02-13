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

import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.annotations.Indexed;

import com.google.gson.annotations.SerializedName;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;
@Setter
@Getter
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
