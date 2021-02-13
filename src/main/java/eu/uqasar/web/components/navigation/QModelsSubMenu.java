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
import eu.uqasar.model.qmtree.IQMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.web.pages.qmodel.QModelImportPage;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.qmodels.QModelEditPage;
import eu.uqasar.web.pages.qmtree.qmodels.QModelExportPage;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;

public class QModelsSubMenu extends NavbarDropDownButton {

	private static final long serialVersionUID = 2708741180794473231L;

	@Inject
	QMTreeNodeService treeNodeService;
	
	public QModelsSubMenu(IModel<String> labelModel) {
		super(labelModel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		getParent().add(new AttributeModifier("id", "qmodelMenu"));
		get("dropdown-menu").add(new CssClassNameAppender("projects"));
	}

	@Override
	public boolean isActive(Component item) {
		return false;
	}

	@Override
	protected List<AbstractLink> newSubMenuButtons(final String buttonMarkupId) {
		final List<AbstractLink> subMenu = new ArrayList<>();
		final List<QModel> qmodels = treeNodeService.getAllQModels();

		subMenu.add(new MenuHeader(new StringResourceModel("menu.qmodels.recently.updated", this, null)));
		for (IQMTreeNode<String> qmodelNode : qmodels) {
			QModel qmodel = (QModel) qmodelNode;
			MenuBookmarkablePageLink<QModelViewPage> qmodelLink = new MenuBookmarkablePageLink<>(
					QModelViewPage.class, QMBaseTreePage.forNode(qmodelNode),
					Model.of(qmodel.getName()));
				subMenu.add(qmodelLink);
		}

		subMenu.add(HeaderNavigationBar.createMenuDevider());
		PageParameters params = new PageParameters();
		params.add("isNew", true);
		MenuBookmarkablePageLink<QModelEditPage> newQModelLink = new MenuBookmarkablePageLink<>(
				QModelEditPage.class, params,
				new StringResourceModel("menu.qmodels.create.text", this, null));
		newQModelLink.setIconType(IconType.plus);
		subMenu.add(newQModelLink);

		
		MenuBookmarkablePageLink<QModelImportPage> importQModelLink = new MenuBookmarkablePageLink<>(
                QModelImportPage.class,
                new PageParameters(),
                new StringResourceModel("menu.qmodels.import.text", this, null));
		importQModelLink.setIconType(new IconType("upload-alt"));
		subMenu.add(importQModelLink);

		MenuBookmarkablePageLink<QModelExportPage> exportQModelLink = new MenuBookmarkablePageLink<>(
				QModelExportPage.class,
				new StringResourceModel("menu.qmodels.export.text", this, null));
		exportQModelLink.setIconType(IconType.downloadalt);
		// Disable the menu item, if no projects exist
		if (treeNodeService.getAllQModels() == null || treeNodeService.getAllQModels().size() == 0) {
			exportQModelLink.setEnabled(false);
		} else {
			exportQModelLink.setEnabled(true);
		}
		
		subMenu.add(exportQModelLink);
		return subMenu;
	}
}
