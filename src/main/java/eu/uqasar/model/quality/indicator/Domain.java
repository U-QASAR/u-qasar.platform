package eu.uqasar.model.quality.indicator;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;


public enum Domain implements IResourceKeyProvider {

	Telecommunications("telecom"),

	Bank("bank"),

	Industry("industry"),

	Insurance("insurance"),
	
	Public("public"),
	
	MobileApp ("mobile"),
	
	CSSolution("cs"),
	
	SaaS("saas")
	
	;

	private String labelKey;

	private Domain(final String labelKey) {
		this.labelKey = labelKey;
	}

	public String toString() {
		return getLabelModel().getObject();
	}
	
	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(this.getClass(), this);
	}
	
	public static List<Domain> getAllDomains(){
		List<Domain> list = new ArrayList<Domain>();
		for (Domain val : Domain.values()) {
		    	list.add(val);
		}
		return list;
	}

	@Override
	public String getKey() {
		return "label.domain." + this.labelKey;
	}
	
}
