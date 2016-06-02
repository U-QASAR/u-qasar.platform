/*
 */
package eu.uqasar.web.pages.tree.panels.filter;

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


import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.tree.QualityStatus;
import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 */
public class TreeFilterStructure implements Serializable {

	private QualityStatus qualityStatus;
	private LifeCycleStage stage;
	private Date startDate, endDate;
	private Long currentlyEditedProjectId;

	public TreeFilterStructure() {

	}

	public TreeFilterStructure(FilterPanel panel) {
		this.qualityStatus = panel.getQualityStatus();
		this.stage = panel.getLCStage();
		this.startDate = panel.getStartDate();
		this.endDate = panel.getEndDate();
	}

	public Long getCurrentlyEditedProjectId() {
		return currentlyEditedProjectId;
	}

	public void setCurrentlyEditedProjectId(Long currentlyEditedProjectId) {
		this.currentlyEditedProjectId = currentlyEditedProjectId;
	}

	public QualityStatus getQualityStatus() {
		return qualityStatus;
	}

	void setQualityStatus(QualityStatus status) {
		this.qualityStatus = status;
	}

	public LifeCycleStage getLCStage() {
		return stage;
	}

	void setStage(LifeCycleStage stage) {
		this.stage = stage;
	}

	public Date getStartDate() {
		return startDate;
	}

	void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
