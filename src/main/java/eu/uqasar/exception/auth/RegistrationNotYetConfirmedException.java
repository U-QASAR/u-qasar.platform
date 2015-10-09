/*
 */

package eu.uqasar.exception.auth;

import eu.uqasar.exception.ModelBasedException;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public class RegistrationNotYetConfirmedException extends ModelBasedException {
	
	/**
	 *
	 */
	public RegistrationNotYetConfirmedException() {
		super();
	}

	/**
	 * @param message
	 */
	public RegistrationNotYetConfirmedException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public RegistrationNotYetConfirmedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param messageModel
	 */
	public RegistrationNotYetConfirmedException(IModel<String> messageModel) {
		super(messageModel.getObject());
	}

	/**
	 *
	 * @param messageModel
	 * @param cause
	 */
	public RegistrationNotYetConfirmedException(IModel<String> messageModel, Throwable cause) {
		super(messageModel.getObject(), cause);
	}
}
