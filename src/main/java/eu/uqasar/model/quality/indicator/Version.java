package eu.uqasar.model.quality.indicator;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;


public enum Version implements IResourceKeyProvider {

	Prototype("prototype"),

	Alfa("alfa"),

	Beta("beta"),

	Final("final"),

	;

	private String labelKey;

	private Version(final String labelKey) {
		this.labelKey = labelKey;
	}

	public String toString() {
		return getLabelModel().getObject();
	}
	
	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(this.getClass(), this);
	}

	@Override
	public String getKey() {
		return "label.version." + this.labelKey;
	}
	
	public static List<Version> getAllVersions(){
		List<Version> list = new ArrayList<Version>();
		for (Version val : Version.values()) {
		    	list.add(val);
		}
		return list;
	}
	
}
