/*
 */
package eu.uqasar.web.pages.errors;

import eu.uqasar.exception.model.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
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
