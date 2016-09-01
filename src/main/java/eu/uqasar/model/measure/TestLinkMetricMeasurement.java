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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.annotations.Indexed;

import com.google.gson.annotations.SerializedName;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;
@Setter
@Getter
@NoArgsConstructor
@Entity
@XmlRootElement
@Table(name = "testLinkmetricmeasurement")
@Indexed
public class TestLinkMetricMeasurement extends AbstractEntity {

	private static final long serialVersionUID = 13394761234433421L;

	private String testLinkMetric; // TestLinkQube metric name
	private String name;
	private String value;
	@SerializedName("key")
	private String testLinkKey;
	private Date timeStamp; // timestamp for the snapshot
	@ManyToOne
	private Project project;
	@ManyToOne(fetch = FetchType.LAZY)
	private AdapterSettings adapter; // To which adapter the measurement belongs

	@Override
	public String toString() {
		return "TestLinkMetricMeasurement [name=" +name + ", value=" +value
				+ ", testLinkKey=" +testLinkKey +", timeStamp=" +timeStamp
				+", testLinkMetric=" +testLinkMetric 
				+", project=" +project
				+"]";
	}
}
