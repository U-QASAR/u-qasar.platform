package eu.uqasar.web.components.navigation;

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


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuHeader;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import eu.uqasar.model.tree.ITreeNode;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.projects.ProjectExportPage;
import eu.uqasar.web.pages.tree.projects.ProjectImportPage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;
import eu.uqasar.web.pages.tree.projects.ProjectWizardPage;
import eu.uqasar.web.pages.tree.visual.VisualPage;
 
public class ProjectsSubMenu extends NavbarDropDownButton {

	private static final long serialVersionUID = 2708741180794473231L;

	@Inject
	private TreeNodeService treeNodeService;
	
	public ProjectsSubMenu(IModel<String> labelModel) {
		super(labelModel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		get("dropdown-menu").add(new CssClassNameAppender("projects"));
		getParent().add(new AttributeModifier("id", "projectsMenu"));
	}

	@Override
	public boolean isActive(Component item) {
		return false;
	}

	@Override
	protected List<AbstractLink> newSubMenuButtons(final String buttonMarkupId) {
		final List<AbstractLink> subMenu = new ArrayList<>();
		final List<Project> projects = 	treeNodeService.getAllProjectsOfLoggedInUser();

		subMenu.add(new MenuHeader(new StringResourceModel("menu.projects.recently.updated", this, null)));
		for (ITreeNode<String> projectNode : projects) {
			Project project = (Project) projectNode;
			MenuBookmarkablePageLink<ProjectViewPage> projectLink = new MenuBookmarkablePageLink<>(
					ProjectViewPage.class, BaseTreePage.forNode(projectNode),
					Model.of(project.getName()));
			projectLink.setIconType(new IconType("sitemap"));
			projectLink.add(new CssClassNameAppender("project", project
					.getQualityStatus().getCssClassName()));
			subMenu.add(projectLink);
		}

		subMenu.add(HeaderNavigationBar.createMenuDevider());
		PageParameters params = new PageParameters();
		params.add("isNew", true);
                
                MenuBookmarkablePageLink<ProjectWizardPage> newProjectLink = new MenuBookmarkablePageLink<>(
                        ProjectWizardPage.class, 
                        new PageParameters(),
                        new StringResourceModel("menu.projects.create.text", this, null));
                newProjectLink.setIconType(IconType.envelope);
                subMenu.add(newProjectLink);
                
    		MenuBookmarkablePageLink<ProjectImportPage> importProjectLink = new MenuBookmarkablePageLink<>(
				ProjectImportPage.class,
				new PageParameters(),
				new StringResourceModel("menu.projects.import.text", this, null));
		importProjectLink.setIconType(new IconType("upload-alt"));
		subMenu.add(importProjectLink);
		
		MenuBookmarkablePageLink<ProjectExportPage> exportProjectLink = new MenuBookmarkablePageLink<>(
				ProjectExportPage.class,
				new StringResourceModel("menu.projects.export.text", this, null));
		exportProjectLink.setIconType(IconType.downloadalt);
				
		// Disable the menu item, if no projects exist
		if (projects == null || projects.isEmpty()) {
			exportProjectLink.setEnabled(false);
		} else {
			exportProjectLink.setEnabled(true);
		}
		
		subMenu.add(exportProjectLink);
		
		MenuBookmarkablePageLink<VisualPage> visualProjectLink = new MenuBookmarkablePageLink<>(
                VisualPage.class,
                new StringResourceModel("menu.projects.visualize.text", this, null));
		visualProjectLink.setIconType(IconType.eyeopen);
		subMenu.add(visualProjectLink);		
		
		return subMenu;
	}
}
