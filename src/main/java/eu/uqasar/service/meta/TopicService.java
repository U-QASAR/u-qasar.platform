package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.Topic;
import eu.uqasar.model.qmtree.QMBaseIndicator_;
import eu.uqasar.model.tree.Project_;
import eu.uqasar.model.user.User;
import eu.uqasar.model.user.User_;

/**
 *
 *
 */
@Stateless
public class TopicService extends MetaDataService<Topic> {

    public TopicService() {
        super(Topic.class);
    }

    @Override
    public boolean isInUse(Topic entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.topics);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.topics);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }

    @Override
    public void delete(Topic entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(Topic entity) {
        List<User> users = getUsersWithMetaData(entity, User_.topics);
        for (User user : users) {
            user.getTopics().remove(entity);
            em.merge(user);
        }
    }
}
