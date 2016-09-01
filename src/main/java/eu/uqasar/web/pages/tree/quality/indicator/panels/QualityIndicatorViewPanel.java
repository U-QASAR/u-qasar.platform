package eu.uqasar.web.pages.tree.quality.indicator.panels;

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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;
import eu.uqasar.service.HistoricalDataService;
import eu.uqasar.web.i18n.Language;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.historic.baseindicator.HistoricBaseIndicatorPage;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;
import eu.uqasar.web.pages.tree.panels.charts.BaseTrendChartPanel;
import eu.uqasar.web.pages.tree.panels.thresholds.ThresholdIndicator;
import eu.uqasar.web.pages.tree.quality.indicator.QualityIndicatorEditPage;

public class QualityIndicatorViewPanel extends BaseTreePanel<QualityIndicator> {

	@Inject
	HistoricalDataService historicalDataService;
	
	private static final long serialVersionUID = 3951719587911193001L;

	public QualityIndicatorViewPanel(String id, IModel<QualityIndicator> model) {
		super(id, model);

		final QualityIndicator qi = model.getObject();
		final QualityStatus qs = model.getObject().getQualityStatus();

		add(new Label("name", new PropertyModel<>(model, "name")));
		
		Label iconLabel = new Label("icon");
		iconLabel.add(new CssClassNameAppender(qi.getIconType().cssClassName()));
		iconLabel.add(new AttributeAppender("class", new Model (qs.getCssClassName()), " "));
		add(iconLabel);

		// add quality status and value
		WebMarkupContainer qualityStatus = new WebMarkupContainer(
				"quality.status");
		qualityStatus.add(new CssClassNameAppender(qs.getCssClassName()));
		Label qualityValue = new Label("quality.value", Model.of(Math.round(qi.getValue())));
		qualityValue.add(new CssClassNameAppender(qs.getCssClassName()));
		qualityStatus.add(qualityValue);
		add(qualityStatus);

		// TODO only show if authorized to edit
		BookmarkablePageLink<QualityIndicatorEditPage> editLink = new BookmarkablePageLink<>(
				"link.edit", QualityIndicatorEditPage.class,
				BaseTreePage.forQualityIndicator(qi));
		add(editLink);

		// Type
		add(new Label("type", qi.getIndicatorType().getLabelModel())
				.setRenderBodyOnly(true));
		add(new Icon("type.icon", qi.getIndicatorType().getIconType()));

		// Purpose
		add(new Label("purpose", qi.getIndicatorPurpose()
				.getLabelModel()).setRenderBodyOnly(true));

		// LifeCycle Stage
		add(new Label("lcStage", qi.getLifeCycleStage().getLabelModel())
				.setRenderBodyOnly(true));

		// Description
		add(new Label("description", new PropertyModel<String>(model,
				"description")).setEscapeModelStrings(false));

		// Threshold indicator
		add(new ThresholdIndicator<>("thresholdIndicator", model));

		// Indicator target value 
		add(new Label("targetValue", qi.getTargetValue()));
		
		// Weight
		add(new Label("weight", qi.getWeight()));

		Date lastUpdated = qi.getLastUpdated();
		String lastUpdatedStr = "";
		if (lastUpdated != null) {
			lastUpdatedStr = Language.fromSession().formatDateTimeLocalized(lastUpdated);
		}
		add(new Label("last.update", lastUpdatedStr).setRenderBodyOnly(true));
		
		// Formula usage
		if(qi.getUseFormula()){
			add(new Label("viewFormula", new PropertyModel<String>(model, "viewFormula")).setEscapeModelStrings(false));
		} else {
			add(new Label("viewFormula", new StringResourceModel("label.non.use.formula", this, null)));
		}
		
		// add quality trend chart
		// TODO: Replace this with real an example utilizing real data
		add(new BaseTrendChartPanel<>("trend", model));
		
		// Historical data panel with the last 10 values
		RepeatingView listOfHistValues = new RepeatingView("listOfHistValues");
		List<HistoricValuesBaseIndicator> historicValues = historicalDataService.getHistValuesForBaseInd(qi.getId(), 0, 10);
		add(new Label("noOfRegisters", historicalDataService.countHistValuesForBaseIndicator(qi.getId())));
		add(new BookmarkablePageLink<HistoricBaseIndicatorPage>(
            	"historical", HistoricBaseIndicatorPage.class, 
            	BaseTreePage.forQualityIndicator(qi)));
		
		for (HistoricValuesBaseIndicator value : historicValues) {
			listOfHistValues.add(new Label(listOfHistValues.newChildId(),
					value.getBaseIndicator().getValue() + "/" +
					value.getDate().toString()));
		}	
		
		add(listOfHistValues);		
	}
}
