package eu.uqasar.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * StringResourceModelPlaceholderDelegate
 * 
 *
 * @version $LastChangedRevision: $
 * 
 */
public class StringResourceModelPlaceholderDelegate extends StringResourceModel {
	private static final long serialVersionUID = 4800632904474672037L;

	/**
	 * Creates a new string resource model using the supplied parameters.
	 * <p>
	 * The relative component parameter should generally be supplied, as without
	 * it resources can not be obtained from resource bundles that are held
	 * relative to a particular component or page. However, for application that
	 * use only global resources then this parameter may be null.
	 * <p>
	 * The model parameter is also optional and only needs to be supplied if
	 * value substitutions are to take place on either the resource key or the
	 * actual resource strings.
	 * <p>
	 * The parameters parameter is also optional and is used for substitutions.
	 * 
	 * @param resourceKey
	 *            The resource key for this string resource
	 * @param component
	 *            The component that the resource is relative to
	 * @param model
	 *            The model to use for property substitutions
	 * @param parameters
	 *            The parameters to substitute using a Java MessageFormat object
	 */
	public StringResourceModelPlaceholderDelegate(final String resourceKey, final Component component, final IModel<?> model,
			final Object... parameters) {
		this(resourceKey, component, model, null, parameters);
	}

	/**
	 * Creates a new string resource model using the supplied parameters.
	 * <p>
	 * The relative component parameter should generally be supplied, as without
	 * it resources can not be obtained from resource bundles that are held
	 * relative to a particular component or page. However, for application that
	 * use only global resources then this parameter may be null.
	 * <p>
	 * The model parameter is also optional and only needs to be supplied if
	 * value substitutions are to take place on either the resource key or the
	 * actual resource strings.
	 * <p>
	 * The parameters parameter is also optional and is used for substitutions.
	 * 
	 * @param resourceKey
	 *            The resource key for this string resource
	 * @param component
	 *            The component that the resource is relative to
	 * @param model
	 *            The model to use for property substitutions
	 * @param defaultValue
	 *            The default value if the resource key is not found.
	 * @param parameters
	 *            The parameters to substitute using a Java MessageFormat object
	 */
	public StringResourceModelPlaceholderDelegate(final String resourceKey, final Component component, final IModel<?> model,
			final String defaultValue, final Object... parameters) {
		super(resourceKey, component, model, defaultValue, parameters);
	}

	/**
	 * Creates a new string resource model using the supplied parameters.
	 * <p>
	 * The model parameter is also optional and only needs to be supplied if
	 * value substitutions are to take place on either the resource key or the
	 * actual resource strings.
	 * <p>
	 * The parameters parameter is also optional and is used for substitutions.
	 * 
	 * @param resourceKey
	 *            The resource key for this string resource
	 * @param model
	 *            The model to use for property substitutions
	 * @param parameters
	 *            The parameters to substitute using a Java MessageFormat object
	 */
	public StringResourceModelPlaceholderDelegate(final String resourceKey, final IModel<?> model, final Object... parameters) {
		this(resourceKey, null, model, null, parameters);
	}

	/**
	 * Creates a new string resource model using the supplied parameters.
	 * <p>
	 * The model parameter is also optional and only needs to be supplied if
	 * value substitutions are to take place on either the resource key or the
	 * actual resource strings.
	 * <p>
	 * The parameters parameter is also optional and is used for substitutions.
	 * 
	 * @param resourceKey
	 *            The resource key for this string resource
	 * @param model
	 *            The model to use for property substitutions
	 * @param parameters
	 *            The parameters to substitute using a Java MessageFormat object
	 * @param defaultValue
	 *            The default value if the resource key is not found.
	 */
	public StringResourceModelPlaceholderDelegate(final String resourceKey, final IModel<?> model, final String defaultValue,
			final Object... parameters) {
		this(resourceKey, null, model, defaultValue, parameters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.model.StringResourceModel#toString()
	 */
	@Override
	public String toString() {
		return getString();
	}

}
