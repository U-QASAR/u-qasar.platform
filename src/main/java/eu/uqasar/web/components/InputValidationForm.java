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

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

/**
 * A simple validation form that will add twitter bootstrap specific error and
 * success CSS to the class attribute of InputBorders contained in this form,
 * depending on whether the border's form component contains invalid or valid
 * input respectively. So far this form only works correctly with
 * {@link eu.uqasar.web.components.InputBorder} and its subclasses.
 * 
 *
 * 
 */
public class InputValidationForm<T> extends Form<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8724766263334650533L;

	/**
	 * 
	 * @param id
	 */
	public InputValidationForm(final String id) {
		super(id);
	}

	/**
	 * 
	 * @param id
	 * @param model
	 */
	public InputValidationForm(final String id, final IModel<T> model) {
		super(id, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.markup.html.form.Form#onError()
	 */
	@Override
	protected void onError() {
		super.onError();
		addErrorCSS();
		onErrorHandling();
	}

	/**
	 * Go through this form's InputValidationBorder components and add error CSS
	 * to their class attribute if the border's inputComponent has a validation
	 * error.
	 */
    private void addErrorCSS() {
		IVisitor<? extends InputBorder<?>, Object> visitor = new IVisitor<InputBorder<?>, Object>() {

			@Override
			public void component(InputBorder<?> object, IVisit<Object> visit) {
				if (object.inputComponent.hasErrorMessage()) {
					object.add(new CSSAppender("error"));
				}
			}
		};
		this.visitChildren(InputBorder.class, visitor);
	}

	/**
	 * Override this method for further specific error handling.
	 */
    private void onErrorHandling() {
	}

}
