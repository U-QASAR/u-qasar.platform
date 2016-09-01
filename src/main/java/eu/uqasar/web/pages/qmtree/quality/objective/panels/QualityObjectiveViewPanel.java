package eu.uqasar.web.pages.qmtree.quality.objective.panels;

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
import java.util.Iterator;

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
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.quality.indicator.Domain;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.role.Role;
import eu.uqasar.service.meta.MetaDataService;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.panels.QMBaseTreePanel;
import eu.uqasar.web.pages.qmtree.quality.objective.QMQualityObjectiveEditPage;

public class QualityObjectiveViewPanel extends QMBaseTreePanel<QMQualityObjective> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1616097622736317089L;

	//container for purpose and its attributes
	private final WebMarkupContainer purposeAtt = new WebMarkupContainer("purposeAtt");

	private final WebMarkupContainer wmcQModelTagData;

    @Inject
    @Named(MetaDataService.NAME)
    private MetaDataService metaDataService;

    
	public QualityObjectiveViewPanel(String id, IModel<QMQualityObjective> model) {
		super(id, model);

		QMQualityObjective indicator = model.getObject();

		// Name
		add(new Label("name", new PropertyModel<>(model, "name")));

		//Type
		add(new Label("icon").add(new CssClassNameAppender(indicator
				.getIconType().cssClassName())));

		//Domain
		Iterator<Domain> itd = indicator.getDomain().iterator();
		StringBuffer domains = new StringBuffer();
		while (itd.hasNext()){
			domains.append(itd.next().toString()).append(" ");
		}
		add(new Label("domain", domains).setRenderBodyOnly(true));


		// Purpose
		add(new Label("purpose", indicator.getIndicatorPurpose()
				.getLabelModel()).setRenderBodyOnly(true));

		Label attLabel;
		Label attContent;
		if (indicator.getIndicatorPurpose().equals(Purpose.Process)){
			// Paradigm
			attLabel = new Label("attLabel", new StringResourceModel("label.objective.paradigm",this, null));
			attContent = new Label("attContent", indicator.getParadigm().getLabelModel());
		} else {
			//version
			attLabel = new Label("attLabel",  new StringResourceModel("label.objective.version",this, null));
			attContent = new Label("attContent", indicator.getVersion().getLabelModel());
		}
		purposeAtt.add(attLabel);
		purposeAtt.add(attContent);
		add(purposeAtt);


		// Description
		add(new Label("description", new PropertyModel<String>(model,
				"description")).setEscapeModelStrings(false));

		// Target value
		Iterator<Role> it = indicator.getTargetAudience().iterator();
		StringBuffer roles = new StringBuffer();
		while (it.hasNext()){
			roles.append(it.next().toString()).append(" ");
		}
		add(new Label("targetAudience", roles).setRenderBodyOnly(true));

		//Lower and upper limits
		if (Double.MIN_VALUE == indicator.getLowerLimit() && Double.MAX_VALUE == indicator.getUpperLimit()){
			add(new Label("limitsLabel", ""));
			add(new Label("upperLabel", ""));
			add(new Label("upperLimit", ""));	
			add(new Label("lowerLabel", ""));
			add(new Label("lowerLimit", ""));
		} else {
			add(new Label("limitsLabel", new StringResourceModel("label.objective.limits",this, null)));
			if (Double.MIN_VALUE != indicator.getLowerLimit()) {
				add(new Label("lowerLabel", new StringResourceModel("label.objective.lower.limit",this, null)));
				add(new Label("lowerLimit", new PropertyModel<>(model, "lowerLimit")));
			} else {
				add(new Label("lowerLabel", ""));
				add(new Label("lowerLimit", ""));			
			}
			if (Double.MAX_VALUE != indicator.getUpperLimit()) {
				add(new Label("upperLabel", new StringResourceModel("label.objective.upper.limit",this, null)));
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

		//TODO only show if authorized to edit
		BookmarkablePageLink<QMQualityObjectiveEditPage> editLink = new BookmarkablePageLink<>(
				"link.edit", QMQualityObjectiveEditPage.class,
				QMBaseTreePage.forQualityObjective(indicator));

		add(editLink);
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
