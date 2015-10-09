package eu.uqasar.web.pages.search.resultpanels.tree;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

import eu.uqasar.model.tree.Project;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.tree.ProjectSearchService;
import eu.uqasar.web.pages.search.resultpanels.AbstractSearchResultsPanel;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;

/**
 *
 *
 */
public class ProjectSearchResultsPanel extends AbstractSearchResultsPanel<Project> {

	@Inject 
	ProjectSearchService service;
	
    public ProjectSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, Project.class);
    }

    @Override
    protected void populateDataViewItem(Item<Project> item) {
        // TODO add quality indicator icon!
        final Project entity = item.getModelObject();
        BookmarkablePageLink<ProjectViewPage> link = new BookmarkablePageLink<>("link",
                ProjectViewPage.class, ProjectViewPage.forProject(entity));
        link.add(new Label("name", Model.of(entity.getName()
                + " (" + entity.getNodeKey() + ")")).setRenderBodyOnly(true));
        item.add(link);

        item.add(new Label("startdate", Model.of(entity.getStartDate())));
        item.add(new Label("enddate", Model.of(entity.getEndDate())));
        item.add(new Label("progress", Model.of(entity.
                getElapsedVsRemainingWithPercentageString())));
    }

	@Override
	protected <S extends AbstractService<Project>> S getService() {
		return (S) service;
	}
}
