package eu.uqasar.model.lifecycle;

import eu.uqasar.util.resources.ResourceBundleLocator;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.model.IModel;


public enum LifeCycleStage {
	
	Specification("spec"),

	Requirements("req"),

	Design("design"),

	Implementation("impl"),

	Testing("test"),
	
	Maintenance("maint"),

	;

	private final String labelKey;

	private LifeCycleStage(final String labelKey) {
		this.labelKey = labelKey;
	}

	@Override
	public String toString() {
		return getLabelModel().getObject();
	}
	
	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(LifeCycleStage.class, "label.lcStage." + labelKey);
	}
	
	public static List<LifeCycleStage> getAllLifeCycleStages(){
		return Arrays.asList(values());
	}


}
