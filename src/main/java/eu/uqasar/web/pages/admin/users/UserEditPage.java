/*
 */
package eu.uqasar.web.pages.admin.users;

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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
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

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import eu.uqasar.exception.auth.register.UserMailAlreadyExistsException;
import eu.uqasar.exception.auth.register.UserNameAlreadyExistsException;
import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.exception.notification.NotificationException;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.notification.message.auth.register.RegistrationMessageService;
import eu.uqasar.service.user.TeamMembershipService;
import eu.uqasar.service.user.TeamService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.WebConstants;
import eu.uqasar.web.components.Effects;
import eu.uqasar.web.components.IndicatingAjaxAutoCompleteTextField;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.pages.admin.teams.TeamEditPage;
import eu.uqasar.web.pages.user.panels.EditProfilePanel;

/**
 *
 *
 */
public final class UserEditPage extends AdminBasePage {

	@Inject
	private UserService userService;

	@Inject
	private TeamService teamService;

	@Inject
	private AuthenticationService authenticationService;

	@Inject
	private RegistrationMessageService registrationMessageService;

	@Inject
	private TeamMembershipService teamMembershipService;

	private final Form teamAddForm;
	private final AutoCompleteTextField<Team> teamName;
	private final DropDownChoice<Role> userRole;
    private final NotificationModal deleteConfirmationModal;
	private final Form teamsListForm;

	private final CheckGroup<TeamMembership> teamGroup;
	private final AjaxSubmitLink deleteSelectedButton;
	private TeamProvider provider;

	private User user = new User();

