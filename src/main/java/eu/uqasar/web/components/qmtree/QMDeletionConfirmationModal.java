package eu.uqasar.web.components.qmtree;

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
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;

public abstract class QMDeletionConfirmationModal extends NotificationModal {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3913695236156146161L;
	
	private final ModalActionButton confirm;
	
	public QMDeletionConfirmationModal(String id, QMTreeNode node) {
		super(id, Model.of(""), Model.of(""), false);
		addButton(confirm = new ModalActionButton(this, Buttons.Type.Danger,
				new StringResourceModel("button.delete.confirm", this, null),
				true) {

			/**
					 * 
					 */
					private static final long serialVersionUID = -1523049024932741527L;
			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				onConfirmed(target);
				closeDeleteConfirmationModal(QMDeletionConfirmationModal.this,
						target);
			}
		});
		addButton(new ModalActionButton(this, Buttons.Type.Default,
				new StringResourceModel("button.delete.cancel", this, null),
				true) {

			/**
					 * 
					 */
					private static final long serialVersionUID = 2416766429463092533L;
			
			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				closeDeleteConfirmationModal(QMDeletionConfirmationModal.this,
						target);
			}
		});
	}

	public void update(QMTreeNode node) {
		header(getHeader(Model.of(node)));
		message(getMessage(Model.of(node)));
		confirm.setVisible(true);
	}

	public void deny(QMTreeNode node) {
		header(new StringResourceModel("header.denydelete.qmodel", Model.of(node)));
		message(new StringResourceModel("message.denydelete.qmodel", Model.of(node)));
		confirm.setVisibilityAllowed(true);
		confirm.setVisible(false);
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}

	private IModel<String> getHeader(Model<?> model) {
		return new StringResourceModel("header.delete", model);
	}

	private IModel<String> getMessage(Model<?> model) {
		QMTreeNode node = (QMTreeNode) model.getObject();
		final String prefix = " (<em>", suffix = "</em>)";
		if (node instanceof QModel) {
			return new StringResourceModel("message.delete.qmodel", model, null, node.getChildrenString(prefix, suffix));
		} else if (node instanceof QMQualityObjective) {
			return new StringResourceModel("message.delete.quality.objective",
					model, null, node.getChildrenString(prefix, suffix));
		} else if (node instanceof QMQualityIndicator) {
			return new StringResourceModel("message.delete.quality.indicator",
					model, null, node.getChildrenString(prefix, suffix));
		} else if (node instanceof QMMetric) {
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
