package eu.uqasar.web.pages.tree.panels.thresholds;

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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import eu.uqasar.model.tree.BaseIndicator;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;

/**
 * @param <Type>
 */
public class ThresholdIndicator<Type extends TreeNode> extends BaseTreePanel<Type>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 933233205684391927L;

	/**
	 * @param id
	 * @param model
	 */
	public ThresholdIndicator(final String id, final IModel<Type> model) {
		super(id, model);

		String upper = "";
		String midd = "";
		String lower ="";
		
		final Type item = model.getObject();
		
		// Values of the three intervals
		int low = 0;
		int mid = 0;
		int up = 0;
		
		// Set values for Low and Up acceptance limits
		if (item instanceof Project) {
			low = (int) ((Project) item).getThreshold().getLowerAcceptanceLimit();
			up = (int) ((Project) item).getThreshold().getUpperAcceptanceLimit();
		} else if(item instanceof BaseIndicator) {
			low = (int) ((BaseIndicator) item).getThreshold().getLowerAcceptanceLimit();
			up = (int) ((BaseIndicator) item).getThreshold().getUpperAcceptanceLimit();
		}
		
		// Midd value is calculated using Up and Low
		mid = 100 - (100-up + low); 
		
		// Threshold color
		if (item instanceof Project) {
			upper = ((Project) item).getThreshold().getColorUpperAcceptanceLimit().toString().toLowerCase();
			midd = ((Project) item).getThreshold().getColorMiddAcceptanceLimit().toString().toLowerCase();
			lower =  ((Project) item).getThreshold().getColorLowerAcceptanceLimit().toString().toLowerCase();
		} else {
			upper =  ((BaseIndicator) item).getThreshold().getColorUpperAcceptanceLimit().toString().toLowerCase();
			midd =  ((BaseIndicator) item).getThreshold().getColorMiddAcceptanceLimit().toString().toLowerCase();
			lower =  ((BaseIndicator) item).getThreshold().getColorLowerAcceptanceLimit().toString().toLowerCase();
		}
		
		// Add Thresholds Limits
		add(new Label("lowerAcceptanceLimit", 
				new StringResourceModel("label.below", this, null).getString()
				+ Integer.valueOf(low).toString() + "%")
			.add( new AttributeModifier("style","width:" + Integer.valueOf(low).toString() + "%;"))
			.add(new CssClassNameAppender(lower)));
		
		add(new Label("middAcceptanceLimit", 
				new StringResourceModel("label.between", this, null).getString())
			.add( new AttributeModifier("style","width:" + Integer.valueOf(mid).toString() + "%;"))
			.add(new CssClassNameAppender(midd)));
		
		add(new Label("upperAcceptanceLimit", 
				new StringResourceModel("label.over", this, null).getString()
				+ Integer.valueOf(up).toString() + "%")
			.add( new AttributeModifier("style","width:" + Integer.valueOf((100-(up))).toString() + "%;"))
			.add(new CssClassNameAppender(upper)));
	
	}
}
