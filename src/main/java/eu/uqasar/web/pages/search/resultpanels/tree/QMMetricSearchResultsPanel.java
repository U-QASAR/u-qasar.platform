package eu.uqasar.web.pages.search.resultpanels.tree;

import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.web.pages.qmtree.metric.QMMetricEditPage;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;
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
public class QMMetricSearchResultsPanel extends AbstractQModelTreeNodeSearchResultPanel<QMMetric> {

    public QMMetricSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, QMMetric.class);
    }

    @Override
    protected void populateDataViewItem(Item<QMMetric> item) {
        final QMMetric entity = item.getModelObject();
        BookmarkablePageLink<QMMetricEditPage> link = new BookmarkablePageLink<>("link",
                QMMetricEditPage.class, QMMetricEditPage.forMetric(entity));
        link.add(new Label("name", Model.of(entity.getName()
                + " (" + entity.getNodeKey() + ")")).setRenderBodyOnly(true));
        item.add(link);
        
        BookmarkablePageLink<QModelViewPage> QMlink = new BookmarkablePageLink<>("qmodel",
        		QModelViewPage.class, QModelViewPage.forQModel(entity.getQModel()));
        QMlink.add(new Label("qmKey", Model.of(entity.getQModel().getNodeKey())));
        item.add(QMlink);
        
        
        item.add(new Label("value", Model.of(entity.getTargetValue())));
    }
}
