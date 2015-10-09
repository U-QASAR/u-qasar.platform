package eu.uqasar.exception.auth;

import eu.uqasar.exception.ModelBasedException;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public class RegistrationCancelledException extends ModelBasedException {

	/**
	 *
	 */
	public RegistrationCancelledException() {
		super();
	}

	/**
	 * @param message
	 */
	public RegistrationCancelledException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public RegistrationCancelledException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param messageModel
	 */
	public RegistrationCancelledException(IModel<String> messageModel) {
		super(messageModel.getObject());
	}

	/**
	 *
	 * @param messageModel
	 * @param cause
	 */
	public RegistrationCancelledException(IModel<String> messageModel, Throwable cause) {
		super(messageModel.getObject(), cause);
	}
}
