package eu.uqasar.model.qmtree;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.ResourceBundleLocator;


public enum QModelStatus {
	
	Active("active"),

	NotActive("notactive"),

	OldActive("oldactive"),


	;

	private final String labelKey;

	private QModelStatus(final String labelKey) {
		this.labelKey = labelKey;
	}

	@Override
	public String toString() {
		return getLabelModel().getObject();
	}
	
	public IModel<String> getLabelModel() {
		return ResourceBundleLocator.getLabelModel(QModelStatus.class, "label.qmodelstatus." + labelKey);
	}
	
	public static List<QModelStatus> getAllLifeCycleStages(){
		return Arrays.asList(values());
	}


}
