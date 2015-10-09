/*
 */

package eu.uqasar.exception.auth;

import eu.uqasar.exception.ModelBasedException;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public class UnknownUserException extends ModelBasedException {
	
	/**
	 *
	 */
	public UnknownUserException() {
		super();
	}

	/**
	 * @param message
	 */
	public UnknownUserException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public UnknownUserException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param messageModel
	 */
	public UnknownUserException(IModel<String> messageModel) {
		super(messageModel.getObject());
	}

	/**
	 *
	 * @param messageModel
	 * @param cause
	 */
	public UnknownUserException(IModel<String> messageModel, Throwable cause) {
		super(messageModel.getObject(), cause);
	}
}
