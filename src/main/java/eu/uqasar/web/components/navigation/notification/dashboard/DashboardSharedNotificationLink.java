package eu.uqasar.web.components.navigation.notification.dashboard;

import java.util.Date;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.notification.NotificationType;
import eu.uqasar.model.notification.dashboard.DashboardSharedNotification;
import eu.uqasar.service.NotificationService;
import eu.uqasar.web.components.navigation.notification.NotificationBookmarkablePageLink;
import eu.uqasar.web.dashboard.DashboardViewPage;

public class DashboardSharedNotificationLink extends NotificationBookmarkablePageLink<DashboardSharedNotification, DashboardViewPage> {

	private static final long serialVersionUID = 3310107463188121652L;
	
	public DashboardSharedNotificationLink(String id,IModel<DashboardSharedNotification> model) {
		this(id, new PageParameters(), model);
	}

	public DashboardSharedNotificationLink(String id, PageParameters parameters, IModel<DashboardSharedNotification> model) {

		super(id, DashboardViewPage.class, parameters.add("id", model.getObject().getDashboard().getId()), model);
		

		//get user who shared dashboard		
		add(new Label("dashboard.user", model.getObject().getDashboard().getSharedBy()));	

		//set containers 
		setIcon(new IconType("dashboard"));
		get("notification.container").add(new AttributeModifier("style","width:95%;"));
		get("notification.container").add(new AttributeModifier("class","notification project green"));	
		
		add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = -4295786924073241665L;
			@Override
			protected void onEvent(AjaxRequestTarget target) {				
				getModelObject().getDashboard().setSharedBy(null);
				setResponsePage(DashboardViewPage.class, getPageParameters().add("id", getModelObject().getDashboard().getId()));
			}
		});
	}	
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		logger.info("########################" + getModelObject().getNotificationType() + "########################");
	}
}
