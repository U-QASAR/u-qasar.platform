package eu.uqasar.model.measure;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;

import com.google.gson.annotations.SerializedName;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;

@Entity
@XmlRootElement
@Table(name = "sonarmetricmeasurement")
@Indexed
public class SonarMetricMeasurement extends AbstractEntity {

	private static final long serialVersionUID = 13394761234433421L;

	private String sonarMetric; // Sonar metric name
	private String name;
	private String value;
	@SerializedName("key")
	private String sonarKey;
	private Date timeStamp; // timestamp for the snapshot
	@ManyToOne
	private Project project;
	@ManyToOne(fetch = FetchType.LAZY)
	private AdapterSettings adapter; // To which adapter the measurement belongs
	
	public SonarMetricMeasurement() {
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
	public String getSonarKey() {
		return sonarKey;
	}
	public void setSonarKey(String sonarKey) {
		this.sonarKey = sonarKey;
	}
	/**
	 * @return the sonarMetric
	 */
	public String getSonarMetric() {
		return sonarMetric;
	}
	
	/**
	 * @param sonarMetric the sonarMetric to set
	 */
	public void setSonarMetric(String sonarMetric) {
		this.sonarMetric = sonarMetric;
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
		return "SonarMetricMeasurement [name=" +name + ", value=" +value
				+ ", sonarKey=" +sonarKey +", timeStamp=" +timeStamp
				+", sonarMetric=" +sonarMetric
				+"]";
	}
}
