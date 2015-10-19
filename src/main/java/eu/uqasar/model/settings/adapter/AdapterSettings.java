package eu.uqasar.model.settings.adapter;

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

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.jasypt.util.text.BasicTextEncryptor;
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
	 * 
	 * @return the settingsName
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the settingsName to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the metricSource
	 */
	public MetricSource getMetricSource() {
		return metricSource;
	}
	
	/**
	 * @param metricSource the metricSource to set
	 */
	public void setMetricSource(MetricSource metricSource) {
		this.metricSource = metricSource;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the username
	 */
	public String getAdapterUsername() {
		return adapterUsername;
	}

	/**
	 * @param adapterUsername the username to set
	 */
	public void setAdapterUsername(String adapterUsername) {
		this.adapterUsername = adapterUsername;
	}
	
	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * Returns the decrypted (decryption executed by using jasypt) adapter password 
	 * @return the adapterPassword
	 */
	public String getAdapterPassword() {
		BasicTextEncryptor textDecryptor = new BasicTextEncryptor();
		String decryptionPwd = UQasarUtil.getEncDecPwd();
		textDecryptor.setPassword(decryptionPwd);
		String decrypted = textDecryptor.decrypt(this.adapterPassword);
		return decrypted;
	}

	/**
	 * Setter for the password (the password is first encryptedby using jasypt)  
	 * @param adapterPassword the adapterPassword to set
	 */
	public void setAdapterPassword(String adapterPassword) {
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		String encryptionPwd = UQasarUtil.getEncDecPwd();
		textEncryptor.setPassword(encryptionPwd);
		String encrypted = textEncryptor.encrypt(adapterPassword);
		this.adapterPassword = encrypted;
	}

	/**
	 * @return the latestUpdate
	 */
	public Date getLatestUpdate() {
		return latestUpdate;
	}

	/**
	 * @param latestUpdate the latestUpdate to set
	 */
	public void setLatestUpdate(Date latestUpdate) {
		this.latestUpdate = latestUpdate;
	}
	
    /**
     * 
     * @return 
     */
    public String getAdapterProject() {
        return adapterProject;
    }

    /**
     * 
     * @param adapterProject 
     */
    public void setAdapterProject(String adapterProject) {
        this.adapterProject = adapterProject;
    }

    /**
     * Get TestPlan property for TestLink adapter.
     * @return TestPlan
     */
    public String getAdapterTestPlan() {
        return adapterTestPlan;
    }

    /**
     * Set TestPlan property for TestLink adapter.
     * @param adapterTestPlan 
     */
    public void setAdapterTestPlan(String adapterTestPlan) {
        this.adapterTestPlan = adapterTestPlan;
    }

    
	public List<SonarMetricMeasurement> getSonarMeasurements() {
		return sonarMeasurements;
	}

	public void setSonarMeasurements(List<SonarMetricMeasurement> sonarMeasurements) {
		this.sonarMeasurements = sonarMeasurements;
	}

	public List<JiraMetricMeasurement> getJiraMeasurements() {
		return jiraMeasurements;
	}

	public void setJiraMeasurements(List<JiraMetricMeasurement> jiraMeasurements) {
		this.jiraMeasurements = jiraMeasurements;
	}

	public List<CubesMetricMeasurement> getCubesMeasurements() {
		return cubesMeasurements;
	}

	public void setCubesMeasurements(List<CubesMetricMeasurement> cubesMeasurements) {
		this.cubesMeasurements = cubesMeasurements;
	}

	public List<JenkinsMetricMeasurement> getJenkinsMeasurements() {
		return jenkinsMeasurements;
	}

	public void setJenkinsMeasurements(
			List<JenkinsMetricMeasurement> jenkinsMeasurements) {
		this.jenkinsMeasurements = jenkinsMeasurements;
	}

	public List<TestLinkMetricMeasurement> getTestlinkMeasurements() {
		return testlinkMeasurements;
	}

	public void setTestlinkMeasurements(
			List<TestLinkMetricMeasurement> testlinkMeasurements) {
		this.testlinkMeasurements = testlinkMeasurements;
	}

	public List<GitlabMetricMeasurement> getGitlabMeasurements() {
		return gitlabMeasurements;
	}

	public void setGitlabMeasurements(
			List<GitlabMetricMeasurement> gitlabMeasurements) {
		this.gitlabMeasurements = gitlabMeasurements;
	}

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
