package eu.uqasar.model.notification.threshold;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import eu.uqasar.model.notification.Notification;
import eu.uqasar.model.notification.NotificationType;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.TreeNode;

@Entity
public class ThresholdReachedNotification extends Notification {

	private static final long serialVersionUID = 6838387940459194342L;

	@ManyToOne
	private Project project;
	
	@OneToMany
	private List<TreeNode> treeNodes;

	public ThresholdReachedNotification() {
		this.notificationType = NotificationType.THRESHOLD;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public List<TreeNode> getTreeNodes() {
		return treeNodes;
	}

	public void setTreeNodes(List<TreeNode> treeNodes) {
		this.treeNodes = treeNodes;
	}

}
