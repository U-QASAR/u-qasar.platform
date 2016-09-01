/*
 */
package eu.uqasar.web.pages.admin.companies;

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


import javax.inject.Inject;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.company.Company;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.pages.admin.companies.panels.CompanyEditPanel;

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
	    
	    private static PageParameters linkToEdit(Long entityId) {
	        return new PageParameters().set("id", entityId);
	    }

}
