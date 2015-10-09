/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.uqasar.web.pages.tree.projects;

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
