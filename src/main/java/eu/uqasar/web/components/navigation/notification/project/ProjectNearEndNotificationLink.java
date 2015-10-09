package eu.uqasar.web.components.navigation.notification.project;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.notification.project.ProjectNearEndNotification;
import eu.uqasar.model.tree.Project;
import eu.uqasar.web.components.navigation.notification.NotificationBookmarkablePageLink;
import eu.uqasar.web.pages.tree.BaseTreePage;
import eu.uqasar.web.pages.tree.projects.ProjectViewPage;

public class ProjectNearEndNotificationLink extends
		NotificationBookmarkablePageLink<ProjectNearEndNotification, ProjectViewPage> {

	private static final long serialVersionUID = 3310107463188121652L;
	
	public ProjectNearEndNotificationLink(String id,
			IModel<ProjectNearEndNotification> model) {
		this(id, new PageParameters(), model);
	}

	public ProjectNearEndNotificationLink(String id, PageParameters parameters,
			IModel<ProjectNearEndNotification> model) {
		super(id, ProjectViewPage.class, BaseTreePage.forProject(model.getObject().getProject()), model);
		add(new Label("project.name", getProject().getAbbreviatedName()));
		add(new Label("project.ending", new StringResourceModel("project.about.to.end", null, getProject().getRemainingDays())));
		setIcon(new IconType("sitemap"));
		get("notification.container").add(new CssClassNameAppender("project", getProject().getQualityStatus().getCssClassName()));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}
	
	public Project getProject() {
		return getModelObject() != null ? getModelObject().getProject() : null;
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		
	}
}
