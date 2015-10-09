package eu.uqasar.exception.auth.register;

import eu.uqasar.exception.ModelBasedException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class UserNameAlreadyExistsException extends ModelBasedException {

		/**
	 *
	 */
	public UserNameAlreadyExistsException() {
		super();
	}

	/**
	 * @param message
	 */
	public UserNameAlreadyExistsException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public UserNameAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param messageModel
	 */
	public UserNameAlreadyExistsException(IModel<String> messageModel) {
		super(messageModel.getObject());
	}

	/**
	 *
	 * @param messageModel
	 * @param cause
	 */
	public UserNameAlreadyExistsException(IModel<String> messageModel, Throwable cause) {
		super(messageModel.getObject(), cause);
	}
}
