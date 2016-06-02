package eu.uqasar.model.notification.metric;

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
