/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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


import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.tree.projects.panels.ProjectWizardPanel;

/**
 *
 *
 */
public class ProjectWizardPage extends BasePage {
    
    private static final long serialVersionUID = 1L;    
    private final WebMarkupContainer panel;
   
    
    public ProjectWizardPage(PageParameters parameters){
        super(parameters);
        
      
        add(panel = new ProjectWizardPanel("wizardPanel"));
        

    }
    
    
    @Override
   	public void renderHead(IHeaderResponse response) {
   		super.renderHead(response);
   		response.render(CssHeaderItem.forUrl("assets/css/project/user-badges.css"));
   	}
   
}
