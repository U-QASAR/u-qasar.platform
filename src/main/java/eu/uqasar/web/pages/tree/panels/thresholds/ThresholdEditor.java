package eu.uqasar.web.pages.tree.panels.thresholds;

import java.util.Arrays;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;

/**
 * @param <Type>
 */
public class ThresholdEditor<Type extends TreeNode> extends BaseTreePanel<Type> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 933233205684391927L;

	final private WebMarkupContainer indicatorThreshold;
	
	/**
	 * Threshold editor set values and colors to illustrate intervals of values
	 * 
	 * @param id
	 * @param model
	 */
	public ThresholdEditor(final String id, final IModel<Type> model) {
		super(id, model);

		// Tooltip config
		final TooltipConfig confConfig = new TooltipConfig().withPlacement(TooltipConfig.Placement.top);

		// Inicator container
		indicatorThreshold = new WebMarkupContainer("indicatorThreshold");
		
		indicatorThreshold.add(new TextField<Double>("lowerAcceptanceLimit", new PropertyModel<Double>(model, "threshold.lowerAcceptanceLimit")).add(new TooltipBehavior(new StringResourceModel("help.lower.limit", this, model), confConfig)));
		indicatorThreshold.add(new TextField<Double>("upperAcceptanceLimit", new PropertyModel<Double>(model, "threshold.upperAcceptanceLimit")).add(new TooltipBehavior(new StringResourceModel("help.upper.limit", this, model), confConfig)));
		
		// Adding Color indicators
		indicatorThreshold.add(choiceColorMenu(model, "threshold.colorLowerAcceptanceLimit"));
		indicatorThreshold.add(choiceColorMenu(model, "threshold.colorMiddAcceptanceLimit"));
		indicatorThreshold.add(choiceColorMenu(model, "threshold.colorUpperAcceptanceLimit"));
		
		// Adding indicator 
		indicatorThreshold.setOutputMarkupId(true);
		add(indicatorThreshold);
		
	}

	/**
	 * Returns a Color selector that changes background color according to the
	 * selected option
	 * 
	 * @param model
	 * @param name
	 * @return
	 */
	private DropDownChoice<QualityStatus> choiceColorMenu(final IModel<Type> model, final String name) {
		final DropDownChoice<QualityStatus> choice = new DropDownChoice<QualityStatus>(
				name, Arrays.asList(QualityStatus.values())) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3202203242057441930L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				this.setDefaultModel(new PropertyModel<QualityStatus>(model,
						name));
			}
		};

		choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			/**
			 * 
			 */
			private static final long serialVersionUID = -6980046265508585744L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				target.add(indicatorThreshold);
			}
		});

		return choice;
	}
}
