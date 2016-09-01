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


import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOption;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jboss.solder.logging.Logger;

import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.util.io.exporter.QModelJsonWriter;
import eu.uqasar.util.io.exporter.QModelXmlWriter;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;



public class QModelExportPage extends BasePage {

	private static final long serialVersionUID = 3828016161086818909L;

	private final Logger logger = Logger.getLogger(QModelExportPage.class);

	@Inject
	private QMTreeNodeService treeNodeService;

	private QModel currentQmodel;

	private final String selected = "Json";

	public QModelExportPage(PageParameters parameters) {
		super(parameters);

		final List<QModel> qmodels = treeNodeService.getAllQModels();

		if (qmodels.isEmpty()) {
			// TODO what to show if no qmodels to export?
			setResponsePage(AboutPage.class); // handle no qmodel?
		}

		Form<?> form = new Form<Void>("form") {
			private static final long serialVersionUID = -5807663754036839439L;

			@Override
			protected void onSubmit() {
				logger.info("onSubmit start");
				File file = null;
				String name = "";
				QModel p = treeNodeService.getQModel(currentQmodel.getId());
							
				try {
					
				switch (selected) {
					case "Json":
						{
							QModelJsonWriter writer = new QModelJsonWriter();
							file = writer.createJsonFile(p);
							name = file.getName();
							break;
						}
					default:
						{
							QModelXmlWriter writer = new QModelXmlWriter();
							file = writer.createXmlFile(p);
							name = file.getName();
							break;
						}
				}

				/* start download */
				IResourceStream stream = new FileResourceStream(file);
				getRequestCycle().scheduleRequestHandlerAfterCurrent(
						new ResourceStreamRequestHandler(stream)
						.setContentDisposition(
								ContentDisposition.ATTACHMENT).setFileName(
										name));
				
				logger.info("onSubmit end");
				} catch (JAXBException | JsonGenerationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		currentQmodel = qmodels.get(0);

		DropDownChoice<QModel> dropDown = 
				new DropDownChoice<>("qmodels", 
						new PropertyModel<QModel>(this, "currentQmodel"), 
							qmodels);
		form.add(dropDown);

		Select<String> formats = new Select<>("selector", 
				new PropertyModel<String>(this, "selected"));
		form.add(formats);

		formats.add(new SelectOption<>("filetype1", 
				new Model<>("Json")));
		formats.add(new SelectOption<>("filetype2", 
				new Model<>("Xml")));

		add(form);
	}
}
