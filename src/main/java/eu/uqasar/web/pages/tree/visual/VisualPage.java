/**
 * 
 */
package eu.uqasar.web.pages.tree.visual;

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
