package eu.uqasar.web.pages.tree.panels.charts;

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

import com.googlecode.wickedcharts.highcharts.theme.Theme;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.web.components.charts.DefaultChartOptions;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;

public class BaseTrendChartPanel<Type extends TreeNode> extends
		BaseTreePanel<Type> {

	private static final long serialVersionUID = -4929627660704707931L;

	private final IModel<Type> model;

	public BaseTrendChartPanel(String id, IModel<Type> model) {
		super(id, model);
		this.model = model;
		add(new WebMarkupContainer("chart"));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();

		Theme theme = new Theme();
		theme.setLang(DefaultChartOptions.getLocalizedLanguageOptions());
		replace(new Chart("chart",
                new BaseTrendChartOptions<>(this, model), theme));

	}

}
