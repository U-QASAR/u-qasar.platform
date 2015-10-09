package eu.uqasar.web.components;

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
	public InputBeanValidationBorder(final String id,
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
	protected void addPropertyValidator(FormComponent<T> fc) {
		if (!hasPropertyValidator(fc)) {
			final PropertyValidator<T> propertyValidator = new PropertyValidator<T>();
			fc.add(propertyValidator);
		}
	}

	/**
	 * Checks if the component already has a PropertyValidator.
	 * 
	 * @param fc
	 * @return
	 */
	protected boolean hasPropertyValidator(FormComponent<T> fc) {
		for (IValidator<?> validator : fc.getValidators()) {
			if (validator instanceof PropertyValidator) {
				return true;
			}
		}
		return false;
	}

}
