package eu.uqasar.web.pages.search.resultpanels.tree;


import eu.uqasar.model.qmtree.QModel;

import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class QModelSearchResultsPanel extends AbstractQModelTreeNodeSearchResultPanel<QModel> {

	

    public QModelSearchResultsPanel(String id, String searchTerm) {
        super(id, searchTerm, QModel.class);
    }

    @Override
    protected void populateDataViewItem(Item<QModel> item) {
        final QModel entity = item.getModelObject();
        BookmarkablePageLink<QModelViewPage> link = new BookmarkablePageLink<>("link",
        		QModelViewPage.class, QModelViewPage.forQModel(entity));
        link.add(new Label("name", Model.of(entity.getName()
                + " (" + entity.getNodeKey() + ")")).setRenderBodyOnly(true));
        item.add(link);

        item.add(new Label("description", Model.of(entity.getDescription())));
        

    }

   

}
