package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.TestManagementTool;
import eu.uqasar.model.qmtree.QMBaseIndicator_;
import eu.uqasar.model.tree.Project_;
import eu.uqasar.model.user.User;
import eu.uqasar.model.user.User_;

/**
 *
 *
 */
@Stateless
public class TestManagementToolService extends MetaDataService<TestManagementTool> {

    public TestManagementToolService() {
        super(TestManagementTool.class);
    }

    @Override
    public void delete(TestManagementTool entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(TestManagementTool entity) {
        List<User> users = getUsersWithMetaData(entity, User_.knownTestManagementTools);
        for (User user : users) {
            user.getKnownTestManagementTools().remove(entity);
            em.merge(user);
        }
    }

    @Override
    public boolean isInUse(TestManagementTool entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.knownTestManagementTools);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.testManagementTools);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }
}
