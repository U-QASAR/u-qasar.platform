package eu.uqasar.service.meta;

import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.IssueTrackingTool;
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
        super.delete(entity);
    }

    private void removeFromUserSkills(IssueTrackingTool entity) {
        List<User> users = getUsersWithMetaData(entity, User_.knownIssueTrackingTools);
        for (User user : users) {
            user.getKnownIssueTrackingTools().remove(entity);
            em.merge(user);
        }
    }

    @Override
    public boolean isInUse(IssueTrackingTool entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.knownIssueTrackingTools);
        return usersWithTool > 0;
    }
}
