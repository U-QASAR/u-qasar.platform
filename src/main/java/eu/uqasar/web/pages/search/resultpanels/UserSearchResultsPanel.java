package eu.uqasar.web.pages.search.resultpanels;

import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.pages.admin.users.UserEditPage;
import eu.uqasar.web.pages.search.resultpanels.AbstractSearchResultsPanel;
import eu.uqasar.web.pages.user.UserPage;


public class UserSearchResultsPanel extends AbstractSearchResultsPanel<User> {

    @Inject
    UserService service;

    public UserSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, User.class);
    }

    @Override
    protected void populateDataViewItem(Item<User> item) {
        final User entity = item.getModelObject();
        BookmarkablePageLink<User> link = new BookmarkablePageLink<>("link", 
                UserEditPage.class, UserEditPage.linkToEdit(entity));
        link.add(new Label("name", Model.of(entity.getFullNameWithUserName())).setRenderBodyOnly(true));
        item.add(link);
        
        item.add(new Label("role", Model.of(entity.getRole())));
        item.add(new Label("email", Model.of(entity.getMail())));
     
    }

    @Override
    protected <S extends AbstractService<User>> S getService() {
    	return (S) service;
    }
}
