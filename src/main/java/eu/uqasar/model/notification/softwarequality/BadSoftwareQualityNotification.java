package eu.uqasar.model.notification.softwarequality;

import eu.uqasar.model.notification.Notification;
import eu.uqasar.model.notification.NotificationType;
import eu.uqasar.model.tree.Project;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class BadSoftwareQualityNotification extends Notification {

	private static final long serialVersionUID = 6838387940459194342L;

	@ManyToOne
	private Project project;
	
	public BadSoftwareQualityNotification() {
		this.notificationType = NotificationType.UNDEFINED;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

}
