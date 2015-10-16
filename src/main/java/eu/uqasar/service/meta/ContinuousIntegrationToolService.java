package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.ContinuousIntegrationTool;
import eu.uqasar.model.qmtree.QMBaseIndicator_;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.Project_;
import eu.uqasar.model.user.User;
import eu.uqasar.model.user.User_;

/**
 *
 *
 */
@Stateless
public class ContinuousIntegrationToolService extends MetaDataService<ContinuousIntegrationTool> {

    public ContinuousIntegrationToolService() {
        super(ContinuousIntegrationTool.class);
    }

    @Override
    public void delete(ContinuousIntegrationTool entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        removeFromProject(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(ContinuousIntegrationTool entity) {
        List<User> users = getUsersWithMetaData(entity, User_.knownContinuousIntegrationTools);
        for (User user : users) {
            user.getKnownContinuousIntegrationTools().remove(entity);
            em.merge(user);
        }
    }

    private void removeFromProject(ContinuousIntegrationTool entity) {
        List<Project> projects = getProjectsWithMetaData(entity, Project_.continuousIntegrationTools);
        for (Project project : projects) {
        	project.getContinuousIntegrationTools().remove(entity);
            em.merge(project);
        }
    }

    @Override
    public boolean isInUse(ContinuousIntegrationTool entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.knownContinuousIntegrationTools);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.continuousIntegrationTools);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);   
    }
}
