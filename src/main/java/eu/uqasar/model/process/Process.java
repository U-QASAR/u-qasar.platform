package eu.uqasar.model.process;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.Namable;
import eu.uqasar.model.tree.Project;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.StringUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

@Entity
@XmlRootElement
@Table(name = "process")
@Indexed
public class Process extends AbstractEntity implements Namable {

	private static final long serialVersionUID = -4632410408985834678L;

	public static IconType ICON = new IconType("sitemap");

	@NotNull
	@Size(min = 2, max = 1024)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String name;
	private HashMap<Integer, String> stages;
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String description;
	@NotNull
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date startDate;
	@NotNull
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date endDate;

	@OneToMany
	@JoinColumn(name = "project_id", nullable = true)
	private Set<Project> projects = new HashSet<Project>();

	public Process() {
	}

	/**
	 *
	 * @param name
	 */
	public Process(final String name) {
		this.setName(name);
	}

	/**
	 *
	 * @param maxLength
	 * @return the abbreviated name
	 */
	public String getAbbreviatedName(int maxLength) {
		return StringUtils.abbreviate(getName(), maxLength);
	}

	/**
	 *
	 * @return the abbreviated name
	 */
	public String getAbbreviatedName() {
		return getAbbreviatedName(48);
	}

	/**
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @param name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *
	 * @param descr the descr to set
	 */
	public void setDescription(String descr) {
		this.description = descr;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the stages
	 */
	public HashMap<Integer, String> getStages() {
		return stages;
	}

	/**
	 * @param stages the stages to set
	 */
	public void setStages(HashMap<Integer, String> stages) {
		this.stages = stages;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getUniqueName() {
		return name;
	}
}
