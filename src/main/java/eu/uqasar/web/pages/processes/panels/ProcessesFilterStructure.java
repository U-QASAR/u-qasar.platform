package eu.uqasar.web.pages.processes.panels;

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
import java.util.Date;

/**
 * 
 */
public class ProcessesFilterStructure implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1306629342832495716L;
	private String name;
	private Date startDate;
	private Date endDate;

	public ProcessesFilterStructure() {

	}

	public ProcessesFilterStructure(ProcessManagementPanel panel) {
		this.name = panel.getName();
		this.startDate = panel.getStartDate();
		this.endDate = panel.getEndDate();
	}

	@Override
	public String toString() {
		return "ProcessesFilterStructure{" + "name=" + name + ", start date="
				+ startDate + ", end date=" + endDate + "}";
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return endDate;
	}

}
