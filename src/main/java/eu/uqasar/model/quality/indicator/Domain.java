package eu.uqasar.model.quality.indicator;

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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;


public enum Domain implements IResourceKeyProvider {

	Telecommunications("telecom"),

	Bank("bank"),

	Industry("industry"),

	Insurance("insurance"),
	
	Public("public"),
	
	MobileApp ("mobile"),
	
	CSSolution("cs"),
	
	SaaS("saas")
	
	;

	private final String labelKey;

	Domain(final String labelKey) {
		this.labelKey = labelKey;
	}

	public String toString() {
		return getLabelModel().getObject();
	}
	
	private IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(this.getClass(), this);
	}
	
	public static List<Domain> getAllDomains(){
		List<Domain> list = new ArrayList<>();
		Collections.addAll(list, Domain.values());
		return list;
	}

	@Override
	public String getKey() {
		return "label.domain." + this.labelKey;
	}
	
}
