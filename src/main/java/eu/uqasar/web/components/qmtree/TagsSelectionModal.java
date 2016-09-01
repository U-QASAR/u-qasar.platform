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


import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.DropDownModal;

@Setter
@Getter
public abstract class TagsSelectionModal extends DropDownModal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3913695236156146161L;
	
    private ModalActionButton confirm;
	
    private Long tagId;
    
    private String tagName;
    
    private String inputSelection;

	public TagsSelectionModal(String id, List<Class> metaDataClasses) {
		super(id, Model.of("headerTagsSelectionModal"), metaDataClasses, false);
		
		addButton(new ModalActionButton(this, Buttons.Type.Danger,
				new StringResourceModel("button.tags.confirm", this, null),
				false) {
			private static final long serialVersionUID = -8579196626175159237L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				if (onConfirmed(target)){
					closeTagsSelectionModal(TagsSelectionModal.this,target);
				}
			}
		});

		
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}

	public abstract boolean onConfirmed(AjaxRequestTarget target);

	private void closeTagsSelectionModal(final Modal modal,
			AjaxRequestTarget target) {
		modal.appendCloseDialogJavaScript(target);
	}

}
