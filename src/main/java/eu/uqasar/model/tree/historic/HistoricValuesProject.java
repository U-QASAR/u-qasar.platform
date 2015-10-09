package eu.uqasar.model.tree.historic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import eu.uqasar.model.tree.Project;

@Entity
@Indexed
public class HistoricValuesProject extends AbstractHistoricValues {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3410829444860479588L;
	@ManyToOne
	@IndexedEmbedded
	private Project project;

	public HistoricValuesProject() {
	}

	public HistoricValuesProject(Project project) {
		super();
		super.setDate(project.getLastUpdated());
		super.setValue(Float.valueOf(project.getValue().toString()));
		super.setQualityStatus(project.getQualityStatus());
		super.setLowerAcceptanceLimit(project.getThreshold().getLowerAcceptanceLimit());
		super.setUpperAcceptanceLimit(project.getThreshold().getUpperAcceptanceLimit());
		this.project = project;
	}

	/**
	 * @return
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HistoricValuesProject [project=" + project + "]";
	}

}