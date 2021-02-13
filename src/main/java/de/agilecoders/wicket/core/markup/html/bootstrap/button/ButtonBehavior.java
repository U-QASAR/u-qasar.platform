package de.agilecoders.wicket.core.markup.html.bootstrap.button;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.base.Strings;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;
import de.agilecoders.wicket.core.util.Components;
import de.agilecoders.wicket.core.util.CssClassNames;

/**
 * Default button behavior that controls the size and type of a button.
 * 
 * WARNING: this class was manually added in order to overwrite the one provided 
 * by wicket bootstrap version 0.8.4, because there was an error in 
 * {@link #onComponentTag(Component, ComponentTag)} method. 
 * It was not possible to set a button to be disabled, because wicket changes the
 * type to "span" but in here it is checked if the tag name is either a, button
 * or input although there is a hack for fixing this, but this was unfortunately
 * applied in the original source after tag name validation failed.....
 * 
 */
public class ButtonBehavior extends BootstrapBaseBehavior {

	private static final long serialVersionUID = -7385592819134853078L;

	private final IModel<Buttons.Type> buttonType;
	private final IModel<Buttons.Size> buttonSize;
	private final IModel<String> block;
	private final ICssClassNameProvider blockProvider;

	/**
	 * Construct. Uses
	 * {@link de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Size#Medium}
	 * and
	 * {@link de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type#Default}
	 * .
	 */
	public ButtonBehavior() {
		this(Buttons.Type.Default, Buttons.Size.Medium);
	}

	/**
	 * Construct. Uses
	 * {@link de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type#Default}
	 * .
	 * 
	 * @param size
	 *            Size of button
	 */
	public ButtonBehavior(final Buttons.Size size) {
		this(Buttons.Type.Default, size);
	}

	/**
	 * Construct. Uses
	 * {@link de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Size#Medium}
	 * .
	 * 
	 * @param type
	 *            Type of button
	 */
	public ButtonBehavior(final Buttons.Type type) {
		this(type, Buttons.Size.Medium);
	}

	/**
	 * Construct.
	 * 
	 * @param type
	 *            Type of button
	 * @param size
	 *            Size of button
	 */
    private ButtonBehavior(final Buttons.Type type, final Buttons.Size size) {
		this(Model.of(type), Model.of(size));
	}

	/**
	 * Construct.
	 * 
	 * @param buttonType
	 *            Type of button
	 * @param buttonSize
	 *            Size of button
	 */
	public ButtonBehavior(final IModel<Buttons.Type> buttonType,
			final IModel<Buttons.Size> buttonSize) {
		this.buttonType = buttonType;
		this.buttonSize = buttonSize;
		this.block = Model.of("");
		this.blockProvider = CssClassNames.newProvider(block);
	}

	/**
	 * @return size of button
	 */
	public Buttons.Size getSize() {
		return buttonSize.getObject();
	}

	/**
	 * @return type of button
	 */
    private Buttons.Type getType() {
		return buttonType.getObject();
	}

	/**
	 * @return whether this button is a block level element
	 */
	public boolean isBlock() {
		return !Strings.isNullOrEmpty(block.getObject());
	}

	/**
	 * sets this button to be a block element or not
	 * 
	 * @param block
	 *            true, for block mode
	 * @return this instance for chaining
	 */
	public final ButtonBehavior setBlock(final boolean block) {
		this.block.setObject(block ? "btn-block" : "");
		return this;
	}

	/**
	 * sets the type of button
	 * 
	 * @param type
	 *            type to use
	 * @return this instance for chaining
	 */
	public final ButtonBehavior setType(final Buttons.Type type) {
		this.buttonType.setObject(type);
		return this;
	}

	/**
	 * sets the size of button
	 * 
	 * @param size
	 *            size to use
	 * @return this instance for chaining
	 */
	public final ButtonBehavior setSize(final Buttons.Size size) {
		this.buttonSize.setObject(size);
		return this;
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		super.onComponentTag(component, tag);

		// change the order of the two following method calls, otherwise ajax
		// links can never be disabled!
		// HACK issue #79: wicket changes tag name if component wasn't enabled
		Buttons.fixDisabledState(component, tag);
		Components.assertTag(component, tag, "a", "button", "input");

		// a menu button has no css classes, inherits its styles from the menu
		if (!Buttons.Type.Menu.equals(getType())) {
			Buttons.onComponentTag(component, tag, buttonSize.getObject(),
					buttonType.getObject(), blockProvider);
		}
	}
}
