package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.IssueTrackingTool;
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
public class IssueTrackingToolService extends MetaDataService<IssueTrackingTool> {

    public IssueTrackingToolService() {
        super(IssueTrackingTool.class);
    }

    @Override
    public void delete(IssueTrackingTool entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        removeFromProject(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(IssueTrackingTool entity) {
        List<User> users = getUsersWithMetaData(entity, User_.knownIssueTrackingTools);
        for (User user : users) {
            user.getKnownIssueTrackingTools().remove(entity);
            em.merge(user);
        }
    }

    private void removeFromProject(IssueTrackingTool entity) {
        List<Project> projects = getProjectsWithMetaData(entity, Project_.issueTrackingTools);
        for (Project project : projects) {
        	project.getIssueTrackingTools().remove(entity);
            em.merge(project);
        }
    }

    @Override
    public boolean isInUse(IssueTrackingTool entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.knownIssueTrackingTools);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.issueTrackingTools);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }
}
