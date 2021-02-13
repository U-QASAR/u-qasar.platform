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
