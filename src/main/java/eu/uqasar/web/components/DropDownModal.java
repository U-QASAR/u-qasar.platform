package eu.uqasar.web.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

public class DropDownModal extends Modal {

	private static final long serialVersionUID = -4262000688976309218L;

	private DropDownChoice<String> metaDataTypes;

	private String typeSelected;	

	private String tagName;

	private Long tagId;

	private String inputSelection;

	/**
	 * Modal with a title and a DropDownChoice
	 * @param id
	 * @param headerModel header
	 * @param types Options in DropDownChoice
	 * @param showImmediately
	 */
	public DropDownModal(final String id, final IModel<String> headerModel,
			List<Class> types, final boolean showImmediately) {
		super(id);
		show(showImmediately);
		header(headerModel);		

		List<String> ls = new ArrayList<String>();
		Iterator it = types.iterator();
		while (it.hasNext()){
			ls.add(((Class)it.next()).getSimpleName());
		}

		metaDataTypes = new DropDownChoice<String>("metaDataTypes",  new Model(), ls);

		metaDataTypes.add(new AjaxEventBehavior("onchange") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				typeSelected = metaDataTypes.getChoices().get(Integer.valueOf(metaDataTypes.getInput()));
			}
		});

		add(metaDataTypes).setEscapeModelStrings(false);
		setUseKeyboard(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.agilecoders.wicket.markup.html.bootstrap.dialog.Modal#
	 * createBasicInitializerScript(java.lang.String)
	 */
	@Override
	protected String createBasicInitializerScript(final String markupId) {
		return "$('#" + markupId + "').modal({keyboard:" + useKeyboard()
				+ ", show:" + showImmediately() + ", backdrop: 'static'})";
	}

	public String getTypeSelected() {
		return typeSelected;
	}

	public void setTypeSelected(String typeSelected) {
		this.typeSelected = typeSelected;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getInputSelection() {
		return inputSelection;
	}

	public void setInputSelection(String inputSelection) {
		this.inputSelection = inputSelection;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}
}
