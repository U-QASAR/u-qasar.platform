package eu.uqasar.web.pages.search.resultpanels;

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
