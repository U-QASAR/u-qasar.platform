package eu.uqasar.web.provider.ldap;

import eu.uqasar.util.ldap.LdapEntity;
import eu.uqasar.util.ldap.LdapManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingException;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 *
 * @param <Entity>
 */
public abstract class LdapEntityProvider<Entity extends LdapEntity> implements IDataProvider<Entity> {

	protected int maximumNoOfEntities;
	protected LdapManager ldapManager;
	protected boolean initialized;
	protected List<Entity> entities = new ArrayList<>();

	public LdapEntityProvider(LdapManager manager, int max) {
		this.ldapManager = manager;
		this.maximumNoOfEntities = max;
	}

	public int getMaximumNoOfEntities() {
		return maximumNoOfEntities;
	}

	public void setMaximumNoOfEntities(int maximumNoOfEntities) {
		this.maximumNoOfEntities = maximumNoOfEntities;
	}

	public void update(LdapManager manager) throws NamingException {
		this.ldapManager = manager;
		entities = getEntities(manager, maximumNoOfEntities);
		this.initialized = true;
	}

	public void reset() {
		this.initialized = false;
	}

	@Override
	public Iterator<Entity> iterator(long first, long count) {
		if (initialized) {
			return entities.subList((int) first, (int) Math.min(first + count, entities.size())).iterator();
		}
		return Collections.emptyIterator();
	}

	public abstract List<Entity> getEntities(LdapManager manager, int max) throws NamingException;

	@Override
	public long size() {
		if (initialized) {
			return entities != null ? entities.size() : 0;
		}
		return 0;
	}

	@Override
	public IModel<Entity> model(Entity object) {
		return Model.of(object);
	}

	@Override
	public void detach() {

	}
}
