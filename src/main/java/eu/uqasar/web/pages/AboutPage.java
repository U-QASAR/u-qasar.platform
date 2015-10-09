/**
 * 
 */
package eu.uqasar.web.pages;

import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * 
 */
public class AboutPage extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AboutPage(final PageParameters parameters) {
		super(parameters);
		
		add(new ContextImage("dashimg", "assets/img/screenshot/dash.png"));
	}
}
