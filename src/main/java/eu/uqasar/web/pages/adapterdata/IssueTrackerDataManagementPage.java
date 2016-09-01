package eu.uqasar.web.pages.adapterdata;

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
import java.util.Date;
import java.util.Iterator;




import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.measure.JiraMetricMeasurement;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.service.dataadapter.AdapterSettingsService;
import eu.uqasar.service.dataadapter.JiraDataService;
import eu.uqasar.web.components.JSTemplates;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.provider.EntityProvider;

import javax.inject.Inject;

public class IssueTrackerDataManagementPage extends BasePage {

	private static final long serialVersionUID = -1183577810397631644L;

	@Inject
	private JiraDataService jiraService;
	@Inject 
	private AdapterSettingsService adapterService;
	
	// how many items do we show per page
	private static final int itemsPerPage = 10;
	private final WebMarkupContainer jiraContainer = newJiraContainer();
	private final Modal deleteConfirmationModal;
	private final CheckGroup<JiraMetricMeasurement> jiraGroup;
	private final AjaxSubmitLink deleteSelectedButton;

	/**
	 * Constructor building the page
	 *
	 * @param parameters
	 */
	public IssueTrackerDataManagementPage(final PageParameters parameters) {

		super(parameters);
		
		Long adapterId = parameters.get("id").toLongObject();

		final Form<JiraMetricMeasurement> deleteForm = new Form<>("deleteForm");
		add(deleteForm);

		// add checkgroup for selecting multiple measurements
		deleteForm.add(jiraGroup = newJiraCheckGroup());

		// add the container holding list of existing measurements
		jiraGroup.add(jiraContainer.setOutputMarkupId(true));

		jiraContainer.add(new CheckGroupSelector(
				"jiraGroupSelector", jiraGroup));

		DataView<JiraMetricMeasurement> jiraMeasurements = new DataView<JiraMetricMeasurement>(
				"jiraMeasurements", new JiraProvider(adapterId), itemsPerPage) {

			private static final long serialVersionUID = -2997007102890828835L;

			@Override
			protected void populateItem(final Item<JiraMetricMeasurement> item) {
				final JiraMetricMeasurement jiraMetricMeasurement = item.getModelObject();

				item.add(new Check<>("jiraCheck", item
						.getModel(), jiraGroup));

				item.add(new Label("key", new PropertyModel<String>(
						jiraMetricMeasurement, "jiraKey")));

				item.add(new Label("self", new PropertyModel<String>(
						jiraMetricMeasurement, "self")));

				item.add(new Label("jiraMetric", new PropertyModel<String>(
						jiraMetricMeasurement, "jiraMetric")));

				item.add(new Label("jsonContent", new PropertyModel<String>(
						jiraMetricMeasurement, "jsonContent")));

				item.add(new Label("timeStamp", new PropertyModel<Date>(
						jiraMetricMeasurement, "timeStamp")));
						
				// add button to show AddEditPage
                item.add(new BookmarkablePageLink<IssueTrackerDataManagementEditPage>(
                        "edit", IssueTrackerDataManagementEditPage.class,
                        forTableEntity(jiraMetricMeasurement,parameters)));		
			}
		};
		jiraContainer.add(jiraMeasurements);

		// add links for table pagination
		jiraContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorHead", jiraMeasurements));
		jiraContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorFoot", jiraMeasurements));

		// add button to delete selected items
		jiraContainer
		.add(deleteSelectedButton = newDeleteSelectedButton(jiraGroup));

		// add confirmation modal for deleting products
		add(deleteConfirmationModal = newDeleteConfirmationModal());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.uqasar.web.BasePage#getPageTitleModel()
	 */
	@Override
	protected IModel<String> getPageTitleModel() {
		return new StringResourceModel("page.title", this, null);
	}


	/**
     * 
     * @param tableEntity
     * @return
     */
    private static PageParameters forTableEntity(JiraMetricMeasurement tableEntity,final PageParameters parameters) {
        return new PageParameters().set("idForTableEntity", tableEntity.getId()).set("id", parameters.get("id"));
    }
    


	/**
	 *
	 * @return
	 */
	private WebMarkupContainer newJiraContainer() {
		return new WebMarkupContainer("jiraContainer") {

			private static final long serialVersionUID = -6725820191388731244L;

			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				// add javascript to load tagsinput plugin
				response.render(OnLoadHeaderItem.forScript(String.format(
						JSTemplates.LOAD_TABLE_SORTER, "Product-list")));
			}
        }

		;
	}

