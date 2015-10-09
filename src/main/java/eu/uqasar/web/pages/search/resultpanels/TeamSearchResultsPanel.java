package eu.uqasar.web.pages.search.resultpanels;



import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.user.TeamService;
import eu.uqasar.web.pages.admin.teams.TeamEditPage;
import eu.uqasar.web.pages.admin.teams.TeamListPage;

import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class TeamSearchResultsPanel extends AbstractSearchResultsPanel<Team> {

    @Inject
    TeamService service;
    
    public TeamSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, Team.class);
    }

    @Override
    protected void populateDataViewItem(Item<Team> item) {
        final Team entity = item.getModelObject();
        BookmarkablePageLink<TeamEditPage> link = new BookmarkablePageLink<>("link", 
                TeamEditPage.class, TeamEditPage.linkToEdit(entity));
        link.add(new Label("name", Model.of(entity.getName())).setRenderBodyOnly(true));
        item.add(link);
        
        item.add(new Label("description", Model.of(entity.getDescription())));
        
       
        item.add(new Label("member", Model.of(entity.getAllUsers())));
        
        
        
      
    }

    @Override
    protected <S extends AbstractService<Team>> S getService() {
        return (S) service;
    }
}
