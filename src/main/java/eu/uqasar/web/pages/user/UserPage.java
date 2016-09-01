package eu.uqasar.web.pages.user;

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


import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.components.behaviour.user.UserProfilePictureBackgroundBehaviour;
import eu.uqasar.web.pages.BasePage;
import javax.inject.Inject;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public class UserPage extends BasePage {

	private static final long serialVersionUID = -5824482711777726324L;

	@Inject
	UserService userService;

	private final User user;

	public UserPage(PageParameters parameters) {
		super(parameters);

		user = getRequestedUser(parameters.get("userName"));
		if (user == null) {
			throw new EntityNotFoundException(User.class, parameters.get("userName").toString());
		}
		add(new Label("user.firstname", new PropertyModel<>(Model.of(user),
				"firstName")));
		add(new Label("user.lastname", new PropertyModel<>(Model.of(user),
				"lastName")));
		add(new Label("user.username",
				new PropertyModel<>(Model.of(user), "userName")));
		WebMarkupContainer picture = new WebMarkupContainer("picture");
		picture.add(new UserProfilePictureBackgroundBehaviour(user, User.PictureDimensions.Medium));
		add(picture);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssReferenceHeaderItem
				.forUrl("assets/css/user/user-panel.css"));
	}

	protected String getRequestedUsername() {
		StringValue id = getPageParameters().get("userName");
		return id.toString();
	}

	private User getRequestedUser(final StringValue userName) {
		return getRequestedUser(userName.toString());
	}

	private User getRequestedUser(final String userName) {
		return userService.getByUserName(userName);
	}

	public static PageParameters forUser(User user) {
		return forUser(user.getUserName());
	}

	private static PageParameters forUser(final String userName) {
		return new PageParameters().add("userName", userName);
	}
}
