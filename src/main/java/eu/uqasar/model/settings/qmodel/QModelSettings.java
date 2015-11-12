/*
 */
package eu.uqasar.model.settings.qmodel;

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
