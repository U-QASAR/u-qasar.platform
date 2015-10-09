package eu.uqasar.web.pages.tree.projects;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.tree.Project;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.projects.panels.ProjectViewPanel;

public class ProjectViewPage extends BaseTreePage<Project> {

	private static final long serialVersionUID = -5065901880003755807L;

	private ProjectViewPanel panel;

	public ProjectViewPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public WebMarkupContainer getContent(String markupId, IModel<Project> model) {
		panel = new ProjectViewPanel(markupId, model);
		return panel;
	}

}
