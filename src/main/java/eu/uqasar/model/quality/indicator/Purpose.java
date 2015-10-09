package eu.uqasar.model.quality.indicator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;

public enum Purpose implements IResourceKeyProvider {

	Product("product"),

	Process("process"),

	;

	private String labelKey;

	private Purpose(final String labelKey) {
		this.labelKey = labelKey;
	}

	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(this.getClass(), this);
	}

	@Override
	public String getKey() {
		return "label.quality.purpose." + this.labelKey;
	}

	public String toString() {
		return getLabelModel().getObject();
	}


	public static List<Purpose> getAllPurposes(){
		List<Purpose> list = new ArrayList<Purpose>();
		for (Purpose val : Purpose.values()) {
		    	list.add(val);
		}
		return list;
	}

}
