package eu.uqasar.web.pages.tree.panels.charts;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import com.googlecode.wickedcharts.highcharts.theme.Theme;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import eu.uqasar.model.tree.TreeNode;
import eu.uqasar.web.components.charts.DefaultChartOptions;
import eu.uqasar.web.pages.tree.panels.BaseTreePanel;

public class BaseTrendChartPanel<Type extends TreeNode> extends
		BaseTreePanel<Type> {

	private static final long serialVersionUID = -4929627660704707931L;

	private IModel<Type> model;

	public BaseTrendChartPanel(String id, IModel<Type> model) {
		super(id, model);
		this.model = model;
		add(new WebMarkupContainer("chart"));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();

		Theme theme = new Theme();
		theme.setLang(DefaultChartOptions.getLocalizedLanguageOptions());
		replace(new Chart("chart",
				new BaseTrendChartOptions<Type>(this, model), theme));

	}

}
