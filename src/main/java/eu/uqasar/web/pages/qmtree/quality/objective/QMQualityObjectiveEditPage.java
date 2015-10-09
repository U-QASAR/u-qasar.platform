package eu.uqasar.web.pages.qmtree.quality.objective;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.quality.objective.panels.QualityObjectiveEditPanel;

public class QMQualityObjectiveEditPage extends QMBaseTreePage<QMQualityObjective> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8152232417246636501L;
	
	private QualityObjectiveEditPanel panel;
	
	/**
	 * isNew = true if accessing in "create" mode
	 */
	private boolean isNew = false;
	
	public QMQualityObjectiveEditPage(PageParameters parameters) {
		super(parameters);
		if (parameters!=null && parameters.get("isNew").toString()!=null && parameters.get("isNew").toString().equals("true")){
			 isNew = true;
		}
	}

	@Override
	public WebMarkupContainer getContent(String markupId,
			IModel<QMQualityObjective> model) {
		
		panel = new QualityObjectiveEditPanel(markupId, model, isNew);
		return panel;
	}

}
