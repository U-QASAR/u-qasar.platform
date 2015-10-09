package eu.uqasar.model.quality.indicator;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;


public enum Paradigm implements IResourceKeyProvider {

	Waterfall("waterfall"),

	Rup("rup"),

//	Scrum("scrum"),

//	XP("xp"),
	
//	Lean("lean"),
	
//	Kanban("kanban"),
	
	;

	private String labelKey;

	private Paradigm(final String labelKey) {
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
		return "label.paradigm." + this.labelKey;
	}
	public static List<Paradigm> getAllParadigms(){
		List<Paradigm> list = new ArrayList<Paradigm>();
		for (Paradigm val : Paradigm.values()) {
		    	list.add(val);
		}
		return list;
	}
	
}
