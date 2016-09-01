/*
 */
package eu.uqasar.web.pages.admin.companies;

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
import java.util.List;
import java.util.Objects;

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
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.company.Company;
import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.model.user.User;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.service.user.TeamMembershipService;
import eu.uqasar.service.user.TeamService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.web.UQSession;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.admin.AdminBasePage;
import eu.uqasar.web.pages.admin.companies.panels.CompanyFilterPanel;
import eu.uqasar.web.pages.admin.companies.panels.CompanyFilterStructure;
import eu.uqasar.web.provider.EntityProvider;

/**
 *
 *
 */
public class CompanyListPage extends AdminBasePage {

	@Inject
	private CompanyService companyService;
	@Inject
	private UserService userservice;
	@Inject
	private TeamService teamService;
	@Inject
	private TeamMembershipService teamMembershipService;
	
	private final CheckGroup<Company> companyGroup;
	private final AjaxSubmitLink deleteSelectedButton;
	private final BootstrapAjaxPagingNavigator navigator;
	private final Modal deleteConfirmationModal;
	private final WebMarkupContainer companyContainer;
	private CompanyProvider companyProvider;

	// how many items do we show per page
	private static final int itemsPerPage = 10;

	public CompanyListPage(final PageParameters pageParameters) {
		super(pageParameters);

		
		Form<Void> form = new Form("form");
		companyContainer = new WebMarkupContainer("companyContainer");
		
		 CompanyFilterPanel filterPanel = new CompanyFilterPanel("filter") {

			@Override
			public void applyClicked(AjaxRequestTarget target, Form<?> form) {
				if(getFilter().getName() == null && 
						getFilter().getShortName() == null && 
							getFilter().getCountry() == null){
					companyProvider.setFilter(null);
				}else{
					companyProvider.setFilter(this.getFilter());				
				}
				target.add(companyContainer);
			}

			@Override
			public void resetClicked(AjaxRequestTarget target, Form<?> form) {
				companyProvider.setFilter(null);	
				target.add(companyContainer);
			}

		};
		add(filterPanel);
		if(filterPanel.getFilter().getName() == null && 
				filterPanel.getFilter().getShortName() == null && 
					filterPanel.getFilter().getCountry() == null){
			companyProvider = new CompanyProvider();
		} else{
			companyProvider = new CompanyProvider(filterPanel.getFilter());
		}
		form.add(companyGroup = newCheckGroup());
		companyGroup.add(companyContainer.setOutputMarkupId(true));
		CheckGroupSelector checkGroupSelector = new CheckGroupSelector("companyGroupSelector", companyGroup);
		companyContainer.add(checkGroupSelector);
		companyContainer.add(deleteSelectedButton = newDeleteSelectedButton(companyGroup));
		
		DataView<Company> companiesView = new DataView<Company>("companies", companyProvider, itemsPerPage) {
			@Override
			protected void populateItem(Item<Company> item) {
				final Company company = item.getModelObject();
				
				Check<Company> check = newDeleteCheck(item);
				item.add(check);
				
				Link companyEditNameLink = new BookmarkablePageLink("link.name.edit.company", CompanyEditPage.class, new PageParameters().add("id", company.getId()));
				item.add(companyEditNameLink.add(new Label("td.name", new PropertyModel<>(company, "name"))));
				
				item.add(new Label("td.shortName", new PropertyModel<>(company, "shortName")));
				
				item.add(new Label("td.street", new PropertyModel<>(company, "street") )); 
				item.add(new Label("td.zipcode", new PropertyModel<>(company, "zipcode") )); 
				item.add(new Label("td.city", new PropertyModel<>(company, "city") )); 
				item.add(new Label("td.country", new PropertyModel<>(company, "country") ));
				
				item.add(new Label("td.phone", new PropertyModel<>(company, "phone") ));
				item.add(new Label("td.fax", new PropertyModel<>(company, "fax") ));

				Link companyEditLink = new BookmarkablePageLink("link.actions.edit.company", CompanyEditPage.class, new PageParameters().add("id", company.getId()));
				item.add(companyEditLink);

			
				item.setOutputMarkupId(true);
			}
		};

		// add links for table pagination
		companyContainer.add(navigator = new BootstrapAjaxPagingNavigator(
				"navigatorFoot", companiesView));
		companyContainer.add(companiesView);

		BookmarkablePageLink<CompanyEditPage> createUser = new BookmarkablePageLink<>("link.create.company", CompanyEditPage.class);
		companyContainer.add(createUser);
		add(form);

		// add confirmation modal for deleting products
		add(deleteConfirmationModal = newDeleteConfirmationModal());
	}

