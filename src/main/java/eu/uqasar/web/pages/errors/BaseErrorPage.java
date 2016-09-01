/*
 */
package eu.uqasar.web.pages.errors;

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


import eu.uqasar.web.pages.BasePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 * @param <T>
 */
class BaseErrorPage<T extends Throwable> extends BasePage {

	BaseErrorPage(PageParameters parameters) {
		super(parameters);
	}

	public BaseErrorPage(T throwable) {
		this(throwable, new PageParameters());
	}

	private BaseErrorPage(T throwable, PageParameters parameters) {
		super(new PageParameters());
	}

	@Override
	public boolean isVersioned() {
		return false;
	}

	@Override
	public boolean isErrorPage() {
		return true;
	}
}
