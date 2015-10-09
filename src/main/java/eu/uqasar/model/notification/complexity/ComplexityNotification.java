package eu.uqasar.model.notification.complexity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import eu.uqasar.model.notification.Notification;
import eu.uqasar.model.notification.NotificationType;
import eu.uqasar.model.tree.Project;

@Entity
public class ComplexityNotification extends Notification {

	

	private static final long serialVersionUID = 6838387940459194342L;

	@ManyToOne
	private Project project;

	private String name; 
	
	public ComplexityNotification() {
		this.notificationType = NotificationType.WARNING;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ComplexityNotification [project=" + project
				+ ", getCreationDate()=" + getCreationDate()
				+ ", getNotificationType()=" + getNotificationType()
				+ ", getUser()=" + getUser() + ", getReadDate()="
				+ getReadDate() + ", getId()=" + getId() + ", toString()="
				+ super.toString() + ", hashCode()=" + hashCode()
				+ ", getClass()=" + getClass() + "]";
	}
	
	

}
