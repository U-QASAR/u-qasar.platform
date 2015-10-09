/**
 * 
 */
package eu.uqasar.web.components;

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
public class CSSAppender extends AttributeAppender {

	/**
	 * 
	 */
	private static final long serialVersionUID = 875094567205322546L;

	private static final String attribute = "class";

	private static final String separator = " ";

	private IModel<String> model;

	public CSSAppender(IModel<String> model) {
		super(attribute, model, separator);
		this.setModel(model);
	}

	public CSSAppender(String css) {
		this(new Model<String>(css));
	}

	public IModel<String> getModel() {
		return model;
	}

	public void setModel(IModel<String> model) {
		this.model = model;
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
