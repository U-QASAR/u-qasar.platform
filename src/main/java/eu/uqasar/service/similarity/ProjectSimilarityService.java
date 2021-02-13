package eu.uqasar.service.similarity;

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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import eu.uqasar.model.tree.Project;
import eu.uqasar.service.AbstractService;

@Stateless
public class ProjectSimilarityService extends AbstractService<Project> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1867993817677767380L;

	private final Multimap<Project, String> matchedProjectProperties = ArrayListMultimap.create();
	private final List<Project> similarProjects = new ArrayList<>();


	public ProjectSimilarityService() {
		super(Project.class);
	}

	
	/**
	 * Get a ranked list of best suited projects based on the context data. 
	 * @param proj Project to which the other projects are compared
	 *  
	 */
	public List<Project> getSimilarProjects(final Project proj) {
		
		// Clear the previous items from the list of similar projects
		similarProjects.clear();
		
		// Compute the similarity
		computeProjectSimilarity(proj);
		
		// Get a list of similar projects 
		return getProjectsByDescendingSimilarityValue();
	}
	
	
	
	/**
	 * Compute basic similarity of a project compared to the others
	 * TODO: Use cosine similarity for comparing other fields 
	 * TODO: Use only projects the user is entitled to access
	 * @param proj The project for which a similar project is searched for
	 * @return
	 */
	private void computeProjectSimilarity(final Project proj) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> query = cb.createQuery(Project.class);
		query.from(Project.class);

		// Obtain all the projects
		List<Project> projects = em.createQuery(query).getResultList();
		if (projects != null && projects.size() > 0) {
			// Iterate the projects and compute similarity
			for (Project project : projects) {
				// Do not compare the project with itself
				if (project != proj)  {					
					// Compare the project metadata  
					logger.debugf("Comparing project " +project.getName() + " with " +proj.getName());
					matchProjectMetadata(project, project.getCustomerTypes(), proj.getCustomerTypes());
					matchProjectMetadata(project, project.getProjectTypes(), proj.getProjectTypes());
					matchProjectMetadata(project, project.getSoftwareTypes(), proj.getSoftwareTypes());
					matchProjectMetadata(project, project.getSoftwareLicenses(), proj.getSoftwareLicenses());
					matchProjectMetadata(project, project.getTopics(), proj.getTopics());
					matchProjectMetadata(project, project.getProgrammingLanguages(), proj.getProgrammingLanguages());
					matchProjectMetadata(project, project.getIssueTrackingTools(), proj.getIssueTrackingTools());
					matchProjectMetadata(project, project.getSourceCodeManagementTools(), proj.getSourceCodeManagementTools());
					matchProjectMetadata(project, project.getStaticAnalysisTools(), proj.getStaticAnalysisTools());
					matchProjectMetadata(project, project.getTestManagementTools(), proj.getTestManagementTools());
					matchProjectMetadata(project, project.getContinuousIntegrationTools(), proj.getContinuousIntegrationTools());
					matchProjectMetadata(project, project.getSoftwareDevelopmentMethodologies(), proj.getSoftwareDevelopmentMethodologies());
					logger.debugf("Project similarity value after the computations: " +project.getSimilarityValue());
					similarProjects.add(project);
				}
			}
		}
	}
	
	
	/**
     * Compare the project metadata expressed as tags and compute a similarity 
     * value for the project compared. 
     * @param project
     * @param metadataGiven
     * @param metadataNeeded
     */
	private <T> void matchProjectMetadata(Project project, Set<T> metadataGiven, Set<T> metadataNeeded){

		logger.debugf("Project similarity before: " +project.getSimilarityValue());
		
		for(T providedMetadata : metadataGiven){
			for(T requiredMetadata : metadataNeeded){
				if(providedMetadata.equals(requiredMetadata)){		
					matchedProjectProperties.put(project, metadataNeeded.toString());
					project.incrementSimilarityValue();
				}
			}	
		}
		logger.debugf("Project similarity after: " +project.getSimilarityValue());
	}
	
	
	/**
	 * Obtain a list of similar projects ordered according to 
	 * the computed similarity value.
	 * @return Sorted list of projects
	 */
    private List<Project> getProjectsByDescendingSimilarityValue() {

		logger.debugf("Sorting projects to a descending similarity value...");
		if (similarProjects != null && similarProjects.size() > 0) {
			// Sort 
			Collections.sort(similarProjects, Collections.reverseOrder());
		}
		
		return similarProjects;
	}

}
