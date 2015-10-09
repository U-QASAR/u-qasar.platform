/*
 */
package eu.uqasar.exception.model;

import eu.uqasar.exception.ModelBasedRuntimeException;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public class EntityNotFoundException extends ModelBasedRuntimeException {

	private String requestedId;
	private Class<?> requestedEntityType;

	public String getRequestedId() {
		return requestedId;
	}

	public void setRequestedId(String requestedId) {
		this.requestedId = requestedId;
	}

	public Class<?> getRequestedEntityType() {
		return requestedEntityType;
	}

	public void setRequestedEntityType(Class<?> requestedEntityType) {
		this.requestedEntityType = requestedEntityType;
	}

	public EntityNotFoundException(Class<?> requestedEntityType) {
		fillInStackTrace();
		this.requestedEntityType = requestedEntityType;
	}

	public EntityNotFoundException(Class<?> requestedEntityType, String requestedId) {
		fillInStackTrace();
		this.requestedEntityType = requestedEntityType;
		this.requestedId = requestedId;
	}

	public EntityNotFoundException() {
	}

	public EntityNotFoundException(String message) {
		super(message);
	}

	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityNotFoundException(IModel<String> messageModel) {
		super(messageModel);
	}

	public EntityNotFoundException(IModel<String> messageModel, Throwable cause) {
		super(messageModel, cause);
	}

}
