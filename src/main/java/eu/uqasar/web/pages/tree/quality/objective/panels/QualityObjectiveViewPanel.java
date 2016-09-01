package eu.uqasar.web.pages.tree.quality.objective.panels;

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


import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.model.tree.historic.HistoricValuesBaseIndicator;
import eu.uqasar.service.HistoricalDataService;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.historic.baseindicator.HistoricBaseIndicatorPage;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;
import eu.uqasar.web.pages.tree.panels.thresholds.ThresholdIndicator;
import eu.uqasar.web.pages.tree.quality.objective.QualityObjectiveEditPage;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import javax.inject.Inject;
import java.util.List;

public class QualityObjectiveViewPanel extends BaseTreePanel<QualityObjective> {
	
	@Inject
	HistoricalDataService historicalDataService;

	private static final long serialVersionUID = 4415622933806769297L;

	public QualityObjectiveViewPanel(String id, IModel<QualityObjective> model) {
		super(id, model);

		QualityObjective qo = model.getObject();
		QualityStatus qs = model.getObject().getQualityStatus();

		add(new Label("name", new PropertyModel<>(model, "name")));
		
		Label iconLabel = new Label("icon");
		iconLabel.add(new CssClassNameAppender(qo.getIconType().cssClassName()));
		iconLabel.add(new AttributeAppender("class", new Model(qs.getCssClassName()), " "));
		add(iconLabel);
		
		// add quality status and value
		WebMarkupContainer qualityStatus = new WebMarkupContainer(
				"quality.status");
		qualityStatus.add(new CssClassNameAppender(qs.getCssClassName()));
		Label qualityValue = new Label("quality.value", Model.of(Math.round(qo.getValue())));
		qualityValue.add(new CssClassNameAppender(qs.getCssClassName()));
		qualityStatus.add(qualityValue);
		add(qualityStatus);

		// add description
		add(new Label("description", new PropertyModel<String>(model,
				"description")).setEscapeModelStrings(false));

		// Threshold indicator
		add(new ThresholdIndicator<>("thresholdIndicator", model));
		
		// Target value
		add(new Label("targetValue", qo.getTargetValue()));

		// Weight
		add(new Label("weight", qo.getWeight()));
		
		// Formula usage
		if(qo.getUseFormula()){
			add(new Label("formula", new PropertyModel<String>(model, "viewFormula")).setEscapeModelStrings(false));
		} else {
			add(new Label("formula", new StringResourceModel("label.non.use.formula", this, null)));
		}
		// Suggestion based on the contextindicator
		add(new Label("suggestion", qo.getSuggestionType()));		
		
		// TODO only show if authorized to edit
		BookmarkablePageLink<QualityObjectiveEditPage> editLink = new BookmarkablePageLink<>(
				"link.edit", QualityObjectiveEditPage.class,
				BaseTreePage.forQualityObjective(qo));
		add(editLink);
		
		// Historical data panel with the last 10 values
		RepeatingView listOfHistValues = new RepeatingView("listOfHistValues");
		List<HistoricValuesBaseIndicator> historicValues = historicalDataService.getHistValuesForBaseInd(qo.getId(), 0, 10);
		add(new Label("noOfRegisters", historicalDataService.countHistValuesForBaseIndicator(qo.getId())));
		add(new BookmarkablePageLink<HistoricBaseIndicatorPage>(
            	"historical", HistoricBaseIndicatorPage.class, 
            	BaseTreePage.forQualityObjective(qo)));
		
		for (HistoricValuesBaseIndicator value : historicValues) {
			listOfHistValues.add(new Label(listOfHistValues.newChildId(),
					value.getValue() + " / " +
					value.getDate().toString()));
		}			
		
		add(listOfHistValues);
    }
}
