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
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
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
import org.jboss.solder.logging.Logger;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.Project;
import eu.uqasar.service.dataadapter.AdapterSettingsService;
import eu.uqasar.service.dataadapter.CubesDataService;
import eu.uqasar.service.dataadapter.JenkinsDataService;
import eu.uqasar.service.dataadapter.JiraDataService;
import eu.uqasar.service.dataadapter.SonarDataService;
import eu.uqasar.service.dataadapter.TestLinkDataService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.web.components.JSTemplates;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.provider.EntityProvider;

public class AdapterManagementPage extends BasePage {

	private static final long serialVersionUID = 4481705800235759540L;

	@Inject
	private AdapterSettingsService adapterSettingsService;
	@Inject
	private TreeNodeService treeNodeService;
	@Inject
	private JiraDataService jiraDataService;
	@Inject
	private SonarDataService sonarDataService;
	@Inject
	private TestLinkDataService testlinkDataService;
	@Inject 
	private CubesDataService cubesDataService;
	@Inject
	private JenkinsDataService jenkinsDataService;
	
	// how many adapters do we show per page
	private static final int itemsPerPage = 10;

	// container holding the list of adapters
	private final WebMarkupContainer adapterContainer = newAdapterSettingsContainer();

	private final Modal deleteConfirmationModal;

	private final CheckGroup<AdapterSettings> adapterGroup;

	private final AjaxSubmitLink deleteSelectedButton;

	private final Logger logger = Logger.getLogger(AdapterManagementPage.class);

	/**
	 * Constructor building the page
	 *
	 * @param parameters
	 */
	public AdapterManagementPage(final PageParameters parameters) {

		super(parameters);

		final Form<AdapterSettings> deleteForm = new Form<>("deleteForm");
		add(deleteForm);

		// add checkgroup for selecting multiple products
		deleteForm.add(adapterGroup = newAdapterSettingsCheckGroup());

		// add the container holding list of existing products
		adapterGroup.add(adapterContainer.setOutputMarkupId(true));

		adapterContainer.add(new CheckGroupSelector(
				"adapterGroupSelector", adapterGroup));

		DataView<AdapterSettings> adapterSettings = new DataView<AdapterSettings>(
				"adapters", new AdapterProvider(), itemsPerPage) {


			/**
			 * 
			 */
			private static final long serialVersionUID = 5487182988227539575L;

			@Override
			protected void populateItem(final Item<AdapterSettings> item) {
				final AdapterSettings adapterSettings = item.getModelObject();

				item.add(new Check<>("adapterCheck", item
						.getModel(), adapterGroup));

				item.add(new Label("name", new PropertyModel<String>(
						adapterSettings, "name")));

				item.add(new Label("metricSource", 
						new PropertyModel<String>(adapterSettings, 
								"metricSource")));

				item.add(new Label("url", new PropertyModel<String>(
						adapterSettings, "url")));

				item.add(new Label("project", 
						new PropertyModel<Project>(adapterSettings, 
								"project")));

				item.add(new Label("latestUpdate", new PropertyModel<Date>(
						adapterSettings, "latestUpdate")));

				item.add(new IndicatingAjaxLink<Object>("updateAdapterData") {

					private static final long serialVersionUID = 6245494998390009999L;

					@Override
					public void onClick(AjaxRequestTarget target) {
                        logger.info("AdapterManagementPage::updateAdapterData()::onClick()");
						adapterSettings.updateAdapterData();
						viewUpdateSuccessful(target);
						target.add(adapterContainer);
					}
				});

				// add button to show AddEditPage
				item.add(new BookmarkablePageLink<AdapterAddEditPage>(
						"edit", AdapterAddEditPage.class,
						forAdapter(adapterSettings)));

				// Depending on the adapter type add a link to the page 
				// where the adapter data can be managed.
                if (adapterSettings.getMetricSource() == MetricSource.IssueTracker) {
                    item.add(new BookmarkablePageLink<IssueTrackerDataManagementPage>(
                        "manage", IssueTrackerDataManagementPage.class, 
                        forAdapter(adapterSettings)));
                } else if (adapterSettings.getMetricSource() == MetricSource.StaticAnalysis){
                    item.add(new BookmarkablePageLink<StaticAnalysisDataManagementPage>(
                        "manage", StaticAnalysisDataManagementPage.class, 
                        forAdapter(adapterSettings)));
                } else if (adapterSettings.getMetricSource() == MetricSource.TestingFramework) {
                    item.add(new BookmarkablePageLink<TestFrameworkDataManagementPage>(
                    	"manage", TestFrameworkDataManagementPage.class, forAdapter(adapterSettings)));                    
                } else if (adapterSettings.getMetricSource() == MetricSource.CubeAnalysis) {
                	item.add(new BookmarkablePageLink<CubeAnalysisDataManagementPage>(
                    	"manage", CubeAnalysisDataManagementPage.class, 
                    	forAdapter(adapterSettings)));
                } else if (adapterSettings.getMetricSource() == MetricSource.VersionControl) {
                	item.add(new BookmarkablePageLink<GitlabDataManagementPage>(
                		"manage", GitlabDataManagementPage.class, 
                		forAdapter(adapterSettings)));
                } else if (adapterSettings.getMetricSource() == MetricSource.ContinuousIntegration) {
                	item.add(new BookmarkablePageLink<JenkinsDataManagementPage>(
                    		"manage", JenkinsDataManagementPage.class, 
                    		forAdapter(adapterSettings)));                	
                	
                } else {
                    item.add(new WebMarkupContainer("manage").setVisible(false));
                }
			}
		};
		// add list of adapters to container
		adapterContainer.add(adapterSettings);

		// add button to new adapter settings
		adapterContainer
		.add(new BookmarkablePageLink<AdapterAddEditPage>(
				"addAdapterLink", AdapterAddEditPage.class));

		// add links for table pagination
		adapterContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorHead", adapterSettings));
		adapterContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorFoot", adapterSettings));

		// add button to delete selected adapters
		adapterContainer
		.add(deleteSelectedButton = newDeleteSelectedButton(adapterGroup));

		// add confirmation modal for deleting settings
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
	 * @return
	 */
	private WebMarkupContainer newAdapterSettingsContainer() {
		return new WebMarkupContainer("adapterContainer") {


			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				// add javascript to load tagsinput plugin
				response.render(OnLoadHeaderItem.forScript(String.format(
						JSTemplates.LOAD_TABLE_SORTER, "AdapterSettings-list")));
			}
        }

		;
	}

