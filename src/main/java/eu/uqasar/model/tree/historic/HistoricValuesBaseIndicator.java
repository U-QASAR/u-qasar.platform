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

import eu.uqasar.model.tree.BaseIndicator;
@NoArgsConstructor
@Setter
@Getter
@Entity
@Indexed
public class HistoricValuesBaseIndicator extends AbstractHistoricValues {

	private static final long serialVersionUID = 5736986416286265002L;

	@ManyToOne
	@IndexedEmbedded
	private BaseIndicator baseIndicator;

	private Float weight;

	/**
	 * @param baseIndicator
	 */
	public HistoricValuesBaseIndicator(BaseIndicator baseIndicator) {
		super();
		super.setDate(baseIndicator.getLastUpdated());
		super.setValue(baseIndicator.getValue());
		super.setQualityStatus(baseIndicator.getQualityStatus());
		super.setTargetValue(baseIndicator.getTargetValue());
		super.setLowerAcceptanceLimit(baseIndicator.getThreshold().getLowerAcceptanceLimit());
		super.setUpperAcceptanceLimit(baseIndicator.getThreshold().getUpperAcceptanceLimit());
		this.setWeight(baseIndicator.getWeight());
		this.baseIndicator = baseIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HistoricValuesBaseIndicator [baseIndicator=" + baseIndicator
				+ ", weight=" + weight + "]";
	}

}
