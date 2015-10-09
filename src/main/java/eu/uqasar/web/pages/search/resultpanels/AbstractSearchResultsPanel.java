package eu.uqasar.web.pages.search.resultpanels;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.AbstractEntity;
import eu.uqasar.service.AbstractService;
import eu.uqasar.web.provider.EntityProvider;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 *
 * @param <T>
 */
public abstract class AbstractSearchResultsPanel<T extends AbstractEntity> extends Panel {

    protected String searchTerm;
    protected final Class<T> clazz;
    private  WebMarkupContainer container = new WebMarkupContainer("container");
    private DataView<T> entities;
    private WebMarkupContainer tableFoot = new WebMarkupContainer("tableFoot");

    protected int itemsPerPage = 10;

    public AbstractSearchResultsPanel(String id, String searchTerm, Class<T> clazz) {
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

    public int getItemsPerPage() {
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

    protected EntityProvider<T> getEntityProvider() {
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
                long count = getService().
                        countFullTextResults(clazz, terms);
                return count;
            }
        };
    }

    protected abstract void populateDataViewItem(final Item<T> item);

    protected abstract <S extends AbstractService<T>> S getService();

}