	public UserEditPage(final PageParameters pageParameters) {
		super(pageParameters);
		if (!pageParameters.get("id").isEmpty()) {
			user = userService.getById(pageParameters.get("id").toLong());
			if (user == null) {
				throw new EntityNotFoundException(User.class, pageParameters.get("id").toOptionalString());
			}
		}
		add(new Label("page.title", getPageTitleModel()));
		add(new EditProfilePanel("edit.profile", pageParameters.get("id").toOptionalLong()) {
			@Override
			public void onSubmit(final User user, final String password, final String passwordConfirmation) {
				String message = new StringResourceModel("save.confirmed", this, null).getString();
				
				if (user.getId() == null) {
					User registered = tryToRegister(user, password, passwordConfirmation);
					if(registered != null) {
						setResponsePage(UserEditPage.class, new PageParameters().add("id", registered.getId()));
					}
					setResponsePage(UserListPage.class,
							new PageParameters().set(
									MESSAGE_PARAM,
									new StringResourceModel("add.confirmed", this, Model
											.of(user)).getString()).set(LEVEL_PARAM,
									FeedbackMessage.SUCCESS));
				} else {
					if (authenticationService.checkNonEmptyPasswords(password, passwordConfirmation)) {
						// User wants to change his/her password!
						if (authenticationService.checkPasswordsEqual(password, passwordConfirmation)) {
							authenticationService.updateUserPassword(user, password, passwordConfirmation);
							userService.update(user);
						} else {
							error(new StringResourceModel("confirmPassword.EqualPasswordInputValidator", this, null));
						}
					} else {
						userService.update(user);
					}
					getPage().success(message);
					setResponsePage(getPage());
				}
			}

            @Override
            public void onCancel() {
            	
                //setResponsePage(UserListPage.class);
                setResponsePage(UserListPage.class,
						new PageParameters().set(
								MESSAGE_PARAM,
								new StringResourceModel("cancel.confirmed", this, Model
										.of(user)).getString()).set(LEVEL_PARAM,
								FeedbackMessage.WARNING));
                
                
                
            }
		});

		teamsListForm = new Form<>("form.teams.edit");
		teamsListForm.add(new Label("section.teams.title", new StringResourceModel("section.teams.title", this, Model.of(user))));
		DataView<TeamMembership> teamsView = getTeamListing();
		teamsListForm.add(teamGroup = newCheckGroup());
		final WebMarkupContainer usersContainer = new WebMarkupContainer("teamsContainer");
		teamGroup.add(usersContainer.setOutputMarkupId(true));
		CheckGroupSelector checkGroupSelector = new CheckGroupSelector("teamGroupSelector", teamGroup);
		usersContainer.add(checkGroupSelector);
		usersContainer.add(teamsView);
		usersContainer.add(deleteSelectedButton = newDeleteSelectedButton(teamGroup));
		Label deleteSelectedLabel = new Label("deleteSelectedLabel", new StringResourceModel("td.teams.actions.batch.remove", this, Model.of(user)));
		deleteSelectedButton.add(deleteSelectedLabel);
		add(teamsListForm.setOutputMarkupId(true));

		teamAddForm = new Form<>("form.teams.add");
		teamAddForm.add(new Label("header.teams.add", new StringResourceModel("label.add.title", this, Model.of(user))));
		teamAddForm.add(teamName = getTeamNameAutoCompleteField());
		userRole = new DropDownChoice<>("add.userRole", Arrays.asList(Role.teamAssignableRoles()));
		userRole.setNullValid(false);
		userRole.setModel(Model.of(Role.User));
		teamAddForm.add(userRole);
		AjaxSubmitLink addTeam = new AjaxSubmitLink("add.team") {

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedbackPanel);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				addUserToTeam(target, form);
			}
		};
		teamAddForm.add(addTeam.add(new Label("label.add.team", new StringResourceModel("button.add.save", this, Model.of(user)))));
		teamAddForm.add(new Label("label.add.userrole", new StringResourceModel("label.add.userrole", this, Model.of(user))));
		add(teamAddForm);
        add(deleteConfirmationModal = newDeleteConfirmationModal());
	}

	@Override
	protected void onConfigure() {
		teamAddForm.setVisible(user.getId() != null);
		teamsListForm.setVisible(user.getId() != null);
	}

	private DataView<TeamMembership> getTeamListing() {
		return new DataView<TeamMembership>("teams", provider = new TeamProvider(user)) {
			@Override
			protected void populateItem(Item<TeamMembership> item) {
				final TeamMembership team = item.getModelObject();
				Check<TeamMembership> check = newDeleteCheck(item);
				List<Role> roles = new ArrayList<>(Arrays.asList(Role.teamAssignableRoles()));
				item.add(check);
				DropDownChoice<Role> roleSelector = new DropDownChoice<>("td.role",
						new PropertyModel<Role>(team, "role"), roles
				);
				roleSelector.setNullValid(false);
				item.add(roleSelector);
				BookmarkablePageLink<TeamEditPage> editTeamLink = new BookmarkablePageLink<>("link.edit.team", TeamEditPage.class, new PageParameters().add("id", team.getTeam().getId()));
				editTeamLink.add(new Label("td.teamname", new PropertyModel<>(team.getTeam(), "name")));
				item.add(editTeamLink);
				item.setOutputMarkupId(true);
			}
		};
	}

	private AutoCompleteTextField<Team> getTeamNameAutoCompleteField() {
		AutoCompleteTextField<Team> field = new IndicatingAjaxAutoCompleteTextField<Team>("add.teamName", Model.of((Team) null),
				Team.class, getAutocompleteRenderer(), WebConstants.getDefaultAutoCompleteSettings()) {

					@Override
					protected Iterator<Team> getChoices(String input) {
						return getPotentialTeams(input);
					}

					@Override
					public <Team> IConverter<Team> getConverter(Class<Team> type) {
						return (IConverter<Team>) getAutocompleteConverter();
					}
				};
		field.setRequired(true);
		return field;
	}

	private void addUserToTeam(AjaxRequestTarget target, Form<?> form) {
		Team team = teamName.getConvertedInput();
		Role role = userRole.getConvertedInput();
		if (team == null) {
			error(getString("error.team.notvalid"));
			teamName.error(getString("error.team.notvalid"));
			target.add(feedbackPanel);
			target.add(form);
			return;
		}
		TeamMembership membership = new TeamMembership();
		membership.setRole(role);
		membership.setUser(user);
		membership.setTeam(team);
		membership = teamMembershipService.create(membership);

		team.getMembers().add(membership);
		teamService.update(team);
		provider.update();

		teamName.clearInput();
		teamName.setModel(Model.of((Team) null));
		userRole.clearInput();
		userRole.setModelObject(Role.User);

		target.add(teamAddForm);
		target.add(teamsListForm);
	}

	@Override
	protected IModel<String> getPageTitleModel() {
		if (user.getId() == null) {
			return new StringResourceModel("page.title.new", this, null);
		} else {
			return new StringResourceModel("page.title.edit", this, Model.of(user));
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forUrl("assets/css/admin/user.css"));
	}

	private Iterator<Team> getPotentialTeams(final String input) {
		List<Team> alreadyAssociatedTeams = teamService.getForUser(user);
		List<Team> potentialTeams = teamService.getAllExceptAndFilter(alreadyAssociatedTeams, input);
		return potentialTeams.iterator();
	}

	private <T> IConverter<T> getAutocompleteConverter() {
		return new IConverter<T>() {

			@Override
			public T convertToObject(String value, Locale locale) throws ConversionException {
				return (T) teamService.getByName(value);
			}

			@Override
			public String convertToString(T value, Locale locale) {
				return String.valueOf(((Team) value).getName());
			}
		};
	}

	private IAutoCompleteRenderer<Team> getAutocompleteRenderer() {
		return new AbstractAutoCompleteTextRenderer<Team>() {

			@Override
			protected String getTextValue(Team object) {
				return object.getName();
			}
		};
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
                        removeUserFromTeams();
                        deleteConfirmationModal.appendCloseDialogJavaScript(target);
                        Effects.replaceWithFading(target, teamsListForm);
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
    
    private void removeUserFromTeams() {
    	String message = new StringResourceModel("delete.confirmed", this, Model.of(user)).getString();
        Collection<TeamMembership> members = teamGroup.getModelObject();
        if (!members.isEmpty()) {
            for (TeamMembership membership : members) {
                Team team = membership.getTeam();
                team.getMembers().remove(membership);
                teamMembershipService.delete(membership);
                teamService.update(team);
            }
            provider.update();
        }
        getPage().success(message);
		setResponsePage(getPage());
        teamGroup.clearInput();
        teamGroup.setModelObject(new ArrayList<TeamMembership>());
    }

	private CheckGroup newCheckGroup() {
		CheckGroup<TeamMembership> checkGroup = new CheckGroup<>("teamsGroup", new ArrayList<TeamMembership>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(deleteSelectedButton);
			}
		});
		return checkGroup;
	}

	private Check<TeamMembership> newDeleteCheck(final Item<TeamMembership> item) {
        return new Check<>("teamCheck", item.getModel(), teamGroup);
	}

	private User tryToRegister(User newUser, final String password, final String passwordConfirmation) {
		try {
			authenticationService.checkMailAlreadyRegistered(newUser.getMail());
			authenticationService.checkUserNameAlreadyRegistered(newUser.getUserName());
			User registeredUser = authenticationService.register(newUser, password);
			registrationMessageService.sendRegistrationConfirmationMessage(registeredUser);
			return registeredUser;
		} catch (UserMailAlreadyExistsException ex) {
			error(new StringResourceModel("error.user.exists.mail", this, Model.of(newUser)).getString());
			return null;
		} catch (UserNameAlreadyExistsException ex) {
			error(new StringResourceModel("error.user.exists.userName", this, Model.of(newUser)).getString());
			return null;
		} catch (NotificationException ex) {
			error(new StringResourceModel("error.user.register.notification", this, Model.of(newUser)).getString());
			return null;
		}
	}

	private class TeamProvider implements IDataProvider<TeamMembership> {

		private final User user;
		private List<TeamMembership> teams = new ArrayList<>();

		public TeamProvider(final User user) {
			this.user = user;
			if (user.getId() != null) {
				this.teams = teamMembershipService.getForUser(user);
			}
		}

		public void update() {
			if (user.getId() != null) {
				this.teams = teamMembershipService.getForUser(user);
			}
		}

		@Override
		public Iterator<? extends TeamMembership> iterator(long first, long count) {
			// TODO first and count to be implemented!
			return teams.iterator();
		}

		@Override
		public long size() {
			return teams.size();
		}

		@Override
		public IModel<TeamMembership> model(TeamMembership object) {
			return Model.of(object);
		}

		@Override
		public void detach() {

		}
	}
	
    public static PageParameters linkToEdit(User entity) {
        return linkToEdit(entity.getId());
    }
    
    private static PageParameters linkToEdit(Long entityId) {
        return new PageParameters().set("id", entityId);
    }
	
	
}
