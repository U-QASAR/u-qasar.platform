/**
 * 
 */
package eu.uqasar.web.components;

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
	protected void addErrorCSS() {
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
	protected void onErrorHandling() {
	}

}
