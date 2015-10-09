/**
 * 
 */
package eu.uqasar.web.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.model.Model;

/**
 *
 * 
 */
public class DisplayNoneBehavior extends AttributeModifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -331499280670815711L;

	/**
	 * 
	 */
	public DisplayNoneBehavior() {
		super("style", Model.of("display: none"));
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
