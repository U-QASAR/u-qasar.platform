package eu.uqasar.web.pages.processes.panels;

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
