package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;
import eu.uqasar.model.meta.CustomerType;
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
public class CustomerTypeService extends MetaDataService<CustomerType> {

    public CustomerTypeService() {
        super(CustomerType.class);
    }

    @Override
    public boolean isInUse(CustomerType entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.customerTypes);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.customerTypes);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }
    
    @Override
    public void delete(CustomerType entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        removeFromProject(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(CustomerType entity) {
        List<User> users = getUsersWithMetaData(entity, User_.customerTypes);
        for (User user : users) {
            user.getCustomerTypes().remove(entity);
            em.merge(user);
        }
    }
    
    private void removeFromProject(CustomerType entity) {
        List<Project> projects = getProjectsWithMetaData(entity, Project_.customerTypes);
        for (Project project : projects) {
        	project.getCustomerTypes().remove(entity);
            em.merge(project);
        }
    }
}
