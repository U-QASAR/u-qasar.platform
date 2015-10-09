/*
 */
package eu.uqasar.model.settings.qmodel;

import eu.uqasar.model.settings.Settings;


public class QModelSettings extends Settings {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String HIGH = "high.qmodel.entity";
	private static final String MEDIUM = "medium.qmodel.entity";
	private static final String LOW = "low.qmodel.entity";	
	private static final String HIGHENTITY = "highEntity.qmodel.entity";
	private static final String MEDIUMENTITY = "mediumEntity.qmodel.entity";
	private static final String LOWENTITY = "lowEntity.qmodel.entity";


	private static final String[] keys = {
		HIGH,
		HIGHENTITY,
		MEDIUM,
		MEDIUMENTITY,
		LOW,
		LOWENTITY
	};

	@Override
	public String[] getKeys() {
		return keys;
	}

	public String getHighEntity() {
		return getValue(HIGHENTITY);
	}

	public void setHighEntity(final String entity) {
		setValue(HIGHENTITY, entity);
	}

	public String getMediumEntity() {
		return getValue(MEDIUMENTITY);
	}

	public void setMediumEntity(final String entity) {
		setValue(MEDIUMENTITY, entity);
	}

	public String getLowEntity() {
		return getValue(LOWENTITY);
	}

	public void setLowEntity(final String entity) {
		setValue(LOWENTITY, entity);
	}

	public String getHigh() {
		return getValue(HIGH);
	}

	public void setHigh(final String entity) {
		setValue(HIGH, entity);
	}

	public String getMedium() {
		return getValue(MEDIUM);
	}

	public void setMedium(final String entity) {
		setValue(MEDIUM, entity);
	}

	public String getLow() {
		return getValue(LOW);
	}

	public void setLow(final String entity) {
		setValue(LOW, entity);
	}

}
