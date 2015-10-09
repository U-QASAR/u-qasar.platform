package eu.uqasar.model.quality.indicator;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;

public enum Color implements IResourceKeyProvider {

	Green("green"),

	Yellow("yellow"),

	Red("red"),
	
	Gray("gray"),

	;

	private String labelKey;

	private Color(final String labelKey) {
		this.labelKey = labelKey;
	}
	
	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(this.getClass(), this);
	}

	@Override
	public String getKey() {
		return "label.quality.status." + this.labelKey;
	}
	
	public String toString() {
		return getLabelModel().getObject();
	}
}
