/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uqasar.exception.auth;

import eu.uqasar.exception.ModelBasedException;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public class NoPendingRegistrationException extends ModelBasedException {

	/**
	 *
	 */
	public NoPendingRegistrationException() {
		super();
	}

	/**
	 * @param message
	 */
	public NoPendingRegistrationException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public NoPendingRegistrationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param messageModel
	 */
	public NoPendingRegistrationException(IModel<String> messageModel) {
		super(messageModel.getObject());
	}

	/**
	 *
	 * @param messageModel
	 * @param cause
	 */
	public NoPendingRegistrationException(IModel<String> messageModel, Throwable cause) {
		super(messageModel.getObject(), cause);
	}
}
