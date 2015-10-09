package eu.uqasar.web.pages.search.resultpanels.tree;

import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;
import eu.uqasar.web.pages.qmtree.quality.indicator.QMQualityIndicatorEditPage;
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
public class QMQualityIndicatorSearchResultsPanel extends AbstractQModelTreeNodeSearchResultPanel<QMQualityIndicator> {

    public QMQualityIndicatorSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, QMQualityIndicator.class);
    }

    @Override
    protected void populateDataViewItem(Item<QMQualityIndicator> item) {
        final QMQualityIndicator entity = item.getModelObject();
        BookmarkablePageLink<QMQualityIndicatorEditPage> link = new BookmarkablePageLink<>("link",
                QMQualityIndicatorEditPage.class, QMQualityIndicatorEditPage.forQualityIndicator(entity));
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
