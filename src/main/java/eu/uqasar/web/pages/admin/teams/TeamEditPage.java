/*
 */
package eu.uqasar.web.pages.admin.teams;

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
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.model.user.User;
import eu.uqasar.service.user.TeamMembershipService;
import eu.uqasar.service.user.TeamService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.WebConstants;
import eu.uqasar.web.components.Effects;
import eu.uqasar.web.components.HtmlEvent;
import eu.uqasar.web.components.IndicatingAjaxAutoCompleteTextField;
import eu.uqasar.web.components.InputBorder;
import eu.uqasar.web.components.InputValidationForm;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.components.OnEventInputBeanValidationBorder;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.pages.admin.users.UserEditPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
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
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 *
 *
 */
public class TeamEditPage extends AdminBasePage {

	@Inject
	TeamService teamService;

	@Inject
	UserService userService;

	@Inject
	TeamMembershipService teamMemberService;

	private final TextField<String> nameField;
	private final TextArea<String> descriptionField;
	
	private final Form<Team> teamMembersList;
	private final Form<Void> formAdd;
	private final WebMarkupContainer existingTeamContainer;
	private Team team = new Team();
	private final TeamMembership membership = new TeamMembership();

	private final DropDownChoice<Role> userRole;
	private final AutoCompleteTextField<User> userComplete;
	private final CheckGroup<TeamMembership> teamGroup;
	private final AjaxSubmitLink deleteSelectedButton;
    private final NotificationModal deleteConfirmationModal;

