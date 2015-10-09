/**
 * 
 */
package eu.uqasar.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

/**
 *
 * 
 */
public class MainBehavior extends Behavior {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3712548616429988065L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.behavior.Behavior#renderHead(org.apache.wicket.Component
	 * , org.apache.wicket.markup.head.IHeaderResponse)
	 */
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);

		// add java script stuff
		response.render(JavaScriptHeaderItem.forUrl(JSTemplates.MAIN_JS,
				JSTemplates.MAIN_JS_REF_ID));
		// add javascript files for using jquery table sorter and select all
		response.render(JavaScriptHeaderItem.forUrl(
				JSTemplates.JQUERY_TABLESORT_JS,
				JSTemplates.JQUERY_TABLESORT_JS_REF_ID));
		// add javascript files for using jquery ui (sortable, draggable etc.)
		response.render(JavaScriptHeaderItem.forUrl(JSTemplates.JQUERY_UI_JS,
				JSTemplates.JQUERY_UI_JS_REF_ID));

		// initially hide all ".collapse" thingies
		response.render(OnDomReadyHeaderItem
				.forScript(JSTemplates.INIT_COLLAPSE));

	}
}
