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
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.measure.CubesMetricMeasurement;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.service.dataadapter.AdapterSettingsService;
import eu.uqasar.service.dataadapter.CubesDataService;
import eu.uqasar.web.components.JSTemplates;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.provider.EntityProvider;

public class CubeAnalysisDataManagementPage extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2843966510217178969L;

	@Inject
	private CubesDataService cubesService;
	@Inject
	private AdapterSettingsService adapterService;
	
	// how many items do we show per page
	private static final int itemsPerPage = 10;
	private final WebMarkupContainer cubesContainer = newCubesContainer();
	private final Modal deleteConfirmationModal;
	private final CheckGroup<CubesMetricMeasurement> cubesGroup;
	private final AjaxSubmitLink deleteSelectedButton;

	/**
	 * Constructor building the page
	 *
	 * @param parameters
	 */
	public CubeAnalysisDataManagementPage(final PageParameters parameters) {

		super(parameters);
		
		Long adapterId = parameters.get("id").toLongObject();

		final Form<CubesMetricMeasurement> deleteForm = new Form<>("deleteForm");
		add(deleteForm);

		// add checkgroup for selecting multiple measurements
		deleteForm.add(cubesGroup = newCubesCheckGroup());

		// add the container holding list of existing measurements
		cubesGroup.add(cubesContainer.setOutputMarkupId(true));

		cubesContainer.add(new CheckGroupSelector(
				"cubesGroupSelector", cubesGroup));

		DataView<CubesMetricMeasurement> cubesMeasurements = new DataView<CubesMetricMeasurement>(
				"cubesMeasurements", new CubesProvider(adapterId), itemsPerPage) {


		private static final long serialVersionUID = 7687248000895943825L;

			@Override
			protected void populateItem(final Item<CubesMetricMeasurement> item) {
				final CubesMetricMeasurement cubesMetricMeasurement = item.getModelObject();

				item.add(new Check<>("cubesCheck", item.getModel(), cubesGroup));

				item.add(linkCubesQuery(cubesMetricMeasurement));

				item.add(new Label("value", new PropertyModel<String>(
						cubesMetricMeasurement, "value")));

				item.add(new Label("jsonContent", new PropertyModel<String>(
						cubesMetricMeasurement, "jsonContent")));

				item.add(new Label("timeStamp", new PropertyModel<Date>(
						cubesMetricMeasurement, "timeStamp")));
				
				// add button to show AddEditPage
                item.add(new BookmarkablePageLink<CubeAnalysisDataManagementEditPage>(
                        "edit", CubeAnalysisDataManagementEditPage.class,
                        forTableEntity(cubesMetricMeasurement,parameters)));
			}
		};
		cubesContainer.add(cubesMeasurements);

		// add links for table pagination
		cubesContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorHead", cubesMeasurements));
		cubesContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorFoot", cubesMeasurements));

		// add button to delete selected items
		cubesContainer
		.add(deleteSelectedButton = newDeleteSelectedButton(cubesGroup));

		// add confirmation modal for deleting items
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
    private static PageParameters forTableEntity(CubesMetricMeasurement tableEntity,final PageParameters parameters) {
        return new PageParameters().set("idForTableEntity", tableEntity.getId()).set("id", parameters.get("id"));
    }
    
	/**
	 *
	 * @return
	 */
	private WebMarkupContainer newCubesContainer() {
		return new WebMarkupContainer("cubesContainer") {

			private static final long serialVersionUID = 4444294849967215331L;

			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				// add javascript to load tagsinput plugin
				response.render(OnLoadHeaderItem.forScript(String.format(
						JSTemplates.LOAD_TABLE_SORTER, "Product-list")));
			}
        };
	}

	/**
	 * 
	 * @return
	 */
	private CheckGroup<CubesMetricMeasurement> newCubesCheckGroup() {
		CheckGroup<CubesMetricMeasurement> checkGroup = new CheckGroup<>(
                "cubesGroup", new ArrayList<CubesMetricMeasurement>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

			private static final long serialVersionUID = 7348039334236716476L;

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
			final CheckGroup<CubesMetricMeasurement> cubesGroup) {
		return new AjaxSubmitLink("deleteSelected") {

			private static final long serialVersionUID = 1526806233323350647L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				if (cubesGroup.getModelObject().isEmpty()) {
					add(new CssClassNameAppender(Model.of("disabled")) {

						private static final long serialVersionUID = -120425807017807645L;

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

			private static final long serialVersionUID = 4929496705396221870L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// confirmed --> delete
				deleteSelectedMeasurements(cubesGroup.getModelObject(),
						target);
				// close modal
				closeDeleteConfirmationModal(notificationModal, target);
			}
		});
		notificationModal.addButton(new ModalActionButton(notificationModal,
				Buttons.Type.Default, new StringResourceModel(
						"delete.confirmation.modal.cancel.text", this, null),
						true) {

			private static final long serialVersionUID = -7504717586865807253L;

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
			Collection<CubesMetricMeasurement> cubesMeasurements, AjaxRequestTarget target) {
		String message = new StringResourceModel("cubesmeasurement.selected.deleted",
				this, null).getString();
		cubesService.delete(cubesMeasurements);
		getPage().success(message);
		updateFeedbackPanel(target);
		updateCubesList(target);
		// update the delete button
		cubesGroup.updateModel();
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
	private void updateCubesList(AjaxRequestTarget target) {
		target.add(cubesContainer);
	}

	/**
	 *
	 * @param target
	 */
	private void updateFeedbackPanel(AjaxRequestTarget target) {
		target.add(feedbackPanel);
	}

	/**
	 * @return
	 */
	private ExternalLink linkCubesQuery(
			final CubesMetricMeasurement cubesMetricMeasurement) {

		ExternalLink link = new  ExternalLink("linkCubesQuery", cubesMetricMeasurement.getSelf()){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("target","_blank");
			}
		};
		link.add(new Label("cubesMetric", new PropertyModel<String>(
				cubesMetricMeasurement, "cubesMetric")));

		// tooltip config
		TooltipConfig confConfig = new TooltipConfig()
				.withPlacement(TooltipConfig.Placement.top);
		link.add(new TooltipBehavior(new PropertyModel<String>(cubesMetricMeasurement,
				"self"), confConfig));

		return link;
	}	
	
	private final class CubesProvider extends EntityProvider<CubesMetricMeasurement> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3884368466400425503L;
		private final AdapterSettings adapter;
		
		public CubesProvider(Long adapterID) {
			adapter = adapterService.getById(adapterID);
		}

		
		@Override
		public Iterator<? extends CubesMetricMeasurement> iterator(long first, long count) {
			return cubesService.getAllByAdapter(
					Long.valueOf(first).intValue(),
					Long.valueOf(count).intValue(), 
					adapter).iterator();
		}

		@Override
		public long size() {
			return cubesService.countAllByAdapter(adapter);
		}
	}
}


