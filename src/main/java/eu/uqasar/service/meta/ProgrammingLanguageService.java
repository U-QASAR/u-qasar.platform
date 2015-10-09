package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.ProgrammingLanguage;
import eu.uqasar.model.qmtree.QMBaseIndicator_;
import eu.uqasar.model.tree.Project_;
import eu.uqasar.model.user.User;
import eu.uqasar.model.user.User_;

/**
 *
 *
 */
@Stateless
public class ProgrammingLanguageService extends MetaDataService<ProgrammingLanguage> {

    public ProgrammingLanguageService() {
        super(ProgrammingLanguage.class);
    }

    @Override
    public void delete(ProgrammingLanguage entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(ProgrammingLanguage entity) {
        List<User> users = getUsersWithMetaData(entity, User_.knownProgrammingLanguages);
        for (User user : users) {
            user.getKnownProgrammingLanguages().remove(entity);
            em.merge(user);
        }
    }

    @Override
    public boolean isInUse(ProgrammingLanguage entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.knownProgrammingLanguages);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.programmingLanguages);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }
}
