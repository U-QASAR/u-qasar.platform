package eu.uqasar.web.dashboard.widget.sonarqualitywidget;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import eu.uqasar.model.measure.SonarMetricMeasurement;

public class SonarQualityWidgetView extends WidgetView {

	private static final long serialVersionUID = 2880002420318496717L;
	private transient List<SonarMetricMeasurement> measurements;
	private String period;
	private String individualMetric;
	public SonarQualityWidgetView(String id, IModel<Widget> model) {
		super(id, model);

		final SonarQualityWidget qualityWidget = (SonarQualityWidget) model.getObject();
				
		if(qualityWidget.getSettings().get("period")!=null){
			period = qualityWidget.getSettings().get("period");
		} else {
		    // if there is no selection of period then it should be the latest values 
			period = "Latest";
		}
		
		System.out.println("period: "+period);
		
		LoadableDetachableModel<List<SonarMetricMeasurement>> mdl = new LoadableDetachableModel<List<SonarMetricMeasurement>>() {
			private static final long serialVersionUID = 1L;

			protected List<SonarMetricMeasurement> load() {
				measurements = qualityWidget.getMeasurements(period);
				System.out.println("measurements : "+measurements );
				return measurements;
			}
		};
		
		Chart chart;
		individualMetric = qualityWidget.getSettings().get("individualMetric"); 
		if( individualMetric  !=null){
		    chart = new Chart("chart", qualityWidget.getChartOptionsDifferently(mdl.getObject(),individualMetric ));
        } 
        else {
            chart = new Chart("chart", qualityWidget.getChartOptions(mdl.getObject()));
        }
		

		add(chart);

	}	
}
