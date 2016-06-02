package eu.uqasar.web.components.behaviour;

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
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 *
 *
 */
public class DefaultFocusBehavior extends Behavior {

	/**
	 *
	 */
	private static final long serialVersionUID = -561941706017776414L;

	private FormComponent formComponent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.behavior.Behavior#bind(org.apache.wicket.Component)
	 */
	@Override
	public void bind(Component component) {
		if (component instanceof FormComponent) {
			this.formComponent = (FormComponent) component;
			this.formComponent.setOutputMarkupId(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.behavior.Behavior#renderHead(org.apache.wicket.Component
	 * , org.apache.wicket.markup.html.IHeaderResponse)
	 */
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		if (this.formComponent != null) {
			final String js = String.format("$('#%s').focus()",
					this.formComponent.getMarkupId());
			response.render(OnDomReadyHeaderItem.forScript(js));
		}
	}
}
