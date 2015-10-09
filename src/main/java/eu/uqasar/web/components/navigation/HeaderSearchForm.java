package eu.uqasar.web.components.navigation;

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
