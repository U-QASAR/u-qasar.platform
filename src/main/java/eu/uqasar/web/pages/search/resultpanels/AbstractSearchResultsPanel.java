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


import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.AbstractEntity;
import eu.uqasar.service.AbstractService;
import eu.uqasar.web.provider.EntityProvider;
import java.util.Iterator;
import java.util.List;

import lombok.NoArgsConstructor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public abstract class AbstractSearchResultsPanel<T extends AbstractEntity> extends Panel {

    private String searchTerm;
    private final Class<T> clazz;
    private final WebMarkupContainer container = new WebMarkupContainer("container");
    private DataView<T> entities;
    private final WebMarkupContainer tableFoot = new WebMarkupContainer("tableFoot");

    private final int itemsPerPage = 10;

    protected AbstractSearchResultsPanel(String id, String searchTerm, Class<T> clazz) {
        super(id);
        this.clazz = clazz;
        this.searchTerm = searchTerm;
        add(container.setOutputMarkupId(true));
        container.add(new CssClassNameAppender("search-results"));
        container.add(tableFoot);
        setRenderBodyOnly(true);
    }

    public AbstractSearchResultsPanel<T> setSearchTerm(final String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    private int getItemsPerPage() {
        return this.itemsPerPage;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        entities = new DataView<T>("entities", getEntityProvider(), getItemsPerPage()) {
            private static final long serialVersionUID = 1918076240043442975L;

            @Override
            protected void populateItem(final Item<T> item) {
                populateDataViewItem(item);
            }
        };
        container.add(entities);
        container.add(new Label("table.head", new StringResourceModel("panel.title", this, null, Model.of(searchTerm))));
        tableFoot.add(new BootstrapAjaxPagingNavigator("navigatorFoot",
                entities));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (this.entities.getItemCount() == 0) {
            container.setVisible(false);
        }
        if(this.entities.getItemCount() <= getItemsPerPage()) {
            tableFoot.setVisible(false);
        }
    }

    private EntityProvider<T> getEntityProvider() {
        return new EntityProvider<T>() {

            @Override
            public Iterator<? extends T> iterator(long first, long count) {
                final String[] terms = AbstractService.
                        prepareSearchTerm(searchTerm);
                List<T> entities = getService().
                        fullTextSearch(clazz, Long.
                                valueOf(first).intValue(),
                                Long.valueOf(count).intValue(), terms);
                return entities.iterator();
            }

            @Override
            public long size() {
                final String[] terms = AbstractService.
                        prepareSearchTerm(searchTerm);
                return getService().
                        countFullTextResults(clazz, terms);
            }
        };
    }

    protected abstract void populateDataViewItem(final Item<T> item);

    protected abstract <S extends AbstractService<T>> S getService();

}
