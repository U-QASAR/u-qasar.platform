package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.SoftwareDevelopmentMethodology;
import eu.uqasar.model.qmtree.QMBaseIndicator_;
import eu.uqasar.model.tree.Project_;
import eu.uqasar.model.user.User;
import eu.uqasar.model.user.User_;

@Stateless
public class SoftwareDevelopmentMethodologyService extends MetaDataService<SoftwareDevelopmentMethodology> {

    public SoftwareDevelopmentMethodologyService() {
        super(SoftwareDevelopmentMethodology.class);
    }
    
    @Override
    public boolean isInUse(SoftwareDevelopmentMethodology entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.softwareDevelopmentMethodologies);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.softwareDevelopmentMethodologies);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }
    
    @Override
    public void delete(SoftwareDevelopmentMethodology entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(SoftwareDevelopmentMethodology entity) {
        List<User> users = getUsersWithMetaData(entity, User_.softwareDevelopmentMethodologies);
        for (User user : users) {
            user.getSoftwareDevelopmentMethodologies().remove(entity);
            em.merge(user);
        }
    }
    
}
