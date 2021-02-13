package eu.uqasar.web.pages.tree.panels.weight;

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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;

/**
 *
 *
 */
public class WeightIndicator<Type extends TreeNode> extends BaseTreePanel<Type> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5983031453194680472L;

	/**
	 * @param id
	 * @param model
	 */
	public WeightIndicator(final String id, final IModel<Type> model) {
		super(id, model);

		// Node 
		final Type node = model.getObject(); 
		
		// Number of items
		final int items = node.getParent().getChildren().size();

		// Total Weight indicator
		float totalWeight = 0;
		
		// Label to present data
		Label totalWeightInid;
		
		// Message to show percentage and weight available 
		String msg ="";
		
		if( node instanceof QualityObjective){
			for (TreeNode me : node.getParent().getChildren()) {
				totalWeight += ((QualityObjective)me).getWeight();
			}
		} else if(node instanceof QualityIndicator){
			for (final TreeNode me : node.getParent().getChildren()) {
				totalWeight += ((QualityIndicator) me).getWeight();
			}
		}
		
		final float percent = totalWeight / items * 100;
		final float available = (items) - totalWeight; 
		
		// Message is composed
		msg = Float.valueOf(percent).toString() + " / " + Float.valueOf(available).toString();
		
		totalWeightInid = new Label("totalWeight", msg);

		// Badge Color Decoration 
		if (percent == 100) {
			totalWeightInid.add(new CssClassNameAppender("badge-success"));
		} else if (percent < 100) {
			totalWeightInid
					.add(new CssClassNameAppender("badge-important"));
		} else {
			totalWeightInid.add(new CssClassNameAppender("badge-info"));
		}

		add(totalWeightInid);
	}

}