	/**
	 * 
	 * @return
	 */
	private CheckGroup<JiraMetricMeasurement> newJiraCheckGroup() {
		CheckGroup<JiraMetricMeasurement> checkGroup = new CheckGroup<>(
                "jiraGroup", new ArrayList<JiraMetricMeasurement>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

			private static final long serialVersionUID = -6392535303739708646L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateDeleteSelectedButton(target);
			}
		});
		return checkGroup;
	}

	/**
	 *
	 * @return
	 */
	private AjaxSubmitLink newDeleteSelectedButton(
			final CheckGroup<JiraMetricMeasurement> jiraGroup) {
		return new AjaxSubmitLink("deleteSelected") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 9197693153498795398L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				if (jiraGroup.getModelObject().isEmpty()) {
					add(new CssClassNameAppender(Model.of("disabled")) {

						/**
						 * 
						 */
						private static final long serialVersionUID = -5695023994382480066L;

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
	}


	/**
	 *
	 * @return
	 */
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
			private static final long serialVersionUID = -8579196626175159237L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// confirmed --> delete
				deleteSelectedMeasurements(jiraGroup.getModelObject(),
						target);
				// close modal
				closeDeleteConfirmationModal(notificationModal, target);
			}
		});
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Default, new StringResourceModel(
						"delete.confirmation.modal.cancel.text", this, null),
						true) {
			private static final long serialVersionUID = 8931306355855637710L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// Cancel clicked --> do nothing, close modal
				closeDeleteConfirmationModal(notificationModal, target);
			}
		});
		return notificationModal;
	}

	/**
	 *
	 * @param target
	 */
	private void deleteSelectedMeasurements(
			Collection<JiraMetricMeasurement> jiraMeasurements, AjaxRequestTarget target) {
		String message = new StringResourceModel("jirameasurement.selected.deleted",
				this, null).getString();
		jiraService.delete(jiraMeasurements);
		getPage().success(message);
		updateFeedbackPanel(target);
		updateJiraList(target);
		// update the delete button
		jiraGroup.updateModel();
		updateDeleteSelectedButton(target);
	}

	/**
	 *
	 * @param modal
	 * @param target
	 */
	private void closeDeleteConfirmationModal(final Modal modal,
			AjaxRequestTarget target) {
		// close
		modal.appendCloseDialogJavaScript(target);
	}

	/**
	 *
	 * @param target
	 */
	private void updateDeleteSelectedButton(AjaxRequestTarget target) {
		target.add(deleteSelectedButton);
	}

	/**
	 *
	 * @param target
	 */
	private void updateJiraList(AjaxRequestTarget target) {
		target.add(jiraContainer);
	}

	/**
	 *
	 * @param target
	 */
	private void updateFeedbackPanel(AjaxRequestTarget target) {
		target.add(feedbackPanel);
	}

	private final class JiraProvider extends EntityProvider<JiraMetricMeasurement> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2276310015122208686L;
		private final AdapterSettings adapter;
		
		public JiraProvider(Long adapterID) {
			adapter = adapterService.getById(adapterID);
		}

		@Override
		public Iterator<? extends JiraMetricMeasurement> iterator(long first, long count) {
			return jiraService.getAllByAdapter(
					Long.valueOf(first).intValue(),
					Long.valueOf(count).intValue(), 
					adapter).iterator();
		}

		@Override
		public long size() {
			return jiraService.countAllByAdapter(adapter);
		}
	}
}


