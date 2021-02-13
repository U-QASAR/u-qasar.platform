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

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import eu.uqasar.model.AbstractEntity;
@NoArgsConstructor
@Setter
@Getter
@XmlType(propOrder = { "lowerAcceptanceLimit", "upperAcceptanceLimit" , "colorLowerAcceptanceLimit",
		"colorMiddAcceptanceLimit", "colorUpperAcceptanceLimit"})
@Entity
public class Threshold extends AbstractEntity {
	
	private static final long serialVersionUID = 3141232890741369335L;

	private double upperAcceptanceLimit;
	private double lowerAcceptanceLimit;
	
	private QualityStatus colorLowerAcceptanceLimit = QualityStatus.Red;
	
	private QualityStatus colorMiddAcceptanceLimit = QualityStatus.Yellow;
	
	private QualityStatus colorUpperAcceptanceLimit = QualityStatus.Green;
	
	@JsonCreator
	public Threshold(
			@JsonProperty("lowerAcceptanceLimit") final double lowerLimit,
			@JsonProperty("upperAcceptanceLimit") final double upperLimit,
			@JsonProperty("colorLowerAcceptanceLimit") final QualityStatus colorLow,
			@JsonProperty("colorMiddAcceptanceLimit") final QualityStatus colorMidd,
			@JsonProperty("colorUpperAcceptanceLimit") final QualityStatus colorUpp) {

		this.setLowerAcceptanceLimit(lowerLimit);
		this.setUpperAcceptanceLimit(upperLimit);
		this.setColorLowerAcceptanceLimit(colorLow);
		this.setColorMiddAcceptanceLimit(colorMidd);
		this.setColorUpperAcceptanceLimit(colorUpp);
	}
	
	public Threshold(int lowerAcceptanceLimit, int upperAcceptanceLimit) {
		this.lowerAcceptanceLimit = lowerAcceptanceLimit;
		this.upperAcceptanceLimit = upperAcceptanceLimit;
	}


}
