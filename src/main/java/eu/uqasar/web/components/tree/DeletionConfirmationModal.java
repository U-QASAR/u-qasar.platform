package eu.uqasar.web.components.tree;

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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;

public abstract class DeletionConfirmationModal extends NotificationModal {

	private static final long serialVersionUID = -6147090676488651730L;

	public DeletionConfirmationModal(String id, final TreeNode node) {
		super(id, Model.of(""), Model.of(""), false);
		addButton(new ModalActionButton(this, Buttons.Type.Danger,
				new StringResourceModel("button.delete.confirm", this, null),
				true) {
			private static final long serialVersionUID = -8579196626175159237L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				onConfirmed(target);
				closeDeleteConfirmationModal(DeletionConfirmationModal.this,
						target);
			}
		});
		addButton(new ModalActionButton(this, Buttons.Type.Default,
				new StringResourceModel("button.delete.cancel", this, null),
				true) {
			private static final long serialVersionUID = 8931306355855637710L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				closeDeleteConfirmationModal(DeletionConfirmationModal.this,
						target);
			}
		});
	}

	public void update(TreeNode node) {
		header(getHeader(Model.of(node)));
		message(getMessage(Model.of(node)));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}

	private IModel<String> getHeader(Model<?> model) {
		return new StringResourceModel("header.delete", model);
	}

	private IModel<String> getMessage(Model<?> model) {
		TreeNode node = (TreeNode) model.getObject();
		final String prefix = " (<em>", suffix = "</em>)";
		if (node instanceof Project) {
			return new StringResourceModel("message.delete.project", model, null, node.getChildrenString(prefix, suffix));
		} else if (node instanceof QualityObjective) {
			return new StringResourceModel("message.delete.quality.objective",
					model, null, node.getChildrenString(prefix, suffix));
		} else if (node instanceof QualityIndicator) {
			return new StringResourceModel("message.delete.quality.indicator",
					model, null, node.getChildrenString(prefix, suffix));
		} else if (node instanceof Metric) {
			return new StringResourceModel("message.delete.metric", model);
		}
		return Model.of("Are you sure you want to delete?");
	}

	public abstract void onConfirmed(AjaxRequestTarget target);

	private void closeDeleteConfirmationModal(final Modal modal,
			AjaxRequestTarget target) {
		modal.appendCloseDialogJavaScript(target);
	}

}
