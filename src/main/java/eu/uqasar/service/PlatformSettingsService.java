package eu.uqasar.service;

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
        List<PlatformSettings> resultList = em.createQuery(query).getResultList();
        return resultList;
    }

    /**
     * 
     * @param processes
     */
    public void delete(Collection<PlatformSettings> settings) {
        for (PlatformSettings setting : settings) {
            delete(setting);
        }
    }

    /**
     * 
     * @param processId
     * @return
     */
    public boolean ProjectSettingsExists(Long projectSettingId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<PlatformSettings> from = criteria.from(PlatformSettings.class);
        criteria.where(cb.equal(from.get(PlatformSettings_.id), projectSettingId));
        criteria.select(cb.countDistinct(from));
        return (em.createQuery(criteria).getSingleResult().longValue() == 1);
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
