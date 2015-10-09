package eu.uqasar.model.quality.indicator;

import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;

public enum Type implements IResourceKeyProvider {

	Manual("manual", new IconType("edit")),

	Automatic("automatic", new IconType("cogs")),

	;

	private String labelKey;
	private IconType icon;

	private Type(final String labelKey, IconType icon) {
		this.labelKey = labelKey;
		this.icon = icon;
	}

	public IconType getIconType() {
		return icon;
	}
	
	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(this.getClass(), this);
	}

	@Override
	public String getKey() {
		return "label.quality.indicator.type." + this.labelKey;
	}

	public String toString() {
		return getLabelModel().getObject();
	}
}
