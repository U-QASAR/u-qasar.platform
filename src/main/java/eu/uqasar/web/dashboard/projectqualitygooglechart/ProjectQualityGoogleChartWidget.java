package eu.uqasar.web.dashboard.projectqualitygooglechart;

import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;

/**
 * A sample widget using Google Charts API <https://developers.google.com/chart/>
 */
public class ProjectQualityGoogleChartWidget extends AbstractWidget {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2172703196794098298L;

	private static ProjectQualityGoogleChartFactory chartDataFactory;
	
	public static final String GAUGE_TYPE = "GAUGE"; 
	public static final String BAR_TYPE = "BAR";
	public static final String LINE_TYPE = "LINE";

	public static final List<String> TYPES = Arrays.asList(new String[] {
			LINE_TYPE, 
			BAR_TYPE,
			GAUGE_TYPE
	});

	
	public ProjectQualityGoogleChartWidget() {
		super();
		title = "Project Quality";
	}

	@Override
	public void init() {
	}

	public static ProjectQualityGoogleChartFactory getChartDataFactory() {
		return chartDataFactory;
	}

	public static void setChartDataFactory(ProjectQualityGoogleChartFactory chartDataFactory) {
		ProjectQualityGoogleChartWidget.chartDataFactory = chartDataFactory;
	}

	public String getChartData() {
		if (chartDataFactory == null) {
			throw new RuntimeException("ChartDataFactory cannot be null. Use ChartWidget.setChartDataFactory(...)");
		}

		return chartDataFactory.createChart(this);
	}

	public WidgetView createView(String viewId) {
		return new ProjectQualityGoogleChartWidgetView(viewId, new Model<Widget>(this));
	}

	@Override
	public boolean hasSettings() {
		return true;
	}

	@Override
	public Panel createSettingsPanel(String settingsPanelId) {
		return new ProjectQualityGoogleChartSettingsPanel(settingsPanelId, new Model<ProjectQualityGoogleChartWidget>(this));
	}

	public Double returnProjectQualityValue(){
		TreeNodeService dataService = null;

		Project proj = null;
		Double value = (double) 0;
		try {
			InitialContext ic = new InitialContext();
			dataService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
			// Obtain project from the settings
			if (settings.get("project") != null) {
				proj = dataService.getProjectByName(settings.get("project"));
			} else {
				if (dataService != null) {
					proj = dataService.getProjectByName("U-QASAR Platform Development");
				}
			}
			if (proj != null && proj.getValue() != null && !proj.getValue().isInfinite()) {
				value = UQasarUtil.round(proj.getValue());
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return value;

	}
}
