package eu.uqasar.model.analytic;

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

public enum Dimensions {

	Project("project"),
	Status("status"),
	Type("type"),
	Priority("priority"),
	Resolution("resolution"),
	Assignee("assignee"),
	Reporter("reporter"),
	Creator("creator"),
	Created("created"),;

	private final String labelKey;

	Dimensions(final String labelKey) {
		this.labelKey = labelKey;
	}
	

	@Override
	public String toString() {
		return getLabelModel().getObject();
	}

	private IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(Dimensions.class, "label.dimension." + labelKey);
	}
	
	public static List<Dimensions> getAllDimensions(){
		return Arrays.asList(values());
	}


}
