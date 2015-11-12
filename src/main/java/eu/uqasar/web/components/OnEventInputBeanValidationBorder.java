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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Extends {@link eu.uqasar.web.components.InputBeanValidationBorder} with
 * ability to validate input and decorate input component with error- or
 * success-indicating CSS styles upon change of input.
 * 
 *
 * 
 * @param <T>
 */
public class OnEventInputBeanValidationBorder<T> extends
		InputBeanValidationBorder<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1219940337620367185L;

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param inputComponent
	 * @param events
	 */
	public OnEventInputBeanValidationBorder(final String id,
			final FormComponent<T> inputComponent, final HtmlEvent... events) {
		this(id, inputComponent, new Model<String>(), new Model<String>(),
				events);
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param inputComponent
	 * @param labelModel
	 * @param events
	 */
	public OnEventInputBeanValidationBorder(final String id,
			final FormComponent<T> inputComponent,
			final IModel<String> labelModel, final HtmlEvent... events) {
		this(id, inputComponent, labelModel, new Model<String>(), events);
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 * @param inputComponent
	 * @param labelModel
	 * @param helpModel
	 * @param events
	 */
	public OnEventInputBeanValidationBorder(final String id,
			final FormComponent<T> inputComponent,
			final IModel<String> labelModel, final IModel<String> helpModel,
			final HtmlEvent... events) {
		super(id, inputComponent, labelModel, helpModel);

		// add a updating behavior that will trigger validation on form
		// component when the given event occurs
		for (HtmlEvent htmlEvent : events) {
			final AjaxFormComponentUpdatingBehavior behavior = new AjaxFormComponentUpdatingBehavior(
					htmlEvent.getEvent()) {

				private static final long serialVersionUID = -5400347946196842880L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					success(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target,
						RuntimeException e) {
					super.onError(target, e);
					error(target);
				}

			};
			this.inputComponent.add(behavior);
		}
	}

	/**
	 * 
	 * @param target
	 */
	private void success(final AjaxRequestTarget target) {
		// add success css to class attribute
		this.add(new CSSAppender("success"));
		target.add(this);
	}

	/**
	 * 
	 * @param target
	 */
	private void error(final AjaxRequestTarget target) {
		// add error css to class attribute
		this.add(new CSSAppender("error"));
		target.add(this);
	}

}
