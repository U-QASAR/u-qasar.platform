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

	private final Buttons.Type buttonType;

	private final IModel<?> bodyModel;

	private final Modal modal;

	private boolean closeModalAfterSubmit = true;

	/**
	 * 
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
     */
	protected void onAfterClick(AjaxRequestTarget target) {}

}
