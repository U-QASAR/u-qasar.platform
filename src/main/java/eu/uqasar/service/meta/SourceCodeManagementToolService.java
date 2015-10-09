package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.SourceCodeManagementTool;
import eu.uqasar.model.qmtree.QMBaseIndicator_;
import eu.uqasar.model.tree.Project_;
import eu.uqasar.model.user.User;
import eu.uqasar.model.user.User_;

/**
 *
 *
 */
@Stateless
public class SourceCodeManagementToolService extends MetaDataService<SourceCodeManagementTool> {

    public SourceCodeManagementToolService() {
        super(SourceCodeManagementTool.class);
    }

    @Override
    public void delete(SourceCodeManagementTool entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(SourceCodeManagementTool entity) {
        List<User> users = getUsersWithMetaData(entity, User_.knownSourceCodeManagementTools);
        for (User user : users) {
            user.getKnownSourceCodeManagementTools().remove(entity);
            em.merge(user);
        }
    }

    @Override
    public boolean isInUse(SourceCodeManagementTool entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.knownSourceCodeManagementTools);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.sourceCodeManagementTools);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }
}
