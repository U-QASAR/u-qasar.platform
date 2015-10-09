
package eu.uqasar.model.user;

import eu.uqasar.util.resources.ResourceBundleLocator;
import org.apache.wicket.model.IModel;

/**
 *
 *
 */
public enum Gender {
    
    Male("male"),
    
    Female("female");
    
    private final String labelKey;

	private Gender(final String labelKey) {
		this.labelKey = labelKey;
	}

	@Override
	public String toString() {
		return getLabelModel().getObject();
	}

	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(Gender.class, "label.gender." + labelKey);
	}
    
}
