package eu.uqasar.web.pages.search.resultpanels.tree;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.pages.search.resultpanels.AbstractSearchResultsPanel;
import javax.inject.Inject;

/**
 *
 *
 * @param <T>
 */
public abstract class AbstractTreeNodeSearchResultPanel<T extends AbstractEntity> extends AbstractSearchResultsPanel<T> {

    @Inject
    TreeNodeService service;
    
    public AbstractTreeNodeSearchResultPanel(String id, String searchTerm, Class<T> clazz) {
        super(id, searchTerm, clazz);
    }

    @Override
    protected <S extends AbstractService<T>> S getService() {
        return (S)service;
    }
}
