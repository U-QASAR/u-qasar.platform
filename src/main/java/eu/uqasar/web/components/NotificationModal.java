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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

/**
 *
 * 
 */
public class NotificationModal extends Modal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1530140481889690683L;

	private final Label message;
	
	/**
	 * 
	 * @param id
	 * @param showImmediately
	 */
	public NotificationModal(final String id, final IModel<String> headerModel,
			final IModel<String> messageModel, final boolean showImmediately) {
		super(id);
		show(showImmediately);
		header(headerModel);
		//add(new Label("message", messageModel)).setEscapeModelStrings(false);
		add(message = new Label("message", messageModel)).setEscapeModelStrings(false);
		setUseKeyboard(false);
	}

	 /**
     * Sets the header label text.
     *
     * @param label The header label
     * @return This
     */
    public Modal message(IModel<String> label) {
        message(label, false);
        return this;
    }

    /**
     * Sets the header label text and whether model strings should be escaped.
     *
     * @param label        The header label
     * @param escapeMarkup True is model strings should be escaped
     * @return This
     */
    private Modal message(final IModel<String> label, final boolean escapeMarkup) {
    	if(message != null) {
    		message.setDefaultModel(label);
    		message.setEscapeModelStrings(escapeMarkup);
    	}
        return this;
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

}
