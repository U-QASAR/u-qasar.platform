package eu.uqasar.web.pages.tree.quality.indicator;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.quality.indicator.panels.QualityIndicatorEditPanel;

public class QualityIndicatorEditPage extends BaseTreePage<QualityIndicator> {

	private static final long serialVersionUID = 6292290427995801471L;
	
	private QualityIndicatorEditPanel panel;
	private boolean isNew = false;

	public QualityIndicatorEditPage(PageParameters parameters) {
		super(parameters);
		if (parameters !=null && parameters.get("isNew") !=null && 
				parameters.get("isNew").toBoolean() == true){
			isNew = true;
		}
	}

	@Override
	public WebMarkupContainer getContent(String markupId, IModel<QualityIndicator> model) {
		panel = new QualityIndicatorEditPanel(markupId, model, isNew);
		return panel;
	}


}
