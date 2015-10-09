package eu.uqasar.web.pages.tree.quality.objective;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.quality.objective.panels.QualityObjectiveEditPanel;

public class QualityObjectiveEditPage extends BaseTreePage<QualityObjective> {

	private static final long serialVersionUID = -7367161367385562874L;

	private QualityObjectiveEditPanel panel;
	private boolean isNew = false;
	
	public QualityObjectiveEditPage(PageParameters parameters) {
		super(parameters);
		if (parameters !=null && parameters.get("isNew") !=null && 
				parameters.get("isNew").toBoolean() == true){
			isNew = true;
		}
	}
	
	@Override
	public WebMarkupContainer getContent(String markupId,
			IModel<QualityObjective> model) {
		panel = new QualityObjectiveEditPanel(markupId, model, isNew);
		return panel;
	}

}
