package eu.uqasar.web.components.user;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import eu.uqasar.model.user.User;
import eu.uqasar.web.components.behaviour.user.UserProfilePictureBackgroundBehaviour;
import eu.uqasar.web.pages.user.UserPage;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class LinkableUserBadge extends Panel {

	private static final long serialVersionUID = -7324739491842330520L;

	private final Label userName;
	private final User user;

	public LinkableUserBadge(String id, IModel<User> model) {
		super(id, model);
		Validate.notNull(model);
		Validate.notNull(model.getObject());
		user = model.getObject();

		BookmarkablePageLink<UserPage> container = new BookmarkablePageLink<>(
				"profile.link", UserPage.class, UserPage.forUser(user));
		container.add(userName = new Label("userName",
				new PropertyModel<String>(model, "userName")));

		WebMarkupContainer picture = new WebMarkupContainer("picture");
		picture.add(new UserProfilePictureBackgroundBehaviour(user, User.PictureDimensions.Badge));
		container.add(picture);
		container.add(new AttributeModifier("title", user.getFullName()));
		add(container);
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		userName.setVisible(StringUtils.isNotBlank(user.getUserName()));
	}
}
