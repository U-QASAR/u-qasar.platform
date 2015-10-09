package eu.uqasar.web.pages.qmtree.quality.indicator;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.web.pages.qmtree.QMBaseTreePage;
import eu.uqasar.web.pages.qmtree.quality.indicator.panels.QualityIndicatorEditPanel;

public class QMQualityIndicatorEditPage extends QMBaseTreePage<QMQualityIndicator> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6414789944370833810L;
	
	private QualityIndicatorEditPanel panel;
	
	/**
	 * isNew = true if accessing in "create" mode
	 */
	private boolean isNew = false;
	
	public QMQualityIndicatorEditPage(PageParameters parameters) {
		super(parameters);
		if (parameters!=null && parameters.get("isNew").toString()!=null && parameters.get("isNew").toString().equals("true")){
			 isNew = true;
		}
	}

	@Override
	public WebMarkupContainer getContent(String markupId, IModel<QMQualityIndicator> model) {
		panel = new QualityIndicatorEditPanel(markupId, model, isNew);
		return panel;
	}


}
