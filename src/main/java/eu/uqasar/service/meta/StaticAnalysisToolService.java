package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.StaticAnalysisTool;
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
public class StaticAnalysisToolService extends MetaDataService<StaticAnalysisTool> {

    public StaticAnalysisToolService() {
        super(StaticAnalysisTool.class);
    }

    @Override
    public void delete(StaticAnalysisTool entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(StaticAnalysisTool entity) {
        List<User> users = getUsersWithMetaData(entity, User_.knownStaticAnalysisTools);
        for (User user : users) {
            user.getKnownStaticAnalysisTools().remove(entity);
            em.merge(user);
        }
    }
    
   

    @Override
    public boolean isInUse(StaticAnalysisTool entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.knownStaticAnalysisTools);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.staticAnalysisTools);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }
}
