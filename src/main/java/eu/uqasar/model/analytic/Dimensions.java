package eu.uqasar.model.analytic;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.ResourceBundleLocator;

public enum Dimensions {

	Project("project"),
	Status("status"),
	Type("type"),
	Priority("priority"),
	Resolution("resolution"),
	Assignee("assignee"),
	Reporter("reporter"),
	Creator("creator"),
	Created("created"),;

	private final String labelKey;

	private Dimensions(final String labelKey) {
		this.labelKey = labelKey;
	}
	

	@Override
	public String toString() {
		return getLabelModel().getObject();
	}

	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(Dimensions.class, "label.dimension." + labelKey);
	}
	
	public static List<Dimensions> getAllDimensions(){
		return Arrays.asList(values());
	}


}
