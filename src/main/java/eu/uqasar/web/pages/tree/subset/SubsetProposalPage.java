package eu.uqasar.web.pages.tree.subset;

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
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.subset.panel.SubsetProposalPanel;

/**
 *
 *
 */
public class SubsetProposalPage extends BasePage{
	private static final long serialVersionUID = 1404486356253398289L;
	private final SubsetProposalPanel subsetProposalPanel;
	private final Modal saveConfirmationModal;
	
	//TODO: AjaxLink is the best solution to use as button?
	private final AjaxLink saveSelectedButton;

	public SubsetProposalPage(PageParameters parameters) {
		super(parameters);
		subsetProposalPanel = new SubsetProposalPanel("subsetPanel");
		add(subsetProposalPanel);
		
		saveSelectedButton = new AjaxLink("saveSelected") {
			private static final long serialVersionUID = -3695540884955827907L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				if(subsetProposalPanel.getDataGroup().getModelObject().isEmpty()){
					// Prints error message when no element has been selected
					getPage().error(new StringResourceModel("subset.no.selection", this, null).getString());
					target.add(feedbackPanel);
				}
				else{
					// opens the modal to Confirm that the new project will be created from the selected items
					saveConfirmationModal.appendShowDialogJavaScript(target);
					System.out.println("Hola");
				}
			}
		};
		add(saveSelectedButton);
		
		// add confirmation modal to confirm creating items
		add(saveConfirmationModal = newSaveConfirmationModal());
		
	}
	
	/**
	 *
	 * @return
	 */
	private NotificationModal newSaveConfirmationModal() {
		final NotificationModal notificationModal = new NotificationModal("saveConfirmationModal", 
				new StringResourceModel("create.confirmation.modal.header", this, null),
				new StringResourceModel("create.confirmation.modal.message", this, null), false);
		
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Primary, 
				new StringResourceModel("create.confirmation.modal.submit.text", this, null),true) 
		{
			private static final long serialVersionUID = 1797796653537766182L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// close modal
				closeSaveConfirmationModal(notificationModal, target);

				// Prints success message
				getPage().success(new StringResourceModel("subset.selected.created", this, null).getString());
				target.add(feedbackPanel);

				// confirmed --> save
				subsetProposalPanel.saveSelectedItems(target);
			
			}
		});
		
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Default, new StringResourceModel(
						"create.confirmation.modal.cancel.text", this, null),
						true) {

			private static final long serialVersionUID = -7504717586865807253L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// Cancel clicked --> do nothing, close modal
				closeSaveConfirmationModal(notificationModal, target);
			}
		});
		return notificationModal;
	}

	/**
	 *
	 * @param modal
     */
	private void closeSaveConfirmationModal(final Modal modal,
			AjaxRequestTarget target) {
		// close
		modal.appendCloseDialogJavaScript(target);
	}
}
