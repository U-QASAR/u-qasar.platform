/*
 */
package eu.uqasar.web.pages.admin.companies;

import javax.inject.Inject;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.company.Company;
import eu.uqasar.model.user.User;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.pages.admin.companies.panels.CompanyEditPanel;
import eu.uqasar.web.pages.admin.users.UserEditPage;
import eu.uqasar.web.pages.admin.users.UserListPage;
import eu.uqasar.web.pages.qmtree.metric.QMMetricViewPage;
import eu.uqasar.web.pages.user.panels.EditProfilePanel;

/**
 *
 *
 */
public final class CompanyEditPage extends AdminBasePage {

	@Inject private CompanyService companyService; 

	private Company company = new Company();

	public CompanyEditPage(final PageParameters pageParameters) {
		super(pageParameters);
		if (!pageParameters.get("id").isEmpty()) {
			company = companyService.getById(pageParameters.get("id").toLong());
			if (company == null) {
				throw new EntityNotFoundException(Company.class, pageParameters.get("id").toOptionalString());
			}
		}

		add(new Label("page.title", getPageTitleModel()));		
		add(new CompanyEditPanel("companyEditPanel", company){
			@Override
			public void onSubmit(Company company){
				if (company.getId() == null) {
					company = companyService.create(company);
					
					PageParameters parameters = BasePage.appendSuccessMessage(
							CompanyListPage.forCompany(company),
							new StringResourceModel("message.save.new", this, Model.of(company)));
							setResponsePage(CompanyListPage.class, parameters);
				} else{
					company = companyService.update(company);
					
					PageParameters parameters = BasePage.appendSuccessMessage(
								CompanyListPage.forCompany(company),
								new StringResourceModel("message.save", this, Model.of(company)));
					setResponsePage(CompanyListPage.class, parameters);
					
				}
			}
			@Override
            public void onCancel() {
				 PageParameters parameters = BasePage.appendWarnMessage(
							CompanyListPage.forCompany(company),
							new StringResourceModel("message.cancel", this, Model.of(company)));
					setResponsePage(CompanyListPage.class, parameters);
            }
		});
		

	}
	    
    
	@Override
	protected IModel<String> getPageTitleModel() {
		if (company.getId() == null) {
			return new StringResourceModel("page.title.new", this, null);
		} else {
			return new StringResourceModel("page.title.edit", this, Model.of(company));
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forUrl("assets/css/admin/user.css"));
	}
	
	 public static PageParameters linkToEdit(Company entity) {
	        return linkToEdit(entity.getId());
	    }
	    
	    public static PageParameters linkToEdit(Long entityId) {
	        return new PageParameters().set("id", entityId);
	    }

}
