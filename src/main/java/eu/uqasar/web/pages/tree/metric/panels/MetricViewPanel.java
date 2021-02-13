package eu.uqasar.web.pages.tree.metric.panels;

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


import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;
import eu.uqasar.service.HistoricalDataService;
import eu.uqasar.service.dataadapter.AdapterSettingsService;
import eu.uqasar.web.i18n.Language;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.historic.baseindicator.HistoricBaseIndicatorPage;
import eu.uqasar.web.pages.tree.metric.MetricEditPage;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;
import eu.uqasar.web.pages.tree.panels.thresholds.ThresholdIndicator;

public class MetricViewPanel extends BaseTreePanel<Metric> {

	@Inject
	HistoricalDataService historicalDataService;

	@Inject
	AdapterSettingsService adapterSettingsService;

	private static final long serialVersionUID = -2389825567459556163L;

	public MetricViewPanel(String id, IModel<Metric> model) {
		super(id, model);

		Metric metric = model.getObject();
		QualityStatus qs =  metric.getQualityStatus();

		add(new Label("name", new PropertyModel<>(model, "name")));

		Label iconLabel = new Label("icon");
		iconLabel.add(new CssClassNameAppender(metric.getIconType().cssClassName()));
		iconLabel.add(new AttributeAppender("class", new Model(qs.getCssClassName()), " "));
		add(iconLabel);

		// TODO only show if authorized to edit
		BookmarkablePageLink<MetricEditPage> editLink = new BookmarkablePageLink<>(
				"link.edit", MetricEditPage.class,
				BaseTreePage.forMetric(metric));
		add(editLink);

		// Purpose
		add(new Label("purpose", metric.getIndicatorPurpose().getLabelModel()).setRenderBodyOnly(true));

		// LifeCycle Stage
		add(new Label("lcStage", metric.getLifeCycleStage().getLabelModel()).setRenderBodyOnly(true));

		// add quality status and value
		WebMarkupContainer qualityStatus = new WebMarkupContainer(
				"quality.status");
		qualityStatus.add(new CssClassNameAppender(qs.getCssClassName()));
		qualityStatus.add(new Label("unit", new PropertyModel<>(model, "unit")));
		add(qualityStatus);

		add(new Label("value", metric.getValue()));
		add(new Label("targetValue", metric.getTargetValue()));

		add(new Label("description", new PropertyModel<String>(model,
				"description")).setEscapeModelStrings(false));

		// Source of metrics data
		add(new Label("metricSource", metric.getMetricSource()).setRenderBodyOnly(true));

		// Drill down metric Icon to visit the result on the platform provider
		add(drillDownMetric("metricDrill",metric));

		// Metric type
		add(new Label("metricType", metric.getMetricType()).setRenderBodyOnly(true));

		// Threshold indicator
		add(new ThresholdIndicator<>("thresholdIndicator", model));

		add(new Label("weight", metric.getWeight()).setRenderBodyOnly(true));

		Date lastUpdated = metric.getLastUpdated();
		String lastUpdatedStr = "";
		if (lastUpdated != null) {
			lastUpdatedStr = Language.fromSession().formatDateTimeLocalized(lastUpdated);
		}
		add(new Label("lastUpdated", lastUpdatedStr).setRenderBodyOnly(true));
		add(new Label("suggestion", metric.getSuggestionType()).setRenderBodyOnly(true));

		// Historical data panel with the last 10 values
		RepeatingView listOfHistValues = new RepeatingView("listOfHistValues");
		List<HistoricValuesBaseIndicator> historicValues = historicalDataService.getHistValuesForBaseInd(metric.getId(), 0, 10);
		add(new Label("noOfRegisters", historicalDataService.countHistValuesForBaseIndicator(metric.getId())));
		add(new BookmarkablePageLink<HistoricBaseIndicatorPage>(
				"historical", HistoricBaseIndicatorPage.class, 
				BaseTreePage.forMetric(metric)));

		for (HistoricValuesBaseIndicator value : historicValues) {
			listOfHistValues.add(new Label(listOfHistValues.newChildId(),
					value.getValue() + " / " +
							value.getDate().toString()));
		}	

		add(listOfHistValues);		
	}


	/**
	 * @param metric
	 * @return
	 */
	private ExternalLink drillDownMetric(String mark, Metric metric) {
		ExternalLink link = null;
		MetricSource ms = metric.getMetricSource();
		String linkString = "";

		// TODO: Maybe this could be replaced by a database related link between
		// Metric and AdapterSetting
		if (metric.getMetricType() != null) {

			AdapterSettings adapterSettings = adapterSettingsService
					.getAdapterByProjectAndMetricSource(metric.getProject(),
							metric.getMetricSource());

			if (adapterSettings !=null){
				linkString += adapterSettings.getUrl();

				// Add a slash to the end of the string if not there yet
				if (!linkString.endsWith("/")) {
					linkString += "/";
				}

				if (ms.equals(MetricSource.StaticAnalysis)) {
					linkString += "drilldown/measures/"
							//+ adapterSettings.getAdapterProject()
							// TODO: improve to remove this hardcored value
							+ "13" // This number belongs to UQ in SonarQube platform
							+ "?metric="
							+ metric.getMetricType().toLowerCase();
				} else if (ms.equals(MetricSource.CubeAnalysis)) {
					linkString += metric.getMetricType();
				} else if (ms.equals(MetricSource.IssueTracker)) {
					linkString += "issues/?jql=resolution%3DFixed";
					// TODO: improve to remove this hardcored value
					// implement types from:
					// https://github.com/IntrasoftInternational/JiraAdapter/blob/master/src/main/java/eu/uqasar/jira/adapter/JiraAdapter.java
				} else if (ms.equals(MetricSource.TestingFramework)) {
					//TODO: improve to apply other testing framworks
					int ind = linkString.indexOf("lib/api/xmlrpc/v1/xmlrpc.php");
					if (ind != 0) {
						linkString = linkString.substring(0,ind);
					}
				}
			}
		}

		link = new ExternalLink(mark, linkString){
			/**
			 */
			private static final long serialVersionUID = 2989592528503646437L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("target","_blank");
			}
		};

		// tooltip config
		TooltipConfig confConfig = new TooltipConfig()
		.withPlacement(TooltipConfig.Placement.top);
		link.add(new TooltipBehavior(Model.of(linkString), confConfig));

		// Set invisible for manual metrics
		if (metric.getMetricType() == null) link.setVisible(false);

		return link;
	}

}
