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

import javax.inject.Inject;

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
import eu.uqasar.model.measure.JenkinsMetricMeasurement;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.service.dataadapter.AdapterSettingsService;
import eu.uqasar.service.dataadapter.JenkinsDataService;
import eu.uqasar.web.components.JSTemplates;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.provider.EntityProvider;

public class JenkinsDataManagementPage extends BasePage {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	private JenkinsDataService jenkinsService;
	@Inject 
	private AdapterSettingsService adapterService;
	// how many items do we show per page
	private static final int itemsPerPage = 10;
	private final WebMarkupContainer jenkinsContainer = newJenkinsContainer();
	private final Modal deleteConfirmationModal;
	private final CheckGroup<JenkinsMetricMeasurement> jenkinsGroup;
	private final AjaxSubmitLink deleteSelectedButton;

	/**
	 * Constructor building the page
	 *
	 * @param parameters
	 */
	public JenkinsDataManagementPage(final PageParameters parameters) {

		super(parameters);
		
		Long adapterId = parameters.get("id").toLongObject();
		
		final Form<JenkinsMetricMeasurement> deleteForm = new Form<>("deleteForm");
		add(deleteForm);

		// add checkgroup for selecting multiple products
		deleteForm.add(jenkinsGroup = newJenkinsCheckGroup());

		// add the container holding list of existing products
		jenkinsGroup.add(jenkinsContainer.setOutputMarkupId(true));

		jenkinsContainer.add(new CheckGroupSelector(
				"jenkinsGroupSelector", jenkinsGroup));

		DataView<JenkinsMetricMeasurement> jenkinsMeasurements = new DataView<JenkinsMetricMeasurement>(
				"jenkinsMeasurements", new JenkinsProvider(adapterId), itemsPerPage) {


					@Override
					protected void populateItem(final Item<JenkinsMetricMeasurement> item) {
						final JenkinsMetricMeasurement jenkinsMetricMeasurement = item.getModelObject();

						item.add(new Check<>("jenkinsCheck", item
								.getModel(), jenkinsGroup));

						item.add(new Label("name", new PropertyModel<String>(
								jenkinsMetricMeasurement, "name")));

						item.add(new Label("metric", new PropertyModel<String>(
								jenkinsMetricMeasurement, "jenkinsMetric")));
						
						item.add(new Label("value", new PropertyModel<String>(
								jenkinsMetricMeasurement, "value")));

						item.add(new Label("timeStamp", new PropertyModel<Date>(
								jenkinsMetricMeasurement, "timeStamp")));
						
						// add button to show AddEditPage
		                item.add(new BookmarkablePageLink<JenkinsDataManagementEditPage>(
		                        "edit", JenkinsDataManagementEditPage.class,
		                        forTableEntity(jenkinsMetricMeasurement,parameters)));
								
					}
				};
		jenkinsContainer.add(jenkinsMeasurements);

		// add links for table pagination
		jenkinsContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorHead", jenkinsMeasurements));
		jenkinsContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorFoot", jenkinsMeasurements));

		// add button to delete selected items
		jenkinsContainer
				.add(deleteSelectedButton = newDeleteSelectedButton(jenkinsGroup));

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
    private static PageParameters forTableEntity(JenkinsMetricMeasurement tableEntity,final PageParameters parameters) {
        return new PageParameters().set("idForTableEntity", tableEntity.getId()).set("id", parameters.get("id"));
    }
	

	/**
	 *
	 * @return
	 */
	private WebMarkupContainer newJenkinsContainer() {
		return new WebMarkupContainer("jenkinsContainer") {

			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				// add javascript to load tagsinput plugin
				response.render(OnLoadHeaderItem.forScript(String.format(
						JSTemplates.LOAD_TABLE_SORTER, "jenkins-list")));
			}
		;
	}

	;
	}

	/**
	 * 
	 * @return
	 */
	private CheckGroup<JenkinsMetricMeasurement> newJenkinsCheckGroup() {
		CheckGroup<JenkinsMetricMeasurement> checkGroup = new CheckGroup<JenkinsMetricMeasurement>(
				"jenkinsGroup", new ArrayList<JenkinsMetricMeasurement>());
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
	 * @param productGroup
	 * @return
	 */
	private AjaxSubmitLink newDeleteSelectedButton(
			final CheckGroup<JenkinsMetricMeasurement> jenkinsGroup) {
		return new AjaxSubmitLink("deleteSelected") {

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// only enabled if at least one Product is selected
				if (jenkinsGroup.getModelObject().isEmpty()) {
					add(new CssClassNameAppender(Model.of("disabled")) {

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
						deleteSelectedMeasurements(jenkinsGroup.getModelObject(),
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
	 * @param jenkinsMeasurements
	 * @param target
	 */
	private void deleteSelectedMeasurements(
			Collection<JenkinsMetricMeasurement> jenkinsMeasurements, AjaxRequestTarget target) {
		String message = new StringResourceModel("jenkinsmeasurement.selected.deleted",
				this, null).getString();
		jenkinsService.delete(jenkinsMeasurements);
		getPage().success(message);
		updateFeedbackPanel(target);
		updatejenkinsList(target);
		// update the delete button
		jenkinsGroup.updateModel();
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
	private void updatejenkinsList(AjaxRequestTarget target) {
		target.add(jenkinsContainer);
	}

	/**
	 *
	 * @param target
	 */
	private void updateFeedbackPanel(AjaxRequestTarget target) {
		target.add(feedbackPanel);
	}

	/**
	 *
	 * @param Product
	 * @return
	 */
	private static PageParameters forJenkins(JenkinsMetricMeasurement jenkinsMeasurement) {
		return new PageParameters().set("id", jenkinsMeasurement.getId());
	}


	private final class JenkinsProvider extends EntityProvider<JenkinsMetricMeasurement> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5178285310705591769L;
		private AdapterSettings adapter;
		

		public JenkinsProvider(Long adapterID) {
			adapter = adapterService.getById(adapterID);
		}

		@Override
		public Iterator<? extends JenkinsMetricMeasurement> iterator(long first, long count) {
			return jenkinsService.getAllByAdapter(
					Long.valueOf(first).intValue(),
					Long.valueOf(count).intValue(), 
					adapter).iterator();
		}

		@Override
		public long size() {
			return jenkinsService.countAllByAdapter(adapter);
		}
	}
}


