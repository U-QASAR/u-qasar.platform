/**
 * 
 */
package eu.uqasar.model.tree.historic;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.tree.Project;

/**
 *
 *
 */
@Entity
public class Snapshot extends AbstractEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4665534074086875768L;

	// Snapshot time stamp
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date date;

	// Values Last update date
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date lastUpdate;
	
	// Name of the snapshot provided by user
	@NotNull
	@Size(min = 2, max = 1024)
	private String name;

	// Project snapshot owner
	@ManyToOne
	private Project project;
	
	/**
	 * 
	 */
	public Snapshot() {
	}
	
	public Snapshot(String name) {
		super();
		
		this.name = name;
		date = new Date();
		
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return formated date ready to be printed without milliseconds
	 */
	public String getFormatedDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(final Date date) {
		this.date = date;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return  " [" + getFormatedDate() + "] " + name;
	}
	
}
