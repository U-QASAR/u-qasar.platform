package eu.uqasar.web.components.historical;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.LocalDate;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import eu.uqasar.web.components.ModalActionButton;

public abstract class SnapshotSaveConfirmationModal extends Modal {

	private static final long serialVersionUID = -8215655410653633733L;

	private TextField<String> snapNameTextField;
	private ModalActionButton submitButton;

	public SnapshotSaveConfirmationModal(String id, final IModel<String> projectNameModel) {
		super(id);

		add(snapNameTextField = new TextField<>("textfield", projectNameModel));
		
		this.snapNameTextField.add(new OnChangeAjaxBehavior() {

			private static final long serialVersionUID = 6936801286109166680L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(submitButton);
			}

		});

		add(new Label("message", new StringResourceModel(
				"snapshot.confirmation.modal.message", this, null)));

		header(new StringResourceModel("snapshot.confirmation.modal.header",
				this, null));

		add(new Label("timestamp", LocalDate.now().toString()));

		addButton(submitButton = new ModalActionButton(this, Buttons.Type.Info,
				new StringResourceModel("button.snapshot.confirm", this, null),
				true) { 
			private static final long serialVersionUID = 5342016192194431918L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				
				if(snapNameTextField.getModelObject() == null || snapNameTextField.getModelObject().isEmpty()){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
			}
			
			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				onConfirmed(target);
				closeDeleteConfirmationModal(SnapshotSaveConfirmationModal.this,
						target);
			}
		});
		
		addButton(new ModalActionButton(this, Buttons.Type.Default,
				new StringResourceModel("button.snapshot.cancel", this, null),
				true) {
			private static final long serialVersionUID = 4299345298752864106L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				closeDeleteConfirmationModal(SnapshotSaveConfirmationModal.this,
						target);
			}
		});

	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}

	public abstract void onConfirmed(AjaxRequestTarget target);

	private void closeDeleteConfirmationModal(final Modal modal,
			AjaxRequestTarget target) {
		modal.appendCloseDialogJavaScript(target);
	}

}
