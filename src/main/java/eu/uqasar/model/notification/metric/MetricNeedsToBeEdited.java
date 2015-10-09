package eu.uqasar.model.notification.metric;
 
import eu.uqasar.model.notification.Notification;
import eu.uqasar.model.notification.NotificationType;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class MetricNeedsToBeEdited extends Notification {

    private static final long serialVersionUID = 6838387940459194342L;

    @ManyToOne
    private Metric metric;
    
    @ManyToOne
    private Project project;
    
    private int dueDays; 
    
    public MetricNeedsToBeEdited() {
        this.notificationType = NotificationType.METRIC_OUT_OF_DATE;
    }
    
    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
    
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getDueDays() {
        return dueDays;
    }

    public void setDueDays(int dueDays) {
        this.dueDays = dueDays;
    }

}
