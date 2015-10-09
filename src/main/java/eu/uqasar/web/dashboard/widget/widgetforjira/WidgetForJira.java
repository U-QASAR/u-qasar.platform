package eu.uqasar.web.dashboard.widget.widgetforjira;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.AbstractWidget;
import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;
import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.model.measure.JiraMetricMeasurement;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.dataadapter.JiraDataService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.UQasarUtil;

/**
 * A sample widget fetching sample data from a JIRA(R) issues tracking software instance. 
 *  
 */
public class WidgetForJira extends AbstractWidget {

	private static final long serialVersionUID = 7159137620094069518L;
	private Project project;

	public WidgetForJira() {
		super();
		title = "Metrics related to issues from JIRA(R)";
	}

	
	@Override
	public void init() {
		if(settings.get("project") != null && !settings.get("project").isEmpty()){
			String name = settings.get("project");
			project = getProject(name);
		}
	}
	
	@Override
	public WidgetView createView(String viewId) {
		return new WidgetForJIRAView(viewId, new Model<Widget>(this));
	}
	
	@Override
	public boolean hasSettings() {
		return true;
	}
	
	@Override
	public Panel createSettingsPanel(String settingsPanelId) {
		return new WidgetForJIRASettingsPanel(settingsPanelId, 
				new Model<WidgetForJira>(this));
	}

	
	public Project getProject(String projName) {
		Project pro = null;
		if (projName == null || projName.isEmpty()) {
			projName = "U-QASAR Platform Development";
		}
		TreeNodeService treeNodeService = null;
		try {
			InitialContext ic = new InitialContext();
			treeNodeService = (TreeNodeService) ic.lookup("java:module/TreeNodeService");
			pro = treeNodeService.getProjectByName(projName);		
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return pro;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<JiraMetricMeasurement> getMeasurements(Project project, String timeInterval) {		
		
		List<JiraMetricMeasurement> measurements = new ArrayList<JiraMetricMeasurement>();
		try {
			InitialContext ic = new InitialContext();
			JiraDataService dataService = (JiraDataService) ic.lookup("java:module/JiraDataService");

			if (dataService != null) {
				Date latestSnapshotDate = dataService.getLatestDate();
				if (latestSnapshotDate != null) {
					for (String metric : UQasarUtil.getJiraMetricNames()) {
					    List<JiraMetricMeasurement> metricMeasurements = new LinkedList<JiraMetricMeasurement>();
					    if (timeInterval.compareToIgnoreCase("Latest") == 0) {
					        metricMeasurements =         
					            dataService.getMeasurementsPerProjectByMetricWithLatestDate(project, metric);
					    } else {
					        metricMeasurements = 
					                dataService.getMeasurementsPerProjectByMetricWithinPeriod(project, metric, timeInterval);
					    }
						if (metricMeasurements != null && metricMeasurements.size() > 0) {
							measurements.add(metricMeasurements.get(0));
						}
					}
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (uQasarException e) {
			e.printStackTrace();
		}
		return measurements;
	}
}
