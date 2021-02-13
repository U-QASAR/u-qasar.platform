package eu.uqasar.web.components;

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
