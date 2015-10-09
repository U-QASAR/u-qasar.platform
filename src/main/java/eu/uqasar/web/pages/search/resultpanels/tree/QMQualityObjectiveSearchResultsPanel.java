package eu.uqasar.web.pages.search.resultpanels.tree;

import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;
import eu.uqasar.web.pages.qmtree.quality.objective.QMQualityObjectiveEditPage;
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
public class QMQualityObjectiveSearchResultsPanel extends AbstractQModelTreeNodeSearchResultPanel<QMQualityObjective> {

    public QMQualityObjectiveSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, QMQualityObjective.class);
    }

    @Override
    protected void populateDataViewItem(Item<QMQualityObjective> item) {
        final QMQualityObjective entity = item.getModelObject();
       BookmarkablePageLink<QMQualityObjectiveEditPage> link = new BookmarkablePageLink<>("link",
                QMQualityObjectiveEditPage.class, QMQualityObjectiveEditPage.forQualityObjective(entity));
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
