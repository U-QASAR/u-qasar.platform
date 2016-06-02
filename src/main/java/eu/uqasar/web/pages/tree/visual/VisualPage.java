/**
 * 
 */
package eu.uqasar.web.pages.tree.visual;

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

import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.visual.panel.VisualProjectPanel;

/**
 *
 *
 */
public class VisualPage extends BasePage{
	private static final long serialVersionUID = 1404486356253398289L;
	private VisualProjectPanel projectVisualPanel;

	public VisualPage(PageParameters parameters) {
		super(parameters);
		
		projectVisualPanel = new VisualProjectPanel("subsetPanel");
		
		add(projectVisualPanel);
		
	}

}
