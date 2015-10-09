package eu.uqasar.web.components.qmtree;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.DropDownModal;

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
	
	public String getTagName() {
		return super.getTagName();
	}

	public void setTagName(String tagName) {
		super.setTagName(tagName);
	}
	
	public String getInputSelection() {
		return super.getInputSelection();
	}

	public void setInputSelection(String inputSelection) {
		super.setInputSelection(inputSelection);
	}

	public Long getTagId() {
		return super.getTagId();
	}

	public void setTagId(Long tagId) {
		super.setTagId(tagId);
	}
	
}
