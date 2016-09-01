package eu.uqasar.web.dashboard.widget.reportingwidget;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import eu.uqasar.model.measure.ReportMeasurement;

/**
 * 
 *
 *
 */
public class ReportingWidgetView extends WidgetView {

    private static final long serialVersionUID = 2880002420318496717L;
    private transient List<ReportMeasurement> measurements;
    private final String cube;
    private final String chartType;
    private final WebMarkupContainer reportWMC;
    private ListView<ReportMeasurement> reportView;

    private final String urlToLoad;

    public ReportingWidgetView(String id, IModel<Widget> model) {
        super(id, model);

        final ReportingWidget qualityWidget = (ReportingWidget) model.getObject();

        // Create a quarry for Report Generation.
        cube = qualityWidget.getSettings().get("cube");
        chartType = qualityWidget.getSettings().get("chartType");

        urlToLoad = qualityWidget.getSettings().get("urlToLoad");
        Form<Void> formVoid = new Form<>("formVoid");

        LoadableDetachableModel<List<ReportMeasurement>> mdl = new LoadableDetachableModel<List<ReportMeasurement>>() {
            private static final long serialVersionUID = 1L;

            protected List<ReportMeasurement> load() {
                measurements = qualityWidget.getMeasurements(cube, urlToLoad);
                return measurements;
            }
        };

        Chart chart;
        chart = new Chart("chart", qualityWidget.getChartOptions(mdl.getObject(), chartType));
        add(chart);

        add(formVoid);

        reportWMC = new WebMarkupContainer("reportWMC", new Model<ReportMeasurement>());
        reportWMC.setOutputMarkupId(true);
        formVoid.add(reportWMC);

        if (measurements.size() != 0) {
            reportWMC.add(new Label("type", measurements.get(0).getReportType()));
        } else {
            reportWMC.add(new Label("type", "Rule"));
        }

        reportWMC.add(reportView = new ListView<ReportMeasurement>("reportListView", Model.ofList(measurements)) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onConfigure() {
                super.onConfigure();
                // update model
                reportView.setModelObject(measurements);
            }

            @Override
            protected void populateItem(ListItem<ReportMeasurement> item) {

                final ReportMeasurement proposedReport = item.getModelObject();

                item.add(new Label("value", proposedReport.getReportValue()));
                item.add(new Label("count", proposedReport.getCount()));
            }
        });

    }

}
