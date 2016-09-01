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

import javax.ejb.Stateless;

import org.jboss.solder.logging.Logger;

import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.service.AbstractService;

@Stateless
public class QualityObjectiveSimilarityService extends AbstractService<QualityObjective> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6234035716679476943L;
	
	private final Logger logger = Logger.getLogger(QualityObjectiveSimilarityService.class);
	private final List<QualityObjective> similarQOs = new ArrayList<>();
	
	
	private QualityObjectiveSimilarityService() {
		super(QualityObjective.class);
	}

	
	/**
	 * Get a list of similar QualityObjectives
	 * @param qo QualityObjective to which the comparisons are made.
	 * @param projToCompare the Project whose QOs the parameter qo is compared with 
	 * @return
	 */
	public List<QualityObjective> getSimilarQOs(final QualityObjective qo, final Project projToCompare) {

		// Clear the list for starters
		similarQOs.clear();
		
		// Compute the similarity with another QO
		computeQOSimilarity(qo, projToCompare);
		
		// Get a list of similar QOs
		return getQOsByDescendingSimilarityValue();
	}
	

	/**
	 * In the case of QualityObjectives the similarity is computed  
	 * based on purpose, type, metricSource, lifeCycleStage etc.
	 * This is partly accomplished by a direct comparison of field values 
	 * and partly by using cosine similarity by Lucene.
	 * @param qo QualityObjective against which to compare the similarity
	 * @param projToCompare Project whose QOs are compared 
	 */
	private void computeQOSimilarity(final QualityObjective qo, final Project projToCompare) {

		logger.info("computeQOSimilarity for " +qo +" from a similar project " +projToCompare);
		
		if (projToCompare != null && projToCompare.getChildren() != null && 
				projToCompare.getChildren().size() > 0) {

			// Compare the similarity with the other project's children (QualityObjects)
			for (TreeNode childNode : projToCompare.getChildren()) {
				logger.debugf("Checking the childNode: " +childNode.toString());
				if (childNode instanceof QualityObjective) {
					
					QualityObjective qObj = (QualityObjective) childNode;
					
					// Do not compare to itself
					if (!qObj.equals(qo)) {
						if (qObj.getQualityPurpose() != null && qo.getQualityPurpose() != null) {
							if (qObj.getQualityPurpose().equals(qo.getQualityPurpose())) {
								qObj.incrementSimilarityValue();
							}
						}
						if (qObj.getIndicatorType() != null && qo.getIndicatorType() != null) {
							if (qObj.getIndicatorType().equals(qo.getIndicatorType())) {
								qObj.incrementSimilarityValue();
							}
						}
						if (qObj.getMetricSource() != null && qo.getMetricSource() != null) {
							if (qObj.getMetricSource().equals(qo.getMetricSource())) {
								qObj.incrementSimilarityValue();
							}
						} 
						if (qObj.getLifeCycleStage() != null && qo.getLifeCycleStage() != null) {
							if (qObj.getLifeCycleStage().equals(qo.getLifeCycleStage())) {
								qObj.incrementSimilarityValue();
							}
						}						
						//TODO: Semantic similarity (cosine similarity)
						
						similarQOs.add(qObj);
					}
				}
			}
		}		
	}

	
	/**
	 * Obtain a list of similar QOs ordered according to 
	 * the computed similarity value.
	 * @return List of QOs is a descending similarity order 
	 */
	private List<QualityObjective> getQOsByDescendingSimilarityValue() {

		logger.infof("Sorting QOs to a descending similarity order...");
		
		if (similarQOs != null && similarQOs.size() > 0) {

			logger.debugf("Before sorting: ");
			for (QualityObjective qualityObjective : similarQOs) {
				logger.debugf("QO " +qualityObjective +": " +qualityObjective.getSimilarityValue());
			}

			// Sort
			Collections.sort(similarQOs, Collections.reverseOrder());

			logger.infof("After sorting: ");
			for (QualityObjective qualityObjective : similarQOs) {
				logger.debugf("QO " +qualityObjective +": " +qualityObjective.getSimilarityValue());
			}
		}
	
		return similarQOs;
	}
}
