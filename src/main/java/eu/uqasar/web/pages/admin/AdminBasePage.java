/*
 */
package eu.uqasar.web.pages.admin;

import eu.uqasar.auth.strategies.annotation.AuthorizeInstantiation;
import eu.uqasar.model.role.Role;
import eu.uqasar.web.pages.BasePage;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 */
@AuthorizeInstantiation(Role.Administrator)
public abstract class AdminBasePage extends BasePage {

	public AdminBasePage(final PageParameters pageParameters) {
		super(pageParameters);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forUrl("assets/css/admin/admin.css"));
	}
	
}
