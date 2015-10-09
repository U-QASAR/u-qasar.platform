package eu.uqasar.web.pages.search.resultpanels.tree;

import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveEditPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class QualityObjectiveSearchResultsPanel extends AbstractTreeNodeSearchResultPanel<QualityObjective> {

    public QualityObjectiveSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, QualityObjective.class);
    }

    @Override
    protected void populateDataViewItem(Item<QualityObjective> item) {
        final QualityObjective entity = item.getModelObject();
       BookmarkablePageLink<QualityObjectiveEditPage> link = new BookmarkablePageLink<>("link",
                QualityObjectiveEditPage.class, QualityObjectiveEditPage.forQualityObjective(entity));
        link.add(new Label("name", Model.of(entity.getName()
                + " (" + entity.getNodeKey() + ")")).setRenderBodyOnly(true));
        item.add(link);
        
        BookmarkablePageLink<ProjectViewPage> projectLink = new BookmarkablePageLink<>("project",
                ProjectViewPage.class, ProjectViewPage.forProject(entity.getProject()));
        projectLink.add(new Label("projectKey", Model.of(entity.getProject().getNodeKey())));
        item.add(projectLink);
        
        item.add(new Label("purpose", Model.of(entity.getQualityPurpose())));
        item.add(new Label("value", Model.of(entity.getValue())));
    }
}
