package eu.uqasar.web.pages.qmtree.panels.filter;

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
