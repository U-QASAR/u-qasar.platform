/*
 */
package eu.uqasar.web.pages.tree.panels.filter;

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
