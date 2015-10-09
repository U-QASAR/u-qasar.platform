package eu.uqasar.model.measure;

import com.google.gson.annotations.SerializedName;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;

@Entity
@XmlRootElement
@Table(name = "jirametricmeasurement")
@Indexed
public class JiraMetricMeasurement extends AbstractEntity {

	private static final long serialVersionUID = -2599789328193392470L;
	private String jiraMetric;
	private String self;
	@SerializedName("key")
	private String jiraKey;
	private Date timeStamp;
	@Lob
	private String jsonContent; // Content in JSON format, pointed by a URL (self)
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = true)
	private Project project;
	@ManyToOne(fetch = FetchType.LAZY)
	private AdapterSettings adapter; // To which adapter the measurement belongs

	/**
	 * 
	 * @return
	 */
	public String getSelf() {
		return self;
	}
	
	/**
	 * 
	 * @param self
	 */
	public void setSelf(String self) {
		this.self = self;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getJiraKey() {
		return jiraKey;
	}
	
	/**
	 * 
	 * @param key
	 */
	public void setJiraKey(String jiraKey) {
		this.jiraKey = jiraKey;
	}
	
	/**
	 * @return the metric
	 */
	public String getJiraMetric() {
		return jiraMetric;
	}

	/**
	 * @param metric the metric to set
	 */
	public void setJiraMetric(String jiraMetric) {
		this.jiraMetric = jiraMetric;
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
	 * @return the jsonContent
	 */
	public String getJsonContent() {
		return jsonContent;
	}

	/**
	 * @param jsonContent the jsonContent to set
	 */
	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
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

	@Override
	public String toString() {
		return "JiraMetricMeasurement [self=" +self 
				+", jiraKey=" +jiraKey  
				+", jiraMetric=" +jiraMetric 
				+", timeStamp=" +timeStamp
				+", jsonContent=" +jsonContent
				+"]";
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
}
