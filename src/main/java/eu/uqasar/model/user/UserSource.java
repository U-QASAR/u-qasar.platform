package eu.uqasar.model.user;

import eu.uqasar.util.resources.ResourceBundleLocator;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public enum UserSource {

	UQASAR("uqasar"),
	LDAP("ldap"),;

	private final String labelKey;

	private UserSource(final String labelKey) {
		this.labelKey = labelKey;
	}

	@Override
	public String toString() {
		return getLabelModel().getObject();
	}

	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(UserSource.class, "label.source." + labelKey);
	}
}
