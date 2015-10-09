package eu.uqasar.web.pages.search.resultpanels.tree;

import javax.inject.Inject;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.service.AbstractService;
import eu.uqasar.service.tree.ProjectSearchService;
import eu.uqasar.web.pages.search.resultpanels.AbstractSearchResultsPanel;

/**
 *
 * @param <T>
 */
public abstract class AbstractProjectSearchResultPanel<T extends AbstractEntity> extends AbstractSearchResultsPanel<T> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
    ProjectSearchService service;
    
    public AbstractProjectSearchResultPanel(String id, String searchTerm, Class<T> clazz) {
        super(id, searchTerm, clazz);
    }

    @Override
    protected <S extends AbstractService<T>> S getService() {
        return (S)service;
    }
}
