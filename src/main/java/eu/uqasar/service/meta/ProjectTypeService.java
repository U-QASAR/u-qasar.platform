package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.ProjectType;
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
public class ProjectTypeService extends MetaDataService<ProjectType> {

    public ProjectTypeService() {
        super(ProjectType.class);
    }

    @Override
    public boolean isInUse(ProjectType entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.projectTypes);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.projectTypes);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }


    @Override
    public void delete(ProjectType entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        removeFromProject(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(ProjectType entity) {
        List<User> users = getUsersWithMetaData(entity, User_.projectTypes);
        for (User user : users) {
            user.getProjectTypes().remove(entity);
            em.merge(user);
        }
    }
    
    private void removeFromProject(ProjectType entity) {
        List<Project> projects = getProjectsWithMetaData(entity, Project_.projectTypes);
        for (Project project : projects) {
        	project.getProjectTypes().remove(entity);
            em.merge(project);
        }
    }
}
