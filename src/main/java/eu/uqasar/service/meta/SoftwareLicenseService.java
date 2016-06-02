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

import eu.uqasar.model.meta.SoftwareLicense;
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
public class SoftwareLicenseService extends MetaDataService<SoftwareLicense> {

    public SoftwareLicenseService() {
        super(SoftwareLicense.class);
    }

    @Override
    public boolean isInUse(SoftwareLicense entity) {
        long usersWithTool = countUsersWithMetaData(entity, User_.softwareLicenses);
        long projectwithTool = countprojectsWithMetaData(entity, Project_.softwareLicenses);
        long qowithTool = countQOWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qiwithTool = countQIWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        long qmetricwithTool = countQMetricWithMetaData(entity, QMBaseIndicator_.qModelTagData);
        return (usersWithTool > 0 || projectwithTool >0 ||  qowithTool>0 || qiwithTool>0 || qmetricwithTool>0);
    }

    @Override
    public void delete(SoftwareLicense entity) {
        removeFromUserSkills(entity);
        removefromQM(entity);
        removeFromProject(entity);
        super.delete(entity);
    }

    private void removeFromUserSkills(SoftwareLicense entity) {
        List<User> users = getUsersWithMetaData(entity, User_.softwareLicenses);
        for (User user : users) {
            user.getSoftwareLicenses().remove(entity);
            em.merge(user);
        }
    }
    
    private void removeFromProject(SoftwareLicense entity) {
        List<Project> projects = getProjectsWithMetaData(entity, Project_.softwareLicenses);
        for (Project project : projects) {
        	project.getSoftwareLicenses().remove(entity);
            em.merge(project);
        }
    }
}
