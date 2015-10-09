package eu.uqasar.model.tree.historic;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.tree.QualityStatus;

@Entity
public abstract class AbstractHistoricValues extends AbstractEntity implements Comparable<AbstractHistoricValues>{

	private static final long serialVersionUID = -261899956194093964L;
	
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date date = new Date();
	
	private float value;
	private float targetValue;
	private double lowerAcceptanceLimit;
	private double upperAcceptanceLimit;
	@Enumerated(EnumType.STRING)
	private QualityStatus qualityStatus = QualityStatus.Gray;
	private Boolean deleted = Boolean.FALSE;
	
	public AbstractHistoricValues() {
	}
		

	/**
	 * @return
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 */
	public void setDate(final Date date) {
		this.date = date;
	}

	
	/**
	 * @return the value
	 */
	public float getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(final float value) {
		this.value = value;
	}


	/**
	 * @return the targetValue
	 */
	public Float getTargetValue() {
		return targetValue;
	}


	/**
	 * @param targetValue the targetValue to set
	 */
	public void setTargetValue(final Float targetValue) {
		this.targetValue = targetValue;
	}


	/**
	 * @return the lowerAcceptanceLimit
	 */
	public Double getLowerAcceptanceLimit() {
		return lowerAcceptanceLimit;
	}


	/**
	 * @param lowerAcceptanceLimit the lowerAcceptanceLimit to set
	 */
	public void setLowerAcceptanceLimit(final Double lowerAcceptanceLimit) {
		this.lowerAcceptanceLimit = lowerAcceptanceLimit;
	}


	/**
	 * @return the upperAcceptanceLimit
	 */
	public Double getUpperAcceptanceLimit() {
		return upperAcceptanceLimit;
	}


	/**
	 * @param upperAcceptanceLimit the upperAcceptanceLimit to set
	 */
	public void setUpperAcceptanceLimit(final Double upperAcceptanceLimit) {
		this.upperAcceptanceLimit = upperAcceptanceLimit;
	}


	/**
	 * @return the qualityStatus
	 */
	public QualityStatus getQualityStatus() {
		return qualityStatus;
	}


	/**
	 * @param qualityStatus the qualityStatus to set
	 */
	public void setQualityStatus(QualityStatus qualityStatus) {
		this.qualityStatus = qualityStatus;
	}


	/**
	 * @return the deleted
	 */
	public Boolean getDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public int compareTo(AbstractHistoricValues o) {
		return getDate().compareTo(o.getDate());
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AbstractHistoricValues [date=" + date + ", value=" + value
				+ ", targetValue=" + targetValue + ", lowerAcceptanceLimit="
				+ lowerAcceptanceLimit + ", upperAcceptanceLimit="
				+ upperAcceptanceLimit + ", qualityStatus=" + qualityStatus
				+ ", deleted=" + deleted + "]";
	}

}
