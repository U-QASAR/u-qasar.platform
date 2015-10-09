package eu.uqasar.model.measure;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;

@Entity
@XmlRootElement
@Table(name = "jenkinsmetricmeasurement")
@Indexed
public class JenkinsMetricMeasurement extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	private String jenkinsMetric; // jenkins metric name
	private String name;
	@Lob
	private String value;
	private Date timeStamp; // timestamp for the snapshot
	@ManyToOne
	private Project project;
	@ManyToOne(fetch = FetchType.LAZY)
	private AdapterSettings adapter; // To which adapter the measurement belongs

	public JenkinsMetricMeasurement() {
	}

	/**
	 * @return the jenkinsMetric
	 */
	public String getJenkinsMetric() {
		return jenkinsMetric;
	}

	/**
	 * @param jenkinsMetric the jenkinsMetric to set
	 */
	public void setJenkinsMetric(String jenkinsMetric) {
		this.jenkinsMetric = jenkinsMetric;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the timeStamp
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
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
	 * @return the adapter
	 */
	public AdapterSettings getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(AdapterSettings adapter) {
		this.adapter = adapter;
	}

	@Override
	public String toString() {
		return "JenkinsMetricMeasurement [name=" +name 
				+ ", value=" +value
				+", timeStamp=" +timeStamp
				+", jenkinsMetric=" +jenkinsMetric
				+"]";
	}
}
