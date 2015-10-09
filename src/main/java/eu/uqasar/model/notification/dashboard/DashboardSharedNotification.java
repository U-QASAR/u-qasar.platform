package eu.uqasar.model.notification.dashboard;

import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.dashboard.DbDashboard_;
import eu.uqasar.model.notification.Notification;
import eu.uqasar.model.notification.NotificationType;
import eu.uqasar.model.tree.Project;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import ro.fortsoft.wicket.dashboard.Dashboard;

@Entity
public class DashboardSharedNotification extends Notification {

	@Override
	public String toString() {
		return "DashboardSharedNotification [dashboard=" + dashboard
				+ ", getCreationDate()=" + getCreationDate()
				+ ", getNotificationType()=" + getNotificationType()
				+ ", getUser()=" + getUser() + ", getReadDate()="
				+ getReadDate() + ", getId()=" + getId() + "]";
	}

	private static final long serialVersionUID = 6838387940459194342L;

	@ManyToOne
	private DbDashboard dashboard;
	
	public DashboardSharedNotification() {
		
	}
	
	public DbDashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(DbDashboard dashboard) {
		this.dashboard = dashboard;
	} 

}
