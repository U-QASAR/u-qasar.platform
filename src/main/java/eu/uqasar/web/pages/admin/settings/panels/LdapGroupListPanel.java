/*
 */
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


import eu.uqasar.service.user.TeamService;
import eu.uqasar.util.ldap.LdapGroup;
import eu.uqasar.util.ldap.LdapManager;
import eu.uqasar.util.ldap.LdapUser;
import eu.uqasar.web.provider.ldap.LdapGroupProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.naming.NamingException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 */
public class LdapGroupListPanel extends LdapEntityListPanel<LdapGroup> {

	private static final Logger logger = Logger.getLogger(LdapGroupListPanel.class);
	
	@Inject
	TeamService teamService;

	public LdapGroupListPanel(String id, LdapManager manager) {
		super(id, new LdapGroupProvider(manager, Integer.MAX_VALUE));
	}

	private WebMarkupContainer newGroupMembersContainer(final String id, LdapGroup group) {
		WebMarkupContainer container = new WebMarkupContainer(id);
		DataView<LdapUser> membersList = newGroupMembersListView(group);
		container.add(membersList);
		return container;
	}

	private DataView<LdapUser> newGroupMembersListView(LdapGroup group) {
		List<LdapUser> users;
		try {
			users = group.getMembers();
		} catch (NamingException e) {
			logger.warn(e.getMessage(), e);
			users = new ArrayList<>(0);
		}
		DataView<LdapUser> members = new DataView<LdapUser>("membersList", new LdapGroupUsersProvider(users)) {

			@Override
			protected void populateItem(Item<LdapUser> item) {
				LdapUser user = item.getModelObject();
				WebMarkupContainer container = new WebMarkupContainer("member");
				ExternalLink mail = new ExternalLink("mail", Model.of("mailto:" + user.getMail()), new PropertyModel(user, "mail"));
				Label userName = new Label("username", new PropertyModel(user, "userName"));
				final String fullName = user.getFullName();
				Label fullNameLabel = new Label("fullname", Model.of(fullName));
				fullNameLabel.setVisible(fullName != null);
				container.add(mail);
				container.add(userName);
				container.add(fullNameLabel);
				item.add(container);
			}
		};
		members.setOutputMarkupId(true);
		return members;
	}

	@Override
	public void populateItem(Item<LdapGroup> item, Check<LdapGroup> check) {
		LdapGroup group = item.getModelObject();
		item.add(new Label("name", new PropertyModel(group, "name")));
		item.add(new Label("description", new PropertyModel(group, "description")));
		item.add(newGroupMembersContainer("members", group));

		if (isSelectionEnabled()) {
			if (teamService.ldapBasedGroupExists(group)) {
				check.setVisible(false);
				item.add(new AttributeAppender("class", Model.of("warning")));
				item.add(new AttributeAppender("title", new StringResourceModel("table.team.warning.exists", this, null)));
			} else {
				item.add(new AttributeAppender("class", Model.of("success")));
			}
		}
	}

	@Override
	public void selectionChanged(AjaxRequestTarget target) {
		System.out.println("group selection changed");	
	}

	private static class LdapGroupUsersProvider implements IDataProvider<LdapUser> {

		private final List<LdapUser> users;

		public LdapGroupUsersProvider(List<LdapUser> users) {
			this.users = users;
		}

		@Override
		public Iterator<? extends LdapUser> iterator(long first, long count) {
			return users.subList((int) first, (int) Math.min(first + count, size())).iterator();
		}

		@Override
		public long size() {
			return users.size();
		}

		@Override
		public IModel<LdapUser> model(LdapUser object) {
			return Model.of(object);
		}

		@Override
		public void detach() {
		}

	}
}
