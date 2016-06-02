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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.DropDownSubMenu;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.analytic.Analysis;
import eu.uqasar.service.AnalyticService;
import eu.uqasar.web.dashboard.DashboardViewPage;
import eu.uqasar.web.pages.analytic.AnalyticWorkbench;
import eu.uqasar.web.pages.analytic.drilldown.AnalysisDrilldown;
import eu.uqasar.web.pages.tree.BaseTreePage;

public class AnalyticsSubMenu extends DropDownSubMenu {
	private static final long serialVersionUID = -3106612550327247679L;

	@Inject
	private AnalyticService analyticService;
	
	public AnalyticsSubMenu(IModel<String> labelModel) {
		super(labelModel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		get("dropdown-menu").add(new CssClassNameAppender("analytics"));
	}

	@Override
	public boolean isActive(Component item) {
		return false;
	}

	@Override
	protected List<AbstractLink> newSubMenuButtons(final String buttonMarkupId) {
		final List<AbstractLink> subMenu = new ArrayList<>();
		
		List<Analysis> analysis = analyticService.getAllAnalysis();
		
		if (analysis.size() >= 1) {
			for (Analysis analys : analysis) {

				MenuBookmarkablePageLink<AnalysisDrilldown> analysisLink = new MenuBookmarkablePageLink<>(
						AnalysisDrilldown.class, BaseTreePage.forAnalysis(analys),
						Model.of(analys.getName()));
				analysisLink.setIconType(IconType.list);
				subMenu.add(analysisLink);

			}
			subMenu.add(HeaderNavigationBar.createMenuDevider());
		}
			
		MenuBookmarkablePageLink<DashboardViewPage> newDashboardLink = new MenuBookmarkablePageLink<>(
				AnalyticWorkbench.class,
				new PageParameters(),
				new StringResourceModel("menu.analytic.drilldown.text", this, null));
		newDashboardLink.setIconType(IconType.signal);
		subMenu.add(newDashboardLink);
		
		return subMenu;
	}
}
