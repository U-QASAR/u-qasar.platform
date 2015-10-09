package eu.uqasar.web.provider.meta;

import eu.uqasar.model.meta.MetaData;
import eu.uqasar.service.meta.MetaDataService;
import eu.uqasar.web.provider.AbstractEntityChoiceProvider;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.wicket.cdi.CdiContainer;

/**
 *
 *
 * @param <T>
 */
public class MetaDataCreateMissingEntitiesChoiceProvider<T extends MetaData> extends AbstractEntityChoiceProvider<T> {

    private Class<T> clazz;

    @Inject
    @Named(MetaDataService.NAME)
    private MetaDataService metaDataService;

    public MetaDataCreateMissingEntitiesChoiceProvider(List<T> entities, Class<T> clazz) {
        super(entities);
        CdiContainer.get().getNonContextualManager().inject(this);
        this.clazz = clazz;
    }

    @Override
    public Collection<T> toChoices(Collection<String> ids) {
        return metaDataService.
                getExistingMetaDataAndCreateMissingFromChoicesString(clazz, ids);
    }

    @Override
    protected boolean queryMatches(T entity, String term) {
        return entity.getName().toUpperCase().contains(term.toUpperCase());
    }

    @Override
    protected String getDisplayText(T t) {
        return t.getName();
    }
}
