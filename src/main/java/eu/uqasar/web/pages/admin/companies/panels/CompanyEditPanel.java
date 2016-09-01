/*
 */
package eu.uqasar.web.pages.admin.companies.panels;

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

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import eu.uqasar.model.company.Company;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;

/**
 *
 *
 */
public abstract class CompanyEditPanel extends Panel {

	@Inject private CompanyService companyService; 

	private final Company company;
    private final TextField<String> name;
    private final TextField<String> shortName;
    private final TextField<String> street;
    private final TextField<Integer> zipcode;
    private final TextField<String> city;
    private final TextField<String> country;
    private final TextField<String> phone;
    private final TextField<String> fax;


	public CompanyEditPanel(final String id, final Company company) {
		super(id);
		this.company = company;
		
		final Form<Company> form = new Form<>("form", Model.of(company));
		
		form.setOutputMarkupId(true);

		name = new TextField<>("name", new PropertyModel<String>(company, "name")); 
		form.add(new OnEventInputBeanValidationBorder<>("nameValidationBorder", name, new StringResourceModel("label.name", this, null), HtmlEvent.ONBLUR));
		
		shortName = new TextField<>("shortName", new PropertyModel<String>(company, "shortName")); 
		form.add(new OnEventInputBeanValidationBorder<>("shortNameValidationBorder", shortName, new StringResourceModel("label.shortName", this, null), HtmlEvent.ONBLUR));
		
		street = new TextField<>("street", new PropertyModel<String>(company, "street")); 
		form.add(new OnEventInputBeanValidationBorder<>("streetValidationBorder", street, new StringResourceModel("label.street", this, null), HtmlEvent.ONBLUR));
		
		zipcode = new TextField<>("zipcode", new PropertyModel<Integer>(company, "zipcode")); 
		form.add(new OnEventInputBeanValidationBorder<>("zipcodeValidationBorder", zipcode, new StringResourceModel("label.zipcode", this, null), HtmlEvent.ONBLUR));
		
		city = new TextField<>("city", new PropertyModel<String>(company, "city")); 
		form.add(new OnEventInputBeanValidationBorder<>("cityValidationBorder", city, new StringResourceModel("label.city", this, null), HtmlEvent.ONBLUR));
		
		country = new TextField<>("country", new PropertyModel<String>(company, "country")); 
		form.add(new OnEventInputBeanValidationBorder<>("countryValidationBorder", country, new StringResourceModel("label.country", this, null), HtmlEvent.ONBLUR));
		
		phone = new TextField<>("phone", new PropertyModel<String>(company, "phone")); 
		form.add(new OnEventInputBeanValidationBorder<>("phoneValidationBorder", phone, new StringResourceModel("label.phone", this, null), HtmlEvent.ONBLUR));
		
		fax = new TextField<>("fax", new PropertyModel<String>(company, "fax")); 
		form.add(new OnEventInputBeanValidationBorder<>("faxValidationBorder", fax, new StringResourceModel("label.fax", this, null), HtmlEvent.ONBLUR));
       
		form.add(new SubmitLink("submitForm"){
	     	@Override
            public void onSubmit() {
	     		CompanyEditPanel.this.onSubmit(company);
			}
		});
		form.add(new SubmitLink("cancel"){
            @Override
            public void onSubmit() {        
                CompanyEditPanel.this.onCancel();
            }
        });
		
		add(form);
			
	}
   
	public abstract void onSubmit(Company company);
    public abstract void onCancel();

	@Override
	protected void onAfterRender() {
		super.onAfterRender();
		// detach entity to avoid automatic update of changes in form.
		companyService.detach(company);
	}
}
