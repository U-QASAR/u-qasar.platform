package eu.uqasar.model.tree.historic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import eu.uqasar.model.tree.BaseIndicator;

@Entity
@Indexed
public class HistoricValuesBaseIndicator extends AbstractHistoricValues {

	private static final long serialVersionUID = 5736986416286265002L;

	@ManyToOne
	@IndexedEmbedded
	private BaseIndicator baseIndicator;

	private Float weight;

	/**
	 * 
	 */
	public HistoricValuesBaseIndicator() {
	}

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

	public BaseIndicator getBaseIndicator() {
		return baseIndicator;
	}

	public void setBaseIndicator(BaseIndicator baseIndicator) {
		this.baseIndicator = baseIndicator;
	}

	/**
	 * @return the weight
	 */
	public Float getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(Float weight) {
		this.weight = weight;
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
