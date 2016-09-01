package eu.uqasar.model.qmtree;

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


import java.util.Arrays;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.ResourceBundleLocator;


public enum QModelStatus {
	
	Active("active"),

	NotActive("notactive"),

	OldActive("oldactive"),


	;

	private final String labelKey;

	QModelStatus(final String labelKey) {
		this.labelKey = labelKey;
	}

	@Override
	public String toString() {
		return getLabelModel().getObject();
	}
	
	private IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(QModelStatus.class, "label.qmodelstatus." + labelKey);
	}
	
	public static List<QModelStatus> getAllLifeCycleStages(){
		return Arrays.asList(values());
	}


}
