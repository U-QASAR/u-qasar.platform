package eu.uqasar.web.pages.qmtree.quality.objective;

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


import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.quality.objective.panels.QualityObjectiveEditPanel;

public class QMQualityObjectiveEditPage extends QMBaseTreePage<QMQualityObjective> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8152232417246636501L;
	
	private QualityObjectiveEditPanel panel;
	
	/**
	 * isNew = true if accessing in "create" mode
	 */
	private boolean isNew = false;
	
	public QMQualityObjectiveEditPage(PageParameters parameters) {
		super(parameters);
		if (parameters!=null && parameters.get("isNew").toString()!=null && parameters.get("isNew").toString().equals("true")){
			 isNew = true;
		}
	}

	@Override
	public WebMarkupContainer getContent(String markupId,
			IModel<QMQualityObjective> model) {
		
		panel = new QualityObjectiveEditPanel(markupId, model, isNew);
		return panel;
	}

}
