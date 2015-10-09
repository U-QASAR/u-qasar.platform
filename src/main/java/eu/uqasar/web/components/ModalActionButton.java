/**
 * 
 */
package eu.uqasar.web.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

/**
 *
 * 
 */
public class ModalActionButton extends AjaxLink<Void> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6914370101769512451L;

	private static final String id = "button";

	private Buttons.Type buttonType;

	private IModel<?> bodyModel;

	private Modal modal;

	private boolean closeModalAfterSubmit = true;

	/**
	 * 
	 * @param form
	 * @param modal
	 * @param buttonType
	 * @param bodyModel
	 * @param closeModalAfterSubmit
	 */
	public ModalActionButton(final Modal modal,
			final Buttons.Type buttonType, final IModel<?> bodyModel,
			final boolean closeModalAfterSubmit) {
		super(id);

		this.modal = modal;
		this.buttonType = buttonType;
		this.bodyModel = bodyModel;
		this.closeModalAfterSubmit = closeModalAfterSubmit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.Component#onConfigure()
	 */
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setBody(bodyModel);
		add(new ButtonBehavior(buttonType));
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.wicket.ajax.markup.html.AjaxLink#onClick(org.apache.wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	public void onClick(AjaxRequestTarget target) {
		// if configured close modal window
		if (closeModalAfterSubmit) {
			Effects.hideModal(target, modal.getMarkupId());
		}
		onAfterClick(target);
	}

	/**
	 * Override for actions after clicking the button.
	 * 
	 * @param target
	 * @param form
	 */
	protected void onAfterClick(AjaxRequestTarget target) {}

}
