/**
 * 
 */
package eu.uqasar.model;

import java.io.Serializable;

/**
 *
 * 
 */
public interface Persistable<ID extends Serializable> extends Serializable {

	/**
	 * Returns entity's ID.
	 * 
	 * @return
	 */
	ID getId();

}
