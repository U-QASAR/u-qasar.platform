/*
 */
package eu.uqasar.exception.auth.reset;

import eu.uqasar.exception.ModelBasedException;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public class NoPendingResetPWRequestException extends ModelBasedException {

	/**
	 *
	 */
	public NoPendingResetPWRequestException() {
		super();
	}

	/**
	 * @param message
	 */
	public NoPendingResetPWRequestException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public NoPendingResetPWRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param messageModel
	 */
	public NoPendingResetPWRequestException(IModel<String> messageModel) {
		super(messageModel.getObject());
	}

	/**
	 *
	 * @param messageModel
	 * @param cause
	 */
	public NoPendingResetPWRequestException(IModel<String> messageModel, Throwable cause) {
		super(messageModel.getObject(), cause);
	}
}