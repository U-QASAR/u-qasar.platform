/*
 */
package eu.uqasar.exception.model;

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


import eu.uqasar.exception.ModelBasedRuntimeException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
@NoArgsConstructor
@Setter
@Getter
public class EntityNotFoundException extends ModelBasedRuntimeException {

	private String requestedId;
	private Class<?> requestedEntityType;

	public EntityNotFoundException(Class<?> requestedEntityType) {
		fillInStackTrace();
		this.requestedEntityType = requestedEntityType;
	}

	public EntityNotFoundException(Class<?> requestedEntityType, String requestedId) {
		fillInStackTrace();
		this.requestedEntityType = requestedEntityType;
		this.requestedId = requestedId;
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
