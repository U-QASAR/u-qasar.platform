/**
 * 
 */
package eu.uqasar.web.pages.tree.subset;

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
	private SubsetProposalPanel subsetProposalPanel;
	private final Modal saveConfirmationModal;
	
	//TODO: AjaxLink is the best solution to use as button?
	private AjaxLink saveSelectedButton;

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
	 * @param targetcubes
	 */
	private void closeSaveConfirmationModal(final Modal modal,
			AjaxRequestTarget target) {
		// close
		modal.appendCloseDialogJavaScript(target);
	}
}
