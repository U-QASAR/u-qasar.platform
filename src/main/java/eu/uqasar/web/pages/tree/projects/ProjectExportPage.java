package eu.uqasar.web.pages.tree.projects;

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
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import eu.uqasar.model.tree.Project;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.util.io.exporter.QProjectJsonWriter;
import eu.uqasar.util.io.exporter.QProjectXmlWriter;
import eu.uqasar.web.pages.AboutPage;
import eu.uqasar.web.pages.BasePage;


public class ProjectExportPage extends BasePage {

	private static final long serialVersionUID = 3828016161086818909L;

	@Inject
	private TreeNodeService treeNodeService;

	private Project project;
	private final String selected = "Json";

	public ProjectExportPage(PageParameters parameters) {
		super(parameters);

		final List<Project> projects = treeNodeService.getAllProjectsOfLoggedInUser();

		if (projects.isEmpty()) {
			// TODO what if there are no projects?!
			setResponsePage(AboutPage.class);
		}

		Form<?> form = new Form<Void>("form") {
			private static final long serialVersionUID = -5807663754036839439L;

			@Override
			protected void onSubmit() {
				File file;
				String name;
				if (project != null) {
					
					Project p = treeNodeService.getProject(project.getId());
					
					try {
						
					switch (selected) {
						case "Json":
							{
								QProjectJsonWriter writer = new QProjectJsonWriter();
								file = writer.createJsonFile(p);
								name = file.getName();
								break;
							}
						default:
							{
								QProjectXmlWriter writer = new QProjectXmlWriter();
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
					
					} catch (JAXBException | JsonGenerationException e){
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		DropDownChoice<Project> dropDown
				= new DropDownChoice<>("projects",
						new PropertyModel<Project>(this, "project"), projects);
		form.add(dropDown);

		Select<String> formats = new Select<>("selector",
				new PropertyModel<String>(this, "selected"));
		form.add(formats);

		formats.add(new SelectOption<>("filetype1",	new Model<>("Json")));
		formats.add(new SelectOption<>("filetype2",	new Model<>("Xml")));

		form.add(new Button("exportProject", new StringResourceModel("export.button", this, null)));

		add(form);
	}
}
