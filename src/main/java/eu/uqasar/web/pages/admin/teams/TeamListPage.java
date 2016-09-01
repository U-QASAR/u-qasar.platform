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
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.exception.model.EntityNotFoundException;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.service.tree.ProjectSearchService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.service.user.TeamMembershipService;
import eu.uqasar.service.user.TeamService;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.pages.admin.users.UserEditPage;
import eu.uqasar.web.provider.EntityProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 */
public class TeamListPage extends AdminBasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private TeamService teamService;

	@Inject
	private TeamMembershipService teamMemberService;

	@Inject
	private TreeNodeService treeNodeService;

	@Inject
	private ProjectSearchService projectService;

	private static final int itemsPerPage = 10;

	private final CheckGroup<Team> teamGroup;
	private final TeamEntityProvider provider;
	private final WebMarkupContainer teamsContainer;
	private final AjaxSubmitLink deleteSelectedButton;
	private final AjaxSubmitLink addSelectedButton;
	private final BootstrapAjaxPagingNavigator navigator;
	private final Modal deleteConfirmationModal;
	private final Modal addConfirmationModal;
	private final List<Project> allProjects;

	private Project project = new Project();


	public TeamListPage(final PageParameters pageParameters) {
		super(pageParameters);

		if (!pageParameters.get("id").isEmpty()) {
			project = projectService.getById(pageParameters.get("id").toLong());
			if (project == null) {
				throw new EntityNotFoundException(Team.class, pageParameters.get("id").toOptionalString());
			}
		}

		Form<Void> form = new Form("form");
		provider = new TeamEntityProvider();
		DataView<Team> usersView = new DataView<Team>("teams", provider, itemsPerPage) {
			@Override
			protected void populateItem(Item<Team> item) {
				final Team team = item.getModelObject();
				item.add(new Check<>("teamCheck", item.getModel(), teamGroup));
				BookmarkablePageLink<TeamEditPage> editTeam = new BookmarkablePageLink<>("link.edit.team", TeamEditPage.class, new PageParameters().add("id", team.getId()));
				item.add(editTeam.add(new Label("td.teamname", new PropertyModel<>(team, "name"))));
				item.add(new Label("td.description", new PropertyModel<>(team, "description")));
				item.add(newMembersPanel(team));
				Link editButton = new BookmarkablePageLink("button.edit", TeamEditPage.class, new PageParameters().add("id", team.getId()));
				item.add(editButton);
				item.setOutputMarkupId(true);
			}
		};
		form.add(teamGroup = newCheckGroup());
		teamsContainer = new WebMarkupContainer("teamsContainer");
		teamGroup.add(teamsContainer.setOutputMarkupId(true));
		CheckGroupSelector checkGroupSelector = new CheckGroupSelector("teamGroupSelector", teamGroup);
		teamsContainer.add(checkGroupSelector);
		teamsContainer.add(usersView);
		teamsContainer.add(deleteSelectedButton = newDeleteSelectedButton(teamGroup));
		teamsContainer.add(addSelectedButton = addteamButton(teamGroup));
		BookmarkablePageLink<TeamEditPage> createTeam = new BookmarkablePageLink<>("link.create.team", TeamEditPage.class);
		teamsContainer.add(createTeam);
		add(form);

		teamsContainer.add(navigator = new BootstrapAjaxPagingNavigator(
				"navigatorFoot", usersView));

		allProjects = treeNodeService.getAllProjects();

		// add confirmation modal for deleting items, and adding team to a project
		add(deleteConfirmationModal = newDeleteConfirmationModal());
		add(addConfirmationModal = newAddConfirmationModal());
	}

	private void deleteSelectedTeams(Collection<Team> teams, AjaxRequestTarget target, Collection<Project> allProjects) {
		String message = new StringResourceModel("delete.confirmed", this, null).getString();

		for (Team team : teams) {
			//check if team to be deleted is assigned to any of the existing projects
			if(!allProjects.isEmpty()){
				for(Project project : allProjects){
					if(!project.getTeams().isEmpty()){

						for(Team projectTeam :  project.getTeams()){
							if(team.getId().equals(projectTeam.getId())){ 
								project.getTeams().remove(projectTeam);
								break;
							}
						}
					}
				}
			}
			for (TeamMembership member : team.getMembers()) {
				teamMemberService.delete(member);
			}

			teamService.delete(team);
		}
		getPage().success(message);
		target.add(feedbackPanel);
		target.add(teamsContainer);
		teamGroup.updateModel();
		target.add(deleteSelectedButton);
	}




	private void addSelectedTeams(Collection<Team> teams, AjaxRequestTarget target, Collection<Project> allProjects) {
		String message = new StringResourceModel("add.confirmed", this, null).getString();

		for (Team team : teams) {
			if (project != null) {
				if (!project.getTeams().contains(team)) {
					project.getTeams().add(team);
				}
			}
			teamService.update(team);
			projectService.update(project);
		}

		getPage().success(message);
		target.add(feedbackPanel);
		target.add(teamsContainer);
		teamGroup.updateModel();
		target.add(addSelectedButton);
	}

	private NotificationModal newDeleteConfirmationModal() {
		final NotificationModal notificationModal = new NotificationModal(
				"deleteConfirmationModal", new StringResourceModel(
						"delete.confirmation.modal.header", this, null),
						new StringResourceModel("delete.confirmation.modal.message",
								this, null), false);
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Primary, new StringResourceModel(
						"delete.confirmation.modal.submit.text", this, null),
						true) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// confirmed --> delete
				deleteSelectedTeams(teamGroup.getModelObject(), target, allProjects);
				// close modal
				closeDeleteConfirmationModal(notificationModal, target);
			}
		});
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Default, new StringResourceModel(
						"delete.confirmation.modal.cancel.text", this, null),
						true) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// Cancel clicked --> do nothing, close modal
				closeDeleteConfirmationModal(notificationModal, target);
			}
		});
		return notificationModal;
	}

	private void closeDeleteConfirmationModal(final Modal modal,
			AjaxRequestTarget target) {
		modal.appendCloseDialogJavaScript(target);
	}


	private NotificationModal newAddConfirmationModal() {
		final NotificationModal notificationModal = new NotificationModal(
				"addConfirmationModal", new StringResourceModel(
						"add.confirmation.modal.header", this, null),
						new StringResourceModel("add.confirmation.modal.message",
								this, null), false);
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Primary, new StringResourceModel(
						"add.confirmation.modal.submit.text", this, null),
						true) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// confirmed --> delete
				addSelectedTeams(teamGroup.getModelObject(), target, allProjects);
				// close modal
				closeAddConfirmationModal(notificationModal, target);
			}
		});
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Default, new StringResourceModel(
						"add.confirmation.modal.cancel.text", this, null),
						true) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// Cancel clicked --> do nothing, close modal
				closeAddConfirmationModal(notificationModal, target);
			}
		});
		return notificationModal;
	}


	private void closeAddConfirmationModal(final Modal modal,
			AjaxRequestTarget target) {
		modal.appendCloseDialogJavaScript(target);
	}







	@Override
	protected void onConfigure() {
		super.onConfigure();
		navigator.setVisible(provider.size() > itemsPerPage);
	}

	private WebMarkupContainer newMembersPanel(final Team team) {
		final WebMarkupContainer tagsContainer = new WebMarkupContainer("membersList");
		tagsContainer.setOutputMarkupPlaceholderTag(true);
		tagsContainer.add(new ListView<TeamMembership>("member", new ListModel<>(new ArrayList<>(team.getMembers()))) {
			@Override
			protected void populateItem(ListItem<TeamMembership> item) {
				final String userName = item.getModelObject().getUser().getFullName();
				final String role = item.getModelObject().getRole().toString();
				Link editButton = new BookmarkablePageLink("link.edit.member", UserEditPage.class, new PageParameters().add("id", item.getModelObject().getUser().getId()));
				item.add(editButton.add(new Label("user", userName)));
				item.add(new Label("role", role));
			}
		});
		return tagsContainer;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forUrl("assets/css/admin/user.css"));
	}

	private AjaxSubmitLink newDeleteSelectedButton(
			final CheckGroup<Team> teamGroup) {
		AjaxSubmitLink submitLink = new AjaxSubmitLink("deleteSelected") {

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
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				deleteConfirmationModal.appendShowDialogJavaScript(target);
			}
		};
		submitLink.setOutputMarkupId(true);
		return submitLink;
	}





	private AjaxSubmitLink addteamButton(
			final CheckGroup<Team> teamGroup) {
		AjaxSubmitLink submitLink = new AjaxSubmitLink("addSelected") {

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// only enabled if at least one user is selected
				if (!teamGroup.getModelObject().isEmpty() && project.getId() != null) {

					setEnabled(true);
				} else {
					add(new CssClassNameAppender(Model.of("disabled")) {
						private static final long serialVersionUID = 5588027455196328830L;

						// remove css class when component is rendered again
						@Override
						public boolean isTemporary(Component component) {
							return true;
						}
					});
					setEnabled(false);
				}
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				addConfirmationModal.appendShowDialogJavaScript(target);



			}
		};
		submitLink.setOutputMarkupId(true);
		return submitLink;
	}





	private CheckGroup newCheckGroup() {
		CheckGroup<Team> checkGroup = new CheckGroup<>("teamGroup", new ArrayList<Team>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(deleteSelectedButton);
				target.add(addSelectedButton);
			}
		});
		return checkGroup;
	}

	private class TeamEntityProvider extends EntityProvider<Team> {

		@Override
		public Iterator<? extends Team> iterator(long first, long count) {
			return teamService.getAllByAscendingName(Long.valueOf(first).intValue(),
					Long.valueOf(count).intValue()).iterator();
		}

		@Override
		public long size() {
			System.out.println("teamService.countAll()::::"+teamService.countAll());
			return teamService.countAll();
			//return 0;
		}

	}
}
