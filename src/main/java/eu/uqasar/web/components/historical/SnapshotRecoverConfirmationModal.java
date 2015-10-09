package eu.uqasar.web.components.historical;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.historic.Snapshot;
import eu.uqasar.service.SnapshotService;
import eu.uqasar.web.components.ModalActionButton;

public abstract class SnapshotRecoverConfirmationModal extends Modal {

	private static final long serialVersionUID = -8215655410653633733L;

	@Inject
	SnapshotService snapShotService;

	private ModalActionButton submitButton;

	private DropDownChoice<Snapshot> snapDropDown;

	private Snapshot snap;

	public SnapshotRecoverConfirmationModal(String id, final Project project) {
		super(id);

		add(new Label("message", new StringResourceModel(
				"snapshot.confirmation.modal.message", this, null)));

		header(new StringResourceModel("snapshot.confirmation.modal.header",
				this, null));

		snapDropDown = new DropDownChoice<Snapshot>("snapshotName",
				snapShotService.getProjectSnapshot(project));

		snapDropDown.add(new AjaxEventBehavior("onchange") {
			private static final long serialVersionUID = -5413222633224844355L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				snap = snapDropDown.getChoices().get(Integer.valueOf(snapDropDown.getInput()));
			}
		});

		add(snapDropDown);

		addButton(submitButton = new ModalActionButton(this, Buttons.Type.Info,
				new StringResourceModel("button.snapshot.confirm", this, null),
				true) {
			private static final long serialVersionUID = 5342016192194431918L;

			@Override
			protected void onConfigure() {
				super.onConfigure();

				if (snapShotService.getProjectSnapshot(project).size() == 0) {
					setEnabled(false);
				} else {
					setEnabled(true);
				}
			}


			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				onConfirmed(target);
				closeDeleteConfirmationModal(
						SnapshotRecoverConfirmationModal.this, target);
			}
		});

		addButton(new ModalActionButton(this, Buttons.Type.Default,
				new StringResourceModel("button.snapshot.cancel", this, null),
				true) {
			private static final long serialVersionUID = 4299345298752864106L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				closeDeleteConfirmationModal(
						SnapshotRecoverConfirmationModal.this, target);
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

	/**
	 * @return
	 */
	public Snapshot getSnapshot() {
		return snap;
	}

}
