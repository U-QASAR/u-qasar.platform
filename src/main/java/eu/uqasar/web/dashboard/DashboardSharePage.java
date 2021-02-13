package eu.uqasar.web.dashboard;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
import org.apache.wicket.util.string.StringValue;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.dashboard.DbDashboard;
import eu.uqasar.model.user.User;
import eu.uqasar.service.DashboardService;
import eu.uqasar.service.user.TeamMembershipService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.components.behaviour.user.UserProfilePictureBackgroundBehaviour;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.admin.users.UserEditPage;
import eu.uqasar.web.pages.admin.users.panels.UserFilterPanel;
import eu.uqasar.web.pages.admin.users.panels.UserFilterStructure;
import eu.uqasar.web.provider.EntityProvider;

/**
 * Enables sharing dashboards (create a new copy of the respective page) with 
 * other platform users
 * TODO: Show only users that belong to the same project
 * TODO: View notification/dialog to the user with whom the dashboard has been 
 * shared with in order to enable declining/accepting/notifying about the 
 * shared dashboard
 *
 */
public class DashboardSharePage extends BasePage {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6781809021478796976L;

	@Inject
	private UserService userService;

	@Inject
	private TeamMembershipService teamMembershipService;

	@Inject
	private DashboardService dashboardService;

	private final CheckGroup<User> userGroup;
	private final AjaxSubmitLink shareToSelectedButton;
	private final AjaxLink cancelShareButton;
	private final BootstrapAjaxPagingNavigator navigator;
	private final WebMarkupContainer usersContainer;
	private final Modal shareConfirmationModal;

	private final UserEntityProvider userProvider;

	// The dashboard to be shared
    private DbDashboard dashboard;
		
	// how many items do we show per page
	private static final int itemsPerPage = 10;

	public DashboardSharePage(final PageParameters pageParameters) {
		super(pageParameters);

		// Load the dashboard to be shared
		loadDashboard(pageParameters.get("id"));
		
		
		UserFilterPanel filter = new UserFilterPanel("filter") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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
		usersContainer.add(shareToSelectedButton = newShareToSelectedButton(userGroup));
		usersContainer.add(cancelShareButton = newCancelButton());
		DataView<User> usersView = new DataView<User>("users", userProvider, itemsPerPage) {
			@Override
			protected void populateItem(Item<User> item) {
				final User user = item.getModelObject();
				Check<User> check = newShareCheck(item);
				item.add(check);
				Link userEditPictureLink = new BookmarkablePageLink("link.picture.edit.user", UserEditPage.class, new PageParameters().add("id", user.getId()));

				WebMarkupContainer picture = new WebMarkupContainer("td.picture");
				picture.add(new UserProfilePictureBackgroundBehaviour(user, User.PictureDimensions.Badge));
				item.add(userEditPictureLink.add(picture));
				item.add(new Label("td.username", new PropertyModel<>(user, "fullNameWithUserName")));
				item.add(new Label("td.role", new PropertyModel<>(user, "role")));
				item.setOutputMarkupId(true);
			}
		};

		// add links for table pagination
		usersContainer.add(navigator = new BootstrapAjaxPagingNavigator(
				"navigatorFoot", usersView));
		usersContainer.add(usersView);

		// add confirmation modal for sharing dashboard
		add(shareConfirmationModal = newShareConfirmationModal());

		add(form);

	}

	
	/**
	 * Attempt to load the dashboard by its ID
	 * @param idParam
	 */
	private void loadDashboard(final StringValue idParam) {
		if (!idParam.isEmpty()) {
			try {
				dashboard = dashboardService
						.getById(idParam.toOptionalLong());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	
	private void shareToSelectedUsers(Collection<User> users, AjaxRequestTarget target) {
		String message = new StringResourceModel("share.confirmed", this, null).getString();
		
		// Create a new copy of the dashboard, persist it and share 
		// it to the each user
		for (User user : users) {
			dashboard.setSharedBy(UQasar.getSession().getLoggedInUser().getFullName());
			DbDashboard copyDash = new DbDashboard(dashboard);
			DbDashboard persistedDash = dashboardService.create(copyDash);
			
			user.addDashboard(persistedDash);
			userService.update(user);
		}
		
		getPage().success(message);
		target.add(feedbackPanel);
		target.add(usersContainer);
		userGroup.updateModel();
		target.add(shareToSelectedButton);
	}

	private NotificationModal newShareConfirmationModal() {
		final NotificationModal notificationModal = new NotificationModal(
				"shareConfirmationModal", new StringResourceModel(
						"share.confirmation.modal.header", this, null),
						new StringResourceModel("share.confirmation.modal.message",
								this, null), false);
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Primary, new StringResourceModel(
						"share.confirmation.modal.submit.text", this, null),
						true) {

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// confirmed --> share
				shareToSelectedUsers(userGroup.getModelObject(), target);
				// close modal
				closeShareConfirmationModal(notificationModal, target);
			}
		});
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Default, new StringResourceModel(
						"share.confirmation.modal.cancel.text", this, null),
						true) {
			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// Cancel clicked --> do nothing, close modal
				closeShareConfirmationModal(notificationModal, target);
			}
		});
		return notificationModal;
	}

	private void closeShareConfirmationModal(final Modal modal,
			AjaxRequestTarget target) {
		modal.appendCloseDialogJavaScript(target);
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

	private AjaxSubmitLink newShareToSelectedButton(
			final CheckGroup<User> userGroup) {
		AjaxSubmitLink submitLink = new AjaxSubmitLink("shareToSelected") {

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// only enabled if at least one user is selected
				if (userGroup.getModelObject().isEmpty()) {
					add(new CssClassNameAppender(Model.of("disabled")) {

						/**
						 * 
						 */
						private static final long serialVersionUID = -3259529293647254883L;

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
				shareConfirmationModal.appendShowDialogJavaScript(target);
			}
		};
		submitLink.setOutputMarkupId(true);
		return submitLink;
	}

	/**
	 * 
	 * @return
	 */
	private AjaxLink newCancelButton() {
		AjaxLink cancelLink = new AjaxLink("cancelButton") {

			@Override
			public void onClick(AjaxRequestTarget target) {

				PageParameters params = new PageParameters();
				params.add(MESSAGE_PARAM,
						new StringResourceModel("canceled.message", this, null));
				params.add("id", dashboard.getId());

				// redirect to dashboard view page
				setResponsePage(DashboardViewPage.class, params);

			}
		};
		
		cancelLink.setOutputMarkupId(true);
		return cancelLink;
	}

	
	private CheckGroup newCheckGroup() {
		CheckGroup<User> checkGroup = new CheckGroup<>("userGroup", new ArrayList<User>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(shareToSelectedButton);
			}
		});
		return checkGroup;
	}

	private Check<User> newShareCheck(final Item<User> item) {
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
