package eu.uqasar.web.pages.qmtree.metric.panels;

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


import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import eu.uqasar.model.meta.QModelTagData;
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.service.meta.MetaDataService;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.metric.QMMetricEditPage;
import eu.uqasar.web.pages.qmtree.panels.QMBaseTreePanel;

public class MetricViewPanel extends QMBaseTreePanel<QMMetric> {



	/**
	 * 
	 */
	private static final long serialVersionUID = -1159207180589810877L;

	private final WebMarkupContainer wmcQModelTagData;

    @Inject
    @Named(MetaDataService.NAME)
    private MetaDataService metaDataService;

	public MetricViewPanel(String id, IModel<QMMetric> model) {
		super(id, model);

		QMMetric metric = model.getObject();

		add(new Label("name", new PropertyModel<>(model, "name")));
		add(new Label("icon").add(new CssClassNameAppender(metric.getIconType()
				.cssClassName())));
		// TODO only show if authorized to edit
		BookmarkablePageLink<QMMetricEditPage> editLink = new BookmarkablePageLink<>(
				"link.edit", QMMetricEditPage.class,
				QMBaseTreePage.forMetric(metric));
		add(editLink);

		//Metric source
		add(new Label("source", metric.getSource().getLabelModel()).setRenderBodyOnly(true));
		
		//Scale
		add(new Label("scale", metric.getScale().getLabelModel()).setRenderBodyOnly(true));
		
		//Unit
		add(new Label("unit", metric.getUnit().getLabelModel()).setRenderBodyOnly(true));

		add(new Label("description", new PropertyModel<String>(model,
				"description")).setEscapeModelStrings(false));

		
		//Lower and upper limits
		if (Double.MIN_VALUE == metric.getLowerLimit() && Double.MAX_VALUE == metric.getUpperLimit()){
			add(new Label("limitsLabel", ""));
			add(new Label("upperLabel", ""));
			add(new Label("upperLimit", ""));	
			add(new Label("lowerLabel", ""));
			add(new Label("lowerLimit", ""));
		} else {
			add(new Label("limitsLabel", new StringResourceModel("label.metric.limits",this, null)));
			if (Double.MIN_VALUE != metric.getLowerLimit()) {
				add(new Label("lowerLabel", new StringResourceModel("label.metric.lower.limit",this, null)));
				add(new Label("lowerLimit", new PropertyModel<>(model, "lowerLimit")));
			} else {
				add(new Label("lowerLabel", ""));
				add(new Label("lowerLimit", ""));			
			}
			if (Double.MAX_VALUE != metric.getUpperLimit()) {
				add(new Label("upperLabel", new StringResourceModel("label.metric.upper.limit",this, null)));
				add(new Label("upperLimit", new PropertyModel<>(model, "upperLimit")));
			} else {
				add(new Label("upperLabel", ""));
				add(new Label("upperLimit", ""));			
			}	
		}

		
		//Sprint 2. Added QProject attributes
		//targetValue
		add(new Label("targetValue", new PropertyModel<>(model, "targetValue")));
		// Weight
		add(new Label("weight", new PropertyModel<>(model, "weight")));

		//metadata
		wmcQModelTagData = new WebMarkupContainer("wmcQModelTagData");
		wmcQModelTagData.add(newLabelWithWMC("qModelTagData", new PropertyModel<Collection<QModelTagData>>(model, "qModelTagData"), wmcQModelTagData));
		add(wmcQModelTagData);
	}

	private <T> Label newLabelWithWMC(String markUpId, PropertyModel<Collection<T>> model, final WebMarkupContainer wmc){
		return new Label(markUpId, model){
			@Override
			public void onConfigure(){
				super.onConfigure();
				setDefaultModel(replaceBracketsModel(this));
				if(this.getDefaultModelObjectAsString().isEmpty()){
					wmc.setVisible(false);
				}
			}
		};
	}
	
	private IModel<?> replaceBracketsModel(Label label) {
		return Model.of(label.getDefaultModelObjectAsString().replace("[", "").replace("]", ""));
	}
}
