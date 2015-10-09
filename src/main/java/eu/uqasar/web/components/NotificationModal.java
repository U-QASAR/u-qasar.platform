/**
 * 
 */
package eu.uqasar.web.components;

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

	private Label message;
	
	/**
	 * 
	 * @param id
	 * @param model
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
    public Modal message(final IModel<String> label, final boolean escapeMarkup) {
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
