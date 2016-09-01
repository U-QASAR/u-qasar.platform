package eu.uqasar.web.components;

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

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Temporarily (until end of request) appends the css class in the model to a
 * component's class attribute.
 * 
 *
 * 
 */
@Setter
@Getter
public class CSSAppender extends AttributeAppender {

	/**
	 * 
	 */
	private static final long serialVersionUID = 875094567205322546L;

	private static final String attribute = "class";

	private static final String separator = " ";

	private IModel<String> model;

	private CSSAppender(IModel<String> model) {
		super(attribute, model, separator);
		this.setModel(model);
	}

	public CSSAppender(String css) {
		this(new Model<>(css));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.behavior.Behavior#isTemporary(org.apache.wicket.Component
	 * )
	 */
	@Override
	public boolean isTemporary(Component component) {
		return true;
	}
}
