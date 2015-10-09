package eu.uqasar.model.measure;

import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.model.IModel;

/**
 * Units that can be used.
 *
 */
public enum Unit implements IResourceKeyProvider {

	/**
	 * Lines of code.
	 */
	Loc("loc"),

	/**
	 * Number of issues.
	 */
	Issue("issue"),

	/**
	 * Function point.
	 */
	FunctionPoint("fp"),

	/**
	 * Number of tests.
	 */
	Test("test"),
	
	/**
	 * Without unit.
	 */
	Unity("unity"),
	
	;

	private String labelKey;

	private Unit(final String labelKey) {
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
		return "label.unit." + this.labelKey;
	}
	public static List<Unit> getAllUnits(){
		List<Unit> list = new ArrayList<Unit>();
		for (Unit val : Unit.values()) {
		    	list.add(val);
		}
		return list;
	}

}
