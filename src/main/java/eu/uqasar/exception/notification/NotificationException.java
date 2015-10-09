package eu.uqasar.exception.notification;

import eu.uqasar.exception.ModelBasedException;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public class NotificationException extends ModelBasedException {

	/**
	 *
	 */
	public NotificationException() {
		super();
	}

	/**
	 * @param message
	 */
	public NotificationException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public NotificationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param messageModel
	 */
	public NotificationException(IModel<String> messageModel) {
		super(messageModel.getObject());
	}

	/**
	 *
	 * @param messageModel
	 * @param cause
	 */
	public NotificationException(IModel<String> messageModel, Throwable cause) {
		super(messageModel.getObject(), cause);
	}
}
