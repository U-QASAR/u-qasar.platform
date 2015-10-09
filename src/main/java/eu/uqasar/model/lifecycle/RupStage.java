package eu.uqasar.model.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;


public enum RupStage implements IResourceKeyProvider {
	
	Inception("inc"),

	Elaboration("elab"),

	Construction("const"),

	Transition("trans"),

	;

	private String labelKey;

	private RupStage(final String labelKey) {
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
		return "label.rupStage." + this.labelKey;
	}
	
	public static List<RupStage> getAllRupStages(){
		List<RupStage> list = new ArrayList<RupStage>();
		for (RupStage val : RupStage.values()) {
		    	list.add(val);
		}
		return list;
	}


}
