package eu.uqasar.model.measure;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;

public enum Scale implements IResourceKeyProvider {

	Nominal("nom"),

	Ordinal("ord"),

	Interval("int"),

	Ratio("rat"),
	
	;

	private String labelKey;

	private Scale(final String labelKey) {
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
		return "label.scale." + this.labelKey;
	}
	
	public static List<Scale> getAllScales(){
		List<Scale> list = new ArrayList<Scale>();
		for (Scale val : Scale.values()) {
		    	list.add(val);
		}
		return list;
	}

}
