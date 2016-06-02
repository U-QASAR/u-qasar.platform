package eu.uqasar.web.components.user;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
