/*
 */
package eu.uqasar.web.pages.admin.qmodel;

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


import javax.inject.Inject;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.solder.logging.Logger;

import eu.uqasar.model.settings.qmodel.QModelSettings;
import eu.uqasar.service.settings.QModelSettingsService;
import eu.uqasar.web.pages.admin.AdminBasePage;


public class QModelSettingsPage extends AdminBasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;
	
	@Inject
	private QModelSettingsService qmService;

	private final Form<Void> form;
	private final TextField<String> highField, highEntityField, mediumField, mediumEntityField, lowField, lowEntityField;
	
	private final QModelSettings settings;

	public QModelSettingsPage(PageParameters pageParameters) {
		super(pageParameters);
		settings = qmService.get(new QModelSettings());
		form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				String message = new StringResourceModel("save.confirmed", this, null).getString();
				qmService.update(settings);
				getPage().success(message);
			    setResponsePage(getPage());
			}
		};

		highField = new TextField<>("high", new PropertyModel<String>(settings, "high"));
		highField.setRequired(true);
		form.add(highField);

		
		highEntityField = new TextField<>("highEntity", new PropertyModel<String>(settings, "highEntity"));
		highEntityField.setRequired(true);
		form.add(highEntityField);
		
		
		mediumField = new TextField<>("medium", new PropertyModel<String>(settings, "medium"));
		mediumField.setRequired(true);
		form.add(mediumField);
		
		mediumEntityField = new TextField<>("mediumEntity", new PropertyModel<String>(settings, "mediumEntity"));
		mediumEntityField.setRequired(true);
		form.add(mediumEntityField);

		lowField = new TextField<>("low", new PropertyModel<String>(settings, "low"));
		lowField.setRequired(true);
		form.add(lowField);

		lowEntityField = new TextField<>("lowEntity", new PropertyModel<String>(settings, "lowEntity"));
		lowEntityField.setRequired(true);
		form.add(lowEntityField);
		
		add(form);
	}

}
