package eu.uqasar.web.pages.search.resultpanels.tree;

import eu.uqasar.model.tree.Metric;
import eu.uqasar.web.pages.tree.metric.MetricEditPage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class MetricSearchResultsPanel extends AbstractTreeNodeSearchResultPanel<Metric> {

    public MetricSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, Metric.class);
    }

    @Override
    protected void populateDataViewItem(Item<Metric> item) {
        final Metric entity = item.getModelObject();
        BookmarkablePageLink<MetricEditPage> link = new BookmarkablePageLink<>("link",
                MetricEditPage.class, MetricEditPage.forMetric(entity));
        link.add(new Label("name", Model.of(entity.getName()
                + " (" + entity.getNodeKey() + ")")).setRenderBodyOnly(true));
        item.add(link);
        
        BookmarkablePageLink<ProjectViewPage> projectLink = new BookmarkablePageLink<>("project",
                ProjectViewPage.class, ProjectViewPage.forProject(entity.getProject()));
        projectLink.add(new Label("projectKey", Model.of(entity.getProject().getNodeKey())));
        item.add(projectLink);
        
        item.add(new Label("source", Model.of(entity.getMetricSource())));
        item.add(new Label("value", Model.of(entity.getValue())));
    }
}
