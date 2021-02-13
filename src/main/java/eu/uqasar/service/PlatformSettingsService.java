package eu.uqasar.service;

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


import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import eu.uqasar.model.settings.platform.PlatformSettings;
import eu.uqasar.model.settings.platform.PlatformSettings_;

@Stateless
public class PlatformSettingsService extends AbstractService<PlatformSettings> {

    public PlatformSettingsService() {
        super(PlatformSettings.class);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @return
     */
    public List<PlatformSettings> getAllProjectSettings() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PlatformSettings> query = cb.createQuery(PlatformSettings.class);
        query.from(PlatformSettings.class);
        return em.createQuery(query).getResultList();
    }

    /**
     *
     */
    public void delete(Collection<PlatformSettings> settings) {
        for (PlatformSettings setting : settings) {
            delete(setting);
        }
    }

    /**
     * 
     * @return
     */
    public boolean ProjectSettingsExists(Long projectSettingId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<PlatformSettings> from = criteria.from(PlatformSettings.class);
        criteria.where(cb.equal(from.get(PlatformSettings_.id), projectSettingId));
        criteria.select(cb.countDistinct(from));
        return (em.createQuery(criteria).getSingleResult() == 1);
    }

    public List<PlatformSettings> getAllByAscendingName(int first, int count) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PlatformSettings> criteria = cb.createQuery(PlatformSettings.class);
        Root<PlatformSettings> root = criteria.from(PlatformSettings.class);
        criteria.orderBy(cb.asc(root.get(PlatformSettings_.settingKey)));
        return em.createQuery(criteria).setFirstResult(first).setMaxResults(count).getResultList();
    }

    public String getValueByKey(String key) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PlatformSettings> criteria = cb.createQuery(PlatformSettings.class);
        Root<PlatformSettings> root = criteria.from(PlatformSettings.class);
        criteria.where(cb.equal(root.get(PlatformSettings_.settingKey), key));
        return em.createQuery(criteria).getSingleResult().getSettingValue();
    }
}
