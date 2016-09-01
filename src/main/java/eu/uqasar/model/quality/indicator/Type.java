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


import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;

public enum Type implements IResourceKeyProvider {

	Manual("manual", new IconType("edit")),

	Automatic("automatic", new IconType("cogs")),

	;

	private final String labelKey;
	private final IconType icon;

	Type(final String labelKey, IconType icon) {
		this.labelKey = labelKey;
		this.icon = icon;
	}

	public IconType getIconType() {
		return icon;
	}
	
	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(this.getClass(), this);
	}

	@Override
	public String getKey() {
		return "label.quality.indicator.type." + this.labelKey;
	}

	public String toString() {
		return getLabelModel().getObject();
	}
}
