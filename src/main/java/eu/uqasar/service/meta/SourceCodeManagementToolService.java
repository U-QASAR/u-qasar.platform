package eu.uqasar.service.meta;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.List;

import javax.ejb.Stateless;

import eu.uqasar.model.meta.SourceCodeManagementTool;
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
public class SourceCodeManagementToolService extends MetaDataService<SourceCodeManagementTool> {

    public SourceCodeManagementToolService() {
        super(SourceCodeManagementTool.class);
    }

    @Override
    public void delete(SourceCodeManagementTool entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        removeFromProject(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(SourceCodeManagementTool entity) {
        List<User> users = getUsersWithMetaData(entity, User_.knownSourceCodeManagementTools);
        for (User user : users) {
            user.getKnownSourceCodeManagementTools().remove(entity);
            em.merge(user);
        }
    }

    private void removeFromProject(SourceCodeManagementTool entity) {
        List<Project> projects = getProjectsWithMetaData(entity, Project_.sourceCodeManagementTools);
        for (Project project : projects) {
        	project.getSourceCodeManagementTools().remove(entity);
            em.merge(project);
        }
    }

    @Override
    public boolean isInUse(SourceCodeManagementTool entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.knownSourceCodeManagementTools);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.sourceCodeManagementTools);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }
}
