package eu.uqasar.exception.notification;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
