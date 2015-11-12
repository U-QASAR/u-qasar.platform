package eu.uqasar.service.tree;

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


import javax.ejb.Stateless;

import org.hibernate.search.query.dsl.TermMatchingContext;
import org.hibernate.search.query.dsl.WildcardContext;

import eu.uqasar.model.tree.Project;
import eu.uqasar.service.AbstractService;

/**
 *
 *
 */
@Stateless
public class ProjectSearchService extends AbstractService<Project> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProjectSearchService() {
		super(Project.class);
	}

	@Override
    protected <P extends Project> TermMatchingContext setFullTextSearchFields(WildcardContext wc, Class<P> clazz) {
        if (Project.class.isAssignableFrom(clazz)) {
            return wc.onField("name").
                    andField("description").
                    andField("nodeKey").
                    andField("children.name").
                    andField("children.description").
                    andField("children.nodeKey").
                    andField("programmingLanguages_name").
                    andField("topics_name").
                    andField("customerTypes_name").
                    andField("projectTypes_name").
                    andField("softwareTypes_name").
                    andField("softwareLicenses_name").
                    andField("issueTrackingTools_name").
                    andField("sourceCodeManagementTools_name").
                    andField("staticAnalysisTools_name").
                    andField("testManagementTools_name").
                    andField("continuousIntegrationTools_name").
                    andField("softwareDevelopmentMethodologies_name");            
        } else {
            return super.setFullTextSearchFields(wc, clazz);
        }
	}
}