	public TeamEditPage(final PageParameters parameters) {
		super(parameters);

		if (!parameters.get("id").isEmpty()) {
			team = teamService.getById(parameters.get("id").toLong());
			if (team == null) {
				throw new EntityNotFoundException(Team.class, parameters.get("id").toOptionalString());
			}
		}

		add(new Label("page.title", getPageTitleModel()));
		Form<Team> formEdit = new InputValidationForm<Team>("form.edit", Model.of(team)) {

			@Override
			protected void onSubmit() {
				String message = new StringResourceModel("save.confirmed", this, null).getString();
				if (team.getId() == null) {
					team = teamService.create(team);
					setResponsePage(TeamEditPage.class, new PageParameters().add("id", team.getId()));
				} else {
					team = teamService.update(team);
					getPage().success(message);
					setResponsePage(getPage());
				}
			}
		};
		nameField = new TextField<>("name", new PropertyModel<String>(this.team, "name"));
		InputBorder<String> nameValidationBorder = new OnEventInputBeanValidationBorder<>(
				"nameValidationBorder", nameField,
				new StringResourceModel("label.name", this, null), HtmlEvent.ONBLUR);
		formEdit.add(nameValidationBorder);
		
		descriptionField = new TextArea<>("description", new PropertyModel<String>(this.team, "description"));
		InputBorder<String> descriptionValidationBorder = new OnEventInputBeanValidationBorder<>(
				"descriptionValidationBorder", descriptionField, HtmlEvent.ONBLUR);
		formEdit.add(descriptionValidationBorder);

		add(formEdit);

		existingTeamContainer = new WebMarkupContainer("existingTeam");
		add(existingTeamContainer);

		formAdd = new Form<>("form.add");
		userComplete = new IndicatingAjaxAutoCompleteTextField<User>("add.userName", new PropertyModel(membership, "user"),
				User.class, getAutocompleteRenderer(), WebConstants.getDefaultAutoCompleteSettings()) {
					@Override
					protected Iterator<User> getChoices(String input) {
						return getTeamableUsers(input);
					}

					@Override
					public <User> IConverter<User> getConverter(Class<User> type) {
						return (IConverter<User>) getAutocompleteConverter();
					}
				};
		userRole = new DropDownChoice<>("add.userRole", new PropertyModel(membership, "role"), Arrays.asList(Role.teamAssignableRoles()));
		userRole.setNullValid(false);
		AjaxSubmitLink addMember = new AjaxSubmitLink("add.member", formAdd) {

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedbackPanel);
				target.add(form);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				addUserToTeam(target, form);
			}
		};
		addMember.add(new Label("button.add.save", new StringResourceModel("button.add.save", this, Model.of(team))));
		formAdd.add(addMember);
		formAdd.add(userRole);
		formAdd.add(userComplete);
		existingTeamContainer.add(formAdd.setOutputMarkupId(true));

		teamMembersList = new InputValidationForm<>("form.list", Model.of(team));
		DataView<TeamMembership> usersView = getTeamMembersListing();
		teamMembersList.add(teamGroup = newCheckGroup());
		final WebMarkupContainer usersContainer = new WebMarkupContainer("membersContainer");
		teamGroup.add(usersContainer.setOutputMarkupId(true));
		CheckGroupSelector checkGroupSelector = new CheckGroupSelector("memberGroupSelector", teamGroup);
		usersContainer.add(checkGroupSelector);
		usersContainer.add(usersView);
		usersContainer.add(deleteSelectedButton = newDeleteSelectedButton(teamGroup));
		deleteSelectedButton.add(new Label("td.members.actions.batch.remove", new StringResourceModel("td.members.actions.batch.remove", this, Model.of(team))));
		existingTeamContainer.add(teamMembersList.setOutputMarkupId(true));

		existingTeamContainer.add(new Label("label.add.title", new StringResourceModel("label.add.title", this, Model.of(team))));
		existingTeamContainer.add(new Label("label.list.title", new StringResourceModel("label.list.title", this, Model.of(team))));
        add(deleteConfirmationModal = newDeleteConfirmationModal());
	}

	private void addUserToTeam(AjaxRequestTarget target, Form<?> form) {
		User user = userComplete.getConvertedInput();
		if(user == null)  {
			error(getString("error.user.notvalid"));
			userComplete.error(getString("error.user.notvalid"));
			target.add(feedbackPanel);
			target.add(form);
			return;
		}
		
		TeamMembership membership = new TeamMembership();
		membership.setRole(userRole.getConvertedInput());
		membership.setUser(user);
		membership.setTeam(team);
		membership = teamMemberService.create(membership);

		Set<TeamMembership> members = TeamEditPage.this.team.getMembers();
		members.add(membership);
		team.setMembers(members);
		team = teamService.update(team);
		teamMembersList.setModelObject(TeamEditPage.this.team);
		formAdd.clearInput();
		userComplete.setModelObject(null);
		userRole.setModelObject(Role.User);
		target.add(formAdd);
		target.add(teamMembersList);
	}

	private DataView<TeamMembership> getTeamMembersListing() {
		return new DataView<TeamMembership>("members", getMembershipProvider(team)) {
			@Override
			protected void populateItem(Item<TeamMembership> item) {
				final TeamMembership team = item.getModelObject();
				Check<TeamMembership> check = new Check<>("memberCheck", item.getModel(), teamGroup);
				List<Role> roles = new ArrayList<>(Arrays.asList(Role.teamAssignableRoles()));
				item.add(check);
				BookmarkablePageLink<Object> editMemberLink = new BookmarkablePageLink<>("link.edit.member", UserEditPage.class, new PageParameters().add("id", team.getUser().getId()));
				editMemberLink.add(new Label("td.membername", new PropertyModel<>(team.getUser(), "fullName")));
				item.add(editMemberLink);
				DropDownChoice<Role> roleSelector = new DropDownChoice<>("td.role",
						new PropertyModel<Role>(team, "role"), roles
				);
				roleSelector.setNullValid(false);
				item.add(roleSelector);
				item.setOutputMarkupId(true);
			}
		};
	}

	@Override
	protected void onConfigure() {
		if (team.getId() == null) {
			existingTeamContainer.setVisible(false);
		} else {
			existingTeamContainer.setVisible(true);
		}
	}

	private Iterator<User> getTeamableUsers(final String input) {
		List<User> potentialUsers = userService.getAllExceptAndFilter(team.getAllUsers(), input);
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
				// TODO take into account first and count
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

	private AjaxSubmitLink newDeleteSelectedButton(
			final CheckGroup<TeamMembership> teamGroup) {
		AjaxSubmitLink submitLink = new AjaxSubmitLink("deleteSelected") {

			@Override
			protected void onConfigure() {
				super.onConfigure();
				if (teamGroup.getModelObject().isEmpty()) {
					add(new CssClassNameAppender(Model.of("disabled")) {
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
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				target.add(deleteConfirmationModal);
                deleteConfirmationModal.appendShowDialogJavaScript(target);
			}
		};
		submitLink.setOutputMarkupId(true);
		return submitLink;
	}
    
    private void removeUsersFromTeam() {
    	String message = new StringResourceModel("delete.confirmed", this, Model.of(team)).getString();
        Collection<TeamMembership> membersToRemove = teamGroup.getModelObject();
        if (!membersToRemove.isEmpty()) {
            for (TeamMembership membership : membersToRemove) {
                membership.getTeam().getMembers().remove(membership);
                teamMemberService.delete(membership);
            }
            TeamEditPage.this.team = teamService.update(TeamEditPage.this.team);
        }
        getPage().success(message);
		setResponsePage(getPage());
        teamGroup.clearInput();
        teamGroup.setModelObject(new ArrayList<TeamMembership>());
    }

    private NotificationModal newDeleteConfirmationModal() {
        final IModel<String> header = new StringResourceModel(
                "delete.confirmation.modal.header", this, null);
        
        final IModel<String> message = new StringResourceModel(
                "delete.confirmation.modal.message", this, null);

        final NotificationModal notificationModal = new NotificationModal(
                "deleteConfirmationModal", header, message, false);

        notificationModal.addButton(new ModalActionButton(notificationModal,
                Buttons.Type.Primary, new StringResourceModel(
                        "delete.confirmation.modal.submit.text", this, null),
                true) {

                    @Override
                    protected void onAfterClick(AjaxRequestTarget target) {
                        // confirmed --> delete
                        removeUsersFromTeam();
                        deleteConfirmationModal.appendCloseDialogJavaScript(target);
                        Effects.replaceWithFading(target, teamMembersList);
                    }
                });
        notificationModal.addButton(new ModalActionButton(notificationModal,
                Buttons.Type.Default, new StringResourceModel(
                        "delete.confirmation.modal.cancel.text", this, null),
                true) {
                    @Override
                    protected void onAfterClick(AjaxRequestTarget target) {
                        // Cancel clicked --> do nothing, close modal
                        deleteConfirmationModal.appendCloseDialogJavaScript(target);
                    }
                });
        return notificationModal;
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

	@Override
	protected IModel<String> getPageTitleModel() {
		if (team.getId() == null) {
			return new StringResourceModel("page.title.new", this, null);
		} else {
			return new StringResourceModel("page.title.edit", this, Model.of(team));
		}
	}
	
	
	 public static PageParameters linkToEdit(Team entity) {
	        return linkToEdit(entity.getId());
	    }
	    
	    private static PageParameters linkToEdit(Long entityId) {
	        return new PageParameters().set("id", entityId);
	    }

}
