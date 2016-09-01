package eu.uqasar.model.settings.adapter;

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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.jboss.solder.logging.Logger;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.measure.CubesMetricMeasurement;
import eu.uqasar.model.measure.GitlabMetricMeasurement;
import eu.uqasar.model.measure.JenkinsMetricMeasurement;
import eu.uqasar.model.measure.JiraMetricMeasurement;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.measure.SonarMetricMeasurement;
import eu.uqasar.model.measure.TestLinkMetricMeasurement;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.dataadapter.CubesDataService;
import eu.uqasar.service.dataadapter.GitlabDataService;
import eu.uqasar.service.dataadapter.JenkinsDataService;
import eu.uqasar.service.dataadapter.JiraDataService;
import eu.uqasar.service.dataadapter.SonarDataService;
import eu.uqasar.service.dataadapter.TestLinkDataService;
import eu.uqasar.util.UQasarUtil;

@Setter
@Getter
@Entity
@XmlRootElement
@Table(name = "adaptersettings")
@Indexed
public class AdapterSettings extends AbstractEntity {

	private static final long serialVersionUID = -3535301965200581906L;
	private String name; // Name of the settings shown to the user
	private MetricSource metricSource; // Type of the metric source
	private String url; // Address where the adapter resides
	private String adapterUsername; // Username for the service used by the adapter
	private String adapterPassword; // Password for the service used by the adapter
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date latestUpdate; // Timestamp of the last update
    @Nullable
	@ManyToOne
	@IndexedEmbedded
	private Project project;
	private static Logger logger = Logger.getLogger(AdapterSettings.class);
    private String adapterProject;
    private String adapterTestPlan;

	@OneToMany(cascade = CascadeType.ALL, mappedBy="adapter", orphanRemoval=true)
    private List<SonarMetricMeasurement> sonarMeasurements = new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL, mappedBy="adapter", orphanRemoval=true)
    private List<JiraMetricMeasurement> jiraMeasurements = new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL, mappedBy="adapter", orphanRemoval=true)
    private List<CubesMetricMeasurement> cubesMeasurements = new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL, mappedBy="adapter", orphanRemoval=true)
    private List<JenkinsMetricMeasurement> jenkinsMeasurements = new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL, mappedBy="adapter", orphanRemoval=true)
    private List<TestLinkMetricMeasurement> testlinkMeasurements = new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL, mappedBy="adapter", orphanRemoval=true)
    private List<GitlabMetricMeasurement> gitlabMeasurements = new ArrayList<>();


	/**
	 * Get a new snapshot of adapter data
	 */
	public void updateAdapterData() {
        logger.debug("AdapterSettings::updateAdapterData()");
        try {
            InitialContext ic = new InitialContext();
            if (getMetricSource() != null
                && getMetricSource() == MetricSource.IssueTracker) {
                JiraDataService jiraDataService =
                    (JiraDataService) ic.lookup("java:module/JiraDataService");
                jiraDataService.updateAdapterData(this);
            } else if (getMetricSource() != null
                && getMetricSource() == MetricSource.TestingFramework) {
                TestLinkDataService testLinkDataService =
                    (TestLinkDataService) ic.lookup("java:module/TestLinkDataService");
                testLinkDataService.updateAdapterData(this);
            } else if (getMetricSource() != null
                && getMetricSource() == MetricSource.StaticAnalysis) {
                SonarDataService sonarDataService =
                    (SonarDataService) ic.lookup("java:module/SonarDataService");
                sonarDataService.updateAdapterData(this);
            } else if (getMetricSource() != null 
            	&& getMetricSource() == MetricSource.CubeAnalysis){
                CubesDataService cubesDataService =
                        (CubesDataService) ic.lookup("java:module/CubesDataService");
                    cubesDataService.updateAdapterData(this);
            } else if (getMetricSource() != null 
            		&& getMetricSource() == MetricSource.VersionControl) {
            	GitlabDataService gitlabDataService = 
            			(GitlabDataService) ic.lookup("java:module/GitlabDataService");
            	gitlabDataService.updateAdapterData(this);
            } else if (getMetricSource() != null 
            		&& getMetricSource() == MetricSource.ContinuousIntegration) {
            	JenkinsDataService jenkinsDataService = (JenkinsDataService) ic.lookup("java:module/JenkinsDataService");
            	jenkinsDataService.updateAdapterData(this);
            }

            // Update the project tree
            UQasarUtil.updateTree(this.getProject());
            
        } catch (NamingException | uQasarException e) {
            e.printStackTrace();
        }

    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "";
		/*"AdapterSettings [name=" + name + ", metricSource="
				+ metricSource + ", url=" + url + ", adapterUsername="
				+ adapterUsername + ", adapterPassword=" + adapterPassword
				+ ", latestUpdate=" + latestUpdate + ", project=" + project
				+ ", adapterProject=" + adapterProject + ", adapterTestPlan="
				+ adapterTestPlan + "]";
				*/
	}
}
