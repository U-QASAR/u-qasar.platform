package eu.uqasar.model.analytic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.search.annotations.IndexedEmbedded;

import eu.uqasar.model.AbstractEntity;

/**
 *
 *
 */

@Entity
public class Dimension extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5412778455201051728L;
	private String name;
	
	@ManyToOne
	@IndexedEmbedded
	private Analysis analysis;

	public Dimension() {

	}

	public Dimension(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
