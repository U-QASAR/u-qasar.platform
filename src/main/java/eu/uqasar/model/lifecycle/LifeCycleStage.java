package eu.uqasar.model.lifecycle;

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


import eu.uqasar.util.resources.ResourceBundleLocator;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.model.IModel;


public enum LifeCycleStage {
	
	Specification("spec"),

	Requirements("req"),

	Design("design"),

	Implementation("impl"),

	Testing("test"),
	
	Maintenance("maint"),

	;

	private final String labelKey;

	LifeCycleStage(final String labelKey) {
		this.labelKey = labelKey;
	}

	@Override
	public String toString() {
		return getLabelModel().getObject();
	}
	
	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(LifeCycleStage.class, "label.lcStage." + labelKey);
	}
	
	public static List<LifeCycleStage> getAllLifeCycleStages(){
		return Arrays.asList(values());
	}


}
