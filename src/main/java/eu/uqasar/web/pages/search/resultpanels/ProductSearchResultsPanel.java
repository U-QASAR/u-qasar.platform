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


import eu.uqasar.model.product.Product;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.ProductService;
import eu.uqasar.web.pages.products.ProductAddEditPage;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class ProductSearchResultsPanel extends AbstractSearchResultsPanel<Product> {

    @Inject
    ProductService service;

    public ProductSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, Product.class);
    }

    @Override
    protected void populateDataViewItem(Item<Product> item) {
        final Product entity = item.getModelObject();
        BookmarkablePageLink<Product> link = new BookmarkablePageLink<>("link", 
                ProductAddEditPage.class, ProductAddEditPage.linkToEdit(entity));
        link.add(new Label("name", Model.of(entity.getName())).setRenderBodyOnly(true));
        item.add(link);
        
        item.add(new Label("description", Model.of(entity.getDescription())));
        item.add(new Label("version", Model.of(entity.getVersion())));
        item.add(new Label("date", Model.of(entity.getReleaseDate())));
    }

    @Override
    protected <S extends AbstractService<Product>> S getService() {
        return (S) service;
    }
}
