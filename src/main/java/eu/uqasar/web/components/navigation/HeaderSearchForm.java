package eu.uqasar.web.components.navigation;

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


import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarForm;
import eu.uqasar.web.pages.search.SearchResultsPage;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.jboss.solder.logging.Logger;

public class HeaderSearchForm extends NavbarForm<String> {

	@Inject Logger logger;
	
	private static final long serialVersionUID = -8567136594700555997L;

    private TextField searchField;
	private String searchQuery;

	public HeaderSearchForm(String componentId, final String searchQuery) {
		super(componentId);
        this.searchQuery = searchQuery;
	}

    @Override
    protected void onConfigure() {
        super.onConfigure();
        getParent().add(new AttributeModifier("id", "searchMenu"));
        add(searchField = new TextField<>("searchQuery", new PropertyModel<String>(
				this, "searchQuery")));
    }

	/**
	 * @param searchQuery
	 *            the searchQuery to set
	 */
	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
        searchField.setModel(new PropertyModel<String>(this, "searchQuery"));
	}

	@Override
	protected void onSubmit() {
		search();
	}

	/**
	 * 
	 */
	private void search() {
		logger.infof("searching for '%s'", searchQuery);
        setResponsePage(
				SearchResultsPage.class, SearchResultsPage.searchFor(searchQuery));
	}
}
