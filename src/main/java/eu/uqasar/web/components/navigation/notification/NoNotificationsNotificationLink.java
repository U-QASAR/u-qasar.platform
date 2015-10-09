package eu.uqasar.web.components.navigation.notification;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.notification.NoNotificationsNotification;
import eu.uqasar.web.pages.AboutPage;

public class NoNotificationsNotificationLink extends NotificationBookmarkablePageLink<NoNotificationsNotification, AboutPage>{

	private static final long serialVersionUID = 4904334928788603128L;
	
	public NoNotificationsNotificationLink(String id, IModel<NoNotificationsNotification> model) {
		this(id, new PageParameters(), model);
	}

	public NoNotificationsNotificationLink(String id, PageParameters parameters,
			IModel<NoNotificationsNotification> model) {
		super(id, AboutPage.class, parameters, model);
		this.gotoContainer.setVisible(false);
		setEnabled(false);
		setIcon(IconType.check);
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		
	}

}
