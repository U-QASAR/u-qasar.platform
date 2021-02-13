package eu.uqasar.web.pages.processes;

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
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
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
import eu.uqasar.model.process.Process;
import eu.uqasar.service.ProcessService;
import eu.uqasar.web.components.JSTemplates;
import eu.uqasar.web.components.ModalActionButton;
import eu.uqasar.web.components.NotificationModal;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.processes.panels.ProcessManagementPanel;
import eu.uqasar.web.pages.processes.panels.ProcessesFilterStructure;
import eu.uqasar.web.provider.EntityProvider;

public class ProcessManagementPage extends BasePage {

	private static final long serialVersionUID = 2466031856116400997L;

	@Inject
	ProcessService processService;

//	private List dataMeasurements;

	private final ProcessManagementPanel filterPanel;
	// how many products do we show per page
	private static final int itemsPerPage = 10;

	// container holding the list of processes
	private final WebMarkupContainer processesContainer = newProcessesContainer();

	private final Modal deleteConfirmationModal;

	private final CheckGroup<Process> processGroup;

	private final AjaxSubmitLink deleteSelectedButton;

	/**
	 * Constructor building the page
	 * 
	 * @param parameters
	 */
	public ProcessManagementPage(final PageParameters parameters) {

		super(parameters);

		final ProcessProvider processProvider = new ProcessProvider();
		
		final Form<Process> deleteForm = new Form<>("deleteForm");
		add(deleteForm);
		
		filterPanel = new ProcessManagementPanel("filter") {

			@Override
			public void applyClicked(AjaxRequestTarget target, Form<?> form) {
				processProvider.setFilter(this.getFilter());
				target.add(processesContainer);
			}

			@Override
			public void resetClicked(AjaxRequestTarget target, Form<?> form) {
				processProvider.setFilter(new ProcessesFilterStructure());
				target.add(processesContainer);

			}

		};
		deleteForm.add(filterPanel);

		// add checkgroup for selecting multiple processes
		deleteForm.add(processGroup = newProcessCheckGroup());

		// add the container holding list of existing processes
		processGroup.add(processesContainer.setOutputMarkupId(true));

		processesContainer.add(new CheckGroupSelector(
				"processGroupSelector", processGroup));

		DataView<Process> processes = new DataView<Process>(
				"processes", processProvider, itemsPerPage) {

			private static final long serialVersionUID = 789669450347695209L;

			@Override
			protected void populateItem(final Item<Process> item) {
				final Process Process = item.getModelObject();

				item.add(new Check<>("processCheck", item
						.getModel(), processGroup));

				item.add(new Label("name", new PropertyModel<String>(
						Process, "name")));

				item.add(new Label("description", new PropertyModel<String>(
						Process, "description")));
												
				item.add(new DateLabel("startDate", new PropertyModel<Date>(
						Process, "startDate"), new PatternDateConverter(
						"dd.MM.yyyy", true)));

				item.add(new DateLabel("endDate", new PropertyModel<Date>(
						Process, "endDate"), new PatternDateConverter(
						"dd.MM.yyyy", true)));
				
				// add button to show AddEditPage
				item.add(new BookmarkablePageLink<ProcessAddEditPage>(
						"edit", ProcessAddEditPage.class,
						ProcessAddEditPage.linkToEdit(Process)));
			}
		};
		// add list of processes to container
		processesContainer.add(processes);

		// add button to create new Process
		processesContainer
				.add(new BookmarkablePageLink<ProcessAddEditPage>(
						"addProcessLink", ProcessAddEditPage.class));

		// add links for table pagination
		processesContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorHead", processes));
		processesContainer.add(new BootstrapAjaxPagingNavigator(
				"navigatorFoot", processes));

		// add button to delete selected products
		processesContainer
				.add(deleteSelectedButton = newDeleteSelectedButton(processGroup));

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
	 * @return
	 */
	private WebMarkupContainer newProcessesContainer() {
		return new WebMarkupContainer("processesContainer") {

			private static final long serialVersionUID = -6725820191388731244L;

			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				// add javascript to load tagsinput plugin
				response.render(OnLoadHeaderItem.forScript(String.format(
						JSTemplates.LOAD_TABLE_SORTER, "Process-list")));
			}
        };
	}

	/**
	 * 
	 * @return
	 */
	private CheckGroup<Process> newProcessCheckGroup() {
		CheckGroup<Process> checkGroup = new CheckGroup<>(
				"processGroup", new ArrayList<Process>());
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
	 * @param processGroup
	 * @return
	 */
	private AjaxSubmitLink newDeleteSelectedButton(
			final CheckGroup<Process> processGroup) {
		return new AjaxSubmitLink("deleteSelected") {
			private static final long serialVersionUID = 1162060284069587067L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				// only enabled if at least one Process is selected
				if (processGroup.getModelObject().isEmpty()) {
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
				deleteSelectedProcesses(processGroup.getModelObject(),
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
	private void deleteSelectedProcesses(
			Collection<Process> processes, AjaxRequestTarget target) {
		String message = new StringResourceModel("process.selected.deleted",
				this, null).getString();
		processService.delete(processes);
		getPage().success(message);
		updateFeedbackPanel(target);
		updateProcessList(target);
		// update the delete button
		processGroup.updateModel();
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
	private void updateProcessList(AjaxRequestTarget target) {
		target.add(processesContainer);
	}

	/**
	 * 
	 * @param target
	 */
	private void updateFeedbackPanel(AjaxRequestTarget target) {
		target.add(feedbackPanel);
	}

	private final class ProcessProvider extends EntityProvider<Process> {

		private static final long serialVersionUID = -1527580045919906872L;

		private ProcessesFilterStructure filter;

		public ProcessProvider() {
		}

        public ProcessProvider(ProcessesFilterStructure filter) {
			this.filter = filter;
		}

		public void setFilter(ProcessesFilterStructure filter) {
			this.filter = filter;
		}

		@Override
		public Iterator<? extends Process> iterator(long first, long count) {

			if (filterPanel.getSelected() == ProcessManagementPanel.DESCENDING_IS_SELECTED) {
				return processService.getAllByDescendingEndDateNameFiltered(
						filter, Long.valueOf(first).intValue(),
						Long.valueOf(count).intValue()).iterator();
			} else {
				return processService.getAllByAscendingEndDateNameFiltered(
						filter, Long.valueOf(first).intValue(),
						Long.valueOf(count).intValue()).iterator();
			}
		}

		@Override
		public long size() {
			return processService.countAll();
		}
	}
}
