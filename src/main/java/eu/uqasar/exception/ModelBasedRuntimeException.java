/*
 */
package eu.uqasar.exception;

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


import eu.uqasar.util.resources.ResourceBundleLocator;

import lombok.Getter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
@Getter
public class ModelBasedRuntimeException extends RuntimeException {

	private Throwable cause = this;

	private IModel<String> model = Model.of("");

	protected ModelBasedRuntimeException() {
		fillInStackTrace();
	}

	protected ModelBasedRuntimeException(final String message) {
		fillInStackTrace();
		model = Model.of(message);
	}

	protected ModelBasedRuntimeException(final String message, Throwable cause) {
		fillInStackTrace();
		this.cause = cause;
		model = Model.of(message);
	}

	protected ModelBasedRuntimeException(IModel<String> messageModel) {
		fillInStackTrace();
		model = messageModel;
	}

	protected ModelBasedRuntimeException(IModel<String> messageModel, Throwable cause) {
		fillInStackTrace();
		this.cause = cause;
		model = messageModel;
	}

	@Override
	public String getMessage() {
		return model.getObject();
	}

	public IModel<String> getLabelModel(final String key) {
		return getLabelModel(key, this.getClass());
	}

	private static IModel<String> getLabelModel(final String key, final Class<?> clazz) {
		return ResourceBundleLocator.getLabelModel(clazz, key);
	}
}
