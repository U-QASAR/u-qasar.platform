/*
 */
package eu.uqasar.web.pages.admin.teams.panels;

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


import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.model.user.User;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.StyledFeedbackPanel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 *
 *
 */
public abstract class EditTeamPanel extends Panel {

	private final TextField<String> nameField;
	private final Team team;
	private final Form<Team> teamMembersList;
	private final TeamMembership membership = new TeamMembership();

	private final CheckGroup<TeamMembership> teamGroup;
	private final SubmitLink deleteSelectedButton;

	@Inject
	UserService userService;

	public EditTeamPanel(final String markupId, final StyledFeedbackPanel feedbackPanel, final Team team) {
		super(markupId);
		this.team = team;
		Form<Team> formEdit = new InputValidationForm<>("form.edit", Model.of(team));
		nameField = new TextField<>("name", new PropertyModel<String>(this.team, "name"));
		InputBorder<String> serverValidationBorder = new InputBorder<>(
				"nameValidationBorder", nameField,
				new StringResourceModel("label.name", this, null));
		formEdit.add(serverValidationBorder);
		add(formEdit);

		Form<TeamMembership> formAdd = new InputValidationForm<>("form.add", Model.of(membership));
		AutoCompleteSettings settings = new AutoCompleteSettings().setPreselect(true).setShowListOnFocusGain(true).setShowListOnEmptyInput(true);
		AutoCompleteTextField<User> userComplete = new AutoCompleteTextField<User>("add.userName", new PropertyModel(membership, "user"), 
				User.class, getAutocompleteRenderer(), settings) {

			@Override
			protected Iterator<User> getChoices(String input) {
				return getTeamableUsers(input);
			}

			@Override
			public <User> IConverter<User> getConverter(Class<User> type) {
				return (IConverter<User>) getAutocompleteConverter();
			}
		};

		DropDownChoice<Role> userRole = new DropDownChoice<>("add.userRole", new PropertyModel(membership, "role"), Arrays.asList(Role.teamAssignableRoles()));
		userRole.setNullValid(false);
		AjaxSubmitLink addMember = new AjaxSubmitLink("add.member", formAdd) {

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedbackPanel);
				target.add(form);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				TeamMembership membership = (TeamMembership) form.getModelObject();
				EditTeamPanel.this.team.getMembers().add(membership);
				teamMembersList.setModelObject(EditTeamPanel.this.team);
				target.add(teamMembersList);
			}
		};
		addMember.add(new Label("button.add.save", new StringResourceModel("button.add.save", this, Model.of(team))));
		formAdd.add(addMember);
		formAdd.add(userRole);
		formAdd.add(userComplete);
		add(formAdd);

		teamMembersList = new InputValidationForm<>("form.list", Model.of(team));
		DataView<TeamMembership> usersView = new DataView<TeamMembership>("members", getMembershipProvider(team)) {
			@Override
			protected void populateItem(Item<TeamMembership> item) {
				final TeamMembership team = item.getModelObject();
				Check<TeamMembership> check = newDeleteCheck(item);
				List<Role> roles = new ArrayList<>(Arrays.asList(Role.teamAssignableRoles()));
				item.add(check);
				item.add(new Label("td.membername", new PropertyModel<>(team.getUser(), "fullName")));
				DropDownChoice<Role> roleSelector = new DropDownChoice<>("td.role",
						new PropertyModel<Role>(team, "role"), roles
				);
				roleSelector.setNullValid(false);
				item.add(roleSelector);
				item.setOutputMarkupId(true);
			}
		};
		teamMembersList.add(teamGroup = newCheckGroup());
		final WebMarkupContainer usersContainer = new WebMarkupContainer("membersContainer");
		teamGroup.add(usersContainer.setOutputMarkupId(true));
		CheckGroupSelector checkGroupSelector = new CheckGroupSelector("memberGroupSelector", teamGroup);
		usersContainer.add(checkGroupSelector);
		usersContainer.add(usersView);
		usersContainer.add(deleteSelectedButton = newDeleteSelectedButton(teamGroup));
		deleteSelectedButton.add(new Label("td.members.actions.batch.remove", new StringResourceModel("td.members.actions.batch.remove", this, Model.of(team))));
		add(teamMembersList.setOutputMarkupId(true));
		
		add(new Label("label.add.title", new StringResourceModel("label.add.title", this, Model.of(team))));
		add(new Label("label.list.title", new StringResourceModel("label.list.title", this, Model.of(team))));
	}

	private Iterator<User> getTeamableUsers(final String input) {
		List<User> potentialUsers = userService.getAllExcept(team.getAllUsers());
		Iterator<User> iterator = potentialUsers.iterator();
		while (iterator.hasNext()) {
			User user = iterator.next();
			if (!user.matchesInputFilter(input)) {
				iterator.remove();
			}
		}
		return potentialUsers.iterator();
	}

	private <T> IConverter<T> getAutocompleteConverter() {
		return new IConverter<T>() {

			@Override
			public T convertToObject(String value, Locale locale) throws ConversionException {
				return (T) userService.getByFullNameWithUsername(value);
			}

			@Override
			public String convertToString(T value, Locale locale) {
				return String.valueOf(((User)value).getFullNameWithUserName());
			}
		};
	}

	private IAutoCompleteRenderer<User> getAutocompleteRenderer() {
		return new AbstractAutoCompleteTextRenderer<User>() {

			@Override
			protected String getTextValue(User object) {
				return object.getFullNameWithUserName();
			}
		};
	}
	
	private IDataProvider<TeamMembership> getMembershipProvider(final Team team) {
		return new IDataProvider<TeamMembership>() {

			@Override
			public Iterator<? extends TeamMembership> iterator(long first, long count) {
				return team.getMembers().iterator();
			}

			@Override
			public long size() {
				return team.getMembers().size();
			}

			@Override
			public IModel<TeamMembership> model(TeamMembership object) {
				return Model.of(object);
			}

			@Override
			public void detach() {

			}
		};
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forUrl("assets/css/admin/user.css"));
	}

	private SubmitLink newDeleteSelectedButton(
			final CheckGroup<TeamMembership> teamGroup) {
		SubmitLink submitLink = new SubmitLink("deleteSelected") {

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// only enabled if at least one user is selected
				if (teamGroup.getModelObject().isEmpty()) {
					add(new CssClassNameAppender(Model.of("disabled")) {
						private static final long serialVersionUID = 5588027455196328830L;

						// remove css class when component is rendered again
						@Override
						public boolean isTemporary(Component component) {
							return true;
						}
					});
					setEnabled(false);
				} else {
					setEnabled(true);
				}
			}

			@Override
			public void onSubmit() {
				// TODO implement deletion of users!
				System.out.println("DELETE ME!");
			}
		};
		submitLink.setOutputMarkupId(true);
		return submitLink;
	}

	private CheckGroup newCheckGroup() {
		CheckGroup<TeamMembership> checkGroup = new CheckGroup<>("membersGroup", new ArrayList<TeamMembership>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(deleteSelectedButton);
			}
		});
		return checkGroup;
	}

	private Check<TeamMembership> newDeleteCheck(final Item<TeamMembership> item) {
		Check<TeamMembership> check = new Check<TeamMembership>("memberCheck", item.getModel(), teamGroup) {

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// TODO when to hide or disable team delete checkbox?
			}
		};
		return check;
	}

	public abstract void onSubmit();
}
