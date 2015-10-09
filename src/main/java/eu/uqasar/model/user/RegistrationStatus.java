package eu.uqasar.model.user;

import eu.uqasar.util.resources.ResourceBundleLocator;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public enum RegistrationStatus {
	
	PENDING("pending"), 
	
	CONFIRMED("confirmed"), 
	
	CANCELLED("cancelled");
	
	private final String labelKey;

	private RegistrationStatus(final String labelKey) {
		this.labelKey = labelKey;
	}

	@Override
	public String toString() {
		return getLabelModel().getObject();
	}

	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(RegistrationStatus.class, "label.status." + labelKey);
	}
}
