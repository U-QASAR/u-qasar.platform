/**
 * 
 */
package eu.uqasar.web.pages.tree.panels.weight;

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
