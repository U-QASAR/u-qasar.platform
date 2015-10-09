package eu.uqasar.web.pages.search.resultpanels;

import eu.uqasar.model.company.Company;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.web.pages.admin.companies.CompanyEditPage;
import eu.uqasar.web.pages.admin.companies.CompanyListPage;
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
