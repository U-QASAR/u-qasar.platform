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

import org.apache.wicket.bean.validation.PropertyValidator;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidator;

/**
 * Extends {@link eu.uqasar.web.components.InputBorder} and uses Bean
 * Validation via {@link net.ftlines.wicket.validation.bean.PropertyValidator}
 * to determine if a form component's input is valid or not.
 * 
 *
 * 
 * @param <T>
 */
public class InputBeanValidationBorder<T> extends InputBorder<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7672294641405104492L;

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param inputComponent
	 */
	public InputBeanValidationBorder(final String id,
			final FormComponent<T> inputComponent) {
		this(id, inputComponent, new Model<String>(), new Model<String>());
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param inputComponent
	 * @param labelModel
	 *            optional
	 */
	public InputBeanValidationBorder(final String id,
			final FormComponent<T> inputComponent,
			final IModel<String> labelModel) {
		this(id, inputComponent, labelModel, new Model<String>());
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param inputComponent
	 * @param labelModel
	 *            optional
	 * @param helpModel
	 *            optional
	 */
    InputBeanValidationBorder(final String id,
                              final FormComponent<T> inputComponent,
                              final IModel<String> labelModel, final IModel<String> helpModel) {
		super(id, inputComponent, labelModel, helpModel);

		// remove wicket validation
		this.inputComponent.setRequired(false);

		// add a property validator if the component does not already have one
		addPropertyValidator(inputComponent);
	}

	/**
	 * Add a property validator to the component if it does not already have one
	 * and the component is using an appropriate model, e.g. PropertyModel.
	 * 
	 * @param fc
	 */
    private void addPropertyValidator(FormComponent<T> fc) {
		if (!hasPropertyValidator(fc)) {
			final PropertyValidator<T> propertyValidator = new PropertyValidator<>();
			fc.add(propertyValidator);
		}
	}

	/**
	 * Checks if the component already has a PropertyValidator.
	 * 
	 * @param fc
	 * @return
	 */
    private boolean hasPropertyValidator(FormComponent<T> fc) {
		for (IValidator<?> validator : fc.getValidators()) {
			if (validator instanceof PropertyValidator) {
				return true;
			}
		}
		return false;
	}

}
