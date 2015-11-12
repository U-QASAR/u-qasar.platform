package eu.uqasar.web.pages.qmtree.qmodels;

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

import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.qmodels.panels.QModelEditPanel;

public class QModelEditPage extends QMBaseTreePage<QModel> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 451083867628959546L;
	
	private QModelEditPanel panel;
	
	/**
	 * isNew = true if accessing in "create" mode
	 */
	private boolean isNew = false;
	
	public QModelEditPage(PageParameters parameters) {
		super(parameters);
		if (parameters!=null && parameters.get("isNew").toString()!=null && parameters.get("isNew").toString().equals("true")){
					 isNew = true;
		}
	}

	@Override
	public WebMarkupContainer getContent(final String markupId,
			final IModel<QModel> model) {
		panel = new QModelEditPanel(markupId, model, isNew);
		return panel;
	}
}
