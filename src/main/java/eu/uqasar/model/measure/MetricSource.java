package eu.uqasar.model.measure;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import eu.uqasar.util.resources.IResourceKeyProvider;
import eu.uqasar.util.resources.ResourceBundleLocator;

public enum MetricSource implements IResourceKeyProvider {

	TestingFramework("test"),

	IssueTracker("issue"),

	StaticAnalysis("static"),

	ContinuousIntegration("ci"),
	
	CubeAnalysis("cube"),
	
	VersionControl("vcs"),

	Manual("manual"),

	;

	private String labelKey;

	private MetricSource(final String labelKey) {
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
		return "label.source." + this.labelKey;
	}
	
	public static List<MetricSource> getAllMetricSources(){
		List<MetricSource> list = new ArrayList<MetricSource>();
		for (MetricSource val : MetricSource.values()) {
		    	list.add(val);
		}
		return list;
	}

}
