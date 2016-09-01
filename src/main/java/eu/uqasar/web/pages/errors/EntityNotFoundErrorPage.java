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


import eu.uqasar.exception.model.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 */
public class EntityNotFoundErrorPage extends BaseErrorPage<EntityNotFoundException> {

	public EntityNotFoundErrorPage() {
		this(null);
	}

	public EntityNotFoundErrorPage(EntityNotFoundException throwable) {
		super(new PageParameters());
		String objectInfo = "";
		if (throwable != null) {
			StringResourceModel objectInfoModel = new StringResourceModel("description.item", this, null, new Object[] {throwable.getRequestedEntityType().getSimpleName(), throwable.getRequestedId()});
			objectInfo = objectInfoModel.getString();
		}
		StringResourceModel desc = new StringResourceModel("description.text", this, null, new Object[] {objectInfo});
		add(new Label("description", desc).setEscapeModelStrings(false));
	}

	@Override
	protected void configureResponse(WebResponse response) {
		super.configureResponse(response);
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
}
