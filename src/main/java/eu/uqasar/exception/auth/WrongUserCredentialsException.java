package eu.uqasar.exception.auth;

import eu.uqasar.exception.ModelBasedException;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public class WrongUserCredentialsException extends ModelBasedException {

	/**
	 *
	 */
	public WrongUserCredentialsException() {
		super();
	}

	/**
	 * @param message
	 */
	public WrongUserCredentialsException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public WrongUserCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param messageModel
	 */
	public WrongUserCredentialsException(IModel<String> messageModel) {
		super(messageModel.getObject());
	}

	/**
	 *
	 * @param messageModel
	 * @param cause
	 */
	public WrongUserCredentialsException(IModel<String> messageModel, Throwable cause) {
		super(messageModel.getObject(), cause);
	}
}