	/**
	 * 
	 * @return
	 */
	private CheckGroup<AdapterSettings> newAdapterSettingsCheckGroup() {
		CheckGroup<AdapterSettings> checkGroup = new CheckGroup<>(
                "adapterGroup", new ArrayList<AdapterSettings>());
		checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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
			final CheckGroup<AdapterSettings> adapterGroup) {
		return new AjaxSubmitLink("deleteSelected") {
			private static final long serialVersionUID = 1162060284069587067L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// only enabled if at least one adapter is selected
				if (adapterGroup.getModelObject().isEmpty()) {
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
				deleteSelectedAdapters(adapterGroup.getModelObject(),
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
	private void deleteSelectedAdapters(
			Collection<AdapterSettings> adapters, AjaxRequestTarget target) {
		String message = new StringResourceModel("adapter.selected.deleted",
				this, null).getString();

		// Delete the selected adapters
		for (AdapterSettings adapter : adapters) {
			
			// First, remove the adapter from the project if exists
			if (adapter.getProject() != null && adapter.getProject().getAdapterSettings() != null) {
				adapter.setProject(null);
			}
			
			// Delete the measurements belonging to the adapters
			if (adapter.getMetricSource().equals(MetricSource.IssueTracker)) {
				jiraDataService.delete(jiraDataService.getAllByAdapter(adapter));
			}
			else if (adapter.getMetricSource().equals(MetricSource.StaticAnalysis)) {
				sonarDataService.delete(sonarDataService.getAllByAdapter(adapter));
			}
			else if (adapter.getMetricSource().equals(MetricSource.TestingFramework)) {
				testlinkDataService.delete(testlinkDataService.getAllByAdapter(adapter));
			}
			else if (adapter.getMetricSource().equals(MetricSource.CubeAnalysis)) {
				cubesDataService.delete(cubesDataService.getAllByAdapter(adapter));
			}
			else if (adapter.getMetricSource().equals(MetricSource.ContinuousIntegration)) {
				jenkinsDataService.delete(jenkinsDataService.getAllByAdapter(adapter));
			}
			
			// Delete the actual adapter 
			adapterSettingsService.delete(adapter);
		}
		
		getPage().success(message);
		updateFeedbackPanel(target);
		updateAdapterList(target);
		// update the delete button
		adapterGroup.updateModel();
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
	private void updateAdapterList(AjaxRequestTarget target) {
		target.add(adapterContainer);
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
	private static PageParameters forAdapter(AdapterSettings adapter) {
		return new PageParameters().set("id", adapter.getId());
	}


	/**
	 * View message on the panel about a successful update
	 * @param target
	 */
	private void viewUpdateSuccessful(AjaxRequestTarget target) {
		// success message has to be associated to session so that it is shown
		// in the global feedback panel
		String message = new StringResourceModel("updated.message",
				this, null).getString();
		getPage().success(message);
		updateFeedbackPanel(target);
	}

	/**
	 * View message on the panel when the update is failed 
	 * @param target
	 */
	private void viewUpdateFailed(AjaxRequestTarget target) {
		// success message has to be associated to session so that it is shown
		// in the global feedback panel
		String message = new StringResourceModel("error.message",
				this, null).getString();
		getPage().error(message);
		updateFeedbackPanel(target);

	}


	private final class AdapterProvider extends EntityProvider<AdapterSettings> {


		/**
		 * 
		 */
		private static final long serialVersionUID = -8769895854281922343L;

		@Override
		public Iterator<? extends AdapterSettings> iterator(long first, long count) {
			return adapterSettingsService.getAllByAscendingName(
					Long.valueOf(first).intValue(),
					Long.valueOf(count).intValue()).iterator();
		}

		@Override
		public long size() {
			return adapterSettingsService.countAll();
		}
	}
}
