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
import eu.uqasar.model.measure.SonarMetricMeasurement;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.service.dataadapter.AdapterSettingsService;
import eu.uqasar.service.dataadapter.SonarDataService;
import eu.uqasar.web.components.JSTemplates;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.provider.EntityProvider;

public class StaticAnalysisDataManagementPage extends BasePage {

	private static final long serialVersionUID = 7887135260226932487L;

	@Inject
	private SonarDataService sonarService;
	
	@Inject
	private AdapterSettingsService adapterService;

	// how many items do we show per page
	private static final int itemsPerPage = 10;
	private final WebMarkupContainer sonarContainer = newSonarContainer();
	private final Modal deleteConfirmationModal;
	private final CheckGroup<SonarMetricMeasurement> sonarGroup;
	private final AjaxSubmitLink deleteSelectedButton;

	/**
	 * Constructor building the page
	 *
	 * @param parameters
	 */
	public StaticAnalysisDataManagementPage(final PageParameters parameters) {

		super(parameters);
		
		Long adapterId = parameters.get("id").toLongObject();
		
		final Form<SonarMetricMeasurement> deleteForm = new Form<>("deleteForm");
		add(deleteForm);

		// add checkgroup for selecting multiple products
		deleteForm.add(sonarGroup = newSonarCheckGroup());

		// add the container holding list of existing products
		sonarGroup.add(sonarContainer.setOutputMarkupId(true));

		sonarContainer.add(new CheckGroupSelector(
				"sonarGroupSelector", sonarGroup));

		DataView<SonarMetricMeasurement> sonarMeasurements = new DataView<SonarMetricMeasurement>(
				"sonarMeasurements", new SonarProvider(adapterId), itemsPerPage) {

					private static final long serialVersionUID = -392205969465703229L;

					@Override
					protected void populateItem(final Item<SonarMetricMeasurement> item) {
						final SonarMetricMeasurement sonarMetricMeasurement = item.getModelObject();

						item.add(new Check<>("sonarCheck", item
								.getModel(), sonarGroup));

						item.add(new Label("name", new PropertyModel<String>(
								sonarMetricMeasurement, "name")));

						item.add(new Label("key", new PropertyModel<String>(
								sonarMetricMeasurement, "sonarKey")));

						item.add(new Label("metric", new PropertyModel<String>(
								sonarMetricMeasurement, "sonarMetric")));
						
						item.add(new Label("value", new PropertyModel<String>(
								sonarMetricMeasurement, "value")));

						item.add(new Label("timeStamp", new PropertyModel<Date>(
								sonarMetricMeasurement, "timeStamp")));
								
						// add button to show AddEditPage
		                item.add(new BookmarkablePageLink<StaticAnalysisDataManagementEditPage>(
		                        "edit", StaticAnalysisDataManagementEditPage.class,
		                        forTableEntity(sonarMetricMeasurement,parameters)));
								
					}
				};
		sonarContainer.add(sonarMeasurements);

		// add links for table pagination
		sonarContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorHead", sonarMeasurements));
		sonarContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorFoot", sonarMeasurements));

		// add button to delete selected items
		sonarContainer
				.add(deleteSelectedButton = newDeleteSelectedButton(sonarGroup));

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
    private static PageParameters forTableEntity(SonarMetricMeasurement tableEntity,final PageParameters parameters) {
        return new PageParameters().set("idForTableEntity", tableEntity.getId()).set("id", parameters.get("id"));
    }


	/**
	 *
	 * @return
	 */
	private WebMarkupContainer newSonarContainer() {
		return new WebMarkupContainer("sonarContainer") {

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
	private CheckGroup<SonarMetricMeasurement> newSonarCheckGroup() {
		CheckGroup<SonarMetricMeasurement> checkGroup = new CheckGroup<>(
                "sonarGroup", new ArrayList<SonarMetricMeasurement>());
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
			final CheckGroup<SonarMetricMeasurement> sonarGroup) {
		return new AjaxSubmitLink("deleteSelected") {

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// only enabled if at least one Product is selected
				if (sonarGroup.getModelObject().isEmpty()) {
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
						deleteSelectedMeasurements(sonarGroup.getModelObject(),
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
	 * @param sonarMeasurements
	 * @param target
	 */
	private void deleteSelectedMeasurements(
			Collection<SonarMetricMeasurement> sonarMeasurements, AjaxRequestTarget target) {
		String message = new StringResourceModel("sonarmeasurement.selected.deleted",
				this, null).getString();
		sonarService.delete(sonarMeasurements);
		getPage().success(message);
		updateFeedbackPanel(target);
		updateSonarList(target);
		// update the delete button
		sonarGroup.updateModel();
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
	private void updateSonarList(AjaxRequestTarget target) {
		target.add(sonarContainer);
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
	 * @return
	 */
	private static PageParameters forSonar(SonarMetricMeasurement sonarMeasurement) {
		return new PageParameters().set("id", sonarMeasurement.getId());
	}


	private final class SonarProvider extends EntityProvider<SonarMetricMeasurement> {

		private static final long serialVersionUID = 1360608566900210699L;
		private final AdapterSettings adapter;
		
		public SonarProvider(Long adapterID) {
			adapter = adapterService.getById(adapterID);
		}
		
		@Override
		public Iterator<? extends SonarMetricMeasurement> iterator(long first, long count) {
			
			return sonarService.getAllByAdapter(
					Long.valueOf(first).intValue(),
					Long.valueOf(count).intValue(), 
					adapter).iterator();
		}

		@Override
		public long size() {
			return sonarService.countAllByAdapter(adapter);
		}
	}
}


