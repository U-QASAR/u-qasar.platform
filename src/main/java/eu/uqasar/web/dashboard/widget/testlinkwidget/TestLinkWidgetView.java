package eu.uqasar.web.dashboard.widget.testlinkwidget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import eu.uqasar.model.measure.TestLinkMetricMeasurement;
import eu.uqasar.service.dataadapter.TestLinkDataService;

public class TestLinkWidgetView extends WidgetView {

	TestLinkDataService TDT;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1947241458043860995L;
	private String period, individualMetric;	
	public TestLinkWidgetView(String id, IModel<Widget> model) {
		super(id, model);

		final TestLinkWidget testLinkWidget = (TestLinkWidget) model.getObject();
		
		try {
			InitialContext ic = new InitialContext();
			TDT = (TestLinkDataService) ic.lookup("java:module/TestLinkDataService");			
		} catch (NamingException e) {
			e.printStackTrace();
		}

		if(testLinkWidget.getSettings().get("period")!=null){
			period = testLinkWidget.getSettings().get("period");
		} else {
			period = "Latest";
		}
		
		if(testLinkWidget.getSettings().get("project") != null && !testLinkWidget.getSettings().get("project").isEmpty()){
			String name = testLinkWidget.getSettings().get("project");
		}
		
		LoadableDetachableModel<List<TestLinkMetricMeasurement>> metricsData = new LoadableDetachableModel<List<TestLinkMetricMeasurement>>() {
			private static final long serialVersionUID = -8120427341331851718L;

			@Override
			protected List<TestLinkMetricMeasurement> load() {
				Date latestSnapshotDate = TDT.getLatestDate();
				if (latestSnapshotDate != null) {
				    if(period.compareToIgnoreCase("Latest") == 0){
				        return TDT.getMeasurementsForProjectByLatestDate("UQASAR");
				    }else{
				        return TDT.getMeasurementsForProjectByPeriod("UQASAR", period);
				    }
				} else {
					return new ArrayList<TestLinkMetricMeasurement>();
				}
			}
		};
		
		Chart chart;
        individualMetric = testLinkWidget.getSettings().get("individualMetric"); 
        if( individualMetric  !=null){
            chart = new Chart("chart", testLinkWidget.getChartOptionsDifferently(metricsData.getObject(),individualMetric ));
        } 
        else {
            chart = new Chart("chart", testLinkWidget.getChartOptions(metricsData.getObject()));    
        }
		// Add TestLink image
		add(new ContextImage("img", "assets/img/testlink.png"));
		add(chart);
	}
	
}
