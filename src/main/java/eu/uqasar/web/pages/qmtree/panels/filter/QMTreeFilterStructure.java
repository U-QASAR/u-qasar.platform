package eu.uqasar.web.pages.qmtree.panels.filter;

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


import java.io.Serializable;

import eu.uqasar.model.qmtree.QModelStatus;


public class QMTreeFilterStructure implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7775226390415299126L;

	private QModelStatus isActive;
	private Long currentlyEditedQModelId;

	public QMTreeFilterStructure() {

	}

	public QMTreeFilterStructure(QMFilterPanel panel) {
		this.isActive = panel.getActiveSelected();
	}

	public Long getCurrentlyEditedQModelId() {
		return currentlyEditedQModelId;
	}

	public void setCurrentlyEditedQModelId(Long currentlyEditedQModelId) {
		this.currentlyEditedQModelId = currentlyEditedQModelId;
	}

	public QModelStatus getIsActive() {
		return isActive;
	}

	void setStage(QModelStatus isActive) {
		this.isActive = isActive;
	}
}
