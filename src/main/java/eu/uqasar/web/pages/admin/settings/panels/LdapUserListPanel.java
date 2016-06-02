package eu.uqasar.web.pages.admin.settings.panels;

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


import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import eu.uqasar.util.ldap.LdapManager;
import eu.uqasar.util.ldap.LdapUser;
import eu.uqasar.web.provider.ldap.LdapUserProvider;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.AbstractResource;

/**
 *
 *
 */
public class LdapUserListPanel extends LdapEntityListPanel<LdapUser> {

	private final static AbstractResource anonymousPicture = User.getAnonymousPicture();

	@Inject
	UserService userService;
	
	public LdapUserListPanel(String id, LdapManager manager) {
		super(id, new LdapUserProvider(manager, Integer.MAX_VALUE));
	}

	@Override
	public void populateItem(Item<LdapUser> item, Check<LdapUser> check) {
		LdapUser user = item.getModelObject();
		item.add(new Label("username", new PropertyModel(user, "userName")));
		item.add(new ExternalLink("mail", "mailto:" + user.getMail()).add(new Label("mailText", new PropertyModel(user, "mail"))));
		item.add(new Label("firstName", new PropertyModel(user, "firstName")));
		item.add(new Label("lastName", new PropertyModel(user, "lastName")));
		item.add(new Image("photo", user.hasProfilePicture() ? user.getProfilePictureImage() : anonymousPicture));
		if (isSelectionEnabled()) {
			if (userService.ldapBasedUserExists(user)) {
				check.setVisible(false);
				item.add(new AttributeAppender("class", Model.of("warning")));
				item.add(new AttributeAppender("title", new StringResourceModel("table.users.warning.exists", this, null)));
			} else {
				item.add(new AttributeAppender("class", Model.of("success")));
			}
		}
	}

	@Override
	public void selectionChanged(AjaxRequestTarget target) {
		
	}

}
