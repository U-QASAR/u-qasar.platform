package eu.uqasar.web.provider.meta;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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

    private final Class<T> clazz;

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