	private void deleteSelectedCompanies(Collection<Company> companies, AjaxRequestTarget target) {
		String message = new StringResourceModel("delete.confirmed", this, null).getString();
		for (Company company : companies) {
			List<User> users = userservice.getByCompany(company);
			for (User user : users)	{
				Collection<TeamMembership> members = teamMembershipService.getForUser(user);
				if (!members.isEmpty()) {
					for (TeamMembership membership : members) {
						Team team = membership.getTeam();
						team.getMembers().remove(membership);
						teamMembershipService.delete(membership);
						teamService.update(team);
					}
				}
			}
			userservice.delete(users);
		}
		companyService.delete(companies);
		getPage().success(message);
		target.add(feedbackPanel);
		target.add(companyContainer);
		companyGroup.updateModel();
		target.add(deleteSelectedButton);
		setResponsePage(CompanyListPage.class);
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
						deleteSelectedCompanies(companyGroup.getModelObject(), target);
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

	public static PageParameters forCompany(Company company) {
		if(company.getName() == null) {
			return new PageParameters();
		}
		else if (company != null) {
			return forCompany(company.getName());
		} else {
			return new PageParameters();
		}
	}
	
	private static PageParameters forCompany(final String name) {
		return new PageParameters().add("name", name);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		navigator.setVisible(companyProvider.size() > itemsPerPage);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forUrl("assets/css/admin/user.css"));
	}

	private AjaxSubmitLink newDeleteSelectedButton(
			final CheckGroup<Company> companyGroup) {
		AjaxSubmitLink submitLink = new AjaxSubmitLink("deleteSelected") {

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// only enabled if at least one user is selected
				if (companyGroup.getModelObject().isEmpty()) {
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
		CheckGroup<User> checkGroup = new CheckGroup<>("companyGroup", new ArrayList<User>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(deleteSelectedButton);
			}
		});
		return checkGroup;
	}

	private Check<Company> newDeleteCheck(final Item<Company> item) {
        return new Check<Company>("companyCheck", item.getModel(), companyGroup) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!Objects.equals(item.getModelObject().getId(), UQSession.get().getLoggedInUser().getId()));
            }
        };
	}
	
	

	private final class CompanyProvider extends EntityProvider<Company> {

		private static final long serialVersionUID = -1527580045919906872L;

		private CompanyFilterStructure filter;
		private String selected;

		public CompanyProvider() {
			this.filter = null;
		}

        public CompanyProvider(CompanyFilterStructure filter) {
			this.filter = filter;
		}

		public void setFilter(CompanyFilterStructure filter) {
			this.filter = filter;
		}

		@Override
		public Iterator<? extends Company> iterator(long first, long count) {
			if(filter == null){
				return companyService.getAllByAscendingName().iterator();
			} else {
				return companyService.getAllByAscendingNameFiltered(
						filter, 
						Long.valueOf(first).intValue(), 
						Long.valueOf(count).intValue()).iterator();
				}

		}
		@Override
		public long size() {
			if(filter == null){
				return companyService.countAll();
			} else{
				return companyService.countAllFiltered(filter);
			}
		}
	}


}
