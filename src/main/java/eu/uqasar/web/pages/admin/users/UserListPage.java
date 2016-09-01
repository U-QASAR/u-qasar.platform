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


import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.user.RegistrationStatus;
import eu.uqasar.model.user.User;
import eu.uqasar.service.user.TeamMembershipService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.components.AnchorableBookmarkablePageLink;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.components.behaviour.user.UserProfilePictureBackgroundBehaviour;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.pages.admin.users.panels.UserFilterPanel;
import eu.uqasar.web.pages.admin.users.panels.UserFilterStructure;
import eu.uqasar.web.provider.EntityProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import javax.inject.Inject;
import org.apache.wicket.AttributeModifier;
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
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *
 */
public class UserListPage extends AdminBasePage {

	@Inject
	private UserService userService;
	@Inject
	private TeamMembershipService teamMembershipService;
	
	private final CheckGroup<User> userGroup;
	private final AjaxSubmitLink deleteSelectedButton;
	private final BootstrapAjaxPagingNavigator navigator;
	private final Modal deleteConfirmationModal;
	private final WebMarkupContainer usersContainer;

	private final UserEntityProvider userProvider;

	// how many items do we show per page
	private static final int itemsPerPage = 10;

	public UserListPage(final PageParameters pageParameters) {
		super(pageParameters);

		UserFilterPanel filter = new UserFilterPanel("filter") {

			@Override
			public void applyClicked(AjaxRequestTarget target, Form<?> form) {
				userProvider.setFilter(this.getFilter());
				target.add(usersContainer);
			}

			@Override
			public void resetClicked(AjaxRequestTarget target, Form<?> form) {
				userProvider.setFilter(new UserFilterStructure());
				target.add(usersContainer);
			}
		};
		add(filter);
		userProvider = new UserEntityProvider(filter.getFilter());

		Form<Void> form = new Form("form");
		usersContainer = new WebMarkupContainer("usersContainer");
		form.add(userGroup = newCheckGroup());
		userGroup.add(usersContainer.setOutputMarkupId(true));
		CheckGroupSelector checkGroupSelector = new CheckGroupSelector("userGroupSelector", userGroup);
		usersContainer.add(checkGroupSelector);
		usersContainer.add(deleteSelectedButton = newDeleteSelectedButton(userGroup));
		DataView<User> usersView = new DataView<User>("users", userProvider, itemsPerPage) {
			@Override
			protected void populateItem(Item<User> item) {
				final User user = item.getModelObject();
				Check<User> check = newDeleteCheck(item);
				item.add(check);
				Link userEditPictureLink = new BookmarkablePageLink("link.picture.edit.user", UserEditPage.class, new PageParameters().add("id", user.getId()));

				WebMarkupContainer picture = new WebMarkupContainer("td.picture");
				picture.add(new UserProfilePictureBackgroundBehaviour(user, User.PictureDimensions.Badge));
				item.add(userEditPictureLink.add(picture));
				Link userEditNameLink = new BookmarkablePageLink("link.name.edit.user", UserEditPage.class, new PageParameters().add("id", user.getId()));
				item.add(userEditNameLink.add(new Label("td.username", new PropertyModel<>(user, "fullNameWithUserName"))));
				item.add(new Label("td.role", new PropertyModel<>(user, "role")));
				item.add(new Label("td.source", new PropertyModel<>(user, "source")));

				Link userEditLink = new BookmarkablePageLink("link.actions.edit.user", UserEditPage.class, new PageParameters().add("id", user.getId()));
				item.add(userEditLink);

				Link teamEditLink = new AnchorableBookmarkablePageLink("link.actions.edit.team", UserEditPage.class, new PageParameters().add("id", user.getId()), "teams");
				item.add(teamEditLink);

				item.add(getRegistrationStatusIcon(user));
				item.setOutputMarkupId(true);
			}
		};

		// add links for table pagination
		usersContainer.add(navigator = new BootstrapAjaxPagingNavigator(
				"navigatorFoot", usersView));
		usersContainer.add(usersView);

		BookmarkablePageLink<UserEditPage> createUser = new BookmarkablePageLink<>("link.create.user", UserEditPage.class);
		usersContainer.add(createUser);
		add(form);

		// add confirmation modal for deleting products
		add(deleteConfirmationModal = newDeleteConfirmationModal());
	}

	private void deleteSelectedUsers(Collection<User> users, AjaxRequestTarget target) {
		String message = new StringResourceModel("delete.confirmed", this, null).getString();
		teamMembershipService.removeUsersFromTeams(users);
		userService.delete(users);
		getPage().success(message);
		target.add(feedbackPanel);
		target.add(usersContainer);
		userGroup.updateModel();
		target.add(deleteSelectedButton);
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

					@Override
					protected void onAfterClick(AjaxRequestTarget target) {
						// confirmed --> delete
						deleteSelectedUsers(userGroup.getModelObject(), target);
						// close modal
						closeDeleteConfirmationModal(notificationModal, target);
					}
				});
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Default, new StringResourceModel(
						"delete.confirmation.modal.cancel.text", this, null),
				true) {
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

	private Icon getRegistrationStatusIcon(final User user) {
		final RegistrationStatus status = user.getRegistrationStatus();
		String typeString;
		switch (status) {
			case CANCELLED:
				typeString = "ban-circle";
				break;
			case PENDING:
				typeString = "time";
				break;
			default:
				typeString = "check-sign";
		}
		IconType type = new IconType(typeString);
		Icon icon = new Icon("td.status", type);
		icon.add(new AttributeModifier("title", Model.of(user.getRegistrationStatus())));
		return icon;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		navigator.setVisible(userProvider.size() > itemsPerPage);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forUrl("assets/css/admin/user.css"));
	}

	private AjaxSubmitLink newDeleteSelectedButton(
			final CheckGroup<User> userGroup) {
		AjaxSubmitLink submitLink = new AjaxSubmitLink("deleteSelected") {

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// only enabled if at least one user is selected
				if (userGroup.getModelObject().isEmpty()) {
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

	private CheckGroup newCheckGroup() {
		CheckGroup<User> checkGroup = new CheckGroup<>("userGroup", new ArrayList<User>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(deleteSelectedButton);
			}
		});
		return checkGroup;
	}

	private Check<User> newDeleteCheck(final Item<User> item) {
        return new Check<User>("userCheck", item.getModel(), userGroup) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!Objects.equals(item.getModelObject().getId(), UQSession.get().getLoggedInUser().getId()));
            }
        };
	}

	private class UserEntityProvider extends EntityProvider<User> {

		private UserFilterStructure filter;

		public UserEntityProvider(UserFilterStructure filter) {
			this.filter = filter;
		}

		public void setFilter(UserFilterStructure filter) {
			this.filter = filter;
		}

		@Override
		public Iterator<? extends User> iterator(long first, long count) {
			return userService.getAllByAscendingNameFiltered(filter, Long.valueOf(first).intValue(),
					Long.valueOf(count).intValue()).iterator();
		}

		@Override
		public long size() {
			return userService.countAllFiltered(filter);
		}

	}
}
