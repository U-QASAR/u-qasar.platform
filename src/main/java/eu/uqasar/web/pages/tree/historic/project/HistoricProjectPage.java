package eu.uqasar.web.pages.tree.historic.project;

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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.tree.ITreeNode;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.historic.AbstractHistoricValues;
import eu.uqasar.model.tree.historic.HistoricValuesProject;
import eu.uqasar.service.HistoricalDataService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.components.JSTemplates;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.provider.EntityProvider;

/**
 * 
 *
 *
 */
public class HistoricProjectPage extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3800543700842514105L;

	@Inject	HistoricalDataService historicalService;
	
	@Inject TreeNodeService treeNodeService;

	// how many items do we show per page
	private static final int itemsPerPage = 10;
	private final WebMarkupContainer dataContainer = newDataContainer();
	private final Modal deleteConfirmationModal;
	private final CheckGroup<AbstractHistoricValues> dataGroup;
	private final AjaxSubmitLink deleteSelectedButton;
	private final Chart historicChart;
	
	private final Long projectId;
	private final Project project;

	/**
	 * Constructor building the page
	 *
	 * @param parameters
	 */
	public HistoricProjectPage(final PageParameters parameters) {

		super(parameters);
		
		// TODO: I dont know why this does not work???
//		String key = getPageParameters().get("project-key").toOptionalString();
		project = treeNodeService.getTreeNodeByKey("U-QASAR");
		projectId = project.getId();
		
		add(new Label("metricName", project.getName()));
		
		final Form<AbstractHistoricValues> deleteForm = new Form<>("deleteForm");
		add(deleteForm);

		// add checkgroup for selecting multiple measurements
		deleteForm.add(dataGroup = newDataCheckGroup());

		// add the container holding list of existing measurements
		dataGroup.add(dataContainer.setOutputMarkupId(true));

		dataContainer.add(new CheckGroupSelector(
				"dataGroupSelector", dataGroup));

		DataView<AbstractHistoricValues> dataMeasurements = new DataView<AbstractHistoricValues>(
				"dataMeasurements", new DataProvider(), itemsPerPage) {

		private static final long serialVersionUID = 7687248000895943825L;

			@Override
			protected void populateItem(final Item<AbstractHistoricValues> item) {
				final AbstractHistoricValues historicDataMeasurement = item.getModelObject();

				item.add(new Check<>("dataCheck", item
						.getModel(), dataGroup));
	
				item.add(new Label ("value", new PropertyModel<String>(
						historicDataMeasurement, "value")));

				item.add(new Label ("lowerAcceptanceLimit", new PropertyModel<String>(
						historicDataMeasurement, "lowerAcceptanceLimit")));
				
				item.add(new Label("upperAcceptanceLimit", new PropertyModel<String>(
						historicDataMeasurement, "upperAcceptanceLimit")));

				item.add(new Label("timeStamp", new PropertyModel<Date>(
						historicDataMeasurement, "date")));
				
			}
		};
		dataContainer.add(dataMeasurements);
		
		// Add graphical representation of the values
		dataContainer.add(historicChart = historicChart());
		
		// add links for table pagination
		dataContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorHead", dataMeasurements));
		dataContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorFoot", dataMeasurements));

		// add button to delete selected items
		dataContainer
		.add(deleteSelectedButton = newDeleteSelectedButton(dataGroup));

		// add confirmation modal for deleting products
		add(deleteConfirmationModal = newDeleteConfirmationModal());
		
	}

	/**
	 * @return Returns a graphical representation of all the values, 
	 * 		   thresholds and target value by date 
	 */
	private Chart historicChart(){
		Options options = new Options();
		options.setTitle(new Title(project.getName()));
		
		List<Number> values = new ArrayList<>();
		List<Number> upLimit = new ArrayList<>();
		List<Number> lowLimit = new ArrayList<>();
		List<String> dates = new ArrayList<>();
		
		// Prepare information to be show in the graphic
		for (AbstractHistoricValues h : historicalService.getAllHistValuesForProjectAsc(projectId)) {
			values.add(h.getValue());
			upLimit.add(h.getUpperAcceptanceLimit());
			lowLimit.add(h.getLowerAcceptanceLimit());
			dates.add(new SimpleDateFormat("dd.MM.yyyy").format(h.getDate()));
		}
		
		// X Axis
		Axis xAxis = new Axis();
		xAxis.setCategories(dates);
		options.setxAxis(xAxis);
		
		// Y Axis
		Axis yAxis = new Axis();
		options.setyAxis(yAxis);
		
		// Adding series to the graphic
		options.addSeries(new SimpleSeries().setName("Value").setData(values));
		options.addSeries(new SimpleSeries().setName("UpLimit").setData(upLimit));
		options.addSeries(new SimpleSeries().setName("LowLimit").setData(lowLimit));
		
		// Legend
		Legend legend = new Legend();
		legend.setBorderWidth(0);
		options.setLegend(legend);
		
		return new Chart("chart",options);
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
	 * @return
	 */
	private WebMarkupContainer newDataContainer() {
		return new WebMarkupContainer("dataContainer") {

			private static final long serialVersionUID = 4444294849967215331L;

			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				// add javascript to load tagsinput plugin
				response.render(OnLoadHeaderItem.forScript(String.format(
						JSTemplates.LOAD_TABLE_SORTER, "historic-list")));
			}
        };
	}

	/**
	 * 
	 * @return
	 */
	private CheckGroup<AbstractHistoricValues> newDataCheckGroup() {
		CheckGroup<AbstractHistoricValues> checkGroup = new CheckGroup<>(
                "dataGroup", new ArrayList<AbstractHistoricValues>());
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
			final CheckGroup<AbstractHistoricValues> cubesGroup) {
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

			private static final long serialVersionUID = 1797796653537766182L;

			@Override
			protected void onAfterClick(AjaxRequestTarget target) {
				// confirmed --> delete
				deleteSelectedMeasurements(dataGroup.getModelObject(),
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
	 * @param dataMeasurements
	 * @param target
	 */
	private void deleteSelectedMeasurements(
			Collection<AbstractHistoricValues> dataMeasurements, AjaxRequestTarget target) {
		String message = new StringResourceModel("cubesmeasurement.selected.deleted",
				this, null).getString();
		// Soft-delete the current selected data 
		historicalService.softDelete(dataMeasurements);
		getPage().success(message);
		updateFeedbackPanel(target);
		updateDataList(target);
		updateGraphic(target);
		// update the delete button
		dataGroup.updateModel();
		updateDeleteSelectedButton(target);
	}

	/**
	 *
	 * @param modal
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
	private void updateDataList(AjaxRequestTarget target) {
		target.add(dataContainer);
	}

	private void updateGraphic(AjaxRequestTarget target){
		target.add(historicChart);
	}
	
	/**
	 *
	 * @param target
	 */
	private void updateFeedbackPanel(AjaxRequestTarget target) {
		target.add(feedbackPanel);
	}

	protected Long getRequestedNodeId() {
		StringValue id = getPageParameters().get("id");
		if (id.isEmpty()) {
			String key = getPageParameters().get("project-key")
					.toOptionalString();
			if (key != null) {
				ITreeNode<String> node = treeNodeService
						.getTreeNodeByKey(key);
				if (node != null) {
					id = StringValue.valueOf(node.getId());
				} else {
					// TODO give translated message for the 404 reason
					throw new AbortWithHttpErrorCodeException(404);
				}
			}
		}
		return id.toOptionalLong();
	}
	
	/**
	 *
	 *
	 *
	 */
	private final class DataProvider extends EntityProvider<AbstractHistoricValues> {

		private static final long serialVersionUID = 943605630815322199L;

		@Override
		public Iterator<HistoricValuesProject> iterator(long first, long count) {
			
			return historicalService.getAllHistValuesForProject(projectId,
					Long.valueOf(first).intValue(),
					Long.valueOf(count).intValue()).iterator();
		}

		@Override
		public long size() {
			return historicalService.countHistValuesForProject(projectId);
		}
	}
}


