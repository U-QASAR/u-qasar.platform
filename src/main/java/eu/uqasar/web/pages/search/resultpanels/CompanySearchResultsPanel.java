package eu.uqasar.web.pages.search.resultpanels;

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


import eu.uqasar.model.company.Company;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.web.pages.admin.companies.CompanyEditPage;

import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class CompanySearchResultsPanel extends AbstractSearchResultsPanel<Company> {

    @Inject
    CompanyService service;

    public CompanySearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, Company.class);
    }

    @Override
    protected void populateDataViewItem(Item<Company> item) {
        final Company entity = item.getModelObject();
        BookmarkablePageLink<CompanyEditPage> link = new BookmarkablePageLink<>("link", 
        		CompanyEditPage.class, CompanyEditPage.linkToEdit(entity));
        link.add(new Label("name", Model.of(entity.getName())).setRenderBodyOnly(true));
        item.add(link);
        item.add(new Label("shortname", Model.of(entity.getShortName())));
        item.add(new Label("phone", Model.of(entity.getPhone())));
        item.add(new Label("country", Model.of(entity.getCountry())));
        item.add(new Label("fax", Model.of(entity.getFax())));
    }

    @Override
    protected <S extends AbstractService<Company>> S getService() {
        return (S) service;
    }
}
