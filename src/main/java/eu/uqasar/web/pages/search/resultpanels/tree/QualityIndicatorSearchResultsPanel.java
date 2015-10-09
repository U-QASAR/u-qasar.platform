package eu.uqasar.web.pages.search.resultpanels.tree;

import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorEditPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class QualityIndicatorSearchResultsPanel extends AbstractTreeNodeSearchResultPanel<QualityIndicator> {

    public QualityIndicatorSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, QualityIndicator.class);
    }

    @Override
    protected void populateDataViewItem(Item<QualityIndicator> item) {
        final QualityIndicator entity = item.getModelObject();
        BookmarkablePageLink<QualityIndicatorEditPage> link = new BookmarkablePageLink<>("link",
                QualityIndicatorEditPage.class, QualityIndicatorEditPage.forQualityIndicator(entity));
        link.add(new Label("name", Model.of(entity.getName()
                + " (" + entity.getNodeKey() + ")")).setRenderBodyOnly(true));
        item.add(link);
        
        BookmarkablePageLink<ProjectViewPage> projectLink = new BookmarkablePageLink<>("project",
                ProjectViewPage.class, ProjectViewPage.forProject(entity.getProject()));
        projectLink.add(new Label("projectKey", Model.of(entity.getProject().getNodeKey())));
        item.add(projectLink);
        
        item.add(new Label("value", Model.of(entity.getValue())));
    }
}
