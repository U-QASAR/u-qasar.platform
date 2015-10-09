/**
 * 
 */
package eu.uqasar.web.provider;

import org.apache.wicket.cdi.CdiContainer;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import eu.uqasar.model.AbstractEntity;

/**
 *
 * 
 */
public abstract class EntityProvider<T extends AbstractEntity> extends
		SortableDataProvider<T, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7053242016934984848L;

	public EntityProvider() {
		CdiContainer.get().getNonContextualManager().inject(this);
	}

	public IModel<T> model(T entity) {
		return Model.of(entity);
	}

}
