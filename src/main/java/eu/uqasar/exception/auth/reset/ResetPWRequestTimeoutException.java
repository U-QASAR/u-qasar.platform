/*
 */
package eu.uqasar.exception.auth.reset;

import eu.uqasar.exception.ModelBasedException;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public class ResetPWRequestTimeoutException extends ModelBasedException {

	/**
	 *
	 */
	public ResetPWRequestTimeoutException() {
		super();
	}

	/**
	 * @param message
	 */
	public ResetPWRequestTimeoutException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public ResetPWRequestTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param messageModel
	 */
	public ResetPWRequestTimeoutException(IModel<String> messageModel) {
		super(messageModel.getObject());
	}

	/**
	 *
	 * @param messageModel
	 * @param cause
	 */
	public ResetPWRequestTimeoutException(IModel<String> messageModel, Throwable cause) {
		super(messageModel.getObject(), cause);
	}

}
