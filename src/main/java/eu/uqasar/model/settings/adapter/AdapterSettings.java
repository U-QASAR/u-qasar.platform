package eu.uqasar.model.settings.adapter;

import java.util.Date;

import javax.annotation.Nullable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jboss.solder.logging.Logger;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.measure.MetricSource;
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
