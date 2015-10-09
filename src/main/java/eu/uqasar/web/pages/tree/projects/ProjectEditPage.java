package eu.uqasar.web.pages.tree.projects;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.tree.Project;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.projects.panels.ProjectEditPanel;

public class ProjectEditPage extends BaseTreePage<Project> {

	private static final long serialVersionUID = 953462825002460061L;
	private ProjectEditPanel panel;

	private boolean isNew = false;

	public ProjectEditPage(PageParameters parameters) {
		super(parameters);
		if (parameters !=null && parameters.get("isNew") !=null && 
				parameters.get("isNew").toBoolean() == true){
			isNew = true;
		}
	}

	@Override
	public WebMarkupContainer getContent(String markupId, 
			IModel<Project> model) {
		panel = new ProjectEditPanel(markupId, model, isNew);
		return panel;
	}
}
