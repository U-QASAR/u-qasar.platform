package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.SoftwareType;
import eu.uqasar.model.qmtree.QMBaseIndicator_;
import eu.uqasar.model.tree.Project_;
import eu.uqasar.model.user.User;
import eu.uqasar.model.user.User_;

/**
 *
 *
 */
@Stateless
public class SoftwareTypeService extends MetaDataService<SoftwareType> {

    public SoftwareTypeService() {
        super(SoftwareType.class);
    }

    @Override
    public boolean isInUse(SoftwareType entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.softwareTypes);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.softwareTypes);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }

    @Override
    public void delete(SoftwareType entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(SoftwareType entity) {
        List<User> users = getUsersWithMetaData(entity, User_.softwareTypes);
        for (User user : users) {
            user.getSoftwareTypes().remove(entity);
            em.merge(user);
        }
    }
}
