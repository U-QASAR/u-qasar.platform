package eu.uqasar.exception.auth.register;

import eu.uqasar.exception.ModelBasedException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class UserMailAlreadyExistsException extends ModelBasedException {

		/**
	 *
	 */
	public UserMailAlreadyExistsException() {
		super();
	}

	/**
	 * @param message
	 */
	public UserMailAlreadyExistsException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public UserMailAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param messageModel
	 */
	public UserMailAlreadyExistsException(IModel<String> messageModel) {
		super(messageModel.getObject());
	}

	/**
	 *
	 * @param messageModel
	 * @param cause
	 */
	public UserMailAlreadyExistsException(IModel<String> messageModel, Throwable cause) {
		super(messageModel.getObject(), cause);
	}
}
